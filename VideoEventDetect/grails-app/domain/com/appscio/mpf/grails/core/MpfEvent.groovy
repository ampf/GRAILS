/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

package com.appscio.mpf.grails.core

class MpfEvent {

  Date dateCreated

  MpfEventData       data
  MpfEventStreamData streamData

  // the event/report relationship is M:N
  // the MpfEvent is the parent (persistence-controlling) class
  static hasMany   = [contributors:MpfReport] // usually only one
                                              // but can be zero ( a test or derived event)
                                              // or several (a relationship event)

  static belongsTo = [mpfTask:MpfTask]
  static constraints = {
    streamData (nullable:true) // FIXME
  }

  int compareTo(Object them){
	  return this.dateCreated <=> them.dateCreated
  }
}
