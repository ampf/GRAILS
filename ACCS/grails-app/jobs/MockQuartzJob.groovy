/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class MockQuartzJob {
	static def executors = []
	static triggers = {
		// Ten-second ticks.
		cron name:"mock-cron-trigger", cronExpression: "0/10 * * * * ?"
	}
    static int hour = 0, day = 1
    static def execute() {
		try {
			// Mock quartz job for now.
	        getLog().debug "MockQuartz! day=${day} hour=${hour} executors=${executors}"
			executors.each() {executor ->
				executor.call(day, hour)
			}
	        hour = (++hour)%24
	        if (hour == 0) 
	        	day = (++day)%7
		} catch (Throwable x) {
			getLog().error "MockQuartz failed (ignored): " + x
		}
    }
}
