package org.example.models.challenge_ben;

import simudyne.core.AgentContext;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.HashMap;

public class Market extends Agent<trading_challenge_ben.Globals> {

    @Variable(name = "Current market price")
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
                // send price change to normal traders
                market.getLinks(Links.MktTraderLink.class).send(Messages.PriceChange.class, priceChange);
                // send price to momentum traders
                market.getLinks(Links.MktMomentumTraderLink.class).send(Messages.Price.class, market.price);
            }
        });
    }
}