/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core;

class EventExtractionResponse {
    int code // HTTP codes
    def msg // explanatory message
    MpfEvent[] evtList = []

}
