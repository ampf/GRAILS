/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.mpp

import com.appscio.mpf.grails.core.*;
import com.appscio.mpf.rdf.Utils;

class SpeedupAnalysisService {

    boolean transactional = true
    def backgroundService

    void init(def period) {
        backgroundService.execute("analyzeSpeedup", {
            while (true) {
                SpeedupAnalysis.withSession() {session ->
                    try {
                        analyzeSpeedup()
                        session.flush()
                        session.close()
                    } catch (Exception x) {
                        log.warn "SpeedupAnalysis: ", x
                    }
                }
                Thread.sleep(period*1000)
            }
        })
    }

    TaskAnalysis analyzeTask(def task) {
        def events = task.detectedEvents;

        def analysis = new TaskAnalysis()
        analysis.task = task

        def video = task.videoFileUrl
        if (video) {
            video = video.replaceFirst(/MPP-test/,"MPP-done")
            analysis.sizeKb = (int)(new File(video).length()/1024)
        }

        def num_segments = 0
        def max_objects = 0
        def segment_frames = 0;
        // Approximate number of frames in case data does not have it
        if ((video?:"").contains("21FEB"))
            analysis.totalFrames = 1800

        def have_segments = false
        def have_events = false
        for (event in events) {
            log.debug "event=" + event + " task=" + task + " video=" + task.videoFileUrl
            if (event.data instanceof SimpleMpfEventData) {
                def map = Utils.parseN3(new BufferedReader(new StringReader(event.data.body)))
                def subjects = map.subjects
                def segments = subjects.segmenter
                if (segments) {
                    if (have_segments) {
                        analysis.status += "Duplicate segments discarded " + event + " "
                        log.warn analysis.status
                    } else {
                        have_segments = true
                        log.debug "segments=" + segments?.size;
                        segments?.each {
                            log.debug it
                            num_segments++
                            def best_interest = it.segmenter.best_interest.toInteger()
                            def start_frame = it.segmenter.start_frame.toInteger();
                            def end_frame = it.segmenter.end_frame.toInteger();
                            analysis.totalFrames = (it.segmenter.total_frames?: analysis.totalFrames).toInteger();
                            segment_frames += end_frame - start_frame;

                            if (best_interest > max_objects)
                                max_objects = best_interest
                            analysis.totalMppObjects += best_interest
                        }
                    }
                }
                def uscevents = subjects.event
                if (uscevents) {
                    analysis.numUscTracks += uscevents.event.tracks[0].toInteger()
                }
            }
        }
        analysis.numMppSegments = num_segments
        analysis.maxMppObjects = max_objects
        analysis.segmentFrames += segment_frames
        analysis.percentMotion = (int)((analysis.totalFrames? segment_frames/analysis.totalFrames: 0)*100);
        analysis.script = task.detectPrefix
        analysis.video = video
        analysis.time = (task.execTgtCompletionTimes?.elapsedSecs?:0).toInteger()
        return analysis
    }


    void analyzeSpeedup() {

        def tasks = MpfTask.list();
        def id = 1

        def start = System.currentTimeMillis();

        log.info "analyzeSpeedup(): starting"

        tasks.each { task ->
            def video = task.videoFileUrl

            log.debug "\ntask=" + task + ", task.detectPrefix=" + task.detectPrefix
            def events = task.detectedEvents;

            // Split parameters.
            def prefix = task.detectPrefix
            def matcher = prefix=~/(detector[^ ]+)?(.*)/
            def detector = ""
            def params = ""
            if (matcher.matches()) {
                detector = matcher[0][1]?:""
                params = matcher[0][2]?:"<default>"
            }
            log.info "task=${task.id} video='${video}' detector='${detector}' params='${params}'"

            if (video) {
                log.debug "findByVideoAndParams(${video},${params})"
                def speedup = SpeedupAnalysis.findByVideoAndParams(video, params)
                if (!speedup) {
                    speedup = new SpeedupAnalysis()
                    log.info "new SpeedupAnalysis=" + speedup
                }
                speedup.video = video
                speedup.params = params
                params.split(" ").each {param->
                    def kv = param.split("=")
                    if (kv.length > 1) {
                        speedup.parameters.put(kv[0],kv[1])
                    }
                }

                def elapsed = (task.execTgtCompletionTimes?.elapsedSecs?:0).toInteger()
                def analysis = analyzeTask(task);
                if (detector=="" || detector.contains("detect-md.sh")) {
                    if (task.status == MpfTaskStatus.SUCCEEDED) {
                        speedup.detectMd = elapsed
                        speedup.numMppSegments = analysis.numMppSegments
                        speedup.totalMppObjects = analysis.totalMppObjects
                        speedup.totalFrames = analysis.totalFrames
                        speedup.percentMotion = analysis.percentMotion
                    } else {
                        speedup.detectMd = -1
                        speedup.numMppSegments = -1
                        speedup.totalMppObjects = -1
                        speedup.percentMotion = -1
                    }
                } else if (detector.contains("detect-md-usc.sh")) {
                    if (task.status == MpfTaskStatus.SUCCEEDED) {
                        speedup.detectMdUsc = elapsed
                        speedup.numMppUscTracks = analysis.numUscTracks
                    } else {
                        speedup.detectMdUsc = -1
                        speedup.numMppUscTracks = -1
                    }
                } else if (detector.contains("detect-usc.sh")) {
                    if (task.status == MpfTaskStatus.SUCCEEDED) {
                        speedup.detectUsc = elapsed
                        speedup.numUscTracks = analysis.numUscTracks
                    } else {
                        speedup.detectUsc = -1
                        speedup.numUscTracks = -1
                    }
                }
                speedup.uscVersusMdUsc = -1
                speedup.uscVersusMd = -1
                if (speedup.detectUsc > 0 && speedup.detectMdUsc > 0) speedup.uscVersusMdUsc = (speedup.detectUsc/speedup.detectMdUsc)
                if (speedup.detectUsc > 0 && speedup.detectMd > 0) speedup.uscVersusMd = (speedup.detectUsc/speedup.detectMd)
                if (!speedup.save(flush:true)) {
                    log.warn " unable to save SpeedupAnalysis: " + speedup
                }
            }
        }
        log.info "analyzeSpeedup() complete in " + (System.currentTimeMillis()-start)/1000 + " seconds"
    }

}
