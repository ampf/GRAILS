/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputeNodeJMXController {

	def jmxNodeService
	def provisioningService 
	
    def scaffold = true
    
    def list = {
    	try {
    		provisioningService.pollPools()
    	} catch (Throwable t) {
    		log.error t
    	}
    	def nodes = jmxNodeService.jmxNodes(grailsApplication, null)
    	[computeNodeJMXInstanceList:nodes, computeNodeJMXInstanceTotal:nodes.count()]
    }
	
}
