/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputePoolJMX {

	ComputePool pool
	def totalAX = 0, totalSTT = 0
	def nodes = 0
	def status
	
    static constraints = {
		pool(); totalAX(); totalSTT(); nodes(); status()
    }
}
