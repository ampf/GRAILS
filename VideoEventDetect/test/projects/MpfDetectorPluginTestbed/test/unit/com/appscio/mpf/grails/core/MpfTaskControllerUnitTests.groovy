/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

import com.appscio.mpf.grails.core.MpfEvent;
import com.appscio.mpf.grails.core.MpfReport;
import com.appscio.mpf.grails.core.MpfTask;
import com.appscio.mpf.grails.core.SimpleMpfEventData;
import com.appscio.mpf.grails.core.MpfDetectorService
import com.appscio.mpf.grails.task.MpfTaskManagerService

import grails.test.ControllerUnitTestCase;
import groovy.lang.MetaClass;


class MpfTaskControllerUnitTests extends ControllerUnitTestCase {
	@Override
	public String getName() {
		return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
	}
	
	void testStatusForMissingTaskShouldInvokeServiceAndReturnHttp_404(){
		//def videoEventDetectService
		mockLogging(MpfTaskController)
		def mtms = mockFor(MpfTaskManagerService)
		
		this.controller.params.id=1
		this.controller.params.state='running'
		this.controller.params.msg='unit test status'
		mtms.demand.updateTaskStatus(1..1) {params -> 'notfound MpfTask 1'}
		this.controller.mpfTaskManagerService = mtms.createMock()
		
		this.controller.status()
		
		mtms.verify()
		assertEquals  "should be not-found HTTP status of",404, controller.response.status
		
	}
	void testLogErrAndOutForMissingTaskShouldReturnNotFound() {
		mockLogging(MpfTaskController)
		mockDomain(MpfTask,[])
		
		this.controller.params.id=13
		this.controller.params.msg='unit test status'
		
		['log','out','err'].each {
			this.controller.params.state=it
			this.controller.status()
			assertEquals  "should be not-found HTTP status of",404, controller.response.status //"could not find MpfTask 13",response
		}
		
	}
	void testAcceptsTextEvent(){
		mockLogging(MpfTaskController)
		def mpt = mockFor(MpfTask)
		mpt.demand.addToReports(1..1){ rpt -> }
		
		mockDomain(MpfReport,[])
		mockDomain(MpfEvent,[new MpfEvent(data:new SimpleMpfEventData(body:"UNIT_TEST_EVENT"))])
		mockDomain(MpfTask,[
				new MpfTask(
				id:1,
				name:"NO_TASK",
				mpfDetectorName:"NotaskMpfDetector",
				status:MpfTaskStatus.RUNNING,
				useGPU:'if_avail')
				])
		
		
		def veds = mockFor(MpfDetectorService)
		veds.demand.extractEventsFromReport(1..1){ mt, rptText ->
			new EventExtractionResponse(code:200,msg:"processed",evtList:[MpfEvent.get(1)])
		}
		
		this.controller.mpfDetectorService = veds.createMock()
		//this.controller.params.id=
		this.controller.params.event="UNIT_TEST_EVENT"
		
		this.controller.event()
		
		veds.verify()
		String rsp = controller.response.getContentAsString()
		System.out.println 'response is \''+rsp+'\''
		
		def result=new XmlSlurper().parseText(rsp.trim())
		
		assertEquals "1",result.id.text().trim()
	}
	
	void testAcceptsXmlEvent(){
		mockLogging(MpfTaskController)
		def mpt = mockFor(MpfTask)
		mpt.demand.addToReports(1..1){ rpt -> }
		
		mockDomain(MpfReport,[])
		mockDomain(MpfEvent,[new MpfEvent(data:new SimpleMpfEventData(body:"UNIT_TEST_EVENT"))])
		mockDomain(MpfTask,[
				new MpfTask(
				id:1,
				name:"NO_TASK",
				mpfDetectorName:"NotaskMpfDetector",
				status:MpfTaskStatus.RUNNING,
				useGPU:'if_avail')
				])
		
		def veds = mockFor(MpfDetectorService)
		veds.demand.extractEventsFromReport(1..1){ mt, rptText ->
			new EventExtractionResponse(code:200,msg:"processed",evtList:[MpfEvent.get(1)])
		}
		
		this.controller.mpfDetectorService = veds.createMock()
		
		def taskSvc=mockFor(MpfTaskManagerService)
		taskSvc.demand.makeOrGetDefaultTask(1..1) {-> MpfTask.get(1)}
		this.controller.mpfTaskManagerService=taskSvc.createMock()
		
		this.controller.request.content=testEvent.getBytes()
		this.controller.event()
		
		veds.verify()
		
		def result=new XmlSlurper().parseText(controller.response.getContentAsString().trim())
		
		assertEquals "1",result.id.text().trim()
	}
	
	void testShowForIdZeroShouldReturnLatestCreated(){
		mockLogging(MpfTaskController)		
		def t1 =	new MpfTask(
			id:1,
			dateCreated: new Date(),
			name:"NO_TASK",
			mpfDetectorName:"NotaskMpfDetector",
			status:MpfTaskStatus.RUNNING,
			useGPU:'if_avail')
		sleep(100)
		def t2 = new MpfTask(
				id:2,
				dateCreated:new Date(),
				name:"Second Task",
				mpfDetectorName:"NotaskMpfDetector",
				status:MpfTaskStatus.SELECTABLE
				)
		mockDomain(MpfTask, [t2, t1])
		this.controller.params.id=1
		def instanceMap = controller.show()
		println "instanceMap is  " + instanceMap
		// assertEquals "show", renderArgs.view
		//assertEquals "show",redirectArgs["action"]
		assertEquals 1, instanceMap.mpfTaskInstance.id
		
		this.controller.params.id="0"
		instanceMap = controller.show()
		assertEquals 2, instanceMap.mpfTaskInstance.id		
		
	}
	
	/*
	 * TEST DATA
	 */
	def testEvent ='''
        <?xml version='1.0' standalone='yes'?>
        <!-- bits-map-point-vad-event is abbreviated as b-m-p-v-e -->
        <event version='2.0'
        uid='../../../data/predator/predator-1.mpg | ./test-detect-events-cot-mock'
        type='b-m-p-v-e' how='m-g'
        time='1261169554841' start='1123867578027' stale='1123867638027'>
        <point lat='34' lon='-117' hae='1756' ce='9999999' le='9999999' />
            <detail>
                <appscio>
                    <vad activity="false" tracks="0">
                    </vad>
                </appscio>
            </detail>
        </event>
        '''
}
