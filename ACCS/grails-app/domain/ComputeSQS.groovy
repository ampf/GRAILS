/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputeSQS {

	String queueUrl
    static constraints = {
		queueUrl(widget:'label'); drain(); attributes(widget:'label')
    }
	def attributes
	boolean drain
	
	String toString() {
		return queueUrl
	}

}
