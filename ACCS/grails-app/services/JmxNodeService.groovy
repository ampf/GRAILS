/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
import javax.management.*
import javax.management.remote.JMXConnectorFactory as JmxFactory
import javax.management.remote.JMXServiceURL as JmxUrl
import javax.management.remote.JMXConnector
import java.lang.management.*

class JmxNodeServer {
	def server
	def url
	def get(def bean, def attr) {
    	return server.getAttribute(new ObjectName(bean), attr)
	}
	
	def getOp(def bean, def name) {
		return server.invoke(new ObjectName(bean), name, null, null)
	}
	String toString() {
		return 'remote'
	}
}

class JmxNodeService {

    boolean transactional = true
	def RMI_SERVER=9091
	def RMI_REGISTRY=9090
    
    def server(def node) throws Exception {
    	if (node.internalName == '') {
    		return null
    	}
    	def IP_ADDRESS=node.internalName
    	JmxUrl url = new JmxUrl(
    	         "service:jmx:rmi://${IP_ADDRESS}:${RMI_SERVER}/jndi/rmi://${IP_ADDRESS}:${RMI_REGISTRY}/server");
    	def env = [(JMXConnector.CREDENTIALS): (String[])["controller","c@ntroller"]]

    	// println url
    	def server = JmxFactory.connect(url, env).MBeanServerConnection
    	return new JmxNodeServer(server:server, url:url)
    }

    // Cache for JMX nodes.
    static def map = [:]
    
	def jmxNodes(def grailsApplication, def nodes) {
    	def jmxNodes = []
    	if (!nodes) {
    		nodes = ComputeNode.list() 
    	}
     	nodes.each {node ->
     		// Don't bother other than running nodes.
     		if (node.status == ProvisioningService.RUNNING) {
	     		def jmxNode = map[node.id]
	     		if (!jmxNode) {
	         		jmxNode = new ComputeNodeJMX(node:node)
	         		jmxNode.id = node.id
	         		map.put(node.id, jmxNode)
	     		} 
	     		jmxNode.node.refresh()
	     		if (jmxNode?.server == null) {
	     			try {
	     				jmxNode.server = server(node)
	     				if (jmxNode.server) 
	     					jmxNode.status = 'ok'
	     			} catch (Exception x) {
	     				jmxNode.status = x
	     			}
	     		}
	     		if (jmxNode.server) {
	     			try {
	 	        		if (!jmxNode.jconsole) {
	 	        			jmxNode.jconsole = new JmxConsole(url:jmxNode.server.url)
	 	        			jmxNode.jconsole.id = node.id
	 	        		}
	 	        		def ax = grailsApplication.config.jmx.axservice
	 	        		def stt = grailsApplication.config.jmx.sttservice
	 	        		def engine = grailsApplication.config.jmx.engine
	 	    			
	 		    		jmxNode.freePhysicalMemory = format(jmxNode.server.get('java.lang:type=OperatingSystem', 'FreePhysicalMemorySize')/1024/1024) + 'MB'
	 		    		jmxNode.currentHeap = format(jmxNode.server.get('java.lang:type=Memory', 'HeapMemoryUsage').get('used')/1024/1024) + 'MB'
	 		    		jmxNode.maxHeap = format(jmxNode.server.get('java.lang:type=Memory', 'HeapMemoryUsage').get('max')/1024/1024) + 'MB'
	 		    		jmxNode.cpu = format(jmxNode.server.get('java.lang:type=OperatingSystem', 'ProcessCpuTime')/1e9) + 's'
	 		    		
	 		    		jmxNode.AX = format(jmxNode.server.getOp("${engine}:service=${ax}",'getWallTime')/1000)+'s'
	 		    		jmxNode.STT = format(jmxNode.server.getOp("${engine}:service=${stt}",'getWallTime')/1000)+'s'
	 		    		jmxNode.usableDisk = jmxNode.server.getOp("${engine}:service=DiskService",'getFreeSpace')
	 					jmxNode.status = 'ok'
	     			} catch (Exception x) {
	     				jmxNode.status = x
	     				// force reconnect.
	     				jmxNode.server = null
	     			}
	     		}
	 	    	jmxNodes << jmxNode
     		}
     	}
    	return jmxNodes
	}
	def format(def number) {
		return String.format('%.2f', number)
	}

}
