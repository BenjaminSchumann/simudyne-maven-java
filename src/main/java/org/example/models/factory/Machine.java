package org.example.models.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;

public class Machine extends Agent<Globals> {
    private static final Logger logger = LoggerFactory.getLogger("org.example.models.factory");
    /**
     * product currently processed at this machine. Null if none here
     */
    private Product currentProduct = null;

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
    public static  Action<Machine> finishProduct() {
        return Action.create(Machine.class, currMachine  -> {
            if (currMachine.currentProduct == null) {
                logger.error("Could not finish product as machine had none");
                //System.exit(0); // terminate process
            }
            currMachine.currentProduct = null; // destroy product todo how can we destroy Product agent for good
            currMachine.getLongAccumulator("numProdsDone").add(1); // count globally
            // tell upstream conveyor to send next product
            currMachine.getLinks(Links.Link_MachineToConveyor.class).
                    send(Messages.Msg_ReadyForProduct.class);
        });
    }
    /**
     * Prepare machine for next tick
     */
    public static  Action<Machine> prepareNextTick() {
        return Action.create(Machine.class, currMachine  -> {
            Product nextProduct = currMachine.getMessageOfType(Messages.Msg_ProductForMachine.class).getBody();
            if (nextProduct != null) {
                currMachine.startProduct(nextProduct);
            }
        });
    }

    // LOCAL FUNCTIONS
    /**
     * Start machining given product
     * @param productToStart the product to start at this machine
     */
    public void startProduct(Product productToStart) {
        if (currentProduct != null) {
            logger.error("Tried to start a product while machine still had a previous product, not possible");
            return;
        }
        currentProduct = productToStart;
    }
}
