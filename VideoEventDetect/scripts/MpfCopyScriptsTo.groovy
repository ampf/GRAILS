/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

includeTargets << grailsScript("Init")

target(main: "Copy out the utility scripts for the MpfDetectors to user's file system") {
    String ENV_VAR="MPF_DETECTORS_HOME"
    def dest=args
    if(!dest || dest==""){
        def mpf = System.env[ENV_VAR]
        if(mpf){
            dest=mpf // use the environment variable
        } else {
            println "NOTHING COPIED: invoke with destination dir path as argument or set system env variable ${ENV_VAR}"
            System.exit 1
        }
    }
    println "running in " + "pwd".execute().text + " with mpfDetectorDir of ${mpfDetectorPluginDir}"
    println "copying to ${dest}"
    Ant.copy(todir:dest,verbose:true){
        fileset(dir:"${mpfDetectorPluginDir}/scripts/", includes:"*.sh")
    }
}

setDefaultTarget(main)
