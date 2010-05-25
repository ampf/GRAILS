/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core
import groovy.xml.MarkupBuilder

import com.appscio.mpf.grails.core.MpfExecTgt;
import com.appscio.mpf.grails.core.MpfCapability;

class MpfExecTgtController {

    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST', runningList:'GET']

    def mpfTaskManagerService
    def mpfTargetManagerService
    
    def runningList = {
	    def tgtid = params.id == "0" ? latestCreatedForZero() : params.id
	    def mpfExecTgtInstance = MpfExecTgt.get( tgtid )
    	def runningList = mpfTargetManagerService.fetchRunningList(mpfExecTgtInstance)
    	def dbList = mpfTargetManagerService.fetchRunningRemoteStagingDirPerDb(mpfExecTgtInstance)
 //   	def writer = new StringWriter()
  //  	def xml = new MarkupBuilder(writer)

    	render(contentType:"text/xml") {
    		targetInfo(id:mpfExecTgtInstance.id, runningTaskSetSizeMatch:(runningList.size() == dbList.size())){
	    	    runningPerTarget(count:runningList.size()){
	    	        runningList.each {
	    	            rsd(it)
	    	        }
	    	    }
	    	    runningPerDb(count:dbList.size()){
	    	        dbList.each {
	    	            rsd(it)
	    	        }
	    	    }
    		}
    	}    	
    }
    
    def list = {
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
        [ mpfExecTgtInstanceList: MpfExecTgt.list( params ), mpfExecTgtInstanceTotal: MpfExecTgt.count() ]
    }

    def latestCreatedForZero(){
    	def t = MpfExecTgt.list(max:1,sort:"dateCreated",order:"desc")[0]
    	log.info "request for id 0 mapping to ${t}"
 		t.id
    }
    
    def show = {
	    def tgtid = params.id == "0" ? latestCreatedForZero() : params.id
        def mpfExecTgtInstance = MpfExecTgt.get( tgtid )

        if(!mpfExecTgtInstance) {
            flash.message = "MpfExecTgt not found with id ${params.id} mapped to ${tgtid}"
            redirect(action:list)
        }
        else { return [ mpfExecTgtInstance : mpfExecTgtInstance, runningCount:mpfTaskManagerService?.runningTasksForTarget(mpfExecTgtInstance) ] }
    }

    def delete = {
        def mpfExecTgtInstance = MpfExecTgt.get( params.id )
        if(mpfExecTgtInstance) {
            try {
                mpfExecTgtInstance.delete(flush:true)
                flash.message = "MpfExecTgt ${params.id} deleted"
                redirect(action:list)
            }
            catch(org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "MpfExecTgt ${params.id} could not be deleted"
                redirect(action:show,id:params.id)
            }
        }
        else {
            flash.message = "MpfExecTgt not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def mpfExecTgtInstance = MpfExecTgt.get( params.id )

        if(!mpfExecTgtInstance) {
            flash.message = "MpfExecTgt not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ mpfExecTgtInstance : mpfExecTgtInstance,
                     capabilities:MpfCapability.list()]
        }
    }

    def update = {
        def mpfExecTgtInstance = MpfExecTgt.get( params.id )
        if(mpfExecTgtInstance) {
            if(params.version) {
                def version = params.version.toLong()
                if(mpfExecTgtInstance.version > version) {

                    mpfExecTgtInstance.errors.rejectValue("version", "mpfExecTgt.optimistic.locking.failure", "Another user has updated this MpfExecTgt while you were editing.")
                    render(view:'edit',model:[mpfExecTgtInstance:mpfExecTgtInstance, 'capabilities':MpfCapability.list()])
                    return
                }
            }
            mpfExecTgtInstance.properties = params
            if(!mpfExecTgtInstance.hasErrors() && mpfExecTgtInstance.save()) {
                flash.message = "MpfExecTgt ${params.id} updated"
                redirect(action:show,id:mpfExecTgtInstance.id)
            }
            else {
                render(view:'edit',model:[mpfExecTgtInstance:mpfExecTgtInstance, 'capabilities':MpfCapability.list()])
            }
        }
        else {
            flash.message = "MpfExecTgt not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def create = {
        def mpfExecTgtInstance = new MpfExecTgt()
        mpfExecTgtInstance.properties = params
        return ['mpfExecTgtInstance':mpfExecTgtInstance,
                 'capabilities':MpfCapability.list()]
    }
    
    def save = {
        def mpfExecTgtInstance = new MpfExecTgt(params)
        if(!mpfExecTgtInstance.hasErrors() && mpfExecTgtInstance.save()) {
            flash.message = "MpfExecTgt ${mpfExecTgtInstance.id} created"
            redirect(action:show,id:mpfExecTgtInstance.id)
        }
        else {
            render(view:'create',model:[mpfExecTgtInstance:mpfExecTgtInstance])
        }
    }
}
