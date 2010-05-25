/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package tests.integration
import com.appscio.mpf.grails.core.MpfTaskController;

import grails.test.*

class ZZINT_EventEndpointApiTests extends GroovyTestCase {
    def mpfDetectorService

    @Override
    public String getName() {
        return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
      }


    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    boolean makeSingleEventResponseAsserts(controller){

        // should get back an XML doc
        assertEquals "HTTP status ",200,controller.response.status
        def str=controller.response.contentAsString
        def xml = new XmlSlurper().parseText(str)

        //should get back one event, so no wrapper element (mpfEvent at top)
        assertEquals 1, xml.size()

        // this test suite only creates 3 events total, but we shouln't assume that the count is the identifier ...
        // assertTrue (1..3).contains(xml.mpfEvent.'@id'.text() as int)
        /* expected return:
            <?xml version="1.0" encoding="UTF-8"?>
            <list>
                <mpfEvent id="1">
                    <contributors>
                        <mpfReport id="1" /></contributors>
                    <data id="1" />
                    <mpfTask id="1" />
                    <streamData />
                </mpfEvent>
            </list>
        */

    }

    void testEventParameterPostsToDefaultTask() {
        def mpfTaskController = new MpfTaskController()
        mpfTaskController.mpfDetectorService = mpfDetectorService

        mpfTaskController.params.event=sampleEvent

        mpfTaskController.event()

        makeSingleEventResponseAsserts(mpfTaskController)
    }

    void testXmlEventContentPostsToDefaultTask() {
        def mpfTaskController = new MpfTaskController()
        mpfTaskController.mpfDetectorService = mpfDetectorService

        mpfTaskController.request.contentType = 'text/xml'
        mpfTaskController.request.content=sampleEvent.getBytes()

        def rtn = mpfTaskController.event()

        makeSingleEventResponseAsserts(mpfTaskController)
    }

    void testXmlEventWithLeadingWhitespacePostsToDefaultTask() {
        def mpfTaskController = new MpfTaskController()
        mpfTaskController.mpfDetectorService = mpfDetectorService

        mpfTaskController.request.contentType = 'text/xml'
        mpfTaskController.request.content=sampleEventWithLeadingWhitespace.getBytes()

        def rtn = mpfTaskController.event()

        makeSingleEventResponseAsserts(mpfTaskController)
    }

    void testEventPostsToNewTask() {
       if (GroovyTestCase.notYetImplemented(this)) return;
        fail
    }

    void testStatus() {
       if (GroovyTestCase.notYetImplemented(this)) return;
        // send a status for non-existing
        fail
    }

    void testHeartbeat() {
       if (GroovyTestCase.notYetImplemented(this)) return;
        fail
    }

    /*
     * TEST DATA
     */
    def sampleEvent ='''<?xml version='1.0' standalone='yes'?>
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
    def sample2Events='''<?xml version='1.0' standalone='yes'?>
    <!-- bits-map-point-vad-event is abbreviated as b-m-p-v-e -->
    <events>
        <event version='2.0'
        uid='FAKE_UUID:1'
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
        <event version='2.0'
            uid='FAKE_UUID:2'
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
     <events>
    '''
    def sampleEventWithLeadingWhitespace ='''
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
