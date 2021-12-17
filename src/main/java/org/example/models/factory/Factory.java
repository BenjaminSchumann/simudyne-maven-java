package org.example.models.factory;

import simudyne.core.abm.AgentBasedModel;
import simudyne.core.abm.Group;
import simudyne.core.annotations.ModelSettings;
import simudyne.core.annotations.Variable;

@ModelSettings(timeUnit = "SECONDS")
public class Factory extends AgentBasedModel<Globals> {
    @Override
    public void init() {
        // create global outputs
        createLongAccumulator("numProdsDone", "number of products done");
        createLongAccumulator("queueLength", "number of products in queue");

        // load all agents
        registerAgentTypes(Machine.class, Conveyor.class);
        // load all links
        registerLinkTypes(  Links.Link_MachineToConveyor.class,
                            Links.Link_ConveyorToMachine.class);
    }

    @Override
    public void setup() {
        // create agent groups here. Agent indeces created sequentially across groups
        Group<Machine> myMachine = generateGroup(Machine.class, 1);
        Group<Conveyor> myConveyor = generateGroup(Conveyor.class, 1 /*, currConv.createProducts*/);

        // link agents here
        myMachine.fullyConnected(myConveyor, Links.Link_MachineToConveyor.class); // machine knows conveyor
        myConveyor.fullyConnected(myMachine, Links.Link_ConveyorToMachine.class); // conveyor knows machine

        super.setup(); // final Simudyne setup
    }

    @Override
    public void step() {

        super.step(); // FIRST: do Simudyne stepping

        // seed model with initial products (only on first step)
        firstStep(
                Conveyor.initializeProducts(getGlobals().numInitialProducts),
                Machine.initializeProduct()
        );
        // sequence is crucial here, consider Splits
        run(
                // 1. machines finish products -> send msg to conveyors for more
                Machine.finishProduct(),
                // 2. conveyors receive msg -> send msg with next product to machine
                Conveyor.sendProduct(),
                // 3. machines receive msg with product -> await next tick
                Machine.prepareNextTick()
        );
        if (getGlobals().systemFinished) { // triggered if all empty
            System.exit(0);
        }
    }
}
