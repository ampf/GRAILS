/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.mpp

import com.appscio.mpf.grails.core.*
import com.appscio.mpf.rdf.Utils

class UscEventData extends MpfEventData {

    static belongsTo = [rawData:MpfEventData]
    int numUscTracks = 0

    boolean parseN3(def n3) {
        def map = Utils.parseN3(new BufferedReader(new StringReader(n3)))
        def subjects = map.subjects
        def uscevents = subjects.event
        if (uscevents) {
            numUscTracks += uscevents.event.tracks[0].toInteger()
            return true
        }
        return false
    }
    // This shouldn't be necessary?
    String toString() {
        return this.class.name + ": " + id
    }
    static constraints = {
        rawData();
    }
}
