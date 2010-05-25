/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

import com.appscio.mpf.grails.core.MpfEvent;
import com.appscio.mpf.grails.core.MpfReport;
import com.appscio.mpf.grails.core.MpfTask;
import com.appscio.mpf.grails.core.SimpleMpfEventData;
import com.appscio.mpf.grails.core.MpfDetectorService
import com.appscio.mpf.grails.task.MpfTaskManagerService

import grails.test.ControllerUnitTestCase;
import groovy.lang.MetaClass;


class MpfExecTgtControllerUnitTests extends ControllerUnitTestCase {
	@Override
	public String getName() {
		return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
	}
	
	
	void testShowForIdZeroShouldReturnLatestCreated(){
		mockLogging(MpfExecTgtController)		
		def t1 =	new MpfExecTgt(
			id:1,
			dateCreated: new Date()
		)
		sleep(100)
		def t2 = new MpfExecTgt(
				id:2,
				dateCreated:new Date()
		)
		mockDomain(MpfExecTgt, [t2, t1])
		this.controller.params.id=1
		def instanceMap = controller.show()

		assertEquals 1, instanceMap.mpfExecTgtInstance.id
		
		this.controller.params.id="0"
		instanceMap = controller.show()
		assertEquals 2, instanceMap.mpfExecTgtInstance.id		
		
	}
}
