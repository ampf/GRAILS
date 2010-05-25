/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.mpp

import com.appscio.mpf.grails.core.*

class VideoResultController {

    def scaffold = VideoResult
    def id = 1

    VideoResult newVideoResult(def task) {
        def result = new VideoResult()
        result.id = task.id
        result.task = task
        def source = new File(task.videoFileUrl?:"")
        // Check the source file against various locations
        if (!source.exists() && source.parentFile?.path) {
            source = new File(source.parentFile.path.replace("test", "done"), source.name)
        }
        if (!source.exists() && source.parentFile?.path) {
            source = new File(source.parentFile.path.replace("not-tested", "done"), source.name)
        }
        result.video = new Link(file:source.name, path:source.parent)
        result.video.id = id++;
        def dir = "/home/mpf1/completedMpfJobs/prev/" + task.remoteStagingDir + "/scripts/sc/"
        if (!new File(dir).exists())
            dir = "/home/mpf1/completedMpfJobs/" + task.remoteStagingDir + "/scripts/sc/"
        def markup_dir = dir + "/video-markup"
        def segments_dir = dir + "/video-segments"
        def video = new File(dir, result.video.file)

        def markup = new File(dir, video.name + ".markup.mpg")
        if (markup.exists()) {
            result.markup = new Link(file:markup.name, path:dir)
            result.markup.id = id++
        }
        def sc = new File(dir, video.name + ".sc.markup.mpg")

        def files
        files = new File(segments_dir).listFiles()
        files = files?.sort {a, b -> a.lastModified() - b.lastModified()}
        files?.each() {file ->
            // println result.id + ": " + result.video + " adding " + file.name
            def link = new Link(file:file.name, path:file.parent)
            link.id = id++
            result.addToSegments(link)
        }

        files = new File(markup_dir).listFiles()
        files = files?.sort {a, b -> a.lastModified() - b.lastModified()}
        files?.each() {file ->
            // println result.id + ": " + result.video + " adding " + file.name
            def link = new Link(file:file.name, path:file.parent)
            link.id = id++
            result.addToMarkupSegments(link)
        }
        result.analysis = SpeedupAnalysis.findByVideo(task.videoFileUrl?:"")

        return result
    }

    def list = {
        def results = []
        id = 1
        if (!params.show)
            params.show = "markup"
        MpfTask.list().each() {task ->
            def result = newVideoResult(task)
            if (params.show=="all") {
                results += result
            } else {
                if (params.show?.contains("segments") && result.segments && !result.markupSegments) {
                    results += result
                }
                if (params.show?.contains("markup") && result.markup) {
                    results += result
                }
            }
        }
        def size = 0
        results.each {result ->
            size += result.size()
        }
        println "total video size=" + (int)(size/1024/1024) + "MB"
        session.setAttribute("videoResults", results)
        session.setAttribute("videoResult", null)
        [videoResultInstanceList:results, videoResultInstanceTotal:results.count()]
    }

    def show = {
        def task = MpfTask.get(params.id)
        def result = newVideoResult(task)
        session.setAttribute("videoResult", result)
        [videoResultInstance:result]
    }

}
