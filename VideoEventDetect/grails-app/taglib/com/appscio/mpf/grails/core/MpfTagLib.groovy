/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core


class MpfTagLib {
    static namespace = "mpf"

    def eventTH = { attrs ->
        def e = attrs.instance
        def fields = attrs.fields?.split(',')
        if(!e){ // null event object, generate  default header
            fields.each {f ->
                out << "<th><g:message code='MpfEvent." + f + ".label' default='" + f + "'/></th>"
            }
        } else {
            fields.each {f ->
                out << "<th><g:message code='" + e.class.name + '.' + f + ".label' default='" + f + "'/></th>"
            }
        }
    }

    def eventTD = { attrs ->
        def e = attrs.instance
        def fields = attrs.fields?.split(',')
        if(!e){ // null object
            out << "<td></td>" * (fields? fields.size() : 0)

        } else {
            fields.each {f ->
                out << "<td>${e[f]}</td>"
            }
        }
    }
}
