/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.cot

import java.util.Date;
import com.appscio.mpf.grails.core.MpfEventData;

class CotMpfEventData extends MpfEventData {
    String cotUID    // CursorOnTarget message ID, e.g. 'object.1'
    String cotType   // CursorOnTarget classifier for the event, e.g. 'a-u-G-E-V'
    Date   cotStart  // '2009-11-12T16:47:09.00Z'
    Date   cotTime   // '2009-11-12T16:47:09.00Z'
    Date   cotStale  // '2009-11-12T16:49:09.00Z'
    String cotHow    // "m-g"
    float  cotPointLat // '31.539'
    float  cotPointLon // '-110.562'
    float  cotPointHae // '4270.28'
    float  cotPointCe  // '1000'
    float  cotPointLe  // '1000'

    MpfCotDetail cotDetail

    static constraints = {
        cotUID   (nullable:true)
        cotType  (nullable:true)
        cotStart (nullable:true)
        cotTime  (nullable:true)
        cotHow   (nullable:true)
        cotDetail(nullable:true)
    }
}
