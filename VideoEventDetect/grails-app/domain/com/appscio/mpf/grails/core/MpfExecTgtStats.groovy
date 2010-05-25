/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
package com.appscio.mpf.grails.core
import java.util.Date;

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * Collects target statistics (initially, the number of running tasks)
 * @author wstidolph
 *
 */
class MpfExecTgtStats {
	
	Date sampleTime
	MpfExecTgt et
	int runnningPerTgt
	int runningPerDb

    static constraints = {
		sampleTime()
		et()
		runnningPerTgt()
		runningPerDb()
    }
}
