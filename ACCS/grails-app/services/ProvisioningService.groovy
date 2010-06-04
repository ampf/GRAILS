/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ProvisioningService implements org.springframework.beans.factory.InitializingBean {

    static expose = ['jmx']

    void afterPropertiesSet() {
        // Establish refresh timer for accounting.
        RefreshQuartzJob.executors += { pollPools() }
    }

    synchronized void pollPools() {
        try {
            // Query the cloud and update state.
            def pools = ComputePool.list()
            pools.each {pool ->
                updateNodes(pool)
            }
        } catch (Exception x) {
            log.error "unable to poll pools " + x
            x.printStackTrace()
        }
    }

    // TODO: enum this.
    static String CREATED="created", PENDING="pending", RUNNING="running", SHUTTING_DOWN="shutting-down", TERMINATED="terminated"

    synchronized def updateNodes(def pool) {
        def nodes = pool.nodes
        def remove = []
        def restart = 0
        pool.active = 0
        nodes.each {node ->
            def state = pool.providerService.state(node)
            if (state) {
                log.debug "updating node=${node}"
                node.internalName = state.privateDnsName
                node.externalName = state.publicDnsName
                def oldstatus = node.status
                node.status = state.instanceState.name
                log.debug "node.status=${node.status} oldstatus=${oldstatus}"
                if (node.status == RUNNING || node.status == PENDING) {
                    if (oldstatus != RUNNING) {
                        node.provisionTime = (System.currentTimeMillis()-node.started.time)/1000
                    }
                    if (node.status == RUNNING) {
                        node.upTime = (System.currentTimeMillis()-node.started.time)/1000 - node.provisionTime
                        pool.active++
                    }
                } else if (node.status == SHUTTING_DOWN) {
                    pool.active++
                    if (node.upTime > 0) {
                        // Continue to add uptime if it came up.
                        node.upTime = (System.currentTimeMillis()-node.started.time)/1000 - node.provisionTime
                    }
                } else if (node.status == TERMINATED) {
                    remove += node
                    if (node.upTime > 0) {
                        pool.cost.stopped += node.upTime*pool.price()/3600
                        log.info "${node} terminated: pool.cost.stopped = ${pool.cost.stopped}"
                    }
                    pool.stopped++
                    log.info "***** ${pool} node=${node} terminated, stopped=${pool.stopped} cost=${pool.cost}"
                }
            } else if (node.status == RUNNING){
                // Reap and reschedule.
                log.warn "node ${node.id} has died, restarting..."
                node.status = "died"
                pool.cost.stopped += node.upTime*pool.price()/3600
                log.info "${node} died: pool.cost.stopped = ${pool.cost.stopped}"
                remove += node
                pool.instances--
                restart++
            } else if (node.status != CREATED) {
                // Probably wrong account.  Or may be a race condition during scheduling.
                log.warn "Unable to get state for ${node} (Wrong account?: ${pool.account})"
            }
        }
        remove.each {node->
            def history = ComputeNodeHistory.findWhere(nodeId:node.id)
            if (history == null) {
                log.warn("unable to locate history for ${node}")
            } else {
                history.status = node.status
                history.upTime = node.upTime
                history.provisionTime = node.provisionTime
                history.pool = node.pool
                if (!history.save(flush:true))
                    log.error history.errors
            }
            pool.removeFromNodes(node)
            node.delete()
        }
        pool.cost.active = 0
        pool.nodes.each {node->
            if (node.status == RUNNING || node.status == SHUTTING_DOWN) {
                pool.cost.active += node.upTime*pool.price()/3600
                log.debug "${node} running | shutting-down: node.upTime=${node.upTime}"
            }
        }
        log.info "cost for pool=${pool} is ${pool.cost}"
        if (!pool.save(flush:true)) {
            log.error pool.errors
            return
        }
        try {
            setInstances(pool, pool.instances+restart)
        } catch (Throwable t) {
            log.error t
        }
    }

    synchronized def setInstances(def pool, def instances) {
        // Proactively limit instances based on constraints.
        def max = pool.constraints.instances.max
        def min = pool.constraints.instances.min
        if (instances > max) {
            log.warn "instances limited from ${instances} to max ${max}"
            instances = max
        } else if (instances < min) {
            log.warn "instances limited from ${instances} to min ${min}"
            instances = min
        }
        int delta = instances - pool.instances
        int count = Math.abs(delta)
        pool.instances = instances
        // Validate before attempting schedule.
        if (!pool.validate()) {
            rollback(pool.errors)
        }
        if (delta > 0) {
            log.info "starting ${count} instances of ${pool.ami}"
            (1..count).each {
                def node = new ComputeNode(instanceId:"")
                pool.addToNodes(node)
                if (!node.save(flush:true)) {
                    rollback(node.errors)
                }
                // Synthesize a node identifier based on pool name into the user-data.
                node.userData = "POOL_NAME=${pool.name},NODE_ID=${node.id},${pool.userData}"
                log.info "node.userData=(${node.userData})"
                pool.providerService.start(pool, node)
                node.status = PENDING
                def state = pool.providerService.state(node).instanceState.name
                assert state == PENDING || state == RUNNING
            }
            if (!pool.save(flush:true)) {
                rollback(pool.errors)
            }

        } else if (delta < 0){
            log.info "stopping ${count} instances of ${pool.ami}"
            int i = 0;
            pool.nodes.each { node ->
                if (i < count && (node.status == PENDING || node.status == RUNNING)) {
                    i++
                    log.info "${pool.name} stopping ${node}"

                    def history = new ComputeNodeHistory(nodeId:node.id, internalName:node.internalName, externalName:node.externalName,
                            instanceId:node.instanceId, pool:node.pool)
                    if (!history.save(flush:true))
                        log.error history.errors

                    pool.providerService.stop(pool, node)
                    // Be-careful: the node is not guaranteed to do what it is told.
                    // Observed one time: state == PENDING after stop above.
                    def state = pool.providerService.state(node).instanceState.name
                    if (state == SHUTTING_DOWN) {
                        node.status = SHUTTING_DOWN
                    } else {
                        log.error "unexpected state for node ${node}==${state}, should be ${SHUTTING_DOWN}"
                    }
                }
            }
            if (i < count) {
                log.error "unable to find sufficient nodes (need ${count}, only found ${i}) to shut down"
            }
            if (!pool.save(flush:true)) {
                rollback(pool.errors)
            }

        } else {
            // println "PROVISION: Nothing to start/stop"
        }
    }
    static def rollback(def errors) {
        getLog().error errors
        throw new RuntimeException(errors.toString())
    }

    def reboot(def pool) {
        log.info "rebooting ${pool} using provider ${pool.providerService}"
        pool.providerService.reboot(pool)
    }
}
