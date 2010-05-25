/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//
def dest = System.env["MPF_DETECTORS_HOME"]
if(dest){
    Ant.copy(todir:dest,verbose:true){
        fileset(dir:"${mpfDetectorPluginDir}/scripts/", includes:"*.sh")
    }
    println "_Install copied out script files to ${dest}"
} else {
    println "_Install did not copy scripts because MPF_DETECTORS_HOME not set; use 'grails mpf-copy-scripts-to'"
}
