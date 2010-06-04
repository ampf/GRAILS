/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputePoolJMXController {

	def jmxNodeService
	
	def scaffold = true
	
	def list = {
    	try {
    		provisioningService.pollPools()
    	} catch (Throwable t) {
    		log.error t
    	}
    	def pools = []
		def ax = grailsApplication.config.jmx.axservice
		def stt = grailsApplication.config.jmx.sttservice
		def engine = grailsApplication.config.jmx.engine
    	ComputePool.list().each {pool ->
    		def jmxPool = new ComputePoolJMX(pool:pool)
    		jmxPool.id = pool.id
    		jmxNodeService.jmxNodes(grailsApplication, pool.nodes).each() {jmxNode ->
        		// TODO: refactor to share with ComputeNodeJMX controller, possibly by 
    			// doing formatting using constraint widgets.
    			if (jmxNode.server) {
    				jmxPool.nodes++
	        		jmxPool.totalAX += jmxNode.server.getOp("${engine}:service=${ax}",'getWallTime').toInteger()
	        		jmxPool.totalSTT += jmxNode.server.getOp("${engine}:service=${stt}",'getWallTime').toInteger()
    			} else {
    				jmxPool.status = "not all nodes are JMX reachable, totals will be inaccurate"
    			}
    		}
    		jmxPool.totalAX = jmxNodeService.format(jmxPool.totalAX/1000) + 's'
    		jmxPool.totalSTT = jmxNodeService.format(jmxPool.totalSTT/1000) + 's'
    		pools << jmxPool
    	}
    	return [computePoolJMXInstanceList:pools, computePoolJMXInstanceTotal:pools.count()]
	}

}
