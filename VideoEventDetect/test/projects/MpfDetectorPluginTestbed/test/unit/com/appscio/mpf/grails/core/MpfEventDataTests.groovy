/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

import com.appscio.mpf.grails.core.MpfEventData;

import grails.test.*

class MpfEventDataTests extends GrailsUnitTestCase {
    @Override
    public String getName() {
        return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
    }
    protected void setUp() {
        super.setUp()
        mockDomain(MpfEventData, [])
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testId() {
        def mpd = new MpfEventData()
        assertNotNull mpd
        mpd.save()
        assertNotNull mpd.id
    }
}
