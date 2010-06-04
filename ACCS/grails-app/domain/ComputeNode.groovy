/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputeNode {
    String internalName = ""
    String externalName = ""
    String status = ProvisioningService.CREATED
    String instanceId
    String userData = ""
    Date started = new Date()
    int upTime = 0			// seconds
    int provisionTime = 0	// seconds
    static belongsTo = [pool: ComputePool]
    static constraints = {
      // instanceId(widget:'label'); internalName(widget:'label'); externalName(widget:'label'); status(widget:'label');
      instanceId(); internalName(); externalName(); status();
      provisionTime(widget:'label'); upTime(widget:'label'); started(editable:false);
      userData(editable:false);
      // pool(widget:'label');
    }
    String toString() {
      return "${instanceId} (#${id}) (${status})"
    }
}
