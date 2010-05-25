/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.task

import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import com.appscio.mpf.grails.core.*;
import com.appscio.mpf.grails.target.*

class MpfTaskManagerService implements ApplicationContextAware {
    ApplicationContext applicationContext
    def application=ApplicationHolder.application
    def config = ConfigurationHolder.config
    def sessionFactory
    
    def mpfTargetManagerService
    def backgroundService
    boolean transactional = true
    boolean lockTask = config?.com?.appscio?.mpf?.session?.lockTask ?: false
    
    /* detector management functions
     *
     */
    def detectors = [] // active detectors
    static String DEF_TASK_NAME = "NO_TASK"
    def DEFAULT_DETECTOR
    
    def getDetectorForTaskId( mtid, boolean useDefault = false){
        log.debug "getDetectorForTaskIdI() task id ${mtid}, will look in current detectors list (showing detector task id fields) ${detectors.mpfTaskId}"
        if(detectors.size()==0){
            log.warn "getDetectorForTaskId() LOOK =======> DETECTORS LIST IS EMPTY!! after startup, may indicate session error"
        }
        def det = detectors.find {detector ->
            detector.mpfTaskId == mtid
        }
        log.debug "getDetectorsForTaskId() finds detector ${det} tied to task ${det?.mpfTaskId}"
        if (!det){
        	// if it's other than the NotaskMpfDetector, instantiate a detector (but don't hold it)
        	// WORKING HERE
        }
        if(!det && useDefault){
            
            det = makeOrGetDefaultTask().detector
            log.debug "getDetectorForTaskId() fetching default task and returning its detector [${det}] which has mpfTaskId of ${det?.mpfTaskId}"
        }
        if(!det){
            log.warn "getDetectorForTaskId() returning a null detector for task id ${mtid}!!"
        }
        return det
    }
    
    def removeDetForTaskId(mptid){
        log.trace "removeDetForTaskId() task id ${mptid} detectors list ${detectors.mpfTaskId}"
        def tbr = detectors.findAll {det -> det.mpfTaskId == mptid}
        log.trace "removeDetForTaskId() tbr list is ${tbr.mpfTaskId}"
        detectors = detectors - tbr
        log.trace "removeDetForTaskId() task id ${mptid} detectors list ${detectors.mpfTaskId}"    	
    }
    
    def getTaskId ( mptkey=null ) {
        def mpfTaskInstance

        if(mptkey){
            mpfTaskInstance = MpfTask.read( mptkey as int) ?: null
            if(!(mpfTaskInstance instanceof MpfTask)){
                // here we got a key, but it was not found
                log.warn "getTaskId() MpfTask not found with id ${mptkey}"
		return null
            }
        } else {
            log.info "getTaskId() no key supplied so returning default MpfTask"
            mpfTaskInstance = makeOrGetDefaultTask()

            if(!(mpfTaskInstance instanceof MpfTask)){
                log.warn "getTask() failed to make default MpfTask because \n ${mpfTaskInstance}"
                return null
            }
        }
        
        return mpfTaskInstance.id
    }
    
    private cleanupDetector(MpfTask mpt){ // drop any detector(s) for this task
        detectors.remove(mpt.id)
    }
    /* task management
     *
     */
    
    /* list available detectors, for GUI (or empty list) */
    List<String> listDetectorNames() {
        application.getMpfDetectorClasses().collect {it.name } ?: []
    }
    
    // TODO unsynch the following
    def synchronized updateTaskStatus(params){
        log.trace "updateTaskStatus(${params})"
        def state = params.state
        if (!state){
            def errStr= 'null state to updateTaskStatus(), not updating'
            log.warn errStr
            return errStr
        }
        
        def mpfTaskInstance = MpfTask.get( params.id )
        if(!mpfTaskInstance) {
            log.warn "updateTaskStatus() status message dropped - MpfTask not found with id ${params.id}"
            return "notfound MpfTask ${params.id}"
        }
        
        def msg = params.msg
        def result
        if(state=="HEARTBEAT"){
            result= updateHeartbeat(mpfTaskInstance, msg)
        } else {
            result= updateState(mpfTaskInstance, state, msg)
        }
        try{
            if(!mpfTaskInstance.hasErrors() && mpfTaskInstance.save(/*flush:true*/)) {
                return result
            } else {
                def err = mpfTaskInstance.errors.allErrors
                def errStr = "updateState() failed, returning '${result}' with params of ${params} yielding these errors:"
                err.each { errStr += "\n\t" + it }
                log.warn "updateTaskStatus() err saving ${mpfTaskInstance}, is ${errStr}"
                // eject the task instance from the session ?
                
                return errStr
            }
        }catch(org.hibernate.StaleObjectStateException sose){
            log.warn "updateTaskStatus() cause StaleObjectStateException updating task '${mpfTaskInstance.name}' [${mpfTaskInstance.id}], status may be dropped"
            return result
        }
    }
    
    /**
     * 
     * @param mpt_selector an MpfTask instance, an MpfTask key, or the key 'ALL'
     * @return map of detector name to list of prefix descriptor strings
     */
    def make_DETECTOR_PREFIX_FOR_UI_strings(mpt_selector){
    	def mptNames = []
    	switch (mpt_selector){
	    	case "ALL": mptNames = listDetectorNames(); break;
	    	case MpfTask : mptName = [mpt.selector.mpfDetectorName]; break;
	    	default: def t = MpfTask.read(mpt_selector); if(t) mpt_names = [t.mpfDetectorName]
    	}
    	// build det name: DETECTOR_PREFIX_FOR_UI map
    	def namePropMap = [:]
    	mptNames.each {name ->
    		def prop = application.getMpfDetectorClass(name)?.newInstance().DETECTOR_PREFIX_FOR_UI
    		namePropMap[name]=prop
    	}
    	log.trace "built namePropMap of " + namePropMap

        namePropMap.each { detname,propFromDet -> 
            log.info "process propFromDet => ${propFromDet}"
            def sa = []
            propFromDet.each{ prefix ->// property entries can be a string or a map (from which we build a string)
            	if(prefix instanceof String){
            		sa << prefix
            	} else{
            		sa << prefix.key + " :\t" + prefix.help
            	}
            }
            namePropMap[detname]=sa
        }
    	log.debug "rewrote namePropMap to " + namePropMap
    	return namePropMap
    }
    
    
    def updateHeartbeat(mpfTaskInstance, msg){
        msg.eachLine() {line, idx ->
            if (idx == 0){
                mpfTaskInstance.heartbeatHdr=line
                mpfTaskInstance.heartbeatMsg=""
            } else {
                mpfTaskInstance.heartbeatMsg += (line + "\n")
            }
        }
        log.debug "updateHeartbeat() ${mpfTaskInstance.name} [${mpfTaskInstance.id}]"
        log.trace "updateHeartbeat() \n${mpfTaskInstance.heartbeatHdr} \n${mpfTaskInstance.heartbeatMsg}  "
        if(!mpfTaskInstance.status?.equals(MpfTaskStatus.RUNNING) ){
            log.warn "updateHeartbeat() from non-RUNNING task ${mpfTaskInstance.name} [${mpfTaskInstance.id}] task status is ${mpfTaskInstance.status}; target is ${mpfTaskInstance.execTgt}"
        }
        return "updated"
    }
    
    def updateState(mpfTaskInstance, state, msg){
	// if we 'return' here then no SOSE in the running task startup
        if(!(mpfTaskInstance instanceof MpfTask)){
            return "updateState() got invalid mpfTaskInstance param of ${mpfTaskInstance}"
        }
        try {
            boolean wasComplete = mpfTaskInstance.hasCompleted()
            mpfTaskInstance.setState(state, msg)
           // mpfTaskInstance.save()
            if(!wasComplete && mpfTaskInstance.hasCompleted()){
                processCompletion(mpfTaskInstance)
            }
            return "updated"
        } catch (IllegalArgumentException iae){
            return "updatedState() failed, did not understand state '${state}'"
        }
    }
    
    /* kill all detectors, update associated MpfTasks */
    def killAllTask(){
        // TODO
    }
    
    private def killRunningTask(MpfTask mpt){
        // ensure not the default task
        if (mpt?.id == DEFAULT_TASK_ID){
            log.info "ignoring request to kill DEFAULT TASK"
            return
        }
        
        def cmdStr = "screen -X -S ${mpt.remoteStagingDir} kill"
        cmdStr = "screen -X -S ${mpt.remoteStagingDir} quit"
        boolean success = mpfTargetManagerService.doCommandOnTarget(cmdStr, mpt.execTgt)
        if(success){
            log.info "killRunningTask() executed screen kill for task '${mpt?.name}'  on target '${mpt?.execTgt?.tgtName}' [${mpt?.execTgt?.id}]"    	
            mpt.execTgt.removeTask(mpt)
        } else {
            log.warn "killRunningTask() unable to execute screen kill on ${mpt?.execTgt}"
            return
        }
        
        // it may be that the killed task sends back more reports that need parsing
        // so let's waaaaiiitttt a bit
        int taskid = mpt.id
        backgroundService.execute("killRunningTask detector killer",{
            Thread.currentThread().sleep(5000)
            log.info "there are ${detectors.size()} detectors in memory before doing removal"
            def toBeRemoved = detectors.findAll {it.mpfTaskId == taskid}
            log.info "toBeRemoved list is size "+toBeRemoved.size()+" task ids ${toBeRemoved.mpfTaskId}"
            detectors = detectors - toBeRemoved
            log.info "there are ${detectors.size()} in memory after doing removal, with task ids ${detectors.mpfTaskId}"
        })
        log.info "killRunningTask() has launched background thread to delete detectors associated with task ${taskid} in 5 seconds"
    }
    
    /**
     * Kill a task; if running, take off the target and delete detectors.
     * Task may be in a preactivation phase, needs to be killed just due
     * to a target non-readiness.
     * Set task status to REJECTED
     * @param mpt task or id of task to kill
     * @return
     */
    def killTask(mpt, msg="unknown reason for killing"){
        try {
            def task = mpt
            if(!(mpt instanceof MpfTask)){
                task = MpfTask.get(mpt)
            }
            if(lockTask){
                task.lock()
            }
            if(task.status==MpfTaskStatus.RUNNING){
                killRunningTask(task)
            }
            task.setState(MpfTaskStatus.REJECTED,msg)
            task.save()
        } catch (Exception e){
            log.warn "killTask() caught excption " + e.message
        }
    }
    
    /* finds and attaches detector; returns true/false for success */
    static String NO_TASK_DET_CLASS="NotaskMpfDetector"
    def preActivateTask(MpfTask mpt){
        if(!mpt){
            log.warn "preActivateTask() aborting, null mpt param"
            return false
            
        }
        def names = mpt.mpfDetectorName.replace (",", " ").tokenize()
        if (names.size == 0){
            log.warn "preActivateTask() no detectors found in ${mpt} with mpfDetectorName of ${mpt.mpfDetectorName}"
            mpt.detector=null
            return false
        }
        def name = names[0] // just handle one detector right now
        if(!name){
            log.warn "preActivateTask() param ${mpt} with mpfDetectorName = ${mpt?.mpfDetectorName} produces null 'name' so aborting"
            return false
        }
        log.info "preActivateTask() building ${name}"
        
        if(NO_TASK_DET_CLASS.equals(name)) {
            // the "NO_TASK" detector isn't loaded dynamically, we want to ensure it is always avail
            mpt.detector = new NotaskMpfDetector(mpfTaskId:mpt.id)
        } else {
            def clazz = application.getMpfDetectorClass(name)
            mpt.detector=clazz?.newInstance()
        }
        
        if(mpt.detector){
            mpt.detector.mpfTaskId=mpt.id
            detectors << mpt.detector // hold the POGO not the domain object
            log.info "preActivateTask() detectors array now ${detectors.size()} entries, task ids are ${detectors.mpfTaskId}"
            return true
        } else {
            log.warn "preActivateTask() failed newInstance on ${name}"
            mpt.setState(MpfTaskStatus.REJECTED,"NULL detector")
            return false
        }
    }
    
    def activateTask(task){
        if(!task){
            log.warn "activateTask() aborting becuase null 'task' param"
            return
        }
        task.detector.launchDate = new Date()
        def msg = "${task.detector?.DETECTOR_SCRIPT} ==> ${task.execTgt?.tgtName}"
        task.setState(MpfTaskStatus.RUNNING,msg)
        task.save(flush:true) // give back to DB so async updates can happen
    }
    
    def passivateTask(task){
        // 
    }
    
    def processCompletion(MpfTask mpt){
        if(!mpt){
            log.warn "processCompletion() gets null parm"
            return
        }
        log.info "processCompletion() task '${mpt.name}' [${mpt.id}] on target '${mpt.execTgt.tgtName}' [${mpt.execTgt.id}]"
        mpfTargetManagerService.taskCompletedOnET(mpt.execTgt.id)
        // bring back the logs
        
        def mptid = mpt.id
        removeDetForTaskId(mptid)
        // TODO evict the task from the session
        // sessionFactory?.currentSession?.evict(mpt)
    }
    
    /* DEFAULT TASK MANAGEMENT */
    /**
     * This returns the 'NO_TASK' task which catches events that have no task identifier when posted to the MpfTaskController.
     * The task may be a single task for all executions of the MpfDetector plugin, or can be a
     * separate NO_TASK instance for each execution (controlled by the setting @com.appscio.mpf.task.default.new_on_restart={true/false}@)
     */
    def makeOrGetDefaultTask(){
        log.debug "makeOrGetDefaultTask() invoked"
        def defTask = getDefaultTask()
        if (defTask){
            log.debug "makeOrGetDefaultTask() found existing default task"
        } else {
            // if the DB has a default task, instantiate that
            log.info "makeOrGetDefaultTask() seeking task with name: ${DEF_TASK_NAME}"
            defTask = MpfTask.findByName(DEF_TASK_NAME)
            if(defTask){
                restoreTaskFromDB(defTask)
                log.info "makeOrGetDefaultTask() found existing default task in DB, ${defTask}"
            } else {
                // OK, need to create a default task in the DB
                defTask = makeDefaultTask()
                log.info "makeOrGetDefaultTask() adding default task to DB, ${defTask}"
            }
        }
        return defTask
    }
    
    int DEFAULT_TASK_ID=0
    def getDefaultTask(){ // change to a test to find a RUNNING default task in the DB
        // return task, or null
        def det = detectors.find {it instanceof NotaskMpfDetector }
        det ? MpfTask.get(det.mpfTaskId) : null
    }
    
    def shutDownDefaultTask() {
        def mpt=getDefaultTask()
        if(mpt){
            mpt.setState(MpfTaskStatus.SUCCEEDED,"normal shutdown")
            mpt.save(flush:true)
        }
    }
    def restoreTaskFromDB(MpfTask mpt){
        if(preActivateTask(mpt))
            activateTask(mpt)
    }
    
    def synchronized makeDefaultTask(){
        def mpfTaskInstance = getDefaultTask() // check again, *inside* the synchronized block
        if (mpfTaskInstance){
            return mpfTaskInstance
        }
        mpfTaskInstance =
                new MpfTask([
                name:DEF_TASK_NAME,
                mpfDetectorName:"NotaskMpfDetector",
                status:MpfTaskStatus.RUNNING,
                useGPU:'if_avail'
                ])
        if(!mpfTaskInstance.hasErrors() && mpfTaskInstance.save()) {
            log.info "makeDefaultTask() has good mpfTaskInstance"
            def det = new NotaskMpfDetector(mpfTaskId:mpfTaskInstance.id)
            detectors << det // hold the POGO not the domain object
            mpfTaskInstance.detector=det
            mpfTaskInstance.save()
            DEFAULT_TASK_ID=mpfTaskInstance.id
            
            log.info "makeDefaultTask() returning mpfTaskInstance with detector: ${mpfTaskInstance.detector}"
            return mpfTaskInstance
        } else {
            def errStr =  "makeDefaultTask() failed because:"
            def err = mpfTaskInstance.errors.allErrors
            err.each { errStr += "\n\t" + it }
            log.warn errStr
            return errStr
        }
    }
    
    /**
     * Find a task for a set of MpfCapabilities; 
     * @param caps available capabilities of the MpfExecTgt to be tasked
     * @return ID of a SELECTABLE MpfTask
     */
    def nextTaskForCapabilities(caps){
        
    }
    
    // canned queries
    int runningTasksForTarget(tgt){
        // def result = MpfTask.findAllByStatusAndExecTgt(MpfTaskStatus.RUNNING, tgt).size()
        MpfTask.createCriteria().count() {
        	eq ('status', MpfTaskStatus.RUNNING)
        	eq ('execTgt', tgt)
        }
    }
    /**
     * 
     * @param tgt which mpfExecTgt
     * @param limit max number of tasks to return (defaults to 1)
     * @return [ MpfTask ]
     */
    def selectableTasksForTarget(tgt, limit=1){
    	if(!tgt){
    		log.warn "selectableTasksForTarget() got null tgt param"
    		return []
    	}
        MpfTask.withCriteria() {
        	eq ('status', MpfTaskStatus.SELECTABLE)
        	eq ('execTgt', tgt)
        	maxResults(limit)
        }    	
    }
    
    int runningTasksForAllTargets(){
    	MpfTask.createCriteria().count() {
        	eq ('status', MpfTaskStatus.RUNNING)    		
    	}
    }
}
