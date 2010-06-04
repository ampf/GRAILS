/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes

class BootStrap {

	def schedulingService
	def provisioningService

    def init = { servletContext ->
    	if (ComputeSchedule.count() == 0) {
	        // println "Bootstrap.init{}"
	     	def weekday = new ComputeSchedule(name:"US broadcast news (weekday)",
	        	at12am:1,at1am:2,at2am:3,at3am:5,at4am:5,at5am:6,at6am:9,at7am:12,at8am:12,at9am:12,at10am:12,at11am:15,
	        	at12pm:15,at1pm:12,at2pm:12,at3pm:12,at4pm:10,at5pm:8,at6pm:8,at7pm:5,at8pm:5,at9pm:5,at10pm:3,at11pm:1
	     	)
	        if (!weekday.save()) log.error schedule.errors

	        def weekend = new ComputeSchedule(name:"US broadcast news (weekend)",
	        	at12am:1,at1am:1,at2am:1,at3am:2,at4am:2,at5am:3,at6am:3,at7am:4,at8am:4,at9am:5,at10am:5,at11am:5,
	        	at12pm:5,at1pm:4,at2pm:4,at3pm:4,at4pm:3,at5pm:3,at6pm:2,at7pm:2,at8pm:2,at9pm:2,at10pm:1,at11pm:1
	     	)
	        if (!weekend.save()) log.error schedule.errors

	        def cycle = new ComputeSchedule(name:"1-5-1 cycle",
	        	at12am:1,at1am:5,at2am:1,at3am:5,at4am:1,at5am:5,at6am:1,at7am:5,at8am:1,at9am:5,at10am:1,at11am:5,
	        	at12pm:1,at1pm:5,at2pm:1,at3pm:5,at4pm:1,at5pm:5,at6pm:1,at7pm:5,at8pm:1,at9pm:5,at10pm:1,at11pm:5
	        )
	        if (!cycle.save()) log.error schedule.errors

	        def pyramid = new ComputeSchedule(name:"1-5 pyramid",
		        	at12am:1,at1am:1,at2am:1,at3am:2,at4am:2,at5am:2,at6am:3,at7am:3,at8am:3,at9am:4,at10am:4,at11am:4,
		        	at12pm:5,at1pm:5,at2pm:5,at3pm:4,at4pm:4,at5pm:4,at6pm:3,at7pm:3,at8pm:3,at9pm:2,at10pm:2,at11pm:2
		        )
	        if (!pyramid.save()) log.error schedule.errors

		    def zero = new ComputeSchedule(name:"zero",
	        )
	        if (!zero.save()) log.error schedule.errors

	        def weekly = new ComputeWeekly(name:"US broadcast news (weekly)",
	        	monday:weekday, tuesday:weekday, wednesday:weekday, thursday:weekday, friday:weekday, saturday:weekend, sunday:weekend
	        )
	        if (!weekly.save()) log.error schedule.errors

	        def test = new ComputeWeekly(name:"Test (cycle)",
	            	monday:cycle, tuesday:cycle, wednesday:cycle, thursday:cycle, friday:cycle, saturday:cycle, sunday:cycle
	        )
	        if (!test.save()) log.error schedule.errors

	        def pyramidweekly = new ComputeWeekly(name:"Test (pyramid)",
	            	monday:pyramid, tuesday:pyramid, wednesday:pyramid, thursday:pyramid, friday:pyramid, saturday:pyramid, sunday:pyramid
	        )
	        if (!pyramidweekly.save()) log.error schedule.errors

    	}
		// Establish schedulers for all pools present on startup.
		ComputePool.list().each {pool->
			getLog().info "starting scheduler for ${pool}"
            // Spin up nodes.
            schedulingService.schedulePool(pool.id, provisioningService)
            // And give them a kick.
            if (pool.scheduling) {
	            schedulingService.timer.execute()
            }
		}

    }
    def destroy = {
    }
}
