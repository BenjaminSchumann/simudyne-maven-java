package org.example.models.factory;

import simudyne.core.abm.AgentBasedModel;
import simudyne.core.abm.Group;
import simudyne.core.annotations.ModelSettings;
import simudyne.core.annotations.Variable;
import simudyne.core.data.CSVSource;

@ModelSettings(timeUnit = "SECONDS")
public class Factory extends AgentBasedModel<Globals> {
    @Override
    public void init() {
        // create global outputs
        createLongAccumulator("numProdsDone", "number of products done");
        createLongAccumulator("queueLength", "number of products in queue");

        // load all agents
        registerAgentTypes( Machine.class,
                            Conveyor.class,
                            MachineNEW.class,
                            ConveyorNEW.class);
        // load all links
        registerLinkTypes(  Links.Link_MachineToConveyor.class,
                            Links.Link_ConveyorToMachine.class);
    }

    @Override
    public void setup() {
        // create agent groups here. Agent indeces created sequentially across groups
        Group<Machine> myMachine = generateGroup(Machine.class, 1);
        Group<Conveyor> myConveyor = generateGroup(Conveyor.class, 1);

        // new setup from csv
        CSVSource machinesSource = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\machines.csv");
        Group<MachineNEW> machines = loadGroup(MachineNEW.class, machinesSource);
        CSVSource conveyorSource = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\conveyors.csv");
        Group<ConveyorNEW> conveyors = loadGroup(ConveyorNEW.class, conveyorSource);
        CSVSource linksSource_MachinesToConveyors = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\links_machines_to_conveyors.csv");
        machines.loadConnections(conveyors, Links.Link_MachineToConveyor.class, linksSource_MachinesToConveyors);
        CSVSource linksSource_ConveyorsToMachines = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\links_conveyors_to_machines.csv");
        conveyors.loadConnections(machines, Links.Link_ConveyorToMachine.class, linksSource_ConveyorsToMachines);

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
                Machine.initializeProduct(),
                ConveyorNEW.initialize(),
                MachineNEW.initialize()
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
