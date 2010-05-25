/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

import com.appscio.mpf.grails.core.MpfDetectorArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsClass
import org.springframework.core.io.*

class MpfDetectorGrailsPlugin {
    // the plugin version
    def version = "0.5"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.0 > *"
    // the other plugins this plugin depends on
    //def dependsOn = [backgroundThread:"1.3", superFileUpload:"0.4"]
    def dependsOn = ["backgroundThread":"* > 1.6"] //, "super-file-upload":"* > 0.4"]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/mpfDetectors/**",
            "grails-app/services/**/Test*",
            "grails-app/conf/**",
            "grails-app/test/projects/**"
            ]

    def author = "Wayne Stidolph"
    def authorEmail = "wstidolph@appscio.com"
    def title = "Integration with Appscio MPF"
    def description = '''\\
This plugin provides integration with Appscio MPF pipelines for event detection. It features a service for running pipelines, and domain objects for tracking status and events
'''

    // URL to the plugin's documentation
    def documentation = "http://appscio.com/catalog/MpfDetector+Plugin"
    def artefacts = [new MpfDetectorArtefactHandler()]

    def doWithSpring = {
        antBuilderBean(groovy.util.AntBuilder){b ->
            b.scope="prototype"
        }

       // vadScriptDir (ClassPathResource, "web-app/MpfDetector") {} // doesn't work :( ... creates bean OK but doesn't point to any existing thing

        // TODO Implement runtime spring config (optional)
        application.mpfDetectorClasses.each { GrailsClass g ->
            log.info "found mpfDetector class " + g.getName()
        }
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
        // set up the DEFAULT_TASK
        //def mySvc = applicationContext.getBean("mpfDetectorService")
        //log.info "doWithApplicationContext finds service ${mySvc}"

        def myTaskMgr = applicationContext.getBean("mpfTaskManagerService")
        def defTask = myTaskMgr?.makeOrGetDefaultTask()
        log.info "doWithApplicationContext has default task ${defTask}"

        def myTgtSvc = applicationContext.getBean("mpfTargetManagerService")
        def defTgt = myTgtSvc?.ensureDefaultTgt()
        log.info "doWithApplicationContext has default target ${defTgt}"
        def dcTgt = myTgtSvc?.ensureDontCareTgt()
        log.info "doWithApplicationContext has DO NOT CARE target ${dcTgt}"
        myTgtSvc?.startTgtStatCollection()
    }

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
        application.domainClasses.each {domainClass ->//iterate over the domainClasses
           if (domainClass.clazz.name.contains("com.appscio.mpf")) {//only add it to the domains in my plugin
                domainClass.metaClass.retrieveErrors = {
                    def errorString = delegate?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}?.join(' \n')
                    return errorString
                  }
               }
          }
    }
//    def watchedResources = "file:./grails-app/mpfDetectors/**/*MpfDetector.groovy"
    def watchedResources = "file:./grails-app/mpfDetectors/**/*MpfDetector.groovy"

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.

        log.info "reloading ${event.source}"
        Class changedMpfDetector = event.source
        GrailsClass newMpfDetectorClass = application.addArtefact(changedMpfDetector)
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
        log.info "onConfigChange() due to ${event}"
    }

    def onShutdown = {event ->
        log.info "MpfDetectorGrailsPlugin onShutdown called"
        def mySvc = event.ctx.getBean("mpfTaskManagerService")
        mySvc.shutDownDefaultTask()

    }
}
