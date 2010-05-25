/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.target

import org.codehaus.groovy.grails.commons.ConfigurationHolder

import com.appscio.mpf.grails.core.*;

class MpfTargetManagerService  {

    def config = ConfigurationHolder.config
    boolean transactional = true
    
    def antBuilderBean
    def backgroundService
    def defaultSshExecTimeoutMS = config?.com?.appscio?.mpf?.targets?.sshexec?.timeoutms ?: 10000
    		
    /* ensures there is a 'default' target */
    int DEFAULT_TGT_ID=0
    def ensureDefaultTgt(){
        if (DEFAULT_TGT_ID>0) return true;

        def defname = config?.com?.appscio?.mpf?.targets?.default?.name ?: 'default'
        def defuser = config?.com?.appscio?.mpf?.targets?.default?.username ?: "whoami".execute().text
        def defpw = config?.com?.appscio?.mpf.targets?.default?.password ?: "whoami".execute().text.reverse()
        def defip = config?.com.appscio?.mpf?.targets?.default?.ip ?: 'localhost'

        def deftgt=MpfExecTgt.findByTgtName(defname)
        if(!deftgt){
            log.debug "ensureDefaultTgt() didn't find target named 'default' so will look for ip/username"
            deftgt = MpfExecTgt.findByIpAndUsername(defip, defuser)
            log.debug "ensureDefaultTgt() search for ${defip} / ${defuser} found ${deftgt}"
        }
        if(!deftgt){
            deftgt = MpfExecTgt.findByIpAndUsername('127.0.0.1', defuser)
            log.debug "ensureDefaultTgt() search for '127.0.0.1' / ${defuser} found ${deftgt}"
        }
        if(deftgt){
            DEFAULT_TGT_ID=deftgt.id
            log.info "ensureDefaultTgt() found  ${deftgt}, so leaving DEFAULT_TGT_ID as ${DEFAULT_TGT_ID}"
        } else {
            // still haven't found, so create default target
            MpfExecTgt defaultTgt = new MpfExecTgt(tgtName:defname,username:defuser,pw:defpw, ip:defip).save()
            DEFAULT_TGT_ID=defaultTgt.id
            log.info "ensureDefaultTgt() created default exec target ${defaultTgt}"
        }
    }

    def getDefaultExecTgt(){
        MpfExecTgt.get(DEFAULT_TGT_ID)
    }
    
    int DONT_CARE_TGT_ID=0
    def ensureDontCareTgt(){
        if (DONT_CARE_TGT_ID>0) return true;
        def dcname="D0 N0T CARE"
        def deftgt=MpfExecTgt.findByTgtName(dcname)
        if (!deftgt){
        	deftgt = new MpfExecTgt(tgtName:dcname, limitTasks:0, username:'nobody', pw:'-').save(flush:true)
        	log.info "adding dont-care execTgt ${deftgt} named ${deftgt.tgtName}"
        }
    	DONT_CARE_TGT_ID=deftgt.id        
    }
    
    def getDontCareExecTgt(){
        MpfExecTgt.get(DONT_CARE_TGT_ID)    	
    }
    
    def updateExecTgt(ip,user,status){
        MpfExecTgt met = MpfExecTgt.findByIpAndUser(ip, user)
        // This method will handle heartbeat and 'idle' messages, so that
        // if a met reports in idle we can quickly dispatch a new task

    }
    
    boolean activateTaskOnTarget(MpfExecTgt et){
    	def numTasksNow = et.currentTasks
    	if (numTasksNow < et.limitTasks){
    		et.currentTasks = numTasksNow + 1
    		et.save()
    		return true
    	} else {
    		log.warn "activateTaskOnTarget() determines no room on ${et} has ${et.currentTasks} already"
    		return false
    	}
    }
    
    boolean setTaskRunningOnTgt(MpfTask task, MpfExecTgt et){
    	if(!et ||  !task){
    		log.warn "setTaskRunningOnTgt() got a null parm, aborting run; task ${task} target ${et}"
    		return false
    	}
    	if (et.currentTasks < et.limitTasks){
    		et.currentTasks = et.currentTasks + 1
    		et.save()    	
    	} else {
    		log.warn "setTaskRunningOnTgt() asked to add task ${task.id} to full target ${et} current: ${et.currentTasks} limit: ${et.limitTasks}"
    		return false // no room on the target
    	}
    	
    	task.status=MpfTaskStatus.RUNNING
    	task.execTgt=et
    	def saved = task.save(flush:true)
    	if (!saved){
    		log.warn "setTaskRunningOnTgt failed to save task"
    		return false
    	}
    	return true
    }
    
    /**
     * Tracks task completion for dispatching next task when spare capacity
     * @param etid ID of the exec target on which a task completed
     * @return
     */
    def taskCompletedOnET(etid){
    	MpfExecTgt et = MpfExecTgt.get(etid)
    	if(!et){
    		log.warn "taskCompletedOnET() got bad etid param for which target to update (got ${etid})"
    		return
    	}
    	int numTasksNow = et.currentTasks
    	if(numTasksNow <1){
    		log.warn "taskCompletedOnET() validation problem, numTasksNow on ${et.tgtName} [${etid}] is ${numTasksNow}"
    		return
    	}

    	et.currentTasks = numTasksNow - 1

    	// TODO maybe - if the ET has spare capacity and anything in-queue, dispatch that task immediately
    	boolean spare = et.currentTasks < et.limitTasks
    	if(spare){
    		log.debug "taskCompletedOnET() finds spare capacity on ET ${et.tgtName} [${et.id}]"
    	}
    	def saved = et.save(flush:true)
    	if(!saved){
    		log.warn "taskCompletedOnET() failed save() on updated target"
    	}
    	log.info "taskCompletedOnET() saved update to target ${etid} which has ${et.limitTasks - et.currentTasks} spare room"
    }
    
    /**
     * kill all tasks marked as RUNNING on this target;
     * set target currentTasks to 0
     */
    boolean clearTarget(MpfExecTgt et){
    	et.accepting=false
    	et.save(flush:true)
    	def tasks = MpfTask.withCriteria {
			eq ('status',MpfTaskStatus.RUNNING)
			eq ('execTgt', target)   		 
    	}
    	tasks.each{mpfTaskManagerService.killTask(it,"quieting ET")}
    }
    
    def fetchRunningList(MpfExecTgt target){
		String summaryStdOut = "cd ${target?.execDir} ; find . -name CURRENT_STATUS -printf ' %h ' -exec cat {}  \\;"
		def runningList = []
		def sendback=[:]
		doCommandOnTarget(summaryStdOut, target, sendback)
		log.debug "fetchRunningList(target id:${target.id}) gets \n" + sendback.text
		sendback.text?.eachLine { line ->
			def splits = line.split()
			if(splits[-1] == "RUNNING") {
				// all we want is the remote staging dir, the leaf of the dir path that had the CURRENT_STATUS file
				runningList << splits[0].split('/')[-1] 
			}
		}
		return runningList
    }
    
    def fetchRunningRemoteStagingDirPerDb(MpfExecTgt target){
		MpfTask.withCriteria{
			eq ('status',MpfTaskStatus.RUNNING)
			eq ('execTgt', target)
			projections { property('remoteStagingDir') }
		}    	
    }
    
    def fetchMismatchList(MpfExecTgt target){
		def runningList = fetchRunningList(target)
		def runningPerDb = fetchRunningRemoteStagingDirPerDb(target)
		(runningPerDb - runningList) + (runningList - runningPerDb) // in one, but not the other
    }
    
    boolean doCommandOnTarget(String cmdStr, MpfExecTgt target, sendback=[:], timeoutMS=0){
        log.info "doCommandOnTarget() doing sshexec on target '${target.tgtName}' [${target.id}]"
        boolean cmdSucceeded = false
        boolean shouldTrust = true
        def TO = timeoutMS ?: defaultSshExecTimeoutMS
        def response = ""
        try{
        	// redirect the output from antbuilder so it doesn't end up in the log instead of back to caller
        	java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream()
        	groovy.io.GroovyPrintStream gps = new groovy.io.GroovyPrintStream(bos)
        	// note that bos.toString() recovers the output
        	PrintStream sout = System.out // hold the stream
        	System.out = gps // redirect
        	antBuilderBean?.sshexec(host:target.ip, username:target.username, password:target.pw,
        			trust:shouldTrust, verbose:false, timeout:TO, command:cmdStr)
        	System.out = sout // restore the stream
        	log.debug "sshexec output is \n" + bos.toString()
            response = bos.toString() // (antBuilderBean.project.properties["${outname}"] - cmdStr).substring(3)
            sendback['text']= response
        	cmdSucceeded = true
        	try{
        		log.debug "doCommandOnTarget() result (likely delayed): " + bos.toString() //antBuilderBean.project.properties.'dcotoutcome'
        	} catch (Exception e){
        		log.warn "doCommandOnTarget() failed to log outcome because " + e.message
        	}
        } catch (Exception e){
        	def errStr = "doCommandOnTarget() caught Exception with messages: ${e.message} and with params:"
        	    errStr +="\n\thost:${target?.ip} trust:${shouldTrust}"
            	errStr +="\n\tusername:${target?.username} password:${"*" * target?.pw?.size()}"
            	errStr +="\n\tcommand: [${cmdStr}]"
            log.warn errStr
            log.debug e
        } 
        return cmdSucceeded
    }
    
    static boolean SHOULD_COLLECT_TGT_STATS=false
    static boolean COLLECTING_TGT_STATS=false 
    def synchronized startTgtStatCollection(){
    	int secs = config.com?.appscio?.mpf?.targets?.monitorSec ?: 0
    	if(secs < 1){
    		log.info "startTgtStatCollecton() won't start because com.appscio.mpf.targets.monitorSec = ${config.com?.appscio?.mpf?.targets?.monitorSec} (defaults to zero)"
    		return
    	}
    	SHOULD_COLLECT_TGT_STATS = true
    	log.info "mpfExecTgtStats monitor launching"
    	backgroundService.execute("mpfExecTgtStats monitor",{
    		while(SHOULD_COLLECT_TGT_STATS){
    			String lwi = config.com?.appscio?.mpf?.targets?.logWhenIdle ?: 'true'
    			boolean LOG_WHEN_IDLE = (lwi == 'true')
    			
	    		MpfExecTgt.withSession {
	    			def stats=[]
	    			def targets = MpfExecTgt.list()
	    			targets.remove(targets.find{it.id == DONT_CARE_TGT_ID}) // ignore the 'load balancer' cause it's not real
	    			boolean activity = false
	    			Date sampleTime = new Date()
	    			targets.each{ tgt ->
	    				def rlsize = fetchRunningList(tgt)?.size()
	    				def dbsize = fetchRunningRemoteStagingDirPerDb(tgt).size()
	    				def stat= new MpfExecTgtStats(et:tgt,runnningPerTgt:rlsize, runningPerDb:dbsize, sampleTime:sampleTime)
	    				stats << stat
	    				if(rlsize >0 || dbsize>0) activity = true
	    				log.debug "stats tgt: ${tgt.id} tgt:${rlsize} db:${dbsize} activity=${activity}"
	    			}
	    			
	    			if(activity || LOG_WHEN_IDLE){
	    				stats*.save(flush:true)
	    			}
	    		}
	    		Thread.currentThread().sleep(secs * 1000)    
    		} // elihw
    		log.info "mpfExecTgtStats monitor exiting"
    	})    	
    }
    
    def stopTgtStatCollection(){
    	SHOULD_COLLECT_TGT_STATS = false
    }
    
    def fetchFileFromTarget(String filename, String outfile, MpfExecTgt target){
    	boolean cmdSucceeded = false
        try{
        	antBuilderBean?.scp(file:"${target.username}:${host:target.pw}@${target.ip}:${filename}",
        			trust:true)
        	cmdSucceeded = true
        	try{
        		log.debug "fetchFileFromTarget() result (likely delayed): " + antBuilderBean.project.properties.'dcotoutcome'
        	} catch (Exception e){
        		log.warn "fetchFileFromTarget() failed to log outcome because " + e.message
        	}
        } catch (Exception e){
        	def errStr = "fetchFileFromTarget() caught Exception with messages: ${e.message} and with params:"
        	    errStr +="\n\thost:${target?.ip} trust:${shouldTrust}"
            	errStr +="\n\tusername:${target?.username} password:${"*" * target?.pw?.size()}"
            	errStr +="\n\tcommand: [${cmdStr}]"
            log.warn errStr
            log.debug e
        }    	
    }

}
