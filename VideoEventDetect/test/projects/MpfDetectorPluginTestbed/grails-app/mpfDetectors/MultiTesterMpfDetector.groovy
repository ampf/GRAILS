/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
import com.appscio.mpf.grails.core.*;

class MultiTesterMpfDetector extends MpfDetector {

    int lastIdx=0

    /* The parseReport method is called to extract parameters for constructing "events." It is
     * expected to return a list of instances of MpfEventData subclasses. We embed the parseReport
     * implementation to fit the simplest 'DummyDetector' as an example
     */
    // The MultiTester returns various events ... the "real" events sare of the form:
    //    event; <idx>, <time since last report>, <0.01 sec units until next report>
    def parseReport(report){
        def fields = report.split(',')
        if (fields[0]?.startsWith('event:')) {
            def evIdx = fields[0].split(':')[1] as int
            lastIdx++
            if (!(evIdx == lastIdx )){

                log.warn "evIdx ${evIdx} is out of order from lastIdx ${lastIdx} in report: ${report}"
            }
        }
    def mpfEvent = new SimpleMpfEventData(body:report)
    return [mpfEvent]
  }

  /*
   * NOTE that some 'precooked' parsers are available, such as from the com.appscio.mpf.CotEventParser
   * class, as used in the "CoT" report example which follows:
    def parseReport(report){
        def params = CotEventParser.parseSingleEvent(report) //WithCotDetail(report)
        def mpfEvent = new CotMpfEventData(params[0])
        return [mpfEvent]
    }
   */
    /*
     *	count (number of events to generate, defaults to 10)
		int_dist (interval distribution model, RANDOM, UNIFORM; default UNIFORM)
		int_max (interval max, or interval for UNIFORM; 1/200 sec unit; default 200)
     */
  def DETECTOR_PREFIX_FOR_UI = [
      'count',[key:'int_max',help:'inter-event interval in 1/100 sec units'],
      [key:'int_dist', default:'UNIFORM', help:'interval distribution; RANDOM or UNIFORM']
  ]

  /*
   * set default timeout for this detector; overridden by instance MpfTask.timeoutSeconds
   */
  def TIMEOUT_SECONDS = 10


  /* ANT_COPY_TO_SUPPORT section for deploying your files into the support_<foo> directory

   Each closure whose name starts with ANT_COPY_TO_SUPPORT will be processed in alphabetic order
   (ANT_COPY_TO_SUPPORT, ANT_COPY_TO_SUPPORT_1, ANT_COPY_TO_SUPPORT_2, etc)

   Each closure is used as a parameter to the AntBuilder 'copy' task. The 'todir'
   property of the ant.copy() is set to the support_<this class> directory, so you
   can assemble the support files (remember, by default the entire support tree will
   be copied to th staging directory and thence to the exec target working dir)

   For information on what you can do with the Copy task,
   see http://ant.apache.org/manual.CoreTasks/copy.html

   The MPF_DETECTORS_HOME property used in the default is evaluated at runtime, to be:

     env var MPF_DETECTORS_HOME if it exists
           else APPSCIO_HOME/mpf/detectors if APPSCIO_HOME is defined,
                else /usr/share/appscio/mpf/detectors

   NOTE - soon we'll add an archive-expand option to allow populating from an archive (dist) file
   */
  def ANT_COPY_TO_SUPPORT = {
      fileset(dir:"${MPF_DETECTORS_HOME}/MultiTesterMpfDetector/runtime")
  }

  /* The Run_MultiTesterMpfDetector.sh shell script will operate from the scripts/ dir of the exec tgt,
     where it will set up various directories and values an dsource in some support functions
     (such as mpf_send_status) and it will attempt to source in your shell scripts.

     You must supply a script to be sourced by the Run_MultiTesterMpfDetector.sh and identify it in the
     DETECTOR_SCRIPT property below. Your script may source other scripts.
     Your script-sourcing must supply a no-argument 'detect' function to be the main job entry.

     NOTE: The 'detect' function return status should be zero IFF the detector exits normally
  */
    def DETECTOR_SCRIPT = "detect.sh" // sample provided, you replace with your own!
}
