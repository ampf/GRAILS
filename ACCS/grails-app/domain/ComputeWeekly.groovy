/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputeWeekly {
	String name
	static def days = ["sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"]
    ComputeSchedule monday, tuesday, wednesday, thursday, friday, saturday, sunday
    static constraints = {
	    name(); monday(); tuesday(); wednesday(); thursday(); friday(); saturday(); sunday()
	}
	String toString() {
		return name;
	}
}
