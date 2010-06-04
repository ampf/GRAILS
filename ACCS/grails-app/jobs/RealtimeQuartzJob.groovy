/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class RealtimeQuartzJob {
	static def executors = []
	static int hour, day
	static triggers = {
		// Once per hour.
		cron name:"cron-trigger", cronExpression: "0 0 * * * ?"
	}
    static def execute() {
		try {
	        getLog().info "RealtimeQuartz! ${new Date()} executors=${executors}"
	        def date = new Date()
	        hour = date.hours
	        day = date.day
			executors.each() {executor ->
				executor.call(day, hour)
			}
		} catch (Throwable x) {
			getLog().error "RealtimeQuartz failed (ignored): " + x
		}
	        
    }
}
