package org.example.models.factory;

import com.google.errorprone.annotations.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Constant;
import simudyne.core.annotations.Variable;

import javax.crypto.Mac;
import java.util.LinkedList;

public class Conveyor extends Agent<Globals> {
    private static final Logger logger = LoggerFactory.getLogger("org.example.models.factory");
    /**
     * FIFO queue of all products currently queuing in this conveyor
     */
    private LinkedList<Product> queue = new LinkedList<>();
    /**
     * Which machine feeds this conveyor? Null if this is the initial conveyor in the system
     */
    public Machine machineUpstream;
    /**
     * machine pulling products from this conveyor when it is ready
     */
    public Machine machineDownstream;
    @Constant
    String name; // loaded from csv

    @Variable
    public int queueLength = 0;

    /**
     * Initialize conveyor queue with given number of products. Only call on first step
     * @param numProducts how many products to put into conveyor
     */
    public static  Action<Conveyor> initializeProducts(int numProducts) {
        return Action.create(Conveyor.class, currConveyor -> {
            for (int i=0; i<numProducts; i++) {
                currConveyor.queue.addLast(new Product());
                currConveyor.getLongAccumulator("queueLength").add(1);
                currConveyor.queueLength ++;
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
                    currConveyor.queueLength --;
                    // send oldest product to downstream machine
                    currConveyor.getLinks(Links.Link_ConveyorToDownstreamMachine.class).
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
