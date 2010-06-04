/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class RefreshQuartzJob {
	static def executors = []
	static triggers = {
		// 30 second refresh, avoiding the top of the minute until transaction collision
		// associated with polling at random times can be sorted out.
		cron name:"refresh-cron-trigger", cronExpression: "15/30 * * * * ?"
	}
    static int hour = 0, day = 1
    static def execute() {
		try {
	        // Mock quartz job for now.
	        getLog().debug "RefreshQuartz! ${new Date()}"
			executors.each() {executor ->
				executor.call()
			}
		} catch (Throwable x) {
			getLog().error "RefreshQuartz failed (ignored): " + x
		}
    }
}
