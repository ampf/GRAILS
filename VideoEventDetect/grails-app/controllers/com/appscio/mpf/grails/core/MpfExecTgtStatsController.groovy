/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

class MpfExecTgtStatsController {
	def scaffold=true

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        
        [mpfExecTgtStatsInstanceList: MpfExecTgtStats.list(params),
         mpfExecTgtStatsInstanceTotal: MpfExecTgtStats.count(),
         chartUrl:buildLoadChartUrl()
         ]
    }

    def create = {
        def mpfExecTgtStatsInstance = new MpfExecTgtStats()
        mpfExecTgtStatsInstance.properties = params
        return [mpfExecTgtStatsInstance: mpfExecTgtStatsInstance]
    }

    def save = {
        def mpfExecTgtStatsInstance = new MpfExecTgtStats(params)
        if (mpfExecTgtStatsInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats'), mpfExecTgtStatsInstance.id])}"
            redirect(action: "show", id: mpfExecTgtStatsInstance.id)
        }
        else {
            render(view: "create", model: [mpfExecTgtStatsInstance: mpfExecTgtStatsInstance])
        }
    }

    def show = {
        def mpfExecTgtStatsInstance = MpfExecTgtStats.get(params.id)
        if (!mpfExecTgtStatsInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats'), params.id])}"
            redirect(action: "list")
        }
        else {
            [mpfExecTgtStatsInstance: mpfExecTgtStatsInstance]
        }
    }

    def edit = {
        def mpfExecTgtStatsInstance = MpfExecTgtStats.get(params.id)
        if (!mpfExecTgtStatsInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [mpfExecTgtStatsInstance: mpfExecTgtStatsInstance]
        }
    }

    def update = {
        def mpfExecTgtStatsInstance = MpfExecTgtStats.get(params.id)
        if (mpfExecTgtStatsInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (mpfExecTgtStatsInstance.version > version) {
                    
                    mpfExecTgtStatsInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats')] as Object[], "Another user has updated this MpfExecTgtStats while you were editing")
                    render(view: "edit", model: [mpfExecTgtStatsInstance: mpfExecTgtStatsInstance])
                    return
                }
            }
            mpfExecTgtStatsInstance.properties = params
            if (!mpfExecTgtStatsInstance.hasErrors() && mpfExecTgtStatsInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats'), mpfExecTgtStatsInstance.id])}"
                redirect(action: "show", id: mpfExecTgtStatsInstance.id)
            }
            else {
                render(view: "edit", model: [mpfExecTgtStatsInstance: mpfExecTgtStatsInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def mpfExecTgtStatsInstance = MpfExecTgtStats.get(params.id)
        if (mpfExecTgtStatsInstance) {
            try {
                mpfExecTgtStatsInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats'), params.id])}"
            redirect(action: "list")
        }
    }
	
	def buildLoadChartUrl(){
/*
		def chart = new GoogleChartBuilder()
		def result = chart.lineChart{
		  size(w:300, h:200)
		  title{
		      row('Joy vs. Pain')
		  }
		  data(encoding:'extended'){
		      dataSet([1,18,200,87,1090,44,3999])
		      dataSet([88,900,77,1,2998,4])
		  }
		  colors{
		      color('66CC00')
		      color('3399ff')
		  }
		  lineStyle(line1:[1,6,3])
		  legend{
		      label('Joy')
		      label('Pain')
		  }
		  axis(left:(1..5).toList(), bottom:[])
		  backgrounds{
		      background{
		          solid(color:'999999')
		      }
		      area{
		          gradient(angle:45, start:'CCCCCC', end:'999999')
		      }
		  }
		   markers{
		      rangeMarker(type:'horizontal', color:'FF0000', start:0.75, end:0.25)
		      rangeMarker(type:'vertical', color:'0000cc', start:0.7, end:0.71)
		  }
		}
		result
		*/
	}
}
