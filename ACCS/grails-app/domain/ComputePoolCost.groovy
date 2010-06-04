/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
class ComputePoolCost {

    double stopped, active
    static belongsTo = [pool: ComputePool]

    public double cost() {
        return stopped + active
    }

    public String toString() {
        double burn = 0
        int count = 0
        if (pool && pool.nodes) {
            count = pool.nodes.size()
            burn = count*pool.price()
        }
        return "cost=${String.format('%.2f',cost())} burn=\$${String.format('%.2f',burn)}/hr (${count} nodes}"
    }
}
