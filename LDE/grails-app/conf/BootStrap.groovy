/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
import com.appscio.mpf.grails.core.MpfTask
import com.appscio.mpf.mpp.Link

class BootStrap {

    def mpfDetectorService
    def speedupAnalysisService

    def java.text.NumberFormat myFormat = java.text.NumberFormat.getInstance();
    {
        myFormat.setMaximumFractionDigits(1);
        myFormat.setMinimumFractionDigits(1);
    }

    def init = { servletContext ->

        // Enable MpfTask dispatcher if running in production, or development/local database mode.
        // Disable otherwise, to prevent development instances "stealing" jobs.
        //
        def env = System.getProperty("grails.env")
        def db = System.getProperty("appscio.db", "local")

        if (env != "development" || db == "local") {
            mpfDetectorService.dispatchJobs()
        } else {
            log.warn "*****************************************************************************************************"
            log.warn "* MpfTask dispatching disabled: env=${env} db=${db}"
            log.warn "*****************************************************************************************************"
        }

        speedupAnalysisService.init(30)

        // Augment domain objects for <href title> rendering.
        MpfTask.metaClass.title = {col ->
            try {
                return "" + status + ", " + detectPrefix + ", elapsed=" + execTgtCompletionTimes?.elapsedSecs + " seconds"
            } catch (Exception ) {
                log.error x
            }
        }
        Link.metaClass.title = {col ->
            try {
                def size = size()/1024
                if (size > 1024) {
                    return myFormat.format(size/1024) + "MB"
                } else {
                    return myFormat.format(size) + "KB"
                }
            } catch (Exception ) {
                log.error x
            }
        }

    }
    def destroy = {
    }
}
