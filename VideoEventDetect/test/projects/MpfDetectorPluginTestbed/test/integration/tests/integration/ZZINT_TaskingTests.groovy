/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package tests.integration
import com.appscio.mpf.grails.core.*
import com.appscio.mpf.grails.task.*;
import com.appscio.mpf.grails.target.*;

class ZZINT_TaskingTests extends GroovyTestCase {
    def mpfDetectorService

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

    
    void testPreactivateShouldInstantiateAndAttachNamedDetector(){

        def mpt = new MpfTask(name:'test', status:MpfTaskStatus.PENDING, mpfDetectorName:'IntegrationTestSupportMpfDetector')
        mpt.save()

        def testSvc = new MpfTaskManagerService()
        def detectorCount = testSvc.detectors?.size()
        //assertTrue "detectors list have at least default detector at start",testSvc.detectors?.size() > 0
        testSvc.preActivateTask mpt
        assertEquals "should be one more detector active now",detectorCount + 1,testSvc.detectors?.size()
        assertEquals "detector should have MpfTask id",mpt.id,mpt.detector?.mpfTaskId
    }

    void testPreactivateShouldRejectUnknownDetector(){

        def mpt = new MpfTask(name:'test', status:MpfTaskStatus.PENDING, mpfDetectorName:'SillyMpfDetector')
        mpt.save()

        def testSvc = new MpfTaskManagerService()
        def detectorCount = testSvc.detectors?.size()
        testSvc.preActivateTask mpt
        assertEquals "should not be more detectors active now",detectorCount,testSvc.detectors?.size()
        assertEquals "task should be marked REJECTED",MpfTaskStatus.REJECTED,mpt.status
    }

    void testEpnrl180RapidStatusUpdatesShouldBeAcceptable(){
        def taskids = (1..5).collect { idx ->
            def mpt=new MpfTask(name:"test${idx}",
                                status:MpfTaskStatus.PENDING,
                                mpfDetectorName:'IntegrationTestSupportMpfDetector').save(flush:true)
            mpt.id}
        // def mpt = new MpfTask(name:'test', status:MpfTaskStatus.PENDING, mpfDetectorName:'IntegrationTestSupportMpfDetector')
        // mpt.save(flush:true)

        // def states = ["pending","rejected","running","succeeded","failed","unknown"]

        def testSvc = new MpfTaskManagerService()
        testSvc.mpfTargetManagerService = new com.appscio.mpf.grails.target.MpfTargetManagerService()
        def messages=[
            building:"building",
            HEARTBEAT:"hearbeat 1",
            building:"building",
            running:"running",
            HEARTBEAT:"hearbeat 2",
            HEARTBEAT:"hearbeat 3",
            // succeeded:"succeeded", don't go to a 'completion' state, it triggers inappropriate completion processing
            HEARTBEAT:'''heartbeat 4
This is the second line''',
            silly:"this should fail"
        ]

        messages.each {state, msg ->
            taskids.each { taskid ->
                def rtn = testSvc.updateTaskStatus([id:taskid, state:state, msg:msg])
                if (state == 'silly')
                     assertTrue "rtn is '${rtn}'",rtn.startsWith('updatedState() failed')
                else assertEquals "state:${state} msg:${msg}","updated", rtn
            }
        }
    }
    void testFindTaskEtPairHonorsExistingAssignment(){
 
        def testService = new MpfDetectorService()
        testService.killDispatcher()
        testService.mpfTargetManagerService= new MpfTargetManagerService()
        
        def et1 = new MpfExecTgt(tgtName:"et1", dateCreated:new Date())
        def et2 = new MpfExecTgt(tgtName:"et2", dateCreated:new Date())
        def et3 = new MpfExecTgt(tgtName:"et3", dateCreated:new Date())
        def ets = [et1, et2, et3]
        ets*.save(flush:true)
        ets.each {assertNotNull "execTgt should exist now",it}
        
        def mptS1 = new MpfTask(execTgt:et2,name:"selectable", mpfDetectorName:"whocares", status:MpfTaskStatus.SELECTABLE)
        def mptS2 = new MpfTask(name:"selectable2", mpfDetectorName:"whocares", status:MpfTaskStatus.SELECTABLE)
        def mptP = new MpfTask(name:"pending", mpfDetectorName:"whocares", status:MpfTaskStatus.PENDING)
        def mptR = new MpfTask(name:"rejected", mpfDetectorName:"whocares", status:MpfTaskStatus.REJECTED)
        def tasks = [mptS1, mptP, mptR, mptS2]
        tasks*.save(flush:true)
        tasks.each {assertNotNull "tasks should exist now",it}
        

        def taskpairs = []
        def tp
        while (tp = testService.findTaskETPair()){
        	def task = MpfTask.get(tp.taskid)
        	def target = MpfExecTgt.get(tp.etid)
        	println "selecting task ${task.name} with field execTgt ${task.execTgt} to target ${target.tgtName} ($target.id)"
        	task.status=MpfTaskStatus.PENDING
        	task.execTgt=target
        	target.currentTasks +=1
        	task.save(flush:true)
        	target.save(flush:true)
        	taskpairs << tp
        }
        
        def assertableTP = taskpairs.find {it.taskid == mptS1.id} 
        assertEquals "assignment was not honored",et2.id, assertableTP.etid
        println taskpairs
        
    }

    void testCanPutManyTasksOnServers() {
    	// this tests putting up to 13 tasks on each server
    	def testTasks = []
    	def testServerIDs = []
    	def numServers=13
    	(1..numServers).each { srvIdx ->
    		def server = new MpfExecTgt(tgtName:"server_${srvIdx}",limitTasks:srvIdx ).save()
    		assertNotNull "server [server_${srvIdx}] did not save()", server
    		testServerIDs << server.id
    	    srvIdx.times {def task = new MpfTask(name:"task__${server.id}_${it}",
    										status:MpfTaskStatus.SELECTABLE,
    										execTgt:server
    									    ).save()
    						assertNotNull "task__${server.id}_${it} didn't save()", task
    						testTasks << task
    		}
    	}
    	
    	def detSvc = new MpfDetectorService()
	    detSvc.killDispatcher()
    	detSvc.mpfTargetManagerService = new MpfTargetManagerService()
    	
    	def srvPair = ["dummyTask":"dummyServer"]
    	def assignedTasks = []
    	while(srvPair){
    		srvPair = detSvc.findTaskETPair() // gets null if no pair avail
    		if (srvPair){
					println "serverpair is task " + srvPair.taskid + " to server " + srvPair.etid
    			def task = MpfTask.get(srvPair.taskid)
    			task.status = MpfTaskStatus.PENDING
    			task.save(flush:true)
    			assignedTasks << task
    			
    			def server = MpfExecTgt.get(srvPair.etid)
    			server.currentTasks = server.currentTasks + 1
    			server.save(flush:true)
					println "server ${server.id} now ${server.currentTasks} / ${server.limitTasks}"
    		}
    	} // elihw// at this point we should have picked up every one of the tasks
    	println "testing with ${testTasks.size()} across ${numServers} servers"
    	assertEquals "created ${testTasks.size()} tasks on ${numServers} servers, assigned ${assignedTasks.size()} (should be same)", testTasks.size(), assignedTasks.size()
    	testServerIDs.each {
				MpfExecTgt s = MpfExecTgt.read(it)
				assertTrue "server ${s} has current/limit ${s.currentTasks} / ${s.limitTasks} ",  s.currentTasks == s.limitTasks
			}
    }
    
    void testCanSelectWhenMoreTasksThanEtLimit(){
    	// test whether we can have a lot of tasks selecting the same server
    	
    	// 3 servers with limits 1, 11, 111
        def detService = new MpfDetectorService()
        detService.killDispatcher()
        detService.mpfTargetManagerService= new MpfTargetManagerService()
        
        def et1 = new MpfExecTgt(tgtName:"et1", dateCreated:new Date(), limitTasks:1)
        def et2 = new MpfExecTgt(tgtName:"et2", dateCreated:new Date(), limitTasks:11)
        def et3 = new MpfExecTgt(tgtName:"et3", dateCreated:new Date(), limitTasks:111)
        def ets = [et1, et2, et3]
        ets*.save(flush:true)
        ets.each {assertNotNull "execTgt should exist now",it}
        
        200.times {
        	def server
        	switch (it){
        	case 0..10: server = et1; break
        	case 11..50: server=et2; break
        	default: server=et3
        	}
        	def task = new MpfTask(name:"task__${server.id}_${it}",
					status:MpfTaskStatus.SELECTABLE,
					execTgt:server
				    ).save()
        }
        // first, verify that the assignment loop make assign all the servers to their limit
    	def srvPair = ["dummyTask":"dummyServer"]
       	def assignedTasks = []
       	while(srvPair){
       		srvPair = detService.findTaskETPair() // gets null if no pair avail
       		if (srvPair){
       			def task = MpfTask.get(srvPair.taskid)
       			task.status = MpfTaskStatus.PENDING
       			task.save(flush:true)
       			assignedTasks << task
       			
       			def server = MpfExecTgt.get(srvPair.etid)
       			server.currentTasks += 1
       			server.save(flush:true)
       		}
       	} // elihw// at this point we should have picked up every one of the tasks

       	assertEquals assignedTasks.size(), et1.limitTasks + et2.limitTasks + et3.limitTasks

		// we start killing tasks off servers and seeing that they are replaced, until we have worked of all tasks
		// et1
		[et1,et2,et3].each { server ->
		    srvPair=["dummyTask":"dummyServer"]
			while(srvPair){
				def ettasks = MpfTask.findAllByStatusAndExecTgt(MpfTaskStatus.SELECTABLE, server)
				def srv = MpfExecTgt.get(server.id)
				ettasks.each {it.status=MpfTaskStatus.REJECTED; it.save()}
				srv.currentTasks = srv.currentTasks - 1
				srv.save(flush:true)
				srvPair=detService.findTaskETPair()
				if(srvPair){
					// OK, we dropped a task, and we found a task
				}
			}
		}
				
    }
}
