/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package tests.integration
import com.appscio.mpf.grails.core.MpfExecTgt;
import com.appscio.mpf.grails.core.MpfTask;

import grails.test.*

class ZZINT_InitializationTests extends GroovyTestCase {
    def mpfDetectorService
    def mpfTaskManagerService

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


    void testShouldHaveADefaultTarget() {
        def tgt = mpfDetectorService.getDefaultExecTgt()
        assertNotNull tgt
        assertTrue "got a non-MpfExecTgt return from getDefaultExecTgt", tgt instanceof MpfExecTgt
        assertTrue "MpfExecTgt should have valid id", tgt.id > 0
        // com.appscio.mpf.targets.default.name = 'mpf1 on localhost' or might be 'default'
        assertEquals "MpfExecTgt should expected tgtName", 'default', tgt.tgtName
    }

    void testShouldHaveExactlyOneDefaultTarget() {
        def tgts = MpfExecTgt.findAllByTgtName('default')
        assertEquals 1, tgts.size()
    }

    void testShouldHaveDefaultTask() {
        def task = mpfTaskManagerService.getDefaultTask()
        assertNotNull task
        assertTrue "got a non-MpfTask return from getDefaultTask", task instanceof MpfTask
        assertTrue "MpfTask should have valid id", task.id > 0
        assertEquals "MpfTask should should be running NotaskMpfDetector", task.mpfDetectorName, 'NotaskMpfDetector'
    }

    void testShouldHaveExactlyOneDefaultTask() {
        def tasks = MpfTask.findAllByMpfDetectorName('NotaskMpfDetector')
        assertEquals "expected 1 'NotaskMpfDetector' but found ${tasks.size()}",1, tasks.size()
    }


    /*
     * TEST DATA
     */
    def testEvent ='''<?xml version='1.0' standalone='yes'?>
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
    def testEventWithLeadingWhitespace ='''
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
