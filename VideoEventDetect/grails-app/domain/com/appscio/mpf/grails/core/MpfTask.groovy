/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core

import java.util.Date;
import com.appscio.mpf.utility.LogLevels
import com.appscio.mpf.grails.core.MpfDetector
import com.appscio.mpf.grails.core.MpfTaskStatus
// TODO refactor to subpport subtask model
// so one MpfTask represents a collection of subtasks (per Detector, for instance)
/**
 * MpfTask is the unwieldy control class for an execution of a Detector.
 * @author wstidolph
 *
 */
class MpfTask {

    MpfTaskStatus status = MpfTaskStatus.BUILDING
    Date   dateCreated
    Date   lastUpdated

    String name                   // human_readable task name e.g., "test-1"
    String mpfDetectorName = 'pwd'// e.g., 'FooGst' for 'FooGstDetector'
    String sourcekey = "avail for app use"// opaque to plugin; for the using app
    String statusMsg
    String heartbeatHdr        // most-recent heartbeat msg header
    String heartbeatMsg        // most-recent heartbeat msg body

    MpfExecTgt execTgt
    MpfDetector detector
    boolean foregroundThread = true
    MpfTaskStats execTgtCompletionTimes
    long   totalRunTimeMs=0 // duration in RUNNING state
    String executionServer = "localhost"
    String remoteStagingDir = "will be calculated"// dir on exec tgt where we put jars, resources etc per task
    String remoteRunnerScript

    // these are passed to the remote instance
    String videoFileUrl        // URL to video file
    String useGPU = "if_avail"
    String timeout="1m 10s"
    String eventRelUrl  = ""   // e.g., VadTest/mpfTask/event
    String statusRelUrl = ""

    String detectPrefix = ""   // string to prepend to the 'detect' invocation
    String log_to_host = "info"    // send remote JOB.LOG messages to host?
    String out_to_host = "info"   // copy remote 'stdout' to host?
    String err_to_host = "error"   // copy remote 'stderr' to host?

    static hasMany = [reports:MpfReport, // might get back report/event, and/or summary
                      detectedEvents:MpfEvent, // keep link here instead of going via report
                                               // to avoid loading report text
                      requiredCapability:MpfCapability]

    static transients = ["detector", "heartbeatHdr", "heartbeatMsg"] // only meaningful during runtime

    static constraints = {
      name          (blank:false)
      mpfDetectorName (nullable:false)
      videoFileUrl  (nullable:true) //might be just an input stream
      timeout       (nullable:true) // null or "starts with -" mean no timeout // TODO regex validate
      status        (nullable:false)
      statusMsg     (nullable:true)
      execTgtCompletionTimes (nullable:true)
      execTgt       (nullable:true) // might be unassigned when created
      sourcekey     (nullable:true)
      useGPU        (nullable:true, inList:['if_avail','required', 'do_not_use'])
      log_to_host   (inList:LogLevels.levelnames)
      out_to_host   (inList:LogLevels.levelnames)
      err_to_host   (inList:LogLevels.levelnames)
      eventRelUrl   ()
      statusRelUrl  ()
      remoteStagingDir (nullable:true)
      remoteRunnerScript(nullable:true)
      dateCreated   ()
      lastUpdated    ()
    }

    /**
     * @param state String value of target state
     * @param msg String to be retained if the state change succeeds
     * @return the modified MpfTask
     * @throws IllegalArgumentException if state not valid MpfTaskStatus valueOf
     * @see MpfTaskStatus
     */
    def setState(String state, String msg){
        boolean updated = true
        log.trace "${this} setState gets ${state}, ${msg}"
        // TODO make this more tolerant with regex matches
        if(!state){
            log.warn "null state supplied to " + this
            throw new IllegalArgumentException("null state supplied to " + this)
        }
        String ucstate = state.toUpperCase()
        if(ucstate.equals("ELAPSEDSECS")){ // hack!
        	recordCompletionTimes (msg)
        	return this
        }
        this.setState(ucstate as MpfTaskStatus, msg)
    }

    /**
    *
    * @param state MpfTaskStatus
    * @param msg String to be retained if the state change succeeds
    * @return the modified MpfTask
    * @throws IllegalArgumentException if state not valid MpfTaskStatus valueOf
    * @see MpfTaskStatus
    */
    def setState(MpfTaskStatus state, String msg){
        log.trace "${this} setState gets ${state}, ${msg}"
        if(!state){
            log.warn "null state supplied to " + this
            throw new IllegalArgumentException("null state supplied as MpfTaskStatus to " + this)
        }
        status = state;
        statusMsg = msg
        return this
    }
    
    /**
     * 
     * @return true if the task is in a completion state
     */
    boolean hasCompleted(){
    	this.status?.isComplete()
    }
    
    /**
     * Creates/saves or updates an MpfTaskStatus object from the statisticsString
     * @param statisticsString String number of elapsed seconds
     * @return the MpfTask object
     */
    def recordCompletionTimes(String statisticsString){
    	MpfTaskStats stats = execTgtCompletionTimes

    	if (!stats){
	        stats = new MpfTaskStats(MpfTaskStats.makeNamedParams(statisticsString))
    	} else {
    		stats.updateElapsed statisticsString
    	}
        stats.save()
        execTgtCompletionTimes = stats       
        return this
    }
    /**
     * Finds next 'SELECTABLE' MpfTask (in dateCreated-order, oldest first) already
     * queued for a given MpfExecTgt
     * @param et MpfExecTgt 
     * @return MpfTask instance (or null if none)
     */
    static getNextTaskForExecTgt(MpfExecTgt et){
    	if(!et){ return null}
    	def candidate
    	def candidates = MpfTask.withCriteria{
    			eq ('execTgt', et)
    			eq ('status',MpfTaskStatus.SELECTABLE)
    			maxResults(1)
    			firstResult(0)
    			order('dateCreated', 'asc')
    	}
        if(!candidates.isEmpty()){
        	candidate = candidates.toArray()[0]
        }
    	return candidate	
    }
}
