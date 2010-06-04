/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
import org.codehaus.groovy.grails.commons.ConfigurationHolder as ch

class ComputePool {
    String name = "fox-media"
    int instances = 0
    int active = 0
    int stopped = 0
    String type = ""
    String ami = ""
    String securityGroups = ""
    String keyPair = ""
    String userData = ""
    Date created
    ComputePoolCost cost
    ComputePoolAccount account
    def providerService
    static transients = ['providerService', 'active']
    double price() {
        return providerService.price(type)
    }
    boolean scheduling = false
    ComputeWeekly schedule
    String toString() {
        return "${name} ${type} (${instances} instances)"
    }
    List nodes
    static hasMany = [nodes:ComputeNode]
    boolean reboot
    static constraints = {
        name(); instances(min:0,max:ch.config.com.appscio.pool.nodes.max);
        active(widget:'label'); stopped(widget:'label');
        type(widget:'select:providerService.getAmiTypes()');
        ami(widget:'select:providerService.getAmiImages()');
        securityGroups(widget:'select:providerService.getSecurityGroups()')
        keyPair(widget:'select:providerService.getKeyPairs()')
        userData()
        scheduling(); schedule(); cost(editable:false); created(editable:false);
        nodes(); account(editable:false); providerService(widget:'label')
        reboot()

    }
}

