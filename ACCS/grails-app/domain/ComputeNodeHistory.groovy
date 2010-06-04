/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputeNodeHistory {

    long nodeId
    String internalName
    String externalName
    String status = ""
    String instanceId
    Date started = new Date()
    int upTime   			// seconds
    int provisionTime    	// seconds
    ComputePool pool
    static constraints = {
    }
}
