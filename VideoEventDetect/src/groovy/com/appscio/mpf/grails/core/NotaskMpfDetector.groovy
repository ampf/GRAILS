/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core;

/**
 * This Detector is invoked for "no-task" reports, it just copies
 * back the report body
 */
class NotaskMpfDetector extends MpfDetector {
    def parseReport(String report){
        def mpfEvent = new SimpleMpfEventData(body:report.toString())
        return [mpfEvent]
    }
    def DETECTOR_SCRIPT="catchall"
    def TIMEOUT_SECONDS=0 // no timeout at all
}
