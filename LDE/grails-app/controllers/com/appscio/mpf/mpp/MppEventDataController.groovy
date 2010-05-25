/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.mpp

import com.appscio.mpf.grails.core.*

class MppEventDataController {

    def list = {
        def set = new HashSet()
        def events = MppEventData.list()
        events.each {
            set += it.rawData
        }
        def list = SimpleMpfEventData.list()
        list.each() {data->
            if (!set.contains(data)) {
                def task = data.mpfEvent.mpfTask
                def mpp = new MppEventData()
                if (mpp.parseN3(data.body)) {
                    def event = new MpfEvent(data:mpp, mpfTask:task);
                    mpp.rawData = data
                    if (!event.save(flush:true)) {
                        flash.error("Unable to save " + mpp)
                    }
                    println("adding " + mpp)
                }
            }
        }
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        def results = MppEventData.list(params)

        def dir = ((params.order?:"desc")=="asc"?1:-1)

        results = results.sort{a,b->
            if (!a || !b || !params.sort)
                return 0
            def ap = a[params.sort]
            def bp = b[params.sort]
            return dir*(ap==bp?0: (ap>bp?1:-1))
        }
        [mppEventDataInstanceList:results, mppEventDataInstanceTotal:MppEventData.count()]

    }

    def scaffold = MppEventData

}
