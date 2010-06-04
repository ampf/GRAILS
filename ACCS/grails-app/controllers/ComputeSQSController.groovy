/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
import com.amazonaws.queue.*;
import com.amazonaws.queue.model.*;

class ComputeSQSController {

	String name
	
    def scaffold = true

    static def db = []
    
    def list = {
		def sqs = []
		def account = ActiveAccount.get(1).account
        AmazonSQS service = new AmazonSQSClient(account.accessKeyId, account.secretAccessKey)
        def request = new ListQueuesRequest()

        def response = service.listQueues(request)
        def listQueuesResult = response.getListQueuesResult()
		def queueUrlList  =  listQueuesResult.getQueueUrl()
		db = []
		queueUrlList.eachWithIndex() {url, index ->
			request = new GetQueueAttributesRequest()
	        request.setAttributeName(["ApproximateNumberOfMessages","VisibilityTimeout"])
	        request.setQueueUrl(url)
	        response = service.getQueueAttributes(request)
	        def result = response.getQueueAttributesResult
	        def attrs = result.getAttribute()
	        def attributes = [:]
	        attrs.each() {av ->	
				attributes += [(av.name):av.value]
			}
			def tsqs = new ComputeSQS(queueUrl:url, attributes:attributes)
			tsqs.id = index+1
			db << tsqs
		}
		

	    [computeSQSInstanceList:db, computeSQSInstanceTotal:db.count() ]
	}
	
	def create = {
		redirect(action:list)
	}
	
	def edit = {

        def computeSQSInstance = db[params.id.toInteger()-1]

        if(!computeSQSInstance) {
            flash.message = "ComputeSQS not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ computeSQSInstance : computeSQSInstance ]
        }			
	}
	
	def update = {
		def queue = db[params.id.toInteger()-1]
		def count = queue.attributes.ApproximateNumberOfMessages.toInteger()
		if (params.drain) {
			def account = ActiveAccount.get(1).account
	        AmazonSQS service = new AmazonSQSClient(account.accessKeyId, account.secretAccessKey)
			def request = new ReceiveMessageRequest()
			request.setQueueUrl(queue.queueUrl)
			def drained = 0
			def chunk = 10
			// e.g. count=12, drained = 0, 10, 12
			while (drained < count) {
				if (drained + chunk > count) 
					chunk = count - drained
				request.setMaxNumberOfMessages(chunk)
				drained += chunk
				def response = service.receiveMessage(request)
				def receiveMessageResult = response.getReceiveMessageResult()
				def messageList = receiveMessageResult.getMessage()
				messageList.each() {msg ->
					// Delete the message.
					def delete = new DeleteMessageRequest(queue.queueUrl, msg.receiptHandle, [])
					service.deleteMessage(delete)
					log.debug "drained message id=${msg.messageId} body=${msg.body}"
				}
			}
			log.info "drained=${drained} messages from ${queue.queueUrl}"
		}
		redirect(action:list)
	}
	
	// static db = [new ComputeSQS(id:1, name:'sqs-1'), new ComputeSQS(id:2, name:'sqs-2')]

}
