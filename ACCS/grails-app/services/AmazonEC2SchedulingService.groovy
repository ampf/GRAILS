/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class AmazonEC2SchedulingService {

	static expose = ['jmx']

	// TODO: wrap the quartz job with a service and inject it.
	// def timer = MockQuartzJob
	def timer
	
	def schedulePool(def poolid, def provisioningService) {
		getLog().info "schedule pool"
		def executor = {day, hour ->
			log.info "scheduled() at day=${day} hour=${hour}"
			def pool = ComputePool.get(poolid)
			// Deleted?
			if (!pool) {
				log.warn "no pool id=${poolid}"
				return;
			}
			def schedule = pool.schedule."${ComputeWeekly.days[day]}"
			if (pool.scheduling) {
				def instances = schedule.instances(hour)
				log.info "scheduling instances=${instances}"
				provisioningService.updateNodes(pool)
				provisioningService.setInstances(pool, instances);
			} else {
				log.warn "EC2 pool ${pool} scheduling is disabled"
			}
		}
		timer.executors += executor
		
	}
}
