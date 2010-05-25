/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core;

class CotEventParser {

    static Object createParamsMap(node){

        def params = [:]
        if(!node) return params

        params.cotUID = node.@uid.text()
        params.cotType = node.@type.text()
        params.cotStart = new java.util.Date(node.@start.text() as long)
        params.cotTime = new java.util.Date(node.@time.text() as long)
        params.cotStale = new java.util.Date(node.@stale.text() as long)
        params.cotHow = node.@how.text()
        params.cotPointLat = node.point.@lat.text() as float
        params.cotPointLon = node.point.@lon.text() as float
        params.cotPointHae = node.point.@lat.text() as float
        params.cotPointCe = 99999 // evt.point.@lat
        params.cotPointLe = 99999 //evt.point.@lat
        return params
    }

    static Object createCotDetailParamsMap(node) {
        def details = [:]
    }

    static Object parseSingleEvent (report){
        /* example - assume input is an XML doc with CoT ver 2 syntax */
        def evt = new XmlSlurper().parseText(report) // this will blow up if not valid xml
        // there's only one CoT 'event' per report for this parser, so it's the top-level object

        def params = createParamsMap(evt)

        return [params] // and wrap in List
    }

    /*
     * generate an event-map, with a cotDetail field referring to a detail-map
     * // TODO create/config a MpfCotDetail object instead
     */
    static Object parseSingleEventWithCotDetail (report){

        def evt = new XmlSlurper().parseText(report) // this will blow up if not valid xml
        // there's only one CoT 'event' per report for this parser, so it's the top-level object

        def params = createParamsMap(evt) // handle the main part
        if (evt.detail){
            params.cotDetail = createCotDetailParamsMap(evt.detail)
            // here we pretty-print the detail node and stuff it in the 'report' field as text

            StringWriter sw = new StringWriter()
            new XmlNodePrinter(new PrintWriter(sw)).print(evt.detail)
            params.cotDetail.report = sw.toString()
        }

        return [params]
    }


    static Object parseMultipleEvents (report){
        /* hypothetical - assume input is an XML doc with <events> wrapping the CoT <event> tags*/
        def evt = new XmlSlurper().parseText(report) // this will blow up if not valid xml
        // there's only one CoT 'event' per report for this parser, so it's the top-level object
        def evtParamsList = evt.events.collect { def params = createParamsMap(it); params.body = "NOT SURE YET"; params }
        return evtParamsList // already a List, from 'collect'
    }


}
