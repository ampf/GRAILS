/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core
/**
 * Base class for data persisted by detectors; each detector uses a subclass of this class to hold detector/event-specific data.
 *
 * @author wstidolph
 * @see SimpleMpfEventData
 */
class MpfEventData {

    /**
     * Identify the owning MpfEvent
     */
    static belongsTo = [mpfEvent:MpfEvent]

    /**
     * The owning MpfEvent is allowed to be null for test purposes; should never happen in deployment
     */
    static constraints = {
        mpfEvent(nullable:true) // just so I can do a standalone unit test!
    }

    // we expect to move to table-per-subclass
    /**
     * set tablePerHierarchy 'false' because the class tree is wide, not deep
     * and so that as we evolve the subclasses we don't have to rewrite a big hierarchy table
     */
  static mapping = {
    tablePerHierarchy false
  }

}
