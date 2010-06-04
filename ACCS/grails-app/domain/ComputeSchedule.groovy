/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputeSchedule {
    String name;
    // TODO: there must be a smarter way to manage an array.
    int at12am
    int at1am 
    int at2am 
    int at3am
    int at4am
    int at5am
    int at6am
    int at7am
    int at8am
    int at9am
    int at10am
    int at11am
    int at12pm
    int at1pm
    int at2pm
    int at3pm
    int at4pm
    int at5pm
    int at6pm
    int at7pm
    int at8pm
    int at9pm
    int at10pm
    int at11pm
    static constraints = {
        name(); 
        at12am(); at1am(); at2am(); at3am(); at4am(); at5am(); at6am(); at7am(); at8am(); at9am(); at10am(); at11am(); 
        at12pm(); at1pm(); at2pm(); at3pm(); at4pm(); at5pm(); at6pm(); at7pm(); at8pm(); at9pm(); at10pm(); at11pm();
    }
    String toString() {
        return "${name} (#${id})"
    }
    int instances(int index) {
        // Not too difficult.
        def field
        def time = index
        if (index < 12) {
            // am
            if (index == 0) time = 12
            field = "at${time}am"
        } else {
            // pm
            if (index > 12) time = index-12
            field = "at${time}pm"
        }
        int result = this."${field}"
        log.info "ComputeSchedule: instances (${index}) at ${field}=${result}"
        return result
    }
}
