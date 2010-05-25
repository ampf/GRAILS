/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.mpp

class Link {
    String file
    String path

    def size() {
        return new File(path?:"", file?:"").size()
    }

    String toString() {
        return file?.replace(".sc.mpg","").replace(".sc.markup.mpg","").replace(".markup.mpg","")
    }
    static constraints = {
        path(display:false)
    }
}

