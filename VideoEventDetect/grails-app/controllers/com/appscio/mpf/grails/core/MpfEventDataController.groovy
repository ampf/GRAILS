/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core

import java.beans.Introspector

import com.appscio.mpf.grails.core.MpfEventData;

class MpfEventDataController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def scaffold=true
/*
    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [mpfEventDataInstanceList: MpfEventData.list(params), mpfEventDataInstanceTotal: MpfEventData.count()]
    }

    def create = {
        def mpfEventDataInstance = new MpfEventData()
        mpfEventDataInstance.properties = params
        return [mpfEventDataInstance: mpfEventDataInstance]
    }

    def save = {
        def mpfEventDataInstance = new MpfEventData(params)
        if (mpfEventDataInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'mpfEventData.label', default: 'MpfEventData'), mpfEventDataInstance.id])}"
            redirect(action: "show", id: mpfEventDataInstance.id)
        }
        else {
            render(view: "create", model: [mpfEventDataInstance: mpfEventDataInstance])
        }
    }
*/
    def extractName(classname){ // simple utilit function for the 'show' dispatch
        def hier = classname.tokenize('.')
        return Introspector.decapitalize(hier[-1])
    }

    def show = {
        def mpfEventDataInstance = MpfEventData.get(params.id)
        if (!mpfEventDataInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfEventData.label', default: 'MpfEventData'), params.id])}"
            redirect(action: "list")
        }
        else {
            def sname = extractName(mpfEventDataInstance.class.name)
            log.debug "handling ${sname}"
            redirect(controller:sname, action:"show", params:[id:params.id])
        }
    }
/*
    def edit = {
        def mpfEventDataInstance = MpfEventData.get(params.id)
        if (!mpfEventDataInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfEventData.label', default: 'MpfEventData'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [mpfEventDataInstance: mpfEventDataInstance]
        }
    }

    def update = {
        def mpfEventDataInstance = MpfEventData.get(params.id)
        if (mpfEventDataInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (mpfEventDataInstance.version > version) {

                    mpfEventDataInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'mpfEventData.label', default: 'MpfEventData')] as Object[], "Another user has updated this MpfEventData while you were editing")
                    render(view: "edit", model: [mpfEventDataInstance: mpfEventDataInstance])
                    return
                }
            }
            mpfEventDataInstance.properties = params
            if (!mpfEventDataInstance.hasErrors() && mpfEventDataInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'mpfEventData.label', default: 'MpfEventData'), mpfEventDataInstance.id])}"
                redirect(action: "show", id: mpfEventDataInstance.id)
            }
            else {
                render(view: "edit", model: [mpfEventDataInstance: mpfEventDataInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfEventData.label', default: 'MpfEventData'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def mpfEventDataInstance = MpfEventData.get(params.id)
        if (mpfEventDataInstance) {
            try {
                mpfEventDataInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'mpfEventData.label', default: 'MpfEventData'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'mpfEventData.label', default: 'MpfEventData'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfEventData.label', default: 'MpfEventData'), params.id])}"
            redirect(action: "list")
        }
    }
    */
}
