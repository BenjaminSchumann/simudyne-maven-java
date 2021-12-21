package org.example.models.factory;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;

/**
 * Products are not agents but simple objects to speed up processing speeds. Suggested by Jiyan on 2021_12_16
 * @author Benjamin.Schumann@gmail.com
 * @since 2021_12_16
 */
public class Product  {

    public double cycleTime_ticks; // how long should this stay in a machine?
    public long startedAt_tick; // when did product start work on a machine, in ticks. Reset after each machine
    public double distanceToConveyorEnd_m; // if on a conveyor, this reflects how far towards the exit the product has moved currently

    public Product(double cycleTime_ticks) {
        this.cycleTime_ticks = cycleTime_ticks;
    }

    // local functions
    public void startMachining(long startingAt_tick){
        this.startedAt_tick = startingAt_tick;
    }
}
