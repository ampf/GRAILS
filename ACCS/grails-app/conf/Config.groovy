/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
import org.apache.log4j.Priority as Priority

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

// Configure to monitor the actual audio extract and speech-to-text services.  Use Amazon
// provider.
jmx.axservice = 'AXNuanceService'
jmx.sttservice = 'STTNuanceService'
jmx.engine = 'GrailsApp_sttEngine'

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
grails.views.gsp.encoding="UTF-8"
grails.converters.encoding="UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

// set per-environment serverURL stem for creating absolute links
String LOGGING_SOCKETHUB = "logging.sockethub"
String LOGGING_PORT = "logging.port"
String CLOUD_PROVIDER = "cloud.provider"

// Provide access to the system environment variables.
system.env = System.getenv()

com.appscio.logging.sockethub = System.getProperty(LOGGING_SOCKETHUB, system.env.LOGGING_SOCKETHUB?:"foxhubdev.appscio.com")
com.appscio.logging.port = (System.getProperty(LOGGING_PORT, system.env.LOGGING_PORT?:"4560")).toInteger()

com.appscio.pool.nodes.max = 50

environments {
    help {
        println """
        JVM Property/System Environment Variable: (JVM property takes precedence) [default/alternatives]

        ${LOGGING_SOCKETHUB}/LOGGING_SOCKETHUB -- Hostname of socket hub for central logging [foxhubdev.appscio.com/foxhub.appscio.com/localhost]
        ${LOGGING_PORT}/LOGGING_PORT           -- Port number for central logging [4560]
        ${CLOUD_PROVIDER}/CLOUD_PROVIDER       -- Provider for cloud services [AMAZON/MOCK]
    """
        System.exit(0)
    }

    production {
        com.appscio.logging.sockethub = System.getProperty(LOGGING_SOCKETHUB, system.env.LOGGING_SOCKETHUB?: "foxhub.appscio.com")
        cloud.provider = System.getProperty(CLOUD_PROVIDER, system.env.CLOUD_PROVIDER?:'AMAZON')
    }
    development {
        // Point the socket appender at local host by default to avoid
        // long startups.
        com.appscio.logging.sockethub = System.getProperty(LOGGING_SOCKETHUB, system.env.LOGGING_SOCKETHUB?:"localhost")
        cloud.provider = System.getProperty(CLOUD_PROVIDER, system.env.CLOUD_PROVIDER?:'MOCK	')
    }

}

// log4j configuration
log4j = {

    // Local and centralized logging.
    appenders {
        dev logdir = System.getProperty("com.appscio.acce.logdir", ".")
        // Logging not yet initialized.
        println "[INFO] logging to '${logdir}'"
        console name: 'stdout', threshold: Priority.INFO, layout: pattern(conversionPattern:'%-5p [%t] %c{2} %x - %m%n')
        file name:'file', file:"${logdir}/txcloud.log",
                layout:pattern(conversionPattern:"%d{ISO8601} %-5p %c txcloud-${cloud.provider} - %m%n")
        // the 'stacktrace' appender is a magic name grails looks for for stacktrace dumps
        // if it's not defined then Grails' default is to create 'stacktrace.log' file in the runtime
        // root directory, which annoys Tomcat ;)
        file name:'stacktrace', layout: pattern(conversionPattern:'%d{ISO8601} [%t] %-5p %c{2} %x - %m%n'),
                file: "${logdir}/txcloud-stacktrace.log"
        // SocketAppender does not use patterns, it serializes LogEvent objects
        // through the socket.  Send data to central logger.
        appender new org.apache.log4j.net.SocketAppender(name:'central',
                application: "txcloud-${cloud.provider}".toString(), threshold: Priority.INFO,
                remoteHost:com.appscio.logging.sockethub, port:com.appscio.logging.port, locationInfo:false)
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
           'org.hibernate'

    warn   'org.mortbay.log',
           'httpclient', // logs actual wire actions, line by line
           'org.apache.commons.httpclient', // more httpclient
           'org.apache.http',
           'com.amazonaws'

    debug  'grails.app'

    root {
        // Put the root logger at debug level.  Use appender thresholds to filter
        // to a lower level.
        debug 'stdout', 'file', 'central'
    }

}


