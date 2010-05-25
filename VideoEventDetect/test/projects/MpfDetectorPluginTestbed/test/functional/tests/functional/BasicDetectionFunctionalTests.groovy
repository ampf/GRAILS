/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package tests.functional
class BasicDetectionFunctionalTests extends functionaltestplugin.FunctionalTestCase {
    @Override
    public String getName() {
        return super.getName().substring(4).replaceAll("([A-Z])", ' $1').toLowerCase();
    }

    void testEnsureCreateFormDemandsName (){
        get('/mpfTask/create')
        assertStatus 200
        form {
            click "Create"
        }

        assertStatus 200
        /* expecting:
         <div class="errors">
             <ul>
                 <li>Property [name] of class [class com.appscio.mpf.grails.core.MpfTask] cannot be blank</li>
             </ul>
         </div>
         for the moment we can just ensure there is something that starts with that li text
         */
        //def errorInNameQuery = "//*[starts-with(.,'Property [name]')]"
        def errorInNameQuery="//div[@class='errors']/ul/li[starts-with(.,'Property [name]')]"
        def errName= byXPath (errorInNameQuery)

        assertNotNull errName // the error is reported

        // now let's submit *with* a name
        form {
            name "functional test"
            click "Create"
        }
        assertStatus 200
        errName= byXPath (errorInNameQuery)
        assertNull errName
    }

     void testCanExecuteMultitester() {
        // basic detection execution
        get('/mpfTask/create')
        assertStatus 200
        redirectEnabled=false

        form {
            name="testCanExecuteMultitester"
            mpfDetectorName="MultiTesterMpfDetector"
            //detectPrefix=""
            timeout="2m"
            videoFileUrl="foo"
            //sourcekey=""
            useGPU="if_avail"
            selects['execTgt.id'].select 1

            log_to_host "info"
            out_to_host "off"
            err_to_host "error"
            //statusRelUrl="http://foo/status/1"
            //eventRelUrl="http://foo/event/1"

            click "Create"
            }

            assertRedirectUrlContains "/mpfTask/list"
            followRedirect()
    }
}
