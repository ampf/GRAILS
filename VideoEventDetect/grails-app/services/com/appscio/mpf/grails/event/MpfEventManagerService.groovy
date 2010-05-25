/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.event
import com.appscio.mpf.grails.core.*
import com.appscio.mpf.grails.task.*

class MpfEventManagerService {

    boolean transactional = true
	def sessionFactory
	
    def mpfTaskManagerService // uses getTaskId( mptkey ), getDetectorForTaskId(mtid, true)

    /* list all events found for a sourcekey (which may cover multiple tasks) */
    def getEventIdsList (String sourcekey){
        def allEvents = []
        def tasks = MpfTask.findAllBySourcekey(sourcekey)
        tasks.each {allEvents << task.detectedEvents}
        allEvents.flatten().unique()
    }
    
	/**
	 * invoke task's detector to extract events from a report;
	 * called from Controller etc with a session in place.
	 * @param mptkey the 'id' for the MpfTask
	 * @param report the String report
	 * @return EventExtractionResponse
	 */
	def extractEventsFromReport(mptkey, String report){ 
		log.debug "extractEvents() got mptkey ${mptkey}"
		if(!report){
			def errStr = "extractEvents() got null or empty report parameter"
			log.warn errStr
			return new EventExtractionResponse(code:400, msg:errStr, evtList:[])
		}
		log.debug "extractEventsFromReport() CLEARING SESSION"
		sessionFactory?.currentSession?.clear() // ensure the session isn't holding the task
		// safe deref for testing convenience
		
		def mpfReportInstance
		try{
			mpfReportInstance = new MpfReport(body:report).save()
			log.info "extractEventsFromReport() saved MpfReport ${mpfReportInstance?.id}"
		} catch (Exception e){
			log.warn "extractEventsFromReport() making new MpfReport for the task ${mptkey} and report ${report} got Exception",e.message
			return new EventExtractionResponse(code:500,msg:e.message, evtList:[])
		}
		
		def mappedkey = mpfTaskManagerService.getTaskId ( mptkey )
		if((mappedkey as int) != (mptkey as int))
			log.info "extractEventFromReport gets mapped key ${mptkey} ==> ${mappedkey}"
		MpfTask task = MpfTask.read( mappedkey )
		if(!task){ // either a bad key, or a server failure
			def eer = new EventExtractionResponse(code:404,
					msg:"MpfTask not found with id ${mptkey}", evtList:[])
			log.info "extractEvents() returning ${eer}"
			return eer
		}
		
		log.debug "extractEventsFromReport() task is ${task}"
		
		try{
			
			def evtList = extractEvents(task.id, mpfReportInstance)
			// FIXME something wrong with the 'read' of the task in extractEvents?
			log.debug "extractEventsFromReport() CLEARING SESSION"
			sessionFactory?.currentSession?.clear() // ensure the session isn't holding the task
			// safe deref for testing convenience
			
			task = MpfTask.get(mappedkey) /// should I need to re-fetch?? merge?
			task.addToReports(mpfReportInstance)
			// task.save(flush:true)
			
			evtList.each{evt -> task.addToDetectedEvents(evt)}
			task.save(flush:true)
			log.info "extractEventsFromReport() adding ${evtList.size()} events to task ${task.name} [${task.id}]"
			
			return new EventExtractionResponse(code:200,msg:"processed",evtList:evtList)
		}
		catch(org.springframework.dao.OptimisticLockingFailureException e) {
			log.warn "extractEvents() catches locking failure, " + e.message
		}
		catch (Exception e){
			log.warn "extractEvents() processing mptkey ${mptkey} and report ${report} threw exception",e.message
			return new EventExtractionResponse(code:500,msg:e.message, evtList:[])
		}
		log.info "extractEventsFromReport() done, task is save()-ed"
	}
	/**
	 * 
	 * @param mtid MpfTask id
	 * @param mpfReportInstance
	 * @return [ MpfEvent ] which have been persisted
	 */
	def extractEvents( mtid, MpfReport mpfReportInstance ){
		
		String reportStr = mpfReportInstance.body.toString().trim()
		
		log.debug "extractEvents() mtid:${mtid} got report: ${reportStr}"
		
		def det = mpfTaskManagerService.getDetectorForTaskId(mtid, true) // return default is OK
		if(!det){
			log.warn "extractEvents() No detector instance found for task ID ${mtid} and no default returned"
			return []
		}
		log.trace "extractEvents() found detector ${det}"
		
		def evtList = []
		try { // invoking user-supplied code
			evtList = det?.parseReport(reportStr)
		} catch (Throwable t){
			log.warn "extractEvents() using det = ${det} is unable to parse ${reportStr}", t
		}
		if (evtList?.size() != 1){
			log.info "extractEvents() evtList extracted is ${evtList?.size()} long"
		}
		def mt = MpfTask.read(mtid)
		def mpfEventList = [] // need a new empty list to fill with events
		evtList.each {
			// it is a subclass of MpfEventData, we need the MpfEvent to tie together the
			// MpfTask, the received MpfReport, and the generated MpfEventData
			
			MpfEvent mpe = new MpfEvent(data:it, mpfTask:mt)
			mpe.addToContributors(mpfReportInstance)
			
			try {
				if(it.save(/*flush:true*/)){ // flush here cause optimistic lock exception on the MpfTask??
					//mt.save()
					log.debug "extractEvents() persisted event ${it}"
					mpfEventList << mpe
				} else {
					def s = "extractEvents() Failed to save event ${reportStr} because of:"
					it.errors.fieldErrors.each {fieldErr ->  s += "\n\tFIELD  ERROR " + fieldErr}
					it.errors.globalErrors.each{globalErr -> s += "\n\tGLOBAL ERROR " + globalErr}
					log.warn s
				}
			} catch(org.springframework.dao.DataIntegrityViolationException dive) {
				// if the event object is complex, the Detector should have
				// handled that in the <x>MpfEventData#save() sequence
				log.warn "extractEvents() gets DataIntegrityViolationException ... maybe detector didn't handle complex object saving - ${it}", dive
			}
		}
		return mpfEventList
	}
}
