/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.mpp


class SpeedupAnalysisController {

    def scaffold = SpeedupAnalysis

    def table = {
        println "params=" + params
        // Expand the SpeedupAnalysis list with parameters as columns.
        def keys = SpeedupAnalysis.list(max:1)[0].parameters.keySet().sort()
        def result = "<table cellspacing='0' cellpadding='1' border='1'><tr>"

        result+= "<th>video</th> "
        keys.each {key->
            result += "<th>" + key + "</th> "
        }
        result += "<th>totalMppObjects</th>"
        result += "<th>numMppSegments</th>"
        result += "<th>percentMotion</th>"
        result += "</tr>\n<tr>"
        SpeedupAnalysis.list(params).each {analysis->
            if (analysis.params && analysis.totalMppObjects >= 3 && analysis.totalMppObjects < 10 && analysis.percentMotion < 40 && analysis.numMppSegments <= 3) {
                result += "<td>" + analysis.video + "</td> "
                keys.each {key->
                    result += "<td>" + analysis.parameters.get(key) + "</td> "
                }
                result += "<td>" + analysis.totalMppObjects + "</td>"
                result += "<td>" + analysis.numMppSegments + "</td>"
                result += "<td>" + analysis.percentMotion + "</td>"

                result += "</tr>\n"
            }
        }
        result += "</table>"
        render "<html><body>" + result + "</body></html>"
    }

    // Performs analysis of parametric sweep of the MPP detector.  You specify limits on the key outputs from the MPP algorithm
    // such as max_percent_motion, max_total_mpp_objects, max_num_mpp_segments, and the results are filtered then sorted by
    // two criteria:
    //
    //     total_mpp_objects/num_mpp_segments -- This tunes the detector to find as many objects in fewest segments possible.
    //     total_mpp_objects/percent_motion -- This tunes the detector to find as many objects in shortest segments possible
    //
    // The results are displayed in tabular form in an HTML <pre> block, for example, this shows the best settings for
    // alpha, median, momentum_threshold and picture_threshold to satisfy the constraints percent_motion <= 30 and
    // max_total_mpp_objects <= 10, over a set of videos.
    //
    /*
     *   total_mpp_objects/num_mpp_segments best parameters average over 40 records
     *
     *   max_percent_motion=30
     *   max_total_mpp_objects=10
     *   max_num_mpp_segments=5
     *
     *   [alpha:0.035, median:17, momentum_threshold:3050.0, picture_threshold:42.5]
     *
     *   [video:/mnt/21FEB03000018194.mpg, alpha:0.02, median:13, momentum_threshold:2000, picture_threshold:50, total_mpp_objects:5, num_mpp_segments:1, percent_motion:14.0]
     *   [video:/mnt/21FEB03000018194.mpg, alpha:0.08, median:17, momentum_threshold:4000, picture_threshold:30, total_mpp_objects:5, num_mpp_segments:1, percent_motion:14.0]
     *   [video:/mnt/21FEB03000018072.mpg, alpha:0.01, median:17, momentum_threshold:2000, picture_threshold:10, total_mpp_objects:5, num_mpp_segments:1, percent_motion:25.0]
     */
    // TODO: almost too many things to mention.  Make generic against parameters.
    def parameters = {
        def analyses = SpeedupAnalysis.list()
        def results =  []
        def sweep = [:]
        sweep.alpha = [] as SortedSet
        sweep.median = [] as SortedSet
        sweep.momentum_threshold = [] as SortedSet
        sweep.picture_threshold = [] as SortedSet
        def max_percent_motion = (params.max_percent_motion?:30).toInteger()
        def max_total_mpp_objects = (params.max_total_mpp_objects?:5).toInteger()
        def max_num_mpp_segments = (params.max_num_mpp_segments?:5).toInteger()
        def html = "<html><body>"
        analyses.each {analysis ->
            if (analysis.percentMotion >= 5 && analysis.numMppSegments > 0 && analysis.percentMotion <= max_percent_motion &&
                analysis.totalMppObjects <= max_total_mpp_objects && analysis.numMppSegments <= max_num_mpp_segments) {
                results += [video:analysis.video,
                    alpha:analysis.parameters.alpha.toDouble(),
                    median:analysis.parameters.median.toInteger(),
                    momentum_threshold:analysis.parameters.momentum_threshold.toInteger(),
                    picture_threshold:analysis.parameters.picture_threshold.toInteger(),
                    total_mpp_objects:analysis.totalMppObjects, num_mpp_segments:analysis.numMppSegments, percent_motion:analysis.percentMotion
                ]
                sweep.alpha += analysis.parameters.alpha
                sweep.median += analysis.parameters.median.toInteger()
                sweep.momentum_threshold += analysis.parameters.momentum_threshold.toInteger()
                sweep.picture_threshold += analysis.parameters.picture_threshold.toInteger()
            }
        }
        def show = (params.show?:10).toInteger()
        def records = (params.records?:20).toInteger()
        if (show > records) records = show
        if (records > results.size()) records = results.size()

        def keys=["video", "alpha", "median", "momentum_threshold", "picture_threshold"]
        def t_p_html_results = ""
        keys.each{key-> t_p_html_results += key + " "}
        t_p_html_results += "objective\n"
        def t_p_sort = results.sort{a-> println a; -a.total_mpp_objects/a.percent_motion}
        def t_p_opt = [alpha:0,median:0,momentum_threshold:0,picture_threshold:0]
        t_p_sort[0..records-1].eachWithIndex {result, i->
            println "result=" + result
            t_p_opt.alpha = t_p_opt.alpha + result.alpha/records
            t_p_opt.median = t_p_opt.median + result.median/records
            t_p_opt.momentum_threshold = t_p_opt.momentum_threshold + result.momentum_threshold/records
            t_p_opt.picture_threshold = t_p_opt.picture_threshold + result.picture_threshold/records
            if (i < show) {
                keys.each {key->
                    t_p_html_results += result[key] + " "
                }
                t_p_html_results += ((double)result.total_mpp_objects/result.percent_motion).round(2) + "\n"
             }
        }
        t_p_opt.alpha = t_p_opt.alpha.round(3)
        t_p_opt.median = ((int)(t_p_opt.median))/2*2+1
        t_p_opt.momentum_threshold = t_p_opt.momentum_threshold

        def t_s_html_results = ""
        keys.each{key-> t_s_html_results += key + " "}
        t_s_html_results += "objective\n"
        def t_s_sort = results.sort{a-> -a.total_mpp_objects/a.num_mpp_segments}
        def t_s_opt = [alpha:0,median:0,momentum_threshold:0,picture_threshold:0]
        t_s_sort[0..records-1].eachWithIndex {result, i->
            t_s_opt.alpha = t_s_opt.alpha + result.alpha/records
            t_s_opt.median = t_s_opt.median + result.median/records
            t_s_opt.momentum_threshold = t_s_opt.momentum_threshold + result.momentum_threshold/records
            t_s_opt.picture_threshold = t_s_opt.picture_threshold + result.picture_threshold/records
            if (i < show) {
                keys.each {key->
                    t_s_html_results += result[key] + " "
                }
                t_s_html_results += ((double)result.total_mpp_objects/result.num_mpp_segments).round(2) + "\n"
            }
        }
        t_s_opt.alpha = t_s_opt.alpha.round(3)
        t_s_opt.median = ((int)(t_s_opt.median))/2*2+1
        t_s_opt.momentum_threshold = t_s_opt.momentum_threshold

        html += "<pre>"
        html += "sweep=" + sweep + "\n\n"
        html += "max_percent_motion=${max_percent_motion}\n"
        html += "max_total_mpp_objects=${max_total_mpp_objects}\n"
        html += "max_num_mpp_segments=${max_num_mpp_segments}\n"
        html += "<h3>total_mpp_objects/percent_motion best parameters average over ${records} records</h3>"
        html += "\n" + t_p_opt + "\n\n"
        html += t_p_html_results + "... showing first ${show} records"
        html += "<h3>total_mpp_objects/num_mpp_segments best parameters average over ${records} records</h3>"
        html += "\n" + t_s_opt + "\n\n"
        html += t_s_html_results + "... showing first ${show} records"
        html += "</pre>"
        html += "</body></html>"
        render html
    }

}
