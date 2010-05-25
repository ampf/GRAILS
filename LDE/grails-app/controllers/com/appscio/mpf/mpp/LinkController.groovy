/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.mpp

class LinkController {

    def scaffold = Link

    def IFRAME = false

    Link findLink(def id, def result) {
        def link = null
        if (id == result.video.id) link = result.video
        if (!link && id == result.markup?.id) link = result.markup
        if (!link) link = result.segments.find{it.id==id}
        if (!link) link = result.markupSegments.find{it.id==id}
        return link
    }

    Link findLink(def log, def session, def params) {
        def video_result = session.getAttribute("videoResult")
        def link
        def id = params.id.toInteger()
        if (!video_result) {
            // Have to look through the whole list.
            def video_results = session.getAttribute("videoResults")
            video_results.each{result ->
                if (!link) link = findLink(id, result)
            }
            if (!link) {
                log.warn "****** Unable to locate video " + id
                redirect(controller:"videoResult")
                return
            }
        } else {
            // Find the link by id.
            link = findLink(id, video_result)
        }
        return link
    }

    def show = {
        def link = findLink(log, session, params)
        if (!link) {
            redirect(action:"list")
        }

        if (IFRAME) {
            return [linkInstance:link]
        } else {
            // Stream back to the browser.  TODO; render in a new view with a smaller window to speedup playback over network.
            try {
                File file = new File(link.path, link.file)
                file.withInputStream {is ->
                    response.contentType = 'video/mpeg'
                    response.contentLength = is.available()
                    response.outputStream << is
                }
                response.outputStream.flush()
            } catch (Exception x) {
                println "show error: " + x
            }
        }
    }

    def play = {
        // Stream back to the browser.  TODO; render in a new view with a smaller window to speedup playback over network.
        def link = findLink(log, session, params)
        if (!link) {
            redirect(action:"list")
        }
        try {
            File file = new File(link.path, link.file)
            file.withInputStream {is ->
                response.contentType = 'video/mpeg'
                response.contentLength = is.available()
                response.outputStream << is
            }
            response.outputStream.flush()
        } catch (Exception x) {
            println "show error: " + x
        }
    }
}
