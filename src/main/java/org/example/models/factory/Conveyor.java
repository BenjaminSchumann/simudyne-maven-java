package org.example.models.factory;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;

public class Conveyor extends Agent<Globals> {
    // define vars

    public static Action<Conveyor> someConveyorActio() {
        return Action.create(Conveyor.class, currConveyor -> {
           // do action code here with CurrConveyor
            System.out.println("conveyor did some action on tick "+currConveyor.getContext().getTick());
        });
    }

    // local functions
}
