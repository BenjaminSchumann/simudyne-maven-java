package org.example.models.factory;

import simudyne.core.abm.AgentBasedModel;
import simudyne.core.abm.Group;
import simudyne.core.annotations.ModelSettings;

public class Factory extends AgentBasedModel<Globals> {
    // "init"
    @Override
    public void init() {
        // create global outputs
        createLongAccumulator("numProdsDone", "number of products done");

        // load all agents
        registerAgentTypes(Machine.class, Conveyor.class, Product.class);
        // load all links
        registerLinkTypes(Links.NormalLink.class);
    }

    @Override
    public void setup() {
        // create agent groups here
        Group<Machine> myMachine = generateGroup(Machine.class, 1);
        Group<Conveyor> myConveyor = generateGroup(Conveyor.class, 1 /*, currConv.createProducts*/);
        Group<Product> myProducts = generateGroup(Product.class, getGlobals().numInitialProducts);

        // link agents here
        myMachine.fullyConnected(myConveyor, Links.NormalLink.class); // machine knows conveyor
        myConveyor.fullyConnected(myMachine, Links.NormalLink.class); // conveyor knows machine
        myMachine.fullyConnected(myProducts, Links.NormalLink.class); // do so products actually are created


        super.setup(); // final Simudyne setup
    }

    @Override
    public void step() {

        super.step(); // FIRST: do Simudyne stepping

        //firstStep(myMachine.spawnProducts, ); // done before any other behavior on 1sst step
        // sequence is crucial here, consider Splits
        run(
                Machine.someMachineAction()
        );
    }
}
