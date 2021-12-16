package org.example.models.factory;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;

public class Product extends Agent<Globals> {
    // define vars

    public static Action<Product> someProductAction() {
        return Action.create(Product.class, currProduct -> {
           // do action code here with currProduct
            System.out.println("product did some action on tick "+currProduct.getContext().getTick());
        });
    }

    // local functions
}
