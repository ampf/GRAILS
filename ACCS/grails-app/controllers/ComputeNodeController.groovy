/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputeNodeController {
    def provisioningService

    def scaffold = true

    // Support adding an individual node to a pool for purposes of repair.
    def save = {
        ComputeNode.withTransaction { status ->
            if (!params?.pool?.id) {
                flash.error = "ComputeNode pool must be specified"
                render(view:'create',model:[computeNodeInstance:computeNodeInstance])
            }
            def pool = ComputePool.get(params.pool.id)
            def computeNodeInstance = new ComputeNode(params)
            pool.addToNodes(computeNodeInstance)
            pool.instances++
            def state = pool.providerService.state(computeNodeInstance)
            // Approximate the started time. e.g. 2009-08-11T00:37:57.000Z (which appears to be
            // GMT timezone)
            if (state) {
                def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
                computeNodeInstance.started = sdf.parse(state.launchTime)
                computeNodeInstance.status = ProvisioningService.RUNNING
                computeNodeInstance.instanceId = computeNodeInstance.instanceId.trim()
                if(!computeNodeInstance.hasErrors() && computeNodeInstance.save(flush:true)) {
                    flash.message = "ComputePool ${computeNodeInstance.id} created"
                    redirect(action:show,id:computeNodeInstance.id)
                }
                else {
                    render(view:'create',model:[computeNodeInstance:computeNodeInstance])
                }
            } else {
                // Bad node ID?
                computeNodeInstance.errors.rejectValue("instanceId", "message.node.instanceId", "unable to obtain status for instanceid ${params.instanceId}")
                render(view:'create',model:[computeNodeInstance:computeNodeInstance])
                status.setRollbackOnly()
                return
            }
        }
    }
    // def index = { }
    def list = {
        try {
            provisioningService.pollPools()
        } catch (Throwable t) {
            log.error t
        }
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
        [ computeNodeInstanceList: ComputeNode.list( params ), computeNodeInstanceTotal: ComputeNode.count() ]
    }

    def show = {
        try {
            provisioningService.pollPools()
        } catch (Throwable t) {
            log.error t
        }
        def node = ComputeNode.get( params.id )

        if(!node) {
            flash.message = "ComputeNode not found with id ${params.id}"
            redirect(action:list)
        } else {
            return [ computeNodeInstance : node ]
        }
    }
}
