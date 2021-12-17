package org.example.models.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;

import java.util.LinkedList;

public class Conveyor extends Agent<Globals> {
    private static final Logger logger = LoggerFactory.getLogger("org.example.models.factory");
    // define vars
    // private LinkedList<Product> queue = new LinkedList<>();

    /**
     * Initialize conveyor queue with given number of products. Only call on first step
     * @param numProducts how many products to put into conveyor
     */
    public static  Action<Conveyor> initializeProducts(int numProducts) {
        return Action.create(Conveyor.class, currConveyor -> {
            logger.info("curr conv links size ="+currConveyor.getLinks().size());
            for (int i=0; i<numProducts; i++) {
                //currConveyor.getLinks(Links.LinkMachineToConveyor.class).get(0).queue.addLast(new Product());
                //currConveyor.getLongAccumulator("queueLength").add(1);
            }
            logger.info("Created "+currConveyor.getLinks(Links.LinkMachineToConveyor.class).get(0).queue.size()+" products");
        });
    }
}
