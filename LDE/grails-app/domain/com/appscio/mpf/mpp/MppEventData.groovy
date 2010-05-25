/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.mpp

import com.appscio.mpf.grails.core.*
import com.appscio.mpf.rdf.Utils

class MppEventData extends MpfEventData {

    static belongsTo = [rawData:MpfEventData]
    int numMppSegments = 0
    int segmentFrames = 0
    int totalFrames = 0
    int maxMppObjects = 0
    int totalMppObjects = 0

    boolean parseN3(def n3) {
        def map = Utils.parseN3(new BufferedReader(new StringReader(n3)))
        def subjects = map.subjects
        def segments = subjects.segmenter
        if (segments) {
            // println "segments=" + segments?.size;
            segments?.each {
                // println it
                numMppSegments++
                def best_interest = it.segmenter.best_interest.toInteger()
                def start_frame = it.segmenter.start_frame.toInteger();
                def end_frame = it.segmenter.end_frame.toInteger();
                totalFrames = (it.segmenter.total_frames?: totalFrames).toInteger();
                segmentFrames += end_frame - start_frame;

                if (best_interest > maxMppObjects)
                    maxMppObjects = best_interest
                totalMppObjects += best_interest
            }
            return true
        }
        return false
    }
    // This shouldn't be necessary.
    String toString() {
        return this.class.name + ": " + id
    }
    static constraints = {
        rawData();
    }
}
