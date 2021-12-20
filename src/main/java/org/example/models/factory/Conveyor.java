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
                double cycleTime_ticks = currConveyor.getPrng().uniform(
                        currConveyor.getGlobals().cycleTimeMin_ticks,
                        currConveyor.getGlobals().cycleTimeMax_ticks).sample();
                currConveyor.queue.addLast(new Product(cycleTime_ticks));
                currConveyor.queueLength ++;
            }
        });
    }
    /**
     * Load new products into conveyor queue
     * Call each tick to decide if new product(s) should enter the system
     * Only executed if this is the first conveyor of the system
     */
    public static  Action<Conveyor> addNewProducts() {
        return Action.create(Conveyor.class, currConveyor -> {
            if (currConveyor.getID() == 0) { // only add at 1st conveyor
                double rateNewProductsPerMS = currConveyor.getGlobals().rateNewProducts / (60.*1000.); // from "per min" to "per ms"
                int numNewProductsThisTick = (int)Math.floor(rateNewProductsPerMS);
                double remainder = (rateNewProductsPerMS-numNewProductsThisTick);
                double randomValue = currConveyor.getPrng().uniform(0, 1).sample();
                if (randomValue < remainder) {
                    // add remainder only if prob checked (so "1.25" would be 1 each tick and 2 each 4 ticks)
                    numNewProductsThisTick++;
                    //logger.info("Conveyor "+currConveyor.getID()+" created "+numNewProductsThisTick+" new products on tick "+currConveyor.getContext().getTick());
                }
                for (int i=0; i<numNewProductsThisTick; i++) {
                    double cycleTime_ticks = currConveyor.getPrng().uniform(
                            currConveyor.getGlobals().cycleTimeMin_ticks,
                            currConveyor.getGlobals().cycleTimeMax_ticks).sample();
                    currConveyor.queue.addLast(new Product(cycleTime_ticks));
                    currConveyor.queueLength ++;
                }

            }
        });
    }
    /**
     * Add any products sent via message from upstream machine to your queue
     */
    public static  Action<Conveyor> receiveProductAndPushOutOldest() {
        return Action.create(Conveyor.class, currConveyor -> {
            // System.out.println("Conveyor "+currConveyor.getID()+" starts receiveProductForQueue on tick "+currConveyor.getContext().getTick());
            if (currConveyor.getMessageOfType(Messages.Msg_ProductForConveyor.class) != null) { // got a msg actually
                Product arrivingProduct = currConveyor.getMessageOfType(Messages.Msg_ProductForConveyor.class).product;
                currConveyor.queue.addLast(arrivingProduct);
                currConveyor.queueLength ++;
            }
            if (currConveyor.getMessageOfType(Messages.Msg_ReadyForProduct.class) != null) { // got a msg actually
                if (currConveyor.queue.size() > 0) { // got more
                    Product oldestProduct = currConveyor.queue.removeFirst();
                    currConveyor.queueLength --;
                    // send oldest product to downstream machine
                    currConveyor.getLinks(Links.Link_ConveyorToDownstreamMachine.class).
                            send(Messages.Msg_ProductForMachine.class, (message, link) -> {
                                message.product = oldestProduct;
                            });
                } else { // no more products to send, do nothing
                }
            }
        });
    }

}
