/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

import java.util.Date;

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


/**
 * Holds results of a GNU 'time' result string
 */
class MpfTaskStats {
    private static final Log log = LogFactory.getLog(MpfTaskStats.class)
    /*
     * Format string to be passed to the '/usr/bin/time' command; PRESENTLY IGNORED!
     */
    static FORMAT_STRING = '%E %e %S %U' // see man for /usr/bin/time

    /**
     * names of the fields to be filled in by the FORMAT_STRING (in order)
     */
    static FIELDS=['elapsed','elapsedSecs','userSecs','kernelSecs']

    Date   dateCreated // should be created as task is being dispatched
    Date   lastUpdated

    /**
     * report string as received
     */
    String received

    /**
     *  Elasped time on exec tgt in [H:]M:S
     */
    String elapsed = "" // in h:m:s, blank until data arrives

    /**
     * Elapsed time on exect tgt in seconds
     */
    float elapsedSecs = 0.0

    /**
     * User time on exec tgt in seconds
     */
    float userSecs = 0.0

    /**
     * Kernel time on exec tgt in seconds
     */
    float kernelSecs = 0.0

    /**
     * parse a string (assumed to be formatted as per the FORMAT_STRING) into a Map
     * @param str input string to parse, defaults to empty
     * @return map a Map which can be used as params to constructor call:
     */
    static Map makeNamedParams(String str=""){

        float time = 0.0
        def times=str.split()
        if(times.size() == 1){
            // we assume it's the elapsed time in seconds
            try{
                time = times[0] as float
            } catch (java.lang.NumberFormatException nfe){
                log.warn "makeNamedParams got unusable string [${str}]"
            }
        } else {
            log.warn "makeNamedParams got too many tokens, expected 1 but got ${times.size()} string [${str}]"
        }
        return [elapsedSecs:time]
        /* code for variable format string removed because can't seem to use 'time' on a function
         if (times.size() < FIELDS.size()){
         log.warn "makeNamedParams() got insufficient data, no stats collected - input [${str}]"
         return null
         }
         Map rtnMap = [:]
         // TODO make the fields here dynamic instead of fixed
         rtnMap.elapsed     = times[0]
         rtnMap.elapsedSecs = times[1] as float
         rtnMap.userSecs    = times[2] as float
         rtnMap.kernelSecs  = times[3] as float
         return rtnMap
         */
    }

    def updateElapsed(String statString){
    	def parms = makeNamedParams(statString)
    	elapsedSecs = parms.elapsedSecs
    	received = statString
    }
    
    String toString() {
        return "${elapsedSecs} seconds (rcd: ${received}) " //${userSecs} ${kernelSecs} (rcd elSec user krn)"
    }

    static constraints = {
        received (nullable:true)
    }
}
