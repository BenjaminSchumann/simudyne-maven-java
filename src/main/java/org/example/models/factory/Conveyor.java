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

    /**
     * Initialize conveyor queue with given number of products. Only call on first step
     * @param numProducts how many products to put into conveyor
     */
    public static  Action<Conveyor> initializeProducts(int numProducts) {
        return Action.create(Conveyor.class, currConveyor -> {
            for (int i=0; i<numProducts; i++) {
                currConveyor.queue.addLast(new Product());
                currConveyor.getLongAccumulator("queueLength").add(1);
            }
        });
    }
    /**
     * Sends oldest product to connected machine, if requested
     */
    public static  Action<Conveyor> sendProduct() {
        return Action.create(Conveyor.class, currConveyor -> {
            if (currConveyor.getMessageOfType(Messages.Msg_ReadyForProduct.class) != null) { // got a msg actually
                if (currConveyor.queue.size() > 0) { // got more
                    Product oldestProduct = currConveyor.queue.removeFirst();
                    currConveyor.getLongAccumulator("queueLength").add(-1);
                    // send oldest product to downstream machine
                    currConveyor.getLinks(Links.Link_ConveyorToMachine.class).
                            send(Messages.Msg_ProductForMachine.class, (message, link) -> {
                                message.product = oldestProduct;
                            });
                } else {
                    System.out.println("Conveyor has queue size="+currConveyor.queue.size()+" at tick "+currConveyor.getContext().getTick());
                    currConveyor.getGlobals().systemFinished = true; // trigger model end
                }
            }
        });
    }

}
