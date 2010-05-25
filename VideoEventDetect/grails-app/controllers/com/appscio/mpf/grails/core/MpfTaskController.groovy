/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core


import org.hibernate.StaleObjectStateException
import org.apache.log4j.Logger

import com.appscio.mpf.grails.core.MpfReport;
import com.appscio.mpf.grails.core.MpfTask;

import grails.converters.*

class MpfTaskController {
    def mpfDetectorService
    def mpfTaskManagerService
    def sessionFactory
    def grailsApplication
    def filterService

    def scaffold = true

    def index = { redirect(action:list,params:params)
    }

    static allowedMethods = [delete:'POST', save:'POST', update:'POST', event:'POST', status:['PUT','POST'], kill:'POST',debugAccept:['GET','PUT','POST','DELETE']]

    /* perform task-specific translation to an MpfEvent */
    def event = {
        printf "MpfTaskController event got request with " + params
        def requestedTaskKey=params.id
        log.info "report received for task ${requestedTaskKey}"

        def evText=params.event
        try {
            if(!evText) evText=request?.reader?.text?.trim()
        } catch (IllegalStateException isx) {
            log.warn "No event parameter or attached document found"
            // Fall through and hit the error response below.
        }
        if(!evText){
            response.status = 400
            render "No event parameter or attached document found"
            return
        }

        def rstr
        EventExtractionResponse eer
        try{
            eer = mpfDetectorService.extractEventsFromReport(requestedTaskKey, evText)
        } catch (Exception e){
            response.status=500
            return [exception:e]
        }

        if(!eer){
            response.status=500
            rstr= "null reponse from mpfDetectorService, check server logs!"
            log.warn rstr
            render rstr
            return
        }

        if(eer.code != 200){
            response.status=eer.code
            rstr = "mpfDetectorService returns code ${eer.code}, ${eer.msg}"
            log.warn rstr
            render rstr
            return
        }

        try{
            rstr = eer.evtList == [] ?
            '''<?xml version="1.0"?><message>No events founds</message> ''' :
            eer.evtList as XML
            log.debug "returning \n${rstr}"
            response.status=200
            render rstr
            return
        } catch (Exception e){ // possible the evtList convert blows up
            rstr = "converting ${eer.evtList} or returning it caught" + e.toString()
            log.warn rstr, e
            response.status=500
            return [exception:e]
        }
        render rstr // shouldn't get here, should be doing renders above
    }

    /* let remote task update local state (idempotent PUT) */
    def status = { // set task state

        def state= params.state
        def msg = params.msg
        log.debug "status received for task ${params.id} state:${state}"
        log.trace "status call gets ${params}"

        // some special 'state' values are just for logging, so we don't invoke the service at all
        def STATE = state?.toUpperCase()
        switch (STATE){
            case ~/LOG/: // these three we just route to the logs
            case ~/OUT/:
            case ~/ERR/:
            Logger remlogger=Logger.getLogger(grailsApplication?.config?.com?.appscio?.mpf?.remotelogger ?: "mpf.remote")
            // the normal status line comes in with the task name in brackets
            // we want the task name for the vadlogs filename, not on every logged line
            // so lets get and extract the task name
            def tn = msg =~ /vad[^\]]*/
            def msg_no_prefix = tn.size()>0?  msg - tn[0] - "[] " : msg // in case of non-prefixed line
            def task = MpfTask.read(params.id)
            def taskname = tn.size() > 0? tn[0] : task?.name

            if(taskname){
                def tmpdir = "/tmp/vadlogs"
                new File(tmpdir).mkdir() // just in case ...
                File splitFile = new File("${tmpdir}/${taskname}.${STATE}")

                def filterprefix = "curl returns status=0"
                boolean filterOut = msg_no_prefix ==~ /${filterprefix}.*/
                if (!filterOut) splitFile << "${msg_no_prefix}\n"

                // now route to the log4j system
                def loglevel = (STATE == "LOG") ? task.log_to_host :
                (STATE == "OUT" ? task.out_to_host : task.err_to_host)
                remlogger."${loglevel}" "$state :: ${params.msg}"

                response.status=200
                render "MpfTaskController got [ ${state} ] message"
                return
            } else {
                log.warn "status{} can't find task ${params.id} so dropping $state $msg"
                response.status = 404
                render "MpfTaskController could not find MpfTask ${params.id}\n"
                return
            }
            break
            default: // other state messages are actual task states
            def result = mpfTaskManagerService.updateTaskStatus(params)
            log.debug "mpfTaskManagerService.updateTaskStatus returned ${result}"
            switch (result) {
                case ~/^updated.*/:
	                response.status = 200
	                render "MpfTaskController says thanks for the update to state=${state} (msg=${msg})\n"
	                return
                break
                case ~/^notfound.*/ :
	                response.status = 404
	                log.warn "MpfTaskController could not find MpfTask ${params.id}\n"
	                render "MpfTaskController could not find MpfTask ${params.id}\n"
	                return
                break
                case ~/^null state.*/ :
	                response.status = 400
	                log.warn "MpfTaskController got null state var\n"
	                render "MpfTaskController got null state for task ${params.id}, nothing to do\n"
	                return
                break
                default:
	                log.warn "MpfTaskController problem, id=${params.id}, " + result.toString()+"\n"
	                response.status = 500
	                render "MpfTaskController problem, " + result.toString()+"\n"
                return
            }
        }
    }

    def kill = {
        def requestedTaskKey=params.id
        def mpfTaskInstance = MpfTask.get(requestedTaskKey)
        if (!mpfTaskInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfTask.label', default: 'MpfTask'), params.id])}"
            redirect(action: "list")
        } else {
            mpfTaskManagerService.killTask(mpfTaskInstance)
            redirect(action:"show", params:["id":params.id])
        }
    }

    def filter = {
        if(!params.max) params.max = 10
        render( view:'list',
            model:[ domainClassList: filterService.filter( params, MpfTask ),
            domainClassCount: filterService.count( params, MpfTask ),
            filterParams: com.zeddware.grails.plugins.filterpane.FilterUtils.extractFilterParams(params),
            params:params ] )
    }

    def create = {
        def mpfTaskInstance = new MpfTask()
        mpfTaskInstance.statusRelUrl=g.createLink(action:'status', absolute:true)
        mpfTaskInstance.eventRelUrl=g.createLink(action:'event', absolute:true)
        mpfTaskInstance.properties = params
        def targets = MpfExecTgt.list()
        return [
        'mpfTaskInstance':mpfTaskInstance,
        'detectors':mpfDetectorService.listDetectorNames(),
        'prefix_strings': mpfTaskManagerService.make_DETECTOR_PREFIX_FOR_UI_strings('ALL'),
        'targets':targets,
        'capabilities':MpfCapability.list()]
    }

    def clone = {
        def clonedMpfTaskInstance = MpfTask.read(params.id)
        if (!clonedMpfTaskInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfTask.label', default: 'MpfTask'), params.id])}"
            redirect(action: "list")
        }
        else {
            def mpfTaskInsance = new MpfTask()
            mpfTaskInstance.properties = clonedMpfTaskInstance.properties
            return [mpfTaskInstance: mpfTaskInstance,
            'detectors':mpfDetectorService.listDetectorNames(),
            'prefix_strings': mpfTaskManagerService.make_DETECTOR_PREFIX_FOR_UI_strings(mpfTaskInstance),
            'targets':MpfExecTgt.list(),
            'capabilities':MpfCapability.list()]
        }
    }


    def latestCreatedForZero(){
        def t = MpfTask.list(max:1,sort:"dateCreated",order:"desc")[0]
        log.info "request for id 0 mapping to ${t}"
        t.id
    }

    def list = {
    	def	runningTasksForAllTargets = mpfTaskManagerService.runningTasksForAllTargets()
    	params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
        [ mpfTaskInstanceList: MpfTask.list( params ), mpfTaskInstanceTotal: MpfTask.count(),
          runningTasksForAllTargets:runningTasksForAllTargets]
    }

    def show = {
        def taskid = params.id == "0" ? latestCreatedForZero() : params.id
        def mpfTaskInstance = MpfTask.read(taskid)
        if (!mpfTaskInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfTask.label', default: 'MpfTask'), taskid])}"
            redirect(action: "list")
        }
        else {
            [mpfTaskInstance: mpfTaskInstance]
        }
    }

    def edit = {
        def mpfTaskInstance = MpfTask.get(params.id)
        if (!mpfTaskInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfTask.label', default: 'MpfTask'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [mpfTaskInstance: mpfTaskInstance,
            'detectors':mpfDetectorService.listDetectorNames(),
            'prefix_strings': mpfTaskManagerService.make_DETECTOR_PREFIX_FOR_UI_strings(mpfTaskInstance),
            'targets':MpfExecTgt.list(),
            'capabilities':MpfCapability.list()]
        }
    }

    def save = {
        log.info "save() action receives params " + params
        def mpfTaskInstance
        try {
            mpfTaskInstance = new MpfTask(params)
            if(mpfTaskInstance?.hasErrors()) {
                render(view:'create',model:[mpfTaskInstance:mpfTaskInstance])
                return
            }
            if(mpfTaskInstance?.save(flush:true)) {
                flash.message = "MpfTask ${mpfTaskInstance.id} created"
                // the explicit call to submitTask will go way when there is a task-runner job
                mpfDetectorService.submitTask(mpfTaskInstance)
                //redirect(action:show,id:mpfTaskInstance.id)
                redirect(action:list)
            } else {
                log.warn "save() saving failed"
                render(view:'create',model:[mpfTaskInstance:mpfTaskInstance])
                return
            }
        } catch (Exception e){
            log.warn "save() action catches Exception  ",e
            response.status = 500
            return [exception:e]
        }
    }
    /*
     // REST support
     def events = {
     def taskid = params.id == "0" ? latestCreatedForZero() : params.id
     def mpfTaskInstance = MpfTask.get(taskid)
     withFormat {
     xml {render mpfTaskInstance.detectedEvents as XML}
     }
     }
     */
    // test support
    def debugAccept = {
        def clientRequest = request.getHeader("Accept")
        def serverResponse = request.format
        render "Client: ${clientRequest}\nServer: ${serverResponse}\n"
    }

}
