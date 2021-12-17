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

        // load all agents
        registerAgentTypes( Machine.class,
                            Conveyor.class);
        // load all links
        registerLinkTypes(  Links.Link_MachineToDownstreamConveyor.class,
                            Links.Link_MachineToUpstreamConveyor.class,
                            Links.Link_ConveyorToDownstreamMachine.class,
                            Links.Link_ConveyorToUpstreamMachine.class);
    }

    @Override
    public void setup() {
        // create agents
        CSVSource machinesSource = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\machines.csv");
        Group<Machine> machines = loadGroup(Machine.class, machinesSource);
        CSVSource conveyorSource = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\conveyors.csv");
        Group<Conveyor> conveyors = loadGroup(Conveyor.class, conveyorSource);

        // load link data
        CSVSource source_MachineToDownstreamConveyors = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\links_machines_to_downstream_conveyors.csv");
        CSVSource source_MachineToUpstreamConveyors = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\links_machines_to_upstream_conveyors.csv");
        CSVSource source_ConveyorToDownstreamMachines = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\links_conveyors_to_downstream_machines.csv");
        CSVSource source_ConveyorToUpstreamMachines = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\links_conveyors_to_upstream_machines.csv");
        // link machines
        machines.loadConnections(conveyors, Links.Link_MachineToDownstreamConveyor.class, source_MachineToDownstreamConveyors);
        machines.loadConnections(conveyors, Links.Link_MachineToUpstreamConveyor.class, source_MachineToUpstreamConveyors);
        conveyors.loadConnections(machines, Links.Link_ConveyorToDownstreamMachine.class, source_ConveyorToDownstreamMachines);
        conveyors.loadConnections(machines, Links.Link_ConveyorToUpstreamMachine.class, source_ConveyorToUpstreamMachines);

        // MUST BE LAST
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
                // 1. machine finishes product -> push downstream
                Machine.sendDownstream(),
                // 2. conveyors receive msg ->
                Conveyor.receiveProductForQueue()
                // 3. machine flags readiness to upstream conveyor
                // 4. conveyor pushes product to downstream machine
        );
        if (getGlobals().systemFinished) { // triggered if all empty
            System.exit(0);
        }
    }
}
