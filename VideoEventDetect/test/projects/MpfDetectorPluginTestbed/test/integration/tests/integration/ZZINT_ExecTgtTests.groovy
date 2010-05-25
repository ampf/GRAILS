/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package tests.integration

import grails.test.*
import com.appscio.mpf.grails.core.*;
import com.appscio.mpf.grails.task.*;
import com.appscio.mpf.grails.target.*;

class ZZINT_ExecTgtTests extends GrailsUnitTestCase {
	
    @Override
    public String getName() {
        return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
      }
    
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGetExecTgtsWithSpareCapacity() {
    	def targets = []
    	10.times {targets << new MpfExecTgt(name:"num${it}", currentTasks:it, limitTasks:4)}
    	targets*.save() // note that tgt #1 is the default, always-there target
    	def actual = MpfExecTgt.withCriteria {
    		ltProperty ('currentTasks','limitTasks')
    	}
    	assertEquals 5,actual.size() // default plus plus three those with currentTask 0,1,2,3
    	                             // this also verifies the 'dont care' target has no spare capacity
    }
    
    void testFindOpenServerCanHandleReqs(){
    	// set up 10 capabilities (also used as requirements for tasks)
    	def capabilities = []
    	10.times {capabilities << new MpfCapability(name:"cap_${it}")}
    	capabilities*.save()
    	(0..9).each {println "cap ${it} is " + capabilities[it]
    	             assertNotNull capabilities[it]}

    	// and 10 simple tasks, task N requires capabilities N
    	def simpleTasks = []
    	10.times {
    		def t = new MpfTask(name:"simpleTask_${it}").save()
    		t.addToRequiredCapability(capabilities[it]);
    		simpleTasks << t}
    	simpleTasks*.save()

    	// and 10 complex tasks, task N requires capabilities 0..N-1
    	def complexTasks = []
    	10.times {idx ->
    	     def t = new MpfTask(name:"complexTask_${idx}")	.save()	
    		 (0..idx).each {t.addToRequiredCapability(capabilities[it])}
    	     complexTasks << t
    	}
    	complexTasks*.save()
    	// (0..9).each {idx -> println "complexTask ${idx} has ${complexTasks[idx].reqs.size()} requirements, is " + complexTasks[idx].reqs}
    	
    	def totalTasksSize = simpleTasks.size() + complexTasks.size()
    	def qryResult
    	
    	// and 10 ExecTgts
    	def ets = []
    	10.times {ets << new MpfExecTgt(name:"et_${it}")}
    	ets*.save()
    	//def openET = MpfExecTgt.list()
    	//assertEquals 10, openET.size()
    	
    	def aServer = MpfExecTgt.findOpenExecTgtForReqs(null)
    	assertTrue aServer instanceof MpfExecTgt
    	
    	//let ET #N have capabilities 0..N
    	ets.eachWithIndex {et, idx -> capabilities[0..idx].each {cap -> et.addToCapabilities(cap)}}
    	ets*.save()
    	
    	(0..9).each{println "${it} => ${ets[it]} => "  + ets[it].capabilities}
    	aServer = MpfExecTgt.findOpenExecTgtForReqs(null)
    	assertTrue aServer instanceof MpfExecTgt
    	
    	// now we start the combined searches - state and capabilities
    	aServer = MpfExecTgt.findOpenExecTgtForReqs(capabilities[0])
    	println "cap 0 aServer is " + aServer
    	assertTrue aServer instanceof MpfExecTgt
    	
    	aServer = MpfExecTgt.findOpenExecTgtForReqs(capabilities[9])
    	println "cap 9 aServer is " + aServer
    	assertTrue aServer instanceof MpfExecTgt
    	
    	aServer = MpfExecTgt.findOpenExecTgtForReqs([capabilities[9]])
    	println "cap [9] aServer is " + aServer
    	assertTrue aServer instanceof MpfExecTgt
    	
    	aServer = MpfExecTgt.findOpenExecTgtForReqs([capabilities[8],capabilities[9]])
    	println "cap [8,9] aServer is " + aServer
    	assertTrue aServer instanceof MpfExecTgt
    	
    	aServer = MpfExecTgt.findOpenExecTgtForReqs(capabilities[3..7])
    	println "cap [3..7] aServer is " + aServer
    	assertTrue aServer instanceof MpfExecTgt
    }
    
    void testCanUseQueueAsFifo(){
    	def et = new MpfExecTgt(name:"queue_tester")
    	assertNotNull et.save(flush:true)
    	
    	// 10 simple tasks for that target
    	def simpleTasks = []
    	10.times {def t = new MpfTask(name:"simpleTask_${it}", execTgt:et, status:MpfTaskStatus.SELECTABLE);  simpleTasks << t}
    	simpleTasks*.save()

    	10.times{ // verify head of queue, then drain
    		def headOfQueue = MpfTask.getNextTaskForExecTgt(et)
    		assertEquals simpleTasks[it].id, headOfQueue.id
    		headOfQueue.status=MpfTaskStatus.SUCCEEDED
    		headOfQueue.save(flush:true)
    	}
    }
}
