/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class MockProviderService {

    static expose = ['jmx']

    static int MIN_PROVISION_TIME = 5 // Seconds.
    static int MAX_PROVISION_TIME = 15 // Seconds.

    static int MIN_SHUTDOWN_TIME = 5 // Seconds.
    static int MAX_SHUTDOWN_TIME = 15 // Seconds.

    static def states = [:]
    static int id = 1

   static Random random = new Random()

   // Hardcode price and types until figure out how to read from api.
   static MOCK_PRICES = ["m1.small":10,"c1.medium":20]
   static MOCK_TYPES = ["m1.small", "c1.medium"]

   def getAmiImages() {
    ComputePoolAccount account = ActiveAccount.get(1).account
      // println "getAmiImages account=${account}"
      return account?["mock-1","mock-2"]:[]
    }

    def getAmiTypes() {
    ComputePoolAccount account = ActiveAccount.get(1).account
      // println "getAmiTypes account=${account}"
    return account?MOCK_TYPES:[]
  }

    def price(def type) {
      return MOCK_PRICES[type]
    }

    def getSecurityGroups() {
      return ["security-1","security-2"]
    }

    def getKeyPairs() {
      return ["default","rsa"]
    }

    def state(node) {
        return states[node.instanceId]
    }

    def start(def pool, def node) {
        node.instanceId = "mi-${id++}"
        states.put(node.instanceId,[privateDnsName:"",publicDnsName:"",instanceState:[name:ProvisioningService.PENDING]])
        Thread.start("startup-simulator") {
            def sleep = (random.nextInt(MAX_PROVISION_TIME-MIN_PROVISION_TIME)+MIN_PROVISION_TIME)*1000
            // getLog().info "node simulator sleeping for ${sleep} ms"

            Thread.sleep(sleep)
            // Avoid race condition with shutdown.
            synchronized (states) {
                if (states[node.instanceId].instanceState.name == ProvisioningService.PENDING) {
                    getLog().info "node ${node.instanceId} -> ${ProvisioningService.RUNNING}"
                    states.put(node.instanceId,[privateDnsName:"127.0.0.1",publicDnsName:"localhost",instanceState:[name:ProvisioningService.RUNNING]])
                }
            }
        }
    }

    def stop(def pool, def node) {
        synchronized(states) {
            getLog().info "node ${node.instanceId} is stopping"
            states.put(node.instanceId,[privateDnsName:"",publicDnsName:"",instanceState:[name:ProvisioningService.SHUTTING_DOWN]])
        }
        Thread.start("shutdown-simulator") {
            def sleep = (random.nextInt(MAX_SHUTDOWN_TIME-MIN_SHUTDOWN_TIME)+MIN_SHUTDOWN_TIME)*1000
            getLog().info "node simulator sleeping for ${sleep} ms"
            Thread.sleep(sleep)
            // Avoid race condition with startup.
            synchronized(states) {
                getLog().info "node ${node.instanceId} is stopped"
                states.put(node.instanceId,[privateDnsName:"",publicDnsName:"",instanceState:[name:ProvisioningService.TERMINATED]])
            }
        }
    }

    def reboot(def pool) {
        log.info "rebooting ${pool} (NOP)"
    }

    String toString() {
      return 'mock-provider'
    }
}
