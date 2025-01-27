package org.example.models.challenge_ben;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.Random;

public class Trader extends Agent<trading_challenge_ben.Globals> {

    static Random random = new Random();

    @Variable
    public double tradingThresh = random.nextGaussian(); // had to change to public so it works (BS)

    public static Action<Trader> processInformation() {
        return Action.create(Trader.class, trader -> {

            double informationSignal = trader.getGlobals().informationSignal;

            if (Math.abs(informationSignal) > trader.tradingThresh) {
                if (informationSignal > 0) {
                    trader.buy();
                } else {
                    trader.sell();
                }
            }
        });
    }

    public static Action<Trader> updateThreshold() {
        return Action.create(Trader.class, trader -> {
            double updateFrequency = trader.getGlobals().updateFrequency;
            if (random.nextDouble() <= updateFrequency) {
                trader.tradingThresh = trader.getMessageOfType(Messages.PriceChange.class).getBody();
            }
        });
    }

    private void buy() {
        getLongAccumulator("buys").add(1);
        getLinks(Links.TraderMktLink.class).send(Messages.BuyOrderPlaced.class);
    }

    private void sell() {
        getLongAccumulator("sells").add(1);
        getLinks(Links.TraderMktLink.class).send(Messages.SellOrderPlaced.class);
    }

}
