/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputePoolController {

    def provisioningService
    def providerService
    def schedulingService

    def scaffold = true
    // def index = { }

    def save = {
        def pool = new ComputePool(params)
        pool.account = ActiveAccount.get(1).account
        pool.cost = new ComputePoolCost()
        // Fixup provider service, otherwise it turns into a string. TODO: cleaner?
        pool.providerService = providerService
        pool.instances = 0
        if (!pool.created) pool.created = new Date()
        if(!pool.hasErrors() && pool.save(flush:true)) {
            flash.message = "ComputePool ${pool.id} created"
            // Spin up nodes.
            schedulingService.schedulePool(pool.id, provisioningService)
            // Initial kick for job so it doesn't have to wait.
            if (pool.scheduling) {
                schedulingService.timer.execute()
            } else {
                provisioningService.setInstances(pool, params.instances.toInteger());
            }
            // Go to show to avoid transaction problem updating list.
            redirect(action:show, id:pool.id)
        } else {
            render(view:'create',model:[computePoolInstance:pool])
        }
    }

    def create = {
        def computePoolInstance = new ComputePool()
        computePoolInstance.properties = params
        def account = ActiveAccount.get(1).account
        computePoolInstance.account = account
        // TODO: shortcut -- choose default launch script based on account information.
        // This is a hard-code hack and should be replaced by passing through the credentials
        // for the account to the node instance data as environment variables, then using
        // those in the instance Config.groovy to establish credentials with the provider
        // (SQS in this case).
        if (account.name == "ec2dev") {
            computePoolInstance.userData = "AUTOLAUNCH_SCRIPT=launch_sttEngine_devtest"
        } else {
            computePoolInstance.userData = "AUTOLAUNCH_SCRIPT=launch_sttEngine_production"
        }
        return [computePoolInstance:computePoolInstance,account:ActiveAccount.get(1)]
    }

    def update = {
        def pool = ComputePool.get( params.id )
        log.info "update ${pool}"
        if(pool) {
            if(params.version) {
                def version = params.version.toLong()
                if(pool.version > version) {

                    pool.errors.rejectValue("version", "computePool.optimistic.locking.failure", "Another user has updated this ComputePool while you were editing.")
                    render(view:'edit',model:[computePoolInstance:pool])
                    return
                }
            }
            int instances = pool.instances
            // Ignore certain changes after creation.
            if (pool.type != params.type) {
                pool.errors.rejectValue("type", "message.pool.change.type", "cannot change type once pool is created")
            }
            if (pool.ami != params.ami) {
                pool.errors.rejectValue("ami", "message.pool.change.ami", "cannot change ami once pool is created")
            }
            // Reject changes to pool name since this is passed as instance data to nodes
            // while provisioning (for log identity)
            if (pool.name != params.name) {
                pool.errors.rejectValue("name", "message.pool.change.name", "cannot change name once pool is created")
            }
            if (!pool.hasErrors()) {
                pool.properties = params
                pool.instances = instances
                // Fixup provider service, otherwise it turns into a string. TODO: cleaner?
                pool.providerService = providerService
                // Initial kick for job so it doesn't have to wait.
                if (pool.scheduling) {
                    schedulingService.timer.execute()
                } else {
                    provisioningService.setInstances(pool, params.instances.toInteger());
                }
            }
            if (pool.reboot) {
                provisioningService.reboot(pool)
                pool.reboot = false;
            }
            if(!pool.hasErrors() && pool.save(flush:true)) {
                log.info "updated ${pool}"
                flash.message = "ComputePool ${params.id} updated"
                redirect(action:show,id:pool.id)
                // Reboot?
            }
            else {
                render( view:'edit',model:[computePoolInstance:pool])
            }
        }
        else {
            flash.message = "ComputePool not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def list = {
        try {
            provisioningService.pollPools()
        } catch (Throwable t) {
            log.error t
        }
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
        [ computePoolInstanceList: ComputePool.list( params ), computePoolInstanceTotal: ComputePool.count() ]
    }
    def show = {
        try {
            provisioningService.pollPools()
        } catch (Throwable t) {
            log.error t
        }

        def pool = ComputePool.get( params.id )
        if(!pool) {
            flash.message = "ComputePool not found with id ${params.id}"
            redirect(action:list)
        } else {
            return [ computePoolInstance : pool ]
        }
    }

}
