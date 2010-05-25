/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core

import com.appscio.mpf.grails.core.MpfEvent;

class MpfEventController {

    def scaffold = true

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [mpfEventInstanceList: MpfEvent.list(params), mpfEventInstanceTotal: MpfEvent.count()]
    }

    def create = {
        def mpfEventInstance = new MpfEvent()
        mpfEventInstance.properties = params
        return [mpfEventInstance: mpfEventInstance]
    }

    def save = {
        def mpfEventInstance = new MpfEvent(params)
        if (mpfEventInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'mpfEvent.label', default: 'MpfEvent'), mpfEventInstance.id])}"
            redirect(action: "show", id: mpfEventInstance.id)
        }
        else {
            render(view: "create", model: [mpfEventInstance: mpfEventInstance])
        }
    }

    def show = {
        def mpfEventInstance = MpfEvent.get(params.id as int)
        if (!mpfEventInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfEvent.label', default: 'MpfEvent'), params.id])}"
            redirect(action: "list")
        }
        else {
            [mpfEventInstance: mpfEventInstance]
        }
    }

    def edit = {
        def mpfEventInstance = MpfEvent.get(params.id)
        if (!mpfEventInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfEvent.label', default: 'MpfEvent'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [mpfEventInstance: mpfEventInstance]
        }
    }

    def update = {
        def mpfEventInstance = MpfEvent.get(params.id)
        if (mpfEventInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (mpfEventInstance.version > version) {

                    mpfEventInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'mpfEvent.label', default: 'MpfEvent')] as Object[], "Another user has updated this MpfEvent while you were editing")
                    render(view: "edit", model: [mpfEventInstance: mpfEventInstance])
                    return
                }
            }
            mpfEventInstance.properties = params
            if (!mpfEventInstance.hasErrors() && mpfEventInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'mpfEvent.label', default: 'MpfEvent'), mpfEventInstance.id])}"
                redirect(action: "show", id: mpfEventInstance.id)
            }
            else {
                render(view: "edit", model: [mpfEventInstance: mpfEventInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfEvent.label', default: 'MpfEvent'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def mpfEventInstance = MpfEvent.get(params.id)
        if (mpfEventInstance) {
            try {
                mpfEventInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'mpfEvent.label', default: 'MpfEvent'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'mpfEvent.label', default: 'MpfEvent'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfEvent.label', default: 'MpfEvent'), params.id])}"
            redirect(action: "list")
        }
    }
}
