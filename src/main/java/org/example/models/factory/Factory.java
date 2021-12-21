package org.example.models.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simudyne.core.abm.AgentBasedModel;
import simudyne.core.abm.Group;
import simudyne.core.abm.Sequence;
import simudyne.core.abm.Split;
import simudyne.core.annotations.Input;
import simudyne.core.annotations.ModelSettings;
import simudyne.core.annotations.Variable;
import simudyne.core.data.CSVSource;

@ModelSettings(timeUnit = "MILLIS")
public class Factory extends AgentBasedModel<Globals> {
    private static final Logger logger = LoggerFactory.getLogger("org.example.models.factory");
    @Input
    public String A;
    @Input
    public String B;

    @Override
    public void init() {
        // create global outputs
        createLongAccumulator("numProdsDone", "number of products done");

        // load all agents
        registerAgentTypes( Conveyor.class, Machine.class);
        // load all links
        registerLinkTypes(  Links.Link_MachineToDownstreamConveyor.class,
                            Links.Link_MachineToUpstreamConveyor.class,
                            Links.Link_ConveyorToDownstreamMachine.class,
                            Links.Link_ConveyorToUpstreamMachine.class);
    }

    @Override
    public void setup() {
        // create agents: do conveyors first so their Simudyne IDs are same as in csv files
        CSVSource conveyorSource = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\conveyors.csv");
        Group<Conveyor> conveyors = loadGroup(Conveyor.class, conveyorSource);
        CSVSource machinesSource = new CSVSource("C:\\Users\\User\\Documents\\My GitHub repositories\\Simudyne repo\\simudyne-maven-java\\data\\machines.csv");
        Group<Machine> machines = loadGroup(Machine.class, machinesSource);

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
    public void step() { // Each step is 1ms
        super.step(); // FIRST: do Simudyne stepping
        // seed model with initial products (only on first step)
        firstStep(
                Split.create(
                        Machine.initializeProduct(),
                        Conveyor.initializeProducts(getGlobals().numInitialProducts)
                )
        );
        if (getContext().getTick() % 1000 == 0) {
            logger.info("curr tick "+getContext().getTick());
        }
        // sequence is crucial here, consider Splits
        run(
                // 1. machine finishes product -> push downstream AND flag upstream that you have space (must be in 1 func)
                Machine.pushDownstreamAndFlagUpstream(),
                // 2. conveyors: get products from upstream machine. Push out oldest if downstream is free (must be in 1 func)
                Conveyor.receiveProductAndPushOutOldest(),
                // 3. machine receives product from upstream for next tick
                Machine.receiveProductForWork()
        );
        run( // ensure to run this always (even if no msg passed)
                Conveyor.addNewProducts()
        );
        lastStep(
                // add actions for final outputs
        );
    }
    @Override
    public void done(){
        super.done();
        logger.info("finished");
    }

}
