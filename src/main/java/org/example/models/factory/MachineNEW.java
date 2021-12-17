package org.example.models.factory;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Constant;

public class MachineNEW extends Agent<Globals> {
    @Constant
    public String name;

    public static Action<MachineNEW> initialize() {
        return Action.create(MachineNEW.class, currAgent -> {
            System.out.println("Created MachineNew "+currAgent.getID()+" with name="+ currAgent.name);
        });
    }
}
