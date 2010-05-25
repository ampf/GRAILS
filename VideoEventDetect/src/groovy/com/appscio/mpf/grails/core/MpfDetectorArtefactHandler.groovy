/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core
import com.appscio.mpf.grails.plugins.mpfdetector.DefaultGrailsMpfDetectorClass;
import com.appscio.mpf.grails.plugins.mpfdetector.GrailsMpfDetectorClass;
import org.codehaus.groovy.grails.commons.DefaultGrailsClass


import org.springframework.util.ReflectionUtils;
import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;

class MpfDetectorArtefactHandler extends ArtefactHandlerAdapter {
    public static final String TYPE = "MpfDetector";

    public MpfDetectorArtefactHandler() {
        /*
         * The 4 params are:
         • The artefact type
         • The interface to use for the artefact type: extends GrailsClass interface
         • The implementation of the interface for the artefact type:
           Grails provides a default implementation in the DefaultGrailsClass,
           we subclass this to provide custom logic within the artefact type.
         • The suffix that the class name should end with for a java.lang.Class
           to be considered of the artefact type
         */
        //super(TYPE, GrailsMpfDetectorClass.class, DefaultGrailsMpfDetectorClass.class, TYPE);
        super(TYPE, GrailsMpfDetectorClass.class, DefaultGrailsClass.class, TYPE);
    }
/*  use default isArtefact test (ends with TYPE, not a closure)
    public boolean isArtefactClass(Class clazz) {
        System.out.println "CHECKING Class " + clazz.getName()
        if (clazz == null || !clazz.getName().endsWith(TYPE)) return false;
    }
*/
}
