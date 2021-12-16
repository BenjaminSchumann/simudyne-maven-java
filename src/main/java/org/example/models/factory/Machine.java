package org.example.models.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;

public class Machine extends Agent<Globals> {
    private static final Logger logger = LoggerFactory.getLogger("org.example.models.factory");
    // define vars

    /**
     * product currently processed at this machine. Null if none here
     */
    private Product currentProduct;

    public static Action<Machine> someMachineAction() {
        return Action.create(Machine.class, currMachine -> {
           // do action code here with currMachine
            // you can call local functions using currMachine.myFunc()
            System.out.println("machine did some action on tick "+currMachine.getContext().getTick());
        });
    }

    // local functions

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
