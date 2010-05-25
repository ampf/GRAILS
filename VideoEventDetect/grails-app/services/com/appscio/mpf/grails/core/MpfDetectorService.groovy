/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core

import org.springframework.context.*
import org.codehaus.groovy.grails.web.servlet.*
import grails.util.BuildSettings
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ApplicationAttributes
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.codehaus.groovy.grails.commons.GrailsResourceUtils as GRU

import groovy.xml.NamespaceBuilder // for AntUnit support


import javax.servlet.ServletContext

import com.appscio.mpf.utility.*

class MpfDetectorService implements ApplicationContextAware {
	ApplicationContext applicationContext
	def application=ApplicationHolder.application
	def config = ConfigurationHolder.config
	
	boolean transactional = true
	
	def backgroundService
	def antBuilderBean
	
	/* this is mainly a facade service, these are the implementing services */
	def mpfEventManagerService
	def mpfInitializationService
	def mpfTargetManagerService
	def mpfTaskManagerService
	
	def sessionFactory
	
	boolean clearEachTask = config?.com?.appscio?.mpf?.session?.clearTask
	boolean lockTask = config?.com?.appscio?.mpf?.session?.lockTask ?: false
	
	
	/* ************************************************************************
	 * EXTERNAL ACCESS METHODS
	 * ************************************************************************/
	
	/**
	 *  Primary entry point to put a detection task into the system. Task is
	 *  sanity-checked then set to SELECTABLE for later pickup by background
	 *  processing. Can be a new or existing MpfTask (will be save() ed)
	 *  @param mpt MpfTask or MpfTask id
	 *  @return boolean true if task found and set SELECTABLE, else false
	 *  */
	boolean submitTask( mpt ) {
		if(!mpt){
			log.warn "submitTask() got null task"
			return
		}
		MpfTask task = mpt instanceof MpfTask ? mpt : MpfTask.get(mpt)
		if (!task){
			log.warn "submitTask() couldn't make sense of parameter ${mpt}"
			return false
		}
		// make task avail
		task.setState(MpfTaskStatus.SELECTABLE, "queued at ${new Date()}")
		task.save(flush:true)
		if(task.hasErrors()){
			log.warn "submitTask() errors on saving ${task} "
			return false
		} else {
			return true
		}
	}
	
	static private boolean SHOULD_DISPATCH = true
	/**
	 * set flag to stop further dispatch
	 * @return
	 */
	def killDispatcher(){
		SHOULD_DISPATCH = false
	}
	
	private static boolean DISPATCHER_ACTIVE=false
	/**
	 * asynch run a background thread to send out jobs, checks every 5 seconds
	 * @return
	 */
	def synchronized dispatchJobs(){
		if (DISPATCHER_ACTIVE) return
			DISPATCHER_ACTIVE = true
		log.info "dispatchJobs() called"
		backgroundService.execute("job dispatcher",{
			while (MpfDetectorService.SHOULD_DISPATCH) {
				log.trace "dispatch job looping"
				
				try{
//					def job = findTaskETPair()
//					if(job) doAJob(job)
					def jobMap = findETTaskListMap()
					log.debug "dispatchJobs() gets jobMap of ${jobMap}"
					jobMap.each{etid, taskidlist ->
						backgroundService.execute("job dispatch to ${etid}", {
							taskidlist.each { taskid->
								doAJob([etid:etid, taskid:taskid])
							}
						})
					}
				} catch (Exception e){
					log.warn "dispatchJobs() Exception " + e.message
				}
				Thread.currentThread().sleep(1000) 
			}
			MpfDetectorService.DISPATCHER_ACTIVE = false
			log.info "job dispatcher background thread exiting"
		})
		log.info "dispatchJobs() exiting (background thread should still be running)"
	}
	
	def processReports(){
		def reports = MpfReport.findAllByNotIsProcessed()
		reports.each { rpt ->
			mpfEventManagerService.extractEvents( rpt.creatorTask, rpt ){
			}
		}
	}
	
	private mismatches(){
		def mismatchMap = [:]

		def tgst = MpfExecTgt.list()
		tgts.each{target ->
			def mismatchList = mpfTargetManagerService.fetchMismatchList(target)
			if (mismatchList.size() >0){
				mismatchMap[target.id] = mismatchList.flatten()
			}
		}
		mismatchMap
	}
	
	boolean CHECK_RUNNING = true
	def synchronized checkRunningJobs(){
		// create a background job to pull the CURRENT_STATUS file of each RUNNING
		// job, and if that status is not RUNNING then change the task status to
		// UNKNOWN, issue error, and decrement the MpfExecTgt currentTasks count
		backgroundService.execute("running-job checker",{
			if(CHECK_RUNNING){
				try{
			
				def mismatches = mismatches()
				mismatches.each {
					log.info "checkRunningJobs() finds mismatch on target ${it.key} for tasks ${it.value}"
				}
				}catch (Exception e){
					log.warn "checkRunningJobs() Exception " + e.message
				}
			}
			//Thread.currentThread().sleep(5000) 
		})       
	}
	
	/* kill detectors for completed/failed tasks, check timeouts etc */
	def sweepTasks(){
		
	}
	
	/* ************************************************************************
	 * FACADE METHODS
	 * ************************************************************************/

	def getDefaultExecTgt() {
		mpfTargetManagerService.getDefaultExecTgt()
	}
	
	/* update MpfExecTgt status - possible activate new task */
	def updateExecTgt(stateMsg){
		mpfTargetManagerService.updateExecTgt(stateMsg(stateMsg.ip, stateMsg.user, "UNKNOWN"))
	}
	
	/* list available detectors, for GUI (or empty list) */
	List<String> listDetectorNames() {
		mpfTaskManagerService.listDetectorNames()
	}
	
	/* kill detectors associated with this MpfTask */
	def killTask(MpfTask mpt){
		mpfTaskManagerService.killTask(mpt)
	}
	
	
	def updateTaskStatus(params){
		mpfTaskManagerService.updateTaskStatus(params)
	}
	
	def extractEventsFromReport(mptkey, String report) {
		mpfEventManagerService.extractEventsFromReport(mptkey, report)
	}
	
	def getEventIdsList (String sourcekey){
		mpfEventManagerService.getEventIdsList (sourcekey)
	}
	/* ************************************************************************
	 * INTERNAL METHODS
	 * ************************************************************************/
	
	
	/**
	 * [internal] looks for MpfTask/MpfExecTgt pair and dispatches it
	 * @return true iff and only if a job was dispatched
	 */
	boolean doAJob(job){
		//	    MpfTask.withSession {session ->
		log.info "doAJob() handling job ${job}"
		
		if (!job) { log.trace "doAJob() no job found" ; return
		}
		
		MpfTask task = MpfTask.get(job.taskid)
		if(!task){
			log.warn "doAJob gets null for task id ${job.taskid}"
			return
		}
		MpfExecTgt et = MpfExecTgt.get(job.etid)
		if(!et){
			log.warn "doAJob gets null for ET id ${job.etid}"
			return    		
		}
		task.execTgt = et
		task.status = MpfTaskStatus.PENDING
		task.save(/*flush:true*/)
		def taskid = task.id
		try{
			runTask(taskid)  // which will save/flush the task
		} catch (Exception e){
			task = MpfTask.get(taskid)
			task.status=MpfTaskStatus.REJECTED
			task.statusMsg=e.message
			task.save(flush:true)
			log.warn "doAJob thread throws exception running MpfTask ${task.id} ${e.message}"
		}
		// the runTask method delivers the job to the remote
		// this starts status updates flowing back, so no more
		// touching the task after it returns
	}
	/**
	 * Find a list of task/ET pairings.
	 * @return [ et:[task.id] ]
	 */
	def findETTaskListMap(){
		def srvTasklistMap = [:]
		                      
		MpfTask.withSession {session ->
			session.clear()
			                      
			def availET = fetchAvailableServers()
			if (!availET)
				return [:]

		    def dctgt = MpfExecTgt.read( mpfTargetManagerService.getDontCareExecTgt()?.id )
		    availET.each {et ->
			    def spareCapacity = et.spareCapacity
				srvTasklistMap[et.id] = []
				def taskids = []
				// first we get any targeted tasks
				taskids += mpfTaskManagerService.selectableTasksForTarget(et, spareCapacity).id
			    // then, if any space left, we fill with don't_care tasks 
				spareCapacity -= taskids.size()
			    taskids += mpfTaskManagerService.selectableTasksForTarget(dctgt, spareCapacity).id

			    if(taskids.size()>0){
					srvTasklistMap[et.id]=taskids
				}
			}
		}
		return srvTasklistMap
	}
	/**
	 * NOT USED - being replaced with findETTaskListMap
	 * [internal] Find a task/exectgt pair where the task is SELECTABLE and the server has spare capacity
	 * @return [taskid:task.id, etid:server.id]
	 */
	def findTaskETPair(){
		try{
			MpfTask.withSession {session ->
				session.clear()
				
				def selectableTasks = fetchSelectableTasks()
				def availServers = fetchAvailableServers()
				if (!selectableTasks || !availServers) return null
				
				def server = null
				def do_not_care_id = mpfTargetManagerService.DONT_CARE_TGT_ID
				def task = selectableTasks.find { pt ->
					if(pt.execTgt && pt.execTgt.id != do_not_care_id) {
						log.debug "findTaskETPair() task ${pt.id} is seeking server ${pt.execTgt.id}"
						// the task already has a mandatory target
						if(pt.execTgt.id in availServers.id){
							// found our pair
							log.info "findTaskETPair() task ${pt.id} gets server ${pt.execTgt.id}"
							server = pt.execTgt
							true
						} else {
							log.debug "findTaskETPair() server ${pt.execTgt.id} not in available servers list ${availServers.id}"
							return false // so we skip this task and try the next one
						}
					} else { // execTgt is null so we're free to seek one
						server = availServers.find {
							if(it.currentTasks < it.limitTasks){
								it.canHandleReqs(pt.requiredCapability)
							} else {
								log.warn "findTaskETPair() sanity check failure, have ET ${it.id} without spare capacity!"
								false
							}
						}
					}
				}
				if(!server || !task){
					//session.close()
					log.trace "findTaskETPair() finds nothing to dispatch server(${server})/task(${task})"
					return null
				}
				log.info "findTaskETPair() finds task:${task?.id} / ET: ${server?.id} [server is ${server?.currentTasks}/${server?.limitTasks} tasks]"
				return [taskid:task.id, etid:server.id]
			}
		} catch (groovy.lang.MissingMethodException mme) {
			log.warn "findTaskETPair() got MissingMethodException ${mme.message}"
		}
	}
	
	def fetchTasksAndServers(){
		[tasks:fetchSelectableTasks(), et:fetchAvailableServers()]
	}
	
	def fetchSelectableTasks(){
		def selectableTasks = MpfTask.findAllByStatus(MpfTaskStatus.SELECTABLE)
		if(!selectableTasks || selectableTasks?.size() == 0){
			log.trace "findTaskETPair() finds no SELECTABLE tasks"
		} else {
			log.debug "findTaskETPair() finds ${selectableTasks.size()} tasks"
		}
		selectableTasks
	}
	
	def fetchAvailableServers(){
		def availServers = MpfExecTgt.findAllOpenExecTgt()
		if (!availServers || availServers?.size==0){
			log.info "fetchAvailableServers() finds no available servers"
		} else {
			def availString = "fetchAvailableServers() finds avail servers "
			availServers.each {availString += "tgt${it.id}:[${it.currentTasks} current ${it.limitTasks-it.currentTasks} spare]\t"}
			log.debug availString    		    		
		}
		availServers
	}
	
	def runTask(taskid){
		MpfTask task = MpfTask.get(taskid)
		if(lockTask){
			task.lock()
		}
		
		if(!task){
			log.warn "runTask() gets null 'task' for id ${taskid}- ABORTING"
			return
		}
		
		MpfExecTgt target=task.execTgt ?: mpfTargetManagerService.getDefaultExecTgt()
		if(!target){
			log.warn "runTask() fails, null target"
			return
		}
		mpfTaskManagerService.preActivateTask(task) // set up the detector instance
		if(!task.detector){
			log.warn "runTask() fails, have target but null detector"
			return
		}
		task.execTgt = target
		boolean ready = prepExecTgt(task) // depends on detector being attached, to supply the ANT_COPY data
		if (!ready){
			log.warn "runTask() fails, exec target not ready"
			mpfTaskManagerService.killTask(task)
			return
		}
		
		log.info "runTask() running task '${task.name}' / detector '${task.mpfDetectorName}' / target '${target.tgtName}'"
		def runner = task.remoteRunnerScript
		if(!runner){
			log.warn "runTask() finds null remoteRunnerScript for ${task}, ABORTING"
			mpfTaskManagerService.killTask(task)
			return
		}
		
		String taskName = task.name
		mpfTaskManagerService.activateTask(task) // this sets to RUNNING and does save/flush
		
		//sessionFactory?.currentSession?.clear() // make sure it's in DB before the running task sends status
		
		def cmdStr = "cd ${target.execDir} ; cd ${task.remoteStagingDir}/VadScripts ; chmod +x ${runner} ; screen -S ${task.remoteStagingDir} -t ${task.remoteStagingDir} -dm ./${runner}"
		// always give ourselves a second for DB flushing 
		//Thread.currentThread().sleep(1000)
		boolean cmdSucceed = target.doCommand(cmdStr)
		if(!cmdSucceed){
			log.warn "runTask() got false return from ${target} doCommand(), killing ${task}"
			mpfTaskManagerService.killTask(taskid)
		}
		log.info "runTask() exiting normally, done launching task '${taskName}' [${taskid}]"
	}
	
	def File createStagingDir(prefix){
		def staging = FileHelpers.createTempDir(prefix)
		if(!staging){
			log.warning "createStagingDir() aborting due to createTempDir failure"
			return null
		}
		def stagingName = staging.absolutePath
		(new File(stagingName+"/jars")).mkdir()
		(new File(stagingName+"/lib")).mkdir()
		(new File(stagingName+"/plugins")).mkdir()
		(new File(stagingName+"/classes")).mkdir()
		(new File(stagingName+"/scripts")).mkdir()
		(new File(stagingName+"/VadScripts")).mkdir()
		
		log.info "createStagingDir() Created staging directory ${stagingName}"
		
		staging.deleteOnExit() // don't rely on this to handle disk footprint in non-stop server
		return staging
	}
	
	def fillSupportPerUserDef(det, sdir){
		def ant_copy_sel = det.properties.keySet().findAll { it.startsWith('ANT_COPY_TO_SUPPORT') }
		log.info "fillSupportPerUserDef() found ANT_COPY closures list: ${ant_copy_sel}"
		
		ant_copy_sel.sort().each {
			log.debug "fillSupportPerUserDef() processing ${it}"
			def sel = det[it] // NOTE 'sel' is a closure, don't try to eval it against this object
			// for example, don't try to log it with log.info "${sel}" !!
			
			try {
				antBuilderBean.copy(todir:sdir, sel)
			} catch (org.apache.tools.ant.BuildException e) {
				log.warn "fillSupportPerUserDef() nothing copied for ${it} because " + e.message
			}
		}
	}
	
	def copyOut(tgtDir, nameList, detName){
		boolean allOk = true
		
		nameList.each { fname ->
			log.trace "copyOut() fname -> ${fname}"
			File f = Places.firstInstanceOf(fname, detName, null) // let it look in system environment
			
			if(f){
				log.debug "copyOut() copying over ${f}"
				antBuilderBean.copy(toDir:tgtDir, file:f.absolutePath)
			} else {
				log.warn "copyOut() never found ${fname}, run 'mpf-copy-scripts-to'"
				//antBuilderBean.copy(toDir:tgtDir, file:"${vsDir}/${fname}", verbose:true)
				// log.info "GrailsApplication config = " + application.config
				allOk = false // come back to delete this line!
			}
		}
		return allOk
	}
	
	def fillStaging(mpt, staging){
		def sdName = staging.getAbsolutePath()
		
		// copy over the VAD scripts from somewhere in the environment
		boolean copied = copyOut("${sdName}/VadScripts/", ["RunMpfDetector.sh","MpfSupportFunctions.sh"], mpt.mpfDetectorName)
		if(!copied){
			log.warn "fillStaging() aborting because of copyOut() problem"
			return false
		}
		
		// copy in the defaults (from the detector's support directory)
		File support_dir = new File("grails-app/mpfDetectors/${mpt.mpfDetectorName}_runtime")
		
		if(support_dir.exists()){
			
			fillSupportPerUserDef(mpt.detector, support_dir) // let user contribute files
			// now merge the support dir into the staging dir
			
			// important 'filtering' off - it corrupts binary files
			antBuilderBean.copy(todir:sdName, filtering:false){
				fileset(dir:support_dir.getAbsolutePath())
			}
		} else {
			log.info "fillStaging() doesn't find any default dependencies to copy (looked for ${support_dir?.getAbsolutePath()})"
			return false
		}
		def stree = "tree ${sdName}".execute().text
		log.debug "fillStaging() staging tree is \n${stree}"
		// EPNRL-210 add in defaulted DETECTOR_PREFIX keys
		def dp = mpt.detector["DETECTOR_PREFIX_FOR_UI"] // test for the property
		if(dp){
			
		}
		FileHelpers.rewriteFile("${sdName}/VadScripts/RunMpfDetector.sh",
				[
				STATUS_ENDPOINT:mpt.statusRelUrl+"/${mpt.id}",
				EVENT_ENDPOINT:mpt.eventRelUrl+"/${mpt.id}",
				VIDEO_FILE:mpt.videoFileUrl,
				TIMEOUT: mpt.timeout,
				DETECTOR_SCRIPT:mpt.detector["DETECTOR_SCRIPT"],
				DETECTOR_PREFIX:mpt.detectPrefix,
				USE_GPU:mpt.useGPU,
				REPORTING_USER:mpt.execTgt.username,
				REPORTING_PW:mpt.execTgt.pw,
				JOBNAME:staging.toString().split('/')[-1],
				TASK_NAME:mpt.name,
				HOST_LOG:mpt.log_to_host,
				HOST_OUT:mpt.out_to_host,
				HOST_ERR:mpt.err_to_host,
				TIME_FORMAT:MpfTaskStats.FORMAT_STRING
				])
		
		// rename the runner script and track that new name in the domain object
		
		if(new File("${sdName}/VadScripts/RunMpfDetector.sh").exists()){
			def newName = "Run_${mpt.mpfDetectorName}.sh"
			antBuilderBean.move(file:  "${sdName}/VadScripts/RunMpfDetector.sh",
					tofile:"${sdName}/VadScripts/${newName}")
			mpt.remoteRunnerScript=newName
			log.debug "fillStaging() renamed the runner to ${newName}"
		} else {
			log.warn "fillStaging() can't find ${sdName}/VadScripts/RunMpfDetector.sh"
			return false
		}
		return true
	}
	
	private prepExecTgt(MpfTask mpt){
		// push support env on every job
		
		boolean isReady = true
		
		MpfExecTgt tgt = mpt.execTgt
		if(!tgt) {
			log.warn "prepExecTgt() MpfDetectorService prepExecTgt get null execTgt in task: ${mpt}"
			return false
		}
		log.debug "prepExecTgt() target '${tgt?.tgtName}' shows ${tgt.currentTasks} current, ${tgt.limitTasks} limit"
		
		def staging = createStagingDir("vad_${mpt.name.replaceAll(" ","-")}_")
		if (!staging?.isDirectory()){
			log.warn "prepExecTgt() problem creating staging dir ${staging}"
			return false
		}
		mpt.remoteStagingDir = staging.name
		
		boolean staged = fillStaging(mpt, staging) // copy dependencies as req'd
		if(!staged){
			log.warn "prepExecTgt() aborting due to fillStaging() problem"
			return false
		}
		
		boolean added = mpfTargetManagerService.setTaskRunningOnTgt(mpt, tgt)
		if(!added){
			log.warn "prepExecTgt() aborting due to setTaskRunningOnTgt() problem"
			return false
		}
		def execDir = tgt.execDir
		def logStrEd = "prepExecTgt() gets execDir of ${execDir}  "
		if(execDir.size() > 0){
			// ensure ends with single slash
			if (!(execDir.endsWith('/'))){
				logStrEd += "which doesn't end with a slash, so adding '/' to exec dir name"
				execDir +="/"
			} else {
				logStrEd += "which already has a slash ending, so no change needed"
			}
		} else {
			logStrEd += "which is size 0 so no alteration required"
		}
		log.info logStrEd + ", resulting in execDir: ${execDir}"
		
		antBuilderBean.sshexec(host:tgt.ip, username:tgt.username, password:tgt.pw,trust:true,
				command:"cd ${execDir} ; mkdir ${mpt.remoteStagingDir}", timeout:5000)
		
		log.info "prepExecTgt() has execution dir 'execDir' of [${execDir}]"
		def toStr = "${tgt.username}:${tgt.pw}@${tgt.ip}:${execDir}${mpt.remoteStagingDir}/"
		log.info "prepExecTgt() copying to [${toStr}]"       
		antBuilderBean.scp(todir:toStr, trust:true){
			fileset(dir:staging.getAbsolutePath())
		}
		
		// staging.deleteDir()
		// this is also where we will hook in per-task reply
		return isReady
	}
	
}
