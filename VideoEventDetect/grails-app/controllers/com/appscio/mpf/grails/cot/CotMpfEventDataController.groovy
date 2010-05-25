/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.cot

import com.appscio.mpf.grails.cot.CotMpfEventData;

class CotMpfEventDataController {
    def scaffold=true
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [cotMpfEventDataInstanceList: CotMpfEventData.list(params), cotMpfEventDataInstanceTotal: CotMpfEventData.count()]
    }

    def create = {
        def cotMpfEventDataInstance = new CotMpfEventData()
        cotMpfEventDataInstance.properties = params
        return [cotMpfEventDataInstance: cotMpfEventDataInstance]
    }

    def save = {
        def cotMpfEventDataInstance = new CotMpfEventData(params)
        if (cotMpfEventDataInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'cotMpfEventData.label', default: 'CotMpfEventData'), cotMpfEventDataInstance.id])}"
            redirect(action: "show", id: cotMpfEventDataInstance.id)
        }
        else {
            render(view: "create", model: [cotMpfEventDataInstance: cotMpfEventDataInstance])
        }
    }

    def show = {
        def cotMpfEventDataInstance = CotMpfEventData.get(params.id)
        if (!cotMpfEventDataInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'cotMpfEventData.label', default: 'CotMpfEventData'), params.id])}"
            redirect(action: "list")
        }
        else {
            [cotMpfEventDataInstance: cotMpfEventDataInstance]
        }
    }

    def edit = {
        def cotMpfEventDataInstance = CotMpfEventData.get(params.id)
        if (!cotMpfEventDataInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'cotMpfEventData.label', default: 'CotMpfEventData'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [cotMpfEventDataInstance: cotMpfEventDataInstance]
        }
    }

    def update = {
        def cotMpfEventDataInstance = CotMpfEventData.get(params.id)
        if (cotMpfEventDataInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (cotMpfEventDataInstance.version > version) {

                    cotMpfEventDataInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'cotMpfEventData.label', default: 'CotMpfEventData')] as Object[], "Another user has updated this CotMpfEventData while you were editing")
                    render(view: "edit", model: [cotMpfEventDataInstance: cotMpfEventDataInstance])
                    return
                }
            }
            cotMpfEventDataInstance.properties = params
            if (!cotMpfEventDataInstance.hasErrors() && cotMpfEventDataInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'cotMpfEventData.label', default: 'CotMpfEventData'), cotMpfEventDataInstance.id])}"
                redirect(action: "show", id: cotMpfEventDataInstance.id)
            }
            else {
                render(view: "edit", model: [cotMpfEventDataInstance: cotMpfEventDataInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'cotMpfEventData.label', default: 'CotMpfEventData'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def cotMpfEventDataInstance = CotMpfEventData.get(params.id)
        if (cotMpfEventDataInstance) {
            try {
                cotMpfEventDataInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'cotMpfEventData.label', default: 'CotMpfEventData'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'cotMpfEventData.label', default: 'CotMpfEventData'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'cotMpfEventData.label', default: 'CotMpfEventData'), params.id])}"
            redirect(action: "list")
        }
    }
}
