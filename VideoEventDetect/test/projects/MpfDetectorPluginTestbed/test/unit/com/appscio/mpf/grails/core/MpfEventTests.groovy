/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

import com.appscio.mpf.grails.event.*
import com.appscio.mpf.grails.task.*
import com.appscio.mpf.grails.cot.*
import grails.test.*

class MpfEventTests extends GrailsUnitTestCase {
    @Override
    public String getName() {
        return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
    }
    protected void setUp() {
        super.setUp()
        mockDomain(MpfEvent, [])

        mockDomain(MpfEventData, [])
        mockDomain(MpfEventStreamData, [])
        mockDomain(MpfTask, [])
        mockDomain(MpfReport, [])

    }

    protected void tearDown() {
        super.tearDown()
    }

    void testId() {
        def mpd = new MpfEventData().save()
        assertNotNull mpd.id

        def mpt = new MpfTask(name:"unitTester").save()

        def mpe = new MpfEvent(data:mpd, mpfTask:mpt)
        assertNotNull mpe

        mpe.save()
        assertNotNull mpe.id

    }
    
    /* TEST THE EVENT MANAGER SERVICE */
    void testExtractEventsHandlesBadInput() {
        mockLogging(MpfEventManagerService)
        mockDomain(MpfReport, [])

        // need a Task with a Detector
        def det = new BadParserDetector()

        def mpt = new MpfTask(name:'test', mpfDetectorName:'TestMpfDetector', detector:det, id:2)
        mockDomain (MpfTask,[
            mpt
        ])


        def testService = new MpfEventManagerService()
        //testService.detectors = [det]
        def taskSvc=mockFor(MpfTaskManagerService)
        3.times { // the first call with empty report shouldn't touch task manager
            taskSvc.demand.getTaskId(1..1){mt -> mpt.id}
            taskSvc.demand.getDetectorForTaskId(1..1){mt -> det}
        }
        taskSvc.demand.getTask(1..1){mptkey -> mptkey == 1? mpt : null}
        testService.mpfTaskManagerService=taskSvc.createMock()

        def respCode
        def respMsg
        def evtList

        EventExtractionResponse eer =  testService.extractEventsFromReport (mpt.id, null) // handle blanks
        assertEquals EMPTYLIST, eer.evtList
        assertEquals 400, eer.code // 400 for bad requests
                
        eer = testService.extractEventsFromReport (mpt.id, "") // handle blanks
        assertEquals EMPTYLIST, eer.evtList
        assertEquals 400, eer.code

        eer = testService.extractEventsFromReport (mpt.id, "<event>") // handle ill-formed, tho no events
        assertEquals EMPTYLIST, eer.evtList
        assertEquals 200, eer.code

        eer = testService.extractEventsFromReport (mpt.id, SINGLE_EVENT_XML)
        assertEquals 0,eer.evtList.size() // this detector doesn't extract anything!
        assertEquals 200, eer.code

        eer = testService.extractEventsFromReport (0, SINGLE_EVENT_XML) // no such task - ever
        assertEquals 0,eer.evtList.size() // this detector doesn't extract anything!
        assertEquals 200, eer.code // should be OK to hand to default task
    }
    
    void testExtractEventsHandlesOneSimpleEvent(){
        mockLogging(MpfEventManagerService)

        // need a Task with a Detector
        def det = new CotSingleDetector()

        def mpt = new MpfTask(name:'test', mpfDetectorName:'TestMpfDetector', detector:det, id:2)
        mockDomain(MpfReport, [])
        mockDomain(CotMpfEventData, [])
        mockDomain (MpfTask,[mpt])
        mockDomain (MpfEvent,[ // service needs to create the Events

        ])
        def testService = new MpfEventManagerService()
        //testService.detectors = [det]
        //def mpfReport = new MpfReport(body:SINGLE_EVENT_XML)
        def taskSvc=mockFor(MpfTaskManagerService)

        taskSvc.demand.getTaskId(1..1){mt -> mpt.id}
        taskSvc.demand.getDetectorForTaskId(1..1){mt,useDefault -> det}
        testService.mpfTaskManagerService=taskSvc.createMock()

        EventExtractionResponse eer = testService.extractEventsFromReport (mpt.id, SINGLE_EVENT_XML )
        assertEquals 200, eer.code
        assertEquals "controller reports '${eer.msg}'",1,eer.evtList.size()
    }

    void testExtractEventsHandlesOneCotEvent(){
        mockLogging(MpfEventManagerService)

        // need a Task with a Detector
        def det = new CotSingleDetector()

        def mpt = new MpfTask(name:'test', mpfDetectorName:'TestMpfDetector', detector:det, id:2)
        mockDomain(CotMpfEventData, [])
        mockDomain (MpfTask,[mpt])
        mockDomain (MpfEvent,[ // service needs to create the Events

                ])
        mockDomain(MpfReport, [])
        def testService = new MpfEventManagerService()
        def taskSvc=mockFor(MpfTaskManagerService)
        taskSvc.demand.getTaskId(1..1){ mt -> mpt.id}
        taskSvc.demand.getDetectorForTaskId(1..1){mt, useDefault -> det}
        testService.mpfTaskManagerService=taskSvc.createMock()

        EventExtractionResponse eer = testService.extractEventsFromReport (mpt.id, SINGLE_EVENT_XML )
        assertEquals 200, eer.code
        assertEquals "controller reports '${eer.msg}'",1,eer.evtList.size()
    }

    /*
     * TEST DATA
     */
    def EMPTYLIST = []

    def SINGLE_EVENT_XML = '''

    <event version="2.0"
           uid="/tmp/testvid/predator-1.mpg"
           time="1123867578027300"
           start="1123867578027300"
           stale="1123867578087300"
           how="m-g">
          <point lat='34' lon='-117' hae='5762' ce='{appscio.cot.ce}' le='{appscio.cot.le}' />
    </event>'''

    def XML_PI = '''<?xml version='1.0' standalone='yes'?>'''


}

/* TEST CLASSES */
class BadParserDetector extends MpfDetector {
    def parseReport () {null.unknownProperty } // closure to throw exception
}

class CotSingleDetector extends MpfDetector {
    def parseReport(report) {
        def dataList = CotEventParser.parseSingleEvent(report)
        dataList.collect {new CotMpfEventData(it)}
    }
}
