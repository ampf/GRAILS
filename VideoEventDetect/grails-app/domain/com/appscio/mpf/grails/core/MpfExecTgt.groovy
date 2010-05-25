/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ConfigurationHolder
/**
 * Describes a particular execution environment. The environment is defined by an execution host and user.
 */
class MpfExecTgt {
    private static final Log log = LogFactory.getLog(MpfExecTgt.class)
    def mpfTargetManagerService
    
    Date dateCreated
    /**
     * Human name for this configuration, such as 'GPU account on host 3'
     */
    String tgtName="default"

    /**
     * username for logging into this execution account
     */
    String username="tester"

    /**
     * execution directory argument for 'cd' command (defaul blank, goes to home dir)
     */
    String execDir=""
    	
    /**
     * password for logging into this execution env (plaintext for now, will use SSL etc in later evolutions of th eplugin)
     */
    String pw='retset'

    /**
     * Execution host name or IP address
     */
    String ip="localhost"

    /**
     * how many tasks running on target now (unused so far)
     */
    int currentTasks = 0

    /**
     * how many parallel tasks to accept (unused so far)
     */
    int limitTasks   = 1

    /**
     * is this target accepting new tasks?
     */
    boolean accepting=true

    /**
     * priority to run the tasks at ('nice' level, unused so far)
     */
    int priority

    /**
     * capabilities: MpfCapabilities it fulfills
     */
    static hasMany = [capabilities:MpfCapability]

    static constraints = {
        tgtName()
        ip()
        username()
        execDir()
        pw()

    }
    
    /**
     * 
     * @param reqs a Collection of MpfCapabilities
     * @return true if no req (null or empty) or if all members of 'req' are in this.capabilities
     */
	boolean canHandleReqs( reqs){
		if (!reqs) return true
		if (reqs.size() == 0) return  true
		if (!capabilities) return false
		
		return (capabilities.intersect(reqs) ).size() == reqs.size()
	}
    
    /**
     * Find the MpfExecTgt with current < limit (have spare capacity)
     * @return a single MpfExecTgt (or null) 
     */
	static findAllOpenExecTgt(){
		MpfExecTgt.withCriteria {		
			ltProperty ('currentTasks','limitTasks')
			eq ('accepting',true)
		}
	}
	

	/**
	 * Returns the first open (spare-capacity) MpfExecTgt which meets the given requirements 
	 * @param req null, single MpfCapability, or [MpfCapability] 
	 * @return null or a single spare-capacity MpfExecTgt
	 */
	static findOpenExecTgtForReqs(req){
		if(!req){
			def serverList = MpfExecTgt.withCriteria{
				ltProperty ('currentTasks','limitTasks')
				eq ('accepting',true)
				maxResults(1)
			}
			return serverList[0]
		}
		if(req instanceof MpfCapability){
			def serverList = MpfExecTgt.withCriteria {		
				ltProperty ('currentTasks','limitTasks')
				capabilities {
						ilike('name', req.name)
					}
				maxResults(1)
			}
			return serverList[0]
		}
		if(req instanceof List){
			if(req.size()==0){
				def servers =  findOpenExecTgt(null)
				return servers instanceof List ? servers[0]: null
			}
			
			String reqname = ""
			try {
				reqname = req[0].name
				log.trace "findOpenExecTgtForReqs handling list will first filter by req name ${reqname}"
			} catch (Exception e) {
				log.warn "findOpenExecTgtForReqs got List where first entry does not have a 'name' field"
			}
			def selectedServerIdList = MpfExecTgt.withCriteria {		
				ltProperty ('currentTasks','limitTasks')
				capabilities { // can handle at least the first requirement
					ilike('name', reqname)
				}
				projections { distinct('id') } // bring back just the id list, not the objects
			}
			log.trace "findOpenExecTgtForReqs handling list of req gets back candidates ${selectedServerIdList}"
			// check each until we find one that handles all the requirements, then get it from the DB
			def selectedId = selectedServerIdList?.find {etid -> MpfExecTgt.read(etid).canHandleReqs(req)}
			MpfExecTgt.get(selectedId)
		}
	}
	

	/**
	 * Remove a task from this target, adjust task counts
	 * @param mt task (or id) to remove
	 * @return
	 */
	def removeTask(mt){
		MpfTask mpft = mt instanceof MpfTask ? mt : MpfTask.get(mt)
		
	    if(!mpft){
	    	log.warn "removeTask() on ${this} got bad param ${mt}"
	    	return
	    }
		if(mpft?.execTgt?.id != this.id){
			log.warn "removeTask() on ${this} is ${mpft} with non-matching execTgt ${mpft.execTgt}"
			return
		}
		if (mpft.status == MpfTaskStatus.RUNNING){
			if (currentTasks >0) // don't let us drive below zero tasks!
				currentTasks -= 1
			save()
		}
		
	}
	
	def doCommand(cmdStr){
		mpfTargetManagerService.doCommandOnTarget(cmdStr, this)
	}
	
	def fetchFile(filename){
		mpfTargetManagerService.fetchFileFromTarget(filename, this)
	}
	
	def getSpareCapacity(){
		this.limitTasks - this.currentTasks
	}
}
