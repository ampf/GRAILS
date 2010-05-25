/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core

import java.util.Date;

class MpfReport implements Comparable {
    String body="NOT INIT"      // event description string from detector
    Date   dateCreated
    
    // the event/report relationship is M:N
    // the MpfEvent is the parent (persistence-controlling) class
    
    static hasMany = [ detectedEvents:MpfEvent, children:MpfReport, shouldHandle:MpfTask]
    static belongsTo = [ MpfEvent, MpfTask ] // this is the child

    static constraints = {
        body (maxSize:100000)
    }

    int compareTo(Object them){
	    return this.dateCreated <=> then.dateCreated
    }
    
}
