package org.example.models.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Constant;

public class Machine extends Agent<Globals> {
    private static final Logger logger = LoggerFactory.getLogger("org.example.models.factory");
    /**
     * product currently processed at this machine. Null if none here
     */
    private Product currentProduct = null;
    @Constant
    String name; // loaded from csv

    // ACTIONS

    /**
     * Initialize machine by filling its 1 slot with a product
     */
    public static  Action<Machine> initializeProduct() {
        return Action.create(Machine.class, currMachine  -> {
            currMachine.currentProduct = new Product();
            //logger.info("Filled machine with 1 product");
        });
    }
    /**
     * Finish current product (if there is one) and try to pull next product from upstream conveyor
     */
    public static  Action<Machine> sendDownstream() {
        return Action.create(Machine.class, currMachine  -> {
            System.out.println("Machine "+currMachine.getID()+" starts sendDownstream on tick "+currMachine.getContext().getTick());
            // send to downstream conveyor (if there is one)
            if (currMachine.getLinks(Links.Link_MachineToDownstreamConveyor.class).size() > 0) {
                // machine has downstream conveyor: send product there
                currMachine.getLinks(Links.Link_MachineToDownstreamConveyor.class).
                        send(Messages.Msg_ProductForConveyor.class, (message, link) -> {
                            message.product = currMachine.currentProduct;
                        });
            } else { // this is the last machine, nothing downstream
                // count global #products done
                currMachine.getLongAccumulator("numProdsDone").add(1); // count globally
                 // destroy product todo how can we destroy Product agent for good
            }
            currMachine.currentProduct = null;
        });
    }
    /**
     * tell upstream machine about empty slot -> ready for next product
     */
    public static  Action<Machine> flagUpstream() {
        return Action.create(Machine.class, currMachine  -> {
            System.out.println("Machine "+currMachine.getID()+" starts flagUpstream on tick "+currMachine.getContext().getTick());
            if (currMachine.getLinks(Links.Link_MachineToUpstreamConveyor.class).size() > 0) {
                currMachine.getLinks(Links.Link_MachineToUpstreamConveyor.class).
                        send(Messages.Msg_ReadyForProduct.class);
            }
        });
    }
    /**
     * Add any products sent via message from upstream machine to your queue
     */
    public static  Action<Machine> receiveProductForWork() {
        return Action.create(Machine.class, currMachine -> {
            System.out.println("Machine "+currMachine.getID()+" starts receiveProductForWork on tick "+currMachine.getContext().getTick());
            if (currMachine.getMessageOfType(Messages.Msg_ProductForMachine.class) != null) { // got a msg actually
                Product arrivingProduct = currMachine.getMessageOfType(Messages.Msg_ProductForMachine.class).product;
                currMachine.currentProduct = arrivingProduct;
            }
        });
    }
}
