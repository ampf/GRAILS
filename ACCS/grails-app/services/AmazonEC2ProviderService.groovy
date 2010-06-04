/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
import com.amazonaws.ec2.*
import com.amazonaws.ec2.model.*
import com.amazonaws.ec2.mock.*
class AmazonEC2ProviderService {

    static expose = ['jmx']
    // Hardcode price and types until figure out how to read from api.
    static AMI_PRICES = ["m1.small":0.10,"c1.medium":0.20]
    static AMI_TYPES = ["m1.small", "c1.medium"]

    def getAmiTypes() {
        ComputePoolAccount account = ActiveAccount.get(1).account
        return account?AMI_TYPES:[]
    }

    def price(def type) {
        return AMI_PRICES[type]
    }

    def getAmiImages() {
        ComputePoolAccount account = ActiveAccount.get(1).account
        def service = new AmazonEC2Client(account.accessKeyId, account.secretAccessKey)
        def request = new DescribeImagesRequest()
        request.setOwner(Arrays.asList(account.owner))
        def response = service.describeImages(request)
        def images = response.describeImagesResult;
        def result = []
        images.image.each() {image ->
            // println "${image.imageId} platform=${image.platform}"
            if (image.platform == account.platform) {
                def location = image.imageLocation.replaceAll(/.*image_bundles\./,'').replace('.manifest.xml','')
                result << image.imageId + ": " + location
            }
        }
        // Add in shared.
        request = new DescribeImagesRequest()
        request.setExecutableBy(Arrays.asList(account.owner))
        response = service.describeImages(request)
        images = response.describeImagesResult;
        images.image.each() {image ->
            // println "${image.imageId} platform=${image.platform}"
            if (image.platform == account.platform) {
                def location = image.imageLocation.replaceAll(/.*image_bundles\./,'').replace('.manifest.xml','')
                result << image.imageId + ": " + location
            }
        }
        return result.sort()
    }

    def getKeyPairs() {
        ComputePoolAccount account = ActiveAccount.get(1).account
        def service = new AmazonEC2Client(account.accessKeyId, account.secretAccessKey)
        def request = new DescribeKeyPairsRequest()
        def response = service.describeKeyPairs(request)
        def result = response.describeKeyPairsResult
        def pairs = result.keyPair
        result = []
        pairs.each() {pair ->
            result << pair.keyName
        }
        return result
    }

    def getSecurityGroups() {
        ComputePoolAccount account = ActiveAccount.get(1).account
        def service = new AmazonEC2Client(account.accessKeyId, account.secretAccessKey)
        def request = new DescribeSecurityGroupsRequest()
        def response = service.describeSecurityGroups(request)
        def result = response.describeSecurityGroupsResult
        def securityGroupList = result.securityGroup
        result = []
        securityGroupList.each() { sec ->
            result << sec.groupName
        }
        return result
    }

    def state(node) {
        // Query the cloud.
        ComputePoolAccount account = node.pool.account
        def service = new AmazonEC2Client(account.accessKeyId, account.secretAccessKey)
        def request = new DescribeInstancesRequest();
        def response = service.describeInstances(request)
        def instances = response.describeInstancesResult
        def reservations = instances.reservation
        def states = [:]
        reservations.each {
            def running = it.runningInstance
            running.each {
                // println "${it.imageId} ${it.instanceId} ${it.platform}"
                states.put(it.instanceId,it)
            }
        }
        return states[node.instanceId]
    }

    def start(def pool, def node) {
        // TODO: bulk start nodes, but one-by-one is fast enough.
        ComputePoolAccount account = pool.account
        def service = new AmazonEC2Client(account.accessKeyId, account.secretAccessKey)
        def request = new RunInstancesRequest()
        request.setInstanceType(pool.type)
        def ami = (pool.ami =~ /([^:]*):/)[0][1]
        request.setImageId(ami)
        request.setSecurityGroup([pool.securityGroups])
        request.setKeyName(pool.keyPair)
        request.setMaxCount(1)
        request.setMinCount(1)
        request.setUserData(node.userData.bytes.encodeBase64().toString())
        def response = service.runInstances(request)
        def runresult= response.runInstancesResult
        def reservation = runresult.reservation
        def running = reservation.runningInstance
        def it = running[0]
        node.instanceId = it.instanceId
        log.debug response.toXML()
    }

    def stop(def pool, def node) {
        ComputePoolAccount account = pool.account
        def service = new AmazonEC2Client(account.accessKeyId, account.secretAccessKey)
        def request = new TerminateInstancesRequest()
        request.setInstanceId([node.instanceId])
        def response = service.terminateInstances(request)
        log.debug response.toXML()
    }

    def reboot(def pool) {
        if (!pool.nodes) {
            log.warn "pool ${pool} has no nodes to reboot"
            return
        }
        ComputePoolAccount account = pool.account
        def service = new AmazonEC2Client(account.accessKeyId, account.secretAccessKey)
        def request = new RebootInstancesRequest()
        def instances = []
        pool.nodes.each() {
            instances.add(it.instanceId)
        }
        log.info "rebooting ${instances}"
        request.setInstanceId(instances.asList())
        def response = service.rebootInstances(request)
        log.info "pool ${pool} rebooted"
        return response
    }

    String toString() {
        return 'amazon-ec2'
    }
}
