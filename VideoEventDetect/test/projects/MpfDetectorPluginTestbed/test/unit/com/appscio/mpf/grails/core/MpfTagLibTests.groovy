/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

import com.appscio.mpf.grails.core.MpfEvent;
import com.appscio.mpf.grails.core.MpfTask;
import com.appscio.mpf.grails.core.SimpleMpfEventData;

import grails.test.*

class MpfTagLibTests extends TagLibUnitTestCase {
    @Override
    public String getName() {
        return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
    }
    protected void setUp() {
        super.setUp()
        mockDomain(MpfTask, [])
        mockDomain(SimpleMpfEventData, [])
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testEventTHNullInstance() {
        tagLib.eventTH(instance:null, fields:"id,data")
        assertEquals "<th><g:message code='MpfEvent.id.label' default='id'/></th><th><g:message code='MpfEvent.data.label' default='data'/></th>",
                tagLib.out.toString()
    }

    void testEventTH() {
        def mpt = new MpfTask(name:"unitTester").save()
        def instance = new SimpleMpfEventData(body:"UNIT_TEST").save()
        def event = new MpfEvent(data:instance)
        tagLib.eventTH(instance:instance, fields:"id,body")
        assertEquals "<th><g:message code='com.appscio.mpf.grails.core.SimpleMpfEventData.id.label' default='id'/></th><th><g:message code='com.appscio.mpf.grails.core.SimpleMpfEventData.body.label' default='body'/></th>",
                      tagLib.out.toString()
    }

    void testEventTD() {
        def mpt = new MpfTask(name:"unitTester").save()
        def instance = new SimpleMpfEventData(body:"UNIT_TEST").save()
        def event = new MpfEvent(data:instance)
        tagLib.eventTD(instance:instance, fields:"id,body")
        assertEquals "<td>1</td><td>UNIT_TEST</td>", tagLib.out.toString()
    }

    void testEventTDNullInstance() {
        tagLib.eventTD(instance:null, fields:"id,body")
        assertEquals "<td></td><td></td>", tagLib.out.toString()
    }
}
