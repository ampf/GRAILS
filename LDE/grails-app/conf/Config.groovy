/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

/*
grails.config.locations = [  "classpath:${appName}-config.properties",
                             "classpath:${appName}-config.groovy",
                             "file:${userHome}/.grails/${appName}-config.properties",
                             "file:${userHome}/.grails/${appName}-config.groovy"]

*/

if(System.properties["${appName}.config.location"]) {
     grails.config.locations << "file:" + System.properties["${appName}.config.location"]
}

grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]
// The default codec used to encode data with ${}
grails.views.default.codec="none" // none, html, base64
grails.converters.xml.pretty.print = true
grails.converters.encoding = "UTF-8" // "ISO-8859-1"

// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder=false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable fo AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

grails.serverIP = InetAddress.localHost.hostAddress

// set per-environment serverURL stem for creating absolute links
environments {
    port = System.properties['grails.server.port.http'] ?: System.properties['server.port'] ?: '28080'

    production {
        grails.serverURL = "http://${grails.serverIP}:${port}/${appName}"
    }
    development {
        grails.serverURL = "http://${grails.serverIP}:${port}/${appName}"
    }
    testwayne {
        grails.serverURL = "http://${grails.serverIP}:${port}/${appName}"
    }
    test {
        //grails.serverURL = "http://localhost:8080/${appName}"
    }

}
// com.appscio.mpf.targets.default.name = 'mpf1 on localhost'
com.appscio.mpf.targets.default.username = 'mpf1'
com.appscio.mpf.targets.default.password = 'mpf1mpf1'
com.appscio.mpf.targets.default.ip = 'localhost'
com.appscio.mpf.session.clearTask = true
// log4j configuration
com.appscio.mpf.remotelogger="mpf.remote" // or, "omar.testing" or whatever

// Shorten the stack traces to make them more reasonable.
def SHORT_TRACES=true

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    appenders {

        // Quiet down stack-traces sent to console.
        def filter = new it.openutils.log4j.FilteredPatternLayout()
        if (SHORT_TRACES) {
            filter.setFilter("sun.reflect")
            filter.setFilter("groovy.lang")
            filter.setFilter("java.lang.reflect")
            filter.setFilter("org.codehaus.groovy")
        }
        filter.setConversionPattern('%c{2} %m%n')

        console name:'stdout', layout:filter

        tmp = System.getProperty("java.io.tmpdir","/tmp")
        rollingFile  name:'logfile', file:"${tmp}/LDE.log", threshold: org.apache.log4j.Level.INFO, maxFileSize:10024
    }

    root {
        info 'stdout', 'logfile' //, 'mail'
    }

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
         'org.codehaus.groovy.grails.web.pages', //  GSP
         'org.codehaus.groovy.grails.web.sitemesh', //  layouts
         'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
         'org.codehaus.groovy.grails.web.mapping', // URL mapping
         'org.codehaus.groovy.grails.commons', // core / classloading
         'org.codehaus.groovy.grails.plugins', // plugins
         'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
         'org.springframework',
         'org.hibernate',
         'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'

    info    'com.appscio.mpf.utility',
            'com.appscio.mpf.grails.core',
            'com.appscio.mpf.grails.event',
            'com.appscio.mpf.grails.eventstore',
            'com.appscio.mpf.grails.initialization',
            'com.appscio.mpf.grails.report',
            'com.appscio.mpf.grails.target',
            'com.appscio.mpf.grails.task',
            'org.hibernate.sql',
            'grails.app.filters'

    // Log the SpeedupAnalysisService at warn level.  Change to info or debug to diagnose.
    warn 'grails.app.service.com.appscio.mpf.mpp.SpeedupAnalysisService'
}

// tell the code-coverage plugin where the plugin source is
/*
coverage {
    // list of plugin directories to search for source to include in coverage reports
    sourceInclusions = ['../../../src/java',
    '../../../src/groovy',
    '../../../grails-app/controllers',
    '../../../grails-app/domain',
    '../../../grails-app/services',
    '../../../grails-app/taglib'
    ]
}
*/
def MPF_PLUGIN_LOC="../../.."
coverage {
    // list of directories to search for source to include in coverage reports
    sourceInclusions = ["${MPF_PLUGIN_LOC}/src/java",
    "${MPF_PLUGIN_LOC}/src/groovy",
    "${MPF_PLUGIN_LOC}/grails-app/controllers",
    "${MPF_PLUGIN_LOC}/grails-app/domain",
    "${MPF_PLUGIN_LOC}/grails-app/services",
    "${MPF_PLUGIN_LOC}/grails-app/taglib"
    ]
    exclusions = ["**/org/grails/**",
            "**/functionaltestplugin/**",
            "*" // default package, I hope!
            ]
}
// The following properties have been added by the Upgrade process...
grails.views.gsp.encoding="UTF-8"

backgroundThread {
	threadCount = 3
}
