/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core

class MpfCapability {

    String name
    // String type

    static constraints = {
        name (nullable:false)
        //type (nullable:false, inList:['plugin', 'hardware', 'software'])
    }
    
}
