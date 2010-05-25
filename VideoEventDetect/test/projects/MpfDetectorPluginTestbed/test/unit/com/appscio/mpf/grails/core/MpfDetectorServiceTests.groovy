/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core

/*******************************************************************************
 * Copyright (c)  - 2009 All rights reserved.
 * This text is made available according to the terms of the Appscio Customer License Ver 1.0 which accompanies this distribution and is available at http://www.appscio.com/reference/appscio-license.html
 ******************************************************************************/

import java.io.File;
import grails.test.*

import com.appscio.mpf.grails.core.MpfDetectorService;
import com.appscio.mpf.grails.task.MpfTaskManagerService
import com.appscio.mpf.grails.core.MpfEvent;
import com.appscio.mpf.grails.core.MpfReport;
import com.appscio.mpf.grails.core.MpfTask;
import com.appscio.mpf.grails.cot.CotMpfEventData;

class MpfDetectorServiceTests extends GrailsUnitTestCase {

    @Override
    public String getName() {
        return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
    }

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }


    String makeTempDir(){
        File tFile = File.createTempFile('ved_', '_unittest')
        def tName = tFile.absolutePath
        tFile.delete()
        def tempDirOK = new File(tName).mkdir()
        tName
    }

    String createSourceTree(){
        // create a test temporary source tree
        def root = makeTempDir()
        root
    }
    void assertCommonSubdirs(String basedir){
        assertTrue "did not find " + basedir + "/scripts",((new File(basedir + "/scripts")).exists())
        assertTrue ((new File(basedir + "/jars")).exists())
        assertTrue ((new File(basedir + "/classes")).exists())
        assertTrue ((new File(basedir + "/lib")).exists())
    }

    void testEnsureSupportDirGetsBasicStructure(){
        def det = new TestDet()
        assertNotNull det
        mockLogging(MpfDetectorService)
        def testService = new MpfDetectorService()
        testService.antBuilderBean = new AntBuilder()


        def support = makeTempDir()
        System.out.println "test support root is ${support}"
        def source = createSourceTree()
        System.out.println "test root is ${source}"

        assertFalse((new File(support + "/scripts")).exists())

        testService.fillSupportPerUserDef det, support

        // from the first ANT_COPY there should be 4 subdirs
        assertCommonSubdirs(support)
        //and one contained File
        assertTrue ((new File(support + "/scripts/hello.sh")).exists())

        // the second ANT_COPY should supply a file, and *NOT* the excluded file
        assertTrue ((new File(support + "/top_level.sh")).exists())
        assertFalse((new File(support + "/should_be_excluded.sh")).exists())

        // lets make sure the excluded file was present
        assertTrue((new File("test/data/fill_test_source_2/should_be_excluded.sh")).exists())
    }

    void testEnsureStagingDirStructureCreated(){
        mockLogging(MpfDetectorService)
        def testService = new MpfDetectorService()
        def staging = testService.createStagingDir("unittest")
        assertTrue staging.exists()
        assertCommonSubdirs(staging.getAbsolutePath())
        assertTrue new File(staging.getAbsolutePath() + "/VadScripts").exists()
        "rm -rf ${staging}".execute()
    }
}

class TestDet extends MpfDetector {
    def ANT_COPY_TO_SUPPORT = {
            fileset(dir:'test/data/fill_test_source')
            /* this is pre-created as:
                .
                |-- classes
                |-- jars
                |-- lib
                `-- scripts
                    `-- hello.sh
             */
    }

    def ANT_COPY_TO_SUPPORT_1 = {
        fileset(dir:'test/data/fill_test_source_2',
                excludes:'should_be_excluded.sh')
    }

}
