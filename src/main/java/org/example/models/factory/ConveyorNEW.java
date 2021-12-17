package org.example.models.factory;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Constant;

public class ConveyorNEW extends Agent<Globals> {
    @Constant
    public String name;

    public static Action<ConveyorNEW> initialize() {
        return Action.create(ConveyorNEW.class, currAgent -> {
            System.out.println("Created ConveyorNew "+currAgent.getID()+" with name="+ currAgent.name);
        });
    }
}
