/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputeNodeJMX {
	ComputeNode node
	def currentHeap, maxHeap, cpu, freePhysicalMemory
	def AX, STT, usableDisk
	def status, jconsole
    static constraints = {
		node(); currentHeap(); maxHeap(); cpu(); freePhysicalMemory(); 
		AX(); STT(); usableDisk()
		status(); jconsole()
    }
	def server
}
