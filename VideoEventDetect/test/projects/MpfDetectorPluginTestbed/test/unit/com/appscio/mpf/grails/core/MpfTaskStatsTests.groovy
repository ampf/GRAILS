/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

import grails.test.*

class MpfTaskStatsTests extends GrailsUnitTestCase {
    public String getName() {
        return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
    }
    protected void setUp() {
        super.setUp()
        mockDomain(MpfTaskStats, [])
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testTimeStringMakesCorrectMap() {
        String times = "3600"
        def map = MpfTaskStats.makeNamedParams (times)
        assertTrue map instanceof Map
        assertEquals 3600.0, map.elapsedSecs
        /* this test was for '/usr/bin/time' output, which is now deferred
        String times = "1:00.0 3600.0 2.00 1.23"
        def map = MpfTaskStats.makeNamedParams (times)
        assertTrue map instanceof Map
        assertEquals "1:00.0", map.elapsed
        assertEquals "3600.0", map.elapsedSeconds
        assertEquals "2.00",   map.user
        assertEquals "1.23",   map.kernel

        def tsinstance = new MpfTaskStats(map).save()
        assertNotNull "new/save failed",tsinstance
        assertEquals tsinstance.elapsed,"1:00.0"
        */
    }

    void testNoExceptionIfBadString() {
        // test exception thrown if non-number of embedded spaces

        def map = MpfTaskStats.makeNamedParams ("3600 extra")
        assertTrue map instanceof Map
        assertEquals 0, map.elapsedSecs

        MpfTaskStats.makeNamedParams ("3600notANumber")
        assertTrue map instanceof Map
        assertEquals 0, map.elapsedSecs
    }

    void testrendersCorrectToString(){
        String times = "3600"
        def tsinstance = new MpfTaskStats(MpfTaskStats.makeNamedParams (times)).save()
        assertNotNull tsinstance
        tsinstance.received=times
        def rtnStr = tsinstance.toString()
        def tokens = rtnStr?.split()
        assertEquals "3600.0", tokens[0]

        /* this test was for '/usr/bin/time' output, which is now deferred
        String times = "1:00.0 3600.0 2.00 1.23"
        def tsinstance = new MpfTaskStats(MpfTaskStats.makeNamedParams (times)).save()
        def rtnStr = tsinstance.toString() // 1:00.0 3600.0 2.0 1.23 (el elSec user krn)

        def tokens = rtnStr?.split()
        assertEquals "1:00.0", tokens[0]
        assertEquals "3600.0", tokens[1]
        assertEquals "2.0",    tokens[2]
        assertEquals "1.23",   tokens[3]
        */
    }
}
