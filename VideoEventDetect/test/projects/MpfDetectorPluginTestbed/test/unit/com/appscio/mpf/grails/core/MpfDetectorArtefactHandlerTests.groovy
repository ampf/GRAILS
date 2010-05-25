/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core
import grails.test.*
import groovy.util.GroovyTestCase;
import org.codehaus.groovy.grails.commons.GrailsClass;
import com.appscio.mpf.grails.core.MpfDetectorArtefactHandler

class MpfDetectorArtefactHandlerTests extends GroovyTestCase {
    @Override
    public String getName() {
        return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
    }

    void testIsArtefact() {
        def handler = new MpfDetectorArtefactHandler()
        assertTrue handler.isArtefactClass(TestMpfDetector)
        assertFalse handler.isArtefactClass(MpfDetectorArtefactHandlerTests)
        GrailsClass mpfDetectorClass = handler.newArtefactClass(TestMpfDetector)
        assertEquals "TestMpfDetector", mpfDetectorClass.shortName
        assertEquals "com.appscio.mpf.grails.core.TestMpfDetector", mpfDetectorClass.name
        assertEquals "com.appscio.mpf.grails.core.TestMpfDetector", mpfDetectorClass.fullName
        assertEquals "testMpfDetector",mpfDetectorClass.propertyName
        assertEquals "testMpfDetector",mpfDetectorClass.logicalPropertyName
        assertEquals "com.appscio.mpf.grails.core", mpfDetectorClass.packageName
    }
}

class TestMpfDetector {

}

