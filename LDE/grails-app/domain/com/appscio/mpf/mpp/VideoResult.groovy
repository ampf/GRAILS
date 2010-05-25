/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.mpp

import com.appscio.mpf.grails.core.*

class VideoResult {

    Link video
    Link markup

    List segments
    List markupSegments

    SpeedupAnalysis analysis

    MpfTask task

    def size() {
        def size = 0
        size += video.size()
        // size += markup.size()
        // segments.each {size += it.size()}
        // markupSegments.each {size += it.size()}
        return size
    }

    static hasMany = [segments:Link, markupSegments:Link]

    static constraints = {
        task(); video(); markup(); segments(); markupSegments(); analysis()
    }
}
