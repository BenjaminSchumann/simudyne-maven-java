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
    public static Action<Machine> someMachineAction() {
        return Action.create(Machine.class, currMachine -> {
           // do action code here with currMachine
            // you can call local functions using currMachine.myFunc()
            //logger.info("machine did some action on tick "+currMachine.getContext().getTick());
        });
    }
    /**
     * Initialize machine by filling its 1 slot with a product
     */
    public static  Action<Machine> initializeProduct() {
        return Action.create(Machine.class, currMachine  -> {
            currMachine.currentProduct = new Product();
            //logger.info("Filled machine with 1 product");
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
