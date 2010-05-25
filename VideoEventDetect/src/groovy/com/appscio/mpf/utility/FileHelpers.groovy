/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.utility;

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class FileHelpers {
    private static final Log log = LogFactory.getLog(FileHelpers.class)

    static createTempDir(prefix){
        def s = File.createTempFile(prefix,"")
        String stagingName = s.getAbsolutePath()
        s.delete() // we only want the safe name, not the instantiated file

        def dir = new File(stagingName)
        def succeeded = dir.mkdir()
        if (!succeeded){
            log.warn "createTempDir() unable to make temp dir " + stagingName
        }
        return dir
    }

    static rewriteFile(pathToFile, subs){
        def ant=new AntBuilder()
        try{
            // construct the replacement filter set (might be empty ...)
            log.debug "rewriteFile() rewriting " + pathToFile
            log.trace "rewriteFile() substitutions are " + subs
            ant.filterset(id:'reps'){
                subs.each {k,v ->
                    filter(token:k, value:v)
                }
            }
            ant.tempfile(property:'rewritten')
            ant.copy(file:pathToFile, toFile:ant.project.properties.rewritten){filterset(refid:'reps') }
            ant.move(file:pathToFile,toFile:pathToFile+".save")
            ant.move(file:ant.project.properties.rewritten,toFile:pathToFile)
        } catch (org.apache.tools.ant.BuildException be){
            log.warn "could not rewrite file " + pathToFile, be
        }
    }
}
