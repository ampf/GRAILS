// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

 grails.config.locations = [ "classpath:${appName}-config.properties",
                             "classpath:${appName}-config.groovy",
                             "file:${userHome}/.grails/${appName}-config.properties",
                             "file:${userHome}/.grails/${appName}-config.groovy"]

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
grails.views.gsp.encoding="UTF-8"
grails.converters.encoding="UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

grails.serverIP = InetAddress.localHost.hostAddress

// set up the values for the default exec tgt; example:
// com.appscio.mpf.targets.default.name = 'mpf1 on lance' //'default'
// com.appscio.mpf.targets.default.username = 'mpf1'
// com.appscio.mpf.targets.default.password = 'mpf1mpf1'
// com.appscio.mpf.targets.default.ip = 'lancelot'

// where to lok for detector runtimes is controlled with system env
// vars (e.g., $MPF_DETECTORS_HOME, $APPSCIO_HOME)

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://${grails.serverIP}/${appName}"
    }
    development {
        grails.serverURL = "http://${grails.serverIP}:${System.properties['server.port'] ?: '61481'}/${appName}"
        // grails.serverURL = "http://localhost:61481/${appName}"
//        grails.serverURL = "http://localhost:${server.port}/${appName}"
       // com.appscio.mpf.MPF_DETECTORS_HOME="/home/wstidolph/detectors" // overrides env vars
    }
    testwayne {
        grails.serverURL = "http://${grails.serverIP}:${System.properties['server.port'] ?: '61481'}/${appName}"
    }
    test {
        //grails.serverURL = "http://localhost:8080/${appName}"
    }

}
backgroundThread {
    queueSize = 1000 // Maximum number of tasks to queue up
    threadCount = 5 // Number of threads processing background tasks.
    tasksPerDrain = 100 // See Note
}

// log4j configuration
com.appscio.mpf.remotelogger="mpf.remote" // or, "omar.testing" or whatever
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
        tmp = System.getProperty("java.io.tmpdir","/tmp")
        rollingFile  name:'logfile', file:"${tmp}/VadTest.log", threshold: org.apache.log4j.Level.INFO, maxFileSize:10024
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
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.springframework',
            'org.hibernate'

    // warn
    debug   'com.appscio.mpf.grails.core',
            'com.appscio.mpf.grails.event',
            'com.appscio.mpf.grails.eventstore',
            'com.appscio.mpf.grails.report',
            'com.appscio.mpf.grails.target',
            'com.appscio.mpf.grails.task'
}
