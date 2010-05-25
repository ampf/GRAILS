/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package tests.functional
class StatusHandlingFunctionalTests extends functionaltestplugin.FunctionalTestCase {
    void testBadTaskId() {
        // a status message for a non-existent task

        post("/mpfTask/status/999"){ /* no body needed*/ }
        assertStatus 400
        assertContentContains "999"
    }
}
