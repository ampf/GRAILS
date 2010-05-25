/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.utility;

import java.io.File;
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class Places {
    private static final Log log = LogFactory.getLog(Places.class)

    /**
     * Creates list of paths to examine for a given detector name, taking
     * into account the setting of MPF_DETECTORS_HOME and APPSCIO_HOME in
     *  the passed-in environment.
    *  <p>
    *  Returned list for detector Foo would be, in highest-priority-first order:
    *  <ul>
    *  <li><code>MPF_DETECTORS_HOME/Foo</code> (if, and only if, MPF_DETECTORS_HOME is defined)</li>
    *  <li><code>MPF_DETECTORS_HOME/</code> (if, and only if, MPF_DETECTORS_HOME is defined)</li>
    *  <li><code>APPSCIO_HOME/Foo</code> (if, and only if, APPSCIO_HOME is defined)</li>
    *  <li><code>APPSCIO_HOME/</code> (if, and only if, APPSCIO_HOME is defined)</li>
    *  <li><code>/usr/share/appscio/detectors/Foo</code></li>
    *  <li><code>/etc/appscio/detectors/Foo</code></li>
    *  </ul>
    *  <em>NOTE - should change this to APPSCIO_HOME/detectors/Foo</em>
     * @param env Map to search for MPF_DETECTORS_HOME and APPSCIO_HOME keys (if <code>null</null> Map, uses System environment)
     * @param detName name of detector to search for (e.g., <code>Foo</code>)
     * @return List of Strings, each string an absolute file path to a root dir (look under this for 'grails' and 'runtime')
     */
    static def getInterestingPlaceFromParameterEnv(Map env, String detName){
        def placesToLook = []
        if (!env) env= System.getenv()

        def MDH = env["MPF_DETECTORS_HOME"]
        if (MDH) {
            if(MDH.endsWith("/")){
                MDH=MDH.substring(0,MDH.length()-1) // trim trailing '/'
            }
            log.debug "getInteresting sees MPF_DETECTORS_HOME as " + MDH
            placesToLook << MDH + "/$detName"
            placesToLook << MDH
        }
        def AH = env['APPSCIO_HOME']
        if (AH) {
            if(AH.endsWith("/")){
                AH=AH.substring(0,AH.length()-1) // trim trailing '/'
            }
            log.debug "getInteresting sees APPSCIO_HOME as " + AH
            placesToLook << AH + "/$detName"
            placesToLook << AH
        }
        def usad = new File("/usr/share/appscio/detectors"+ "/$detName")
        if(usad.exists() )
            placesToLook << "/usr/share/appscio/detectors"+ "/$detName"
        def ead = new File("/etc/appscio/detectors"+ "/$detName")
        if ( ead.exists() )
            placesToLook<< "/etc/appscio/detectors"+ "/$detName"
        log.debug " getInteresting() returns " + placesToLook
        return placesToLook
    }

    /**
     * Finds highest-priority instance of a named file, for a given detector, in a given environment.
     * Used because the same-named file will exists in the scope of every Detector (e.g.,
     * <code>RunMpfDetector.sh</code>, and may be customized or versioned for that Detector.
     * @param fileName base filename to search for (e.g., <code>RunMpfDetector.sh</code>)
     * @param detname name of detector to search for
     * @param envParam environment Map to search, defaults to <null> (mainly used for testing)
     * @return File or null
     */
    static File firstInstanceOf(fileName, detname, envParam=null){
        def env = envParam ?: System.getenv()
        def places = getInterestingPlaceFromParameterEnv(env, detname)
        def firstPlace = places.find {place -> new File(place,fileName).exists() }
        if(firstPlace){
            log.debug "firstInstanceOf() finds ${fileName} in ${firstPlace}"
        } else {
            log.info "firstInstanceOf() could not find ${fileName} in ${places}"
        }
        return firstPlace ? new File(firstPlace, fileName) : null
    }
}
