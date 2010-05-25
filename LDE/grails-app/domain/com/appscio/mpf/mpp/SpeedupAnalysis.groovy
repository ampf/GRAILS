/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.mpp

import com.appscio.mpf.grails.core.*;

class SpeedupAnalysis {
    String video = ""
    String params = ""
    Map parameters = new HashMap()
    int detectMd = 0
    int detectMdUsc = 0
    int detectUsc = 0
    int numMppSegments = 0
    int totalMppObjects = 0
    float percentMotion = 0
    int numUscTracks = 0
    int numMppUscTracks = 0
    float uscVersusMdUsc = 0
    float uscVersusMd = 0

    int totalFrames = 0

    static constraints = {
        video(); params(); parameters(); numMppSegments(); totalMppObjects(); numUscTracks(); numMppUscTracks(); percentMotion(); detectMd(); detectMdUsc(); detectUsc(); uscVersusMdUsc(); uscVersusMd()
    }
}
