/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core

import com.appscio.mpf.grails.core.MpfReport;
import com.appscio.mpf.grails.report.MpfReportManagerService;

class MpfReportController {
    def mpfReportManagerService

    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
        [ mpfReportInstanceList: MpfReport.list( params ), mpfReportInstanceTotal: MpfReport.count() ]
    }

    def show = {
        def mpfReportInstance = MpfReport.get( params.id )

        if(!mpfReportInstance) {
            flash.message = "MpfReport not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ mpfReportInstance : mpfReportInstance ] }
    }

    def delete = {
        def mpfReportInstance = MpfReport.get( params.id )
        if(mpfReportInstance) {
            try {
                mpfReportInstance.delete(flush:true)
                flash.message = "MpfReport ${params.id} deleted"
                redirect(action:list)
            }
            catch(org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "MpfReport ${params.id} could not be deleted"
                redirect(action:show,id:params.id)
            }
        }
        else {
            flash.message = "MpfReport not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def mpfReportInstance = MpfReport.get( params.id )

        if(!mpfReportInstance) {
            flash.message = "MpfReport not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ mpfReportInstance : mpfReportInstance ]
        }
    }

    def update = {
        def mpfReportInstance = MpfReport.get( params.id )
        if(mpfReportInstance) {
            if(params.version) {
                def version = params.version.toLong()
                if(mpfReportInstance.version > version) {

                    mpfReportInstance.errors.rejectValue("version", "mpfReport.optimistic.locking.failure", "Another user has updated this MpfReport while you were editing.")
                    render(view:'edit',model:[mpfReportInstance:mpfReportInstance])
                    return
                }
            }
            mpfReportInstance.properties = params
            if(!mpfReportInstance.hasErrors() && mpfReportInstance.save()) {
                flash.message = "MpfReport ${params.id} updated"
                redirect(action:show,id:mpfReportInstance.id)
            }
            else {
                render(view:'edit',model:[mpfReportInstance:mpfReportInstance])
            }
        }
        else {
            flash.message = "MpfReport not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def create = {
        def mpfReportInstance = new MpfReport()
        mpfReportInstance.properties = params
        return ['mpfReportInstance':mpfReportInstance]
    }

    def save = {
        def mpfReportInstance = new MpfReport(params)
        if(!mpfReportInstance.hasErrors() && mpfReportInstance.save()) {
            // mpfReportManagerService.process(mpfReportInstance)
            flash.message = "MpfReport ${mpfReportInstance.id} created"
            redirect(action:show,id:mpfReportInstance.id)
        }
        else {
            render(view:'create',model:[mpfReportInstance:mpfReportInstance])
        }
    }
}
