/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.cot

class MpfCotDetail {

    String report   // the text

    static belongsTo = [ parent: CotMpfEventData ]
    static constraints = {
    }
}
