/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

import com.appscio.mpf.grails.core.MpfTask;

import grails.test.*

class MpfTaskTests extends GrailsUnitTestCase {
    @Override
    public String getName() {
        return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
    }

    protected void setUp() {
        super.setUp()
        mockDomain(MpfTask, [])
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCanBeConstructedWithJustName() {
        def mpt = new MpfTask(name:"unitTester").save()
        assertNotNull mpt
        assertNotNull mpt.id
    }

    void testCanSetState(){
        mockLogging(MpfTask)
        def mpt = new MpfTask(name:"unitTester").save()
        ['unknown','building','pending','rejected','running','succeeded','completed','failed'].each {
            def msg = it.toUpperCase() + " unit test message"
            mpt.setState(it, msg)
            assertEquals "state did not set",mpt.status, MpfTaskStatus[it.toUpperCase()]
        }
        mpt.setState('pending',"nothing to say")
        def msg = "SILLY" + " unit test message"
        try {
        	mpt.setState("SILLY", msg) // should be rejected
        } catch (IllegalArgumentException iae) {}
        assertEquals "state should not set for silly value",mpt.status, MpfTaskStatus.PENDING
    }

    void testNullStateInputShouldNotChangeSavedState(){
        mockLogging(MpfTask)
        def mpt = new MpfTask(name:"unitTester").save()
        mpt.setState(MpfTaskStatus.PENDING,"initialize the state")
        assertEquals "set initial state to ", MpfTaskStatus.PENDING, mpt.status

        try{
        	mpt.setState(null,"try with no state")
        } catch (IllegalArgumentException iae) {}
        assertEquals "null state should be ignored ", MpfTaskStatus.PENDING, mpt.status
        try{
        	mpt.setState("","try with empty string state")
        } catch (IllegalArgumentException iae) {}
        assertEquals "empty string state should be ignored ", MpfTaskStatus.PENDING, mpt.status
    }

    void testStatsMsgRecordsTimes(){
        mockLogging(MpfTask)
        MpfTask mpt = new MpfTask(name:"unitTester").save()
        mockDomain(MpfTaskStats,[])

        mpt.setState('elapsedSecs', '987654')
        assertNotNull "MpfTask instance should have created an MpfTaskStats",mpt.execTgtCompletionTimes
        assertTrue "value not close enough", ((987654 - mpt.execTgtCompletionTimes?.elapsedSecs).abs() < 0.001)
    }
}
