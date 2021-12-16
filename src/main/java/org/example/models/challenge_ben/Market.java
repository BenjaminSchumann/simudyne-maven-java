package org.example.models.challenge_ben;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

public class Market extends Agent<trading_challenge_ben.Globals> {

    @Variable
    public double price;

    public static Action<Market> calcPriceImpact() {
        return Action.create(Market.class, market -> {

            int buys = market.getMessagesOfType(Messages.BuyOrderPlaced.class).size();
            int sells = market.getMessagesOfType(Messages.SellOrderPlaced.class).size();

            int netDemand = buys - sells;

            if (netDemand == 0) {
                market.getLinks(Links.MktTraderLink.class).send(Messages.PriceChange.class, 0.0);
            } else {
                long nbTraders = market.getGlobals().nbTraders;
                double lambda = market.getGlobals().lambda;
                double priceChange = (netDemand / (double) nbTraders) / lambda;
                market.price += priceChange;
                market.getDoubleAccumulator("price").add(market.price);
                market.getLinks(Links.MktTraderLink.class).send(Messages.PriceChange.class, priceChange);
            }
        });
    }
}