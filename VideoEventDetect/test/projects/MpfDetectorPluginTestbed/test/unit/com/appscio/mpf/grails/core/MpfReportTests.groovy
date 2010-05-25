/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

import grails.test.*

class MpfReportTests extends GrailsUnitTestCase {
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

    void tesConstraints() {
        def rpt = new MpfReport()
        mockForConstraintsTests(MpfReport, [rpt])

        assertTrue rpt.validate()
        def str = "x" * 100000 + "x"
        rpt.body = str
        assertFalse rpt.validate()
    }
    void testBodyParamShouldBeStored() {
        mockDomain(MpfReport)
        def rpt=new MpfReport().save()
        assertNotNull "report body should be initialized",rpt.body
        def body = "this is some text"
        def rpt_with_body=new MpfReport(body:body).save()
        assertEquals "param ''body'' to report should be set ",body,rpt_with_body.body
        rpt.body=body
        rpt.save()
        assertEquals "param ''body'' to report should change ",body,rpt.body
    }
}
