/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core;

enum MpfTaskStatus {
    UNKNOWN,

    // prep states
    /**
     * incomplete definition
     */
    BUILDING,

    /**
     * task unrunnable, ill-defined, etc
     */
    REJECTED,

    // execution states
    /**
     * task built, ready to be selected for running
     */
    SELECTABLE,
    
    /**
     * task analyzed and prepped, ready to execute
     */
    PENDING,

    /**
     * has been dispatched on execution env
     */
    RUNNING,

    /**
     * task is assumed running remote; in this mode we still recv/process logging messages
     *  without bringing task in from DB or needing a runtime 'detector'
     */
    PASSIVE,

    // completion states
    /**
     * execution done, indeterminate outcome
     */
    COMPLETED,

    /**
     * execution concluded OK
     */
    SUCCEEDED,

    /**
     * some execution or timeout problem
     */
    FAILED;
    
    /**
     * 
     * @return true if the status is one of the completed (no firther action) states
     */
    boolean isComplete(){
    	(this == COMPLETED || this == SUCCEEDED || this == FAILED)
    }
    
    /**
     * 
     * @return a list of valid status
     */
    static list() {
    	[UNKNOWN, BUILDING, REJECTED, PENDING, RUNNING, PASSIVE, COMPLETED, SUCCEEDED, FAILED]
    }
    
}
