/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class JmxConsoleController {

	def scaffold = false
	
    def show = {
		def node = ComputeNode.get(params.id)
		if (node) {
			def jmx = ComputeNodeJMXController.map[node.id]
			log.info "launching jconsole=${jmx.jconsole}"
	    	"jconsole ${jmx.jconsole}".execute()
		} else {
			flash.message = "Unable to launch JConsole ${params}"
		}
    	redirect(controller:'computeNodeJMX', action:'list')
    }
}
