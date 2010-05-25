/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

import java.io.File;
import java.util.Map;

includeTargets << grailsScript("Init")
//includeTargets << new File("src/groovy/com/appscio/mpf/utility/FileHelpers.groovy")

/* UTILITY FUNCTIONS I can't figure out how to easily import from src/groovy */

def getInterestingPlaceFromParameterEnv(Map env, String detName){
    def placesToLook = []
    if (!env) env= System.getenv()

    def MDH = env["MPF_DETECTORS_HOME"]
    if (MDH) {
        if(MDH.endsWith("/")){
            MDH=MDH.substring(0,MDH.length()-1) // trim trailing '/'
        }
        placesToLook << MDH + "/$detName"
    }
    def AH = env['APPSCIO_HOME']
    if (AH) {
        if(AH.endsWith("/")){
            AH=AH.substring(0,AH.length()-1) // trim trailing '/'
        }
        placesToLook << AH + "/$detName"
    }
    def usad = new File("/usr/share/appscio/detectors"+ "/$detName")
    if( usad.exists() )
        placesToLook << "/usr/share/appscio/detectors"+ "/$detName"
    def ead = new File("/etc/appscio/detectors"+ "/$detName")
    if ( ead.exists() )
        placesToLook<< "/etc/appscio/detectors"+ "/$detName"
    return placesToLook
}

File firstInstanceOf(fileName, detname, envParam=null){
    def env = envParam ?: System.getenv()
    def places = getInterestingPlaceFromParameterEnv(env, detname)
    def firstPlace = places.find {place -> new File(place,fileName).exists() }
    return firstPlace ? new File(firstPlace, fileName) : null
}

/* SCRIPT CORE */
def importFrom(srcdir, detname){
    println "importFrom srcdir is ${srcdir} and detname is ${detname}"
    ant.copy(file:"${srcdir}/grails/${detname}MpfDetector.groovy", todir:"grails-app/mpfDetectors",verbose:true, overwrite:true){
        ant.filterset {
            ant.filter(token:"DETECTOR_SOURCE",value:srcdir)
        }
    }
    ant.mkdir(dir:"grails-app/mpfDetectors/${detname}MpfDetector_runtime/scripts")
}

target(main: "Add a named MpfDetector to the app") {
    def detectorName = ''
    detectorName <<= args // make a StringBuffer

    if(!detectorName || detectorName==""){
            println "NOTHING IMPORTED: invoke with detector name argument ('Foo' for 'FooMpfDetector') or special string 'ALLDETECTORS'"
            println "because you have MPF_DETECTORS_HOME= ${System.env['MPF_DETECTORS_HOME']} and APPSCIO_HOME=${System.env['APPSCIO_HOME']} then"
            println "for arg 'Foo' this script would look in 'grails' and 'runtime' subdirs of " + getInterestingPlaceFromParameterEnv(null, "Foo")
            println "for arg 'ALLDETECTORS' this script would look in every child dir of " + getInterestingPlaceFromParameterEnv(null, "")
            System.exit 1
    }

    // ensure detector name starts with a capital, and strip off possible MpfDetector suffix
    detectorName = detectorName[0].toUpperCase() + detectorName[1..detectorName.size()-1]
    detectorName -= "MpfDetector"

    if(detectorName != "ALLDETECTORS"){
        def detFile=firstInstanceOf("${detectorName}MpfDetector.groovy","${detectorName}/grails")
        if (detFile)
           importFrom(detFile.parentFile.parentFile.absolutePath,detectorName)
        else {
           println "did not find ${detectorName}MpfDetector.groovy in grails subdirs of " + getInterestingPlaceFromParameterEnv(null, detectorName)
           System.exit 1
        }
        System.exit 0
    }

    // now handle ALLDETECTORS (exited above if other than ALLDETECTORS)
    def roots = getInterestingPlaceFromParameterEnv(null,"")
    roots.each { rootName ->
        def root = new File(rootName)

        root.eachDir { dir ->
           def detName = dir.name
           boolean existingDetector = new File("grails-app/mpfDetectors/${detName}MpfDetector.groovy").exists()
           if(!existingDetector){
               println "importing ${detName} from ${dir}"
               importFrom(dir,detName)
           } else {
               println "detector already exists in grails-app/mpfDetectors so skipping detector ${detName} from ${dir}"
           }
        }
    }
}

setDefaultTarget(main)
