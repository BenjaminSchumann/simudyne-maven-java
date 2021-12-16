package org.example.models.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;

import java.util.LinkedList;

public class Conveyor extends Agent<Globals> {
    private static final Logger logger = LoggerFactory.getLogger("org.example.models.factory");
    // define vars
    private LinkedList<Product> queue = new LinkedList<>();

    public static Action<Conveyor> someConveyorActio() {
        return Action.create(Conveyor.class, currConveyor -> {
           // do action code here with CurrConveyor
            System.out.println("conveyor did some action on tick "+currConveyor.getContext().getTick());
        });
    }

    /**
     * Initialize conveyor queue with given number of products. Only call on first step
     * @param numProducts how many products to put into conveyor
     */
    public static  Action<Conveyor> initializeProducts(int numProducts) {
        return Action.create(Conveyor.class, currConveyor -> {
            for (int i=0; i<numProducts; i++) {
                currConveyor.queue.addLast(new Product());
            }
            // logger.info("Created "+currConveyor.queue.size()+" products");
        });

    }
}
