package org.example.models.challenge_ben;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.HashMap;

public class MomentumTrader extends Agent<trading_challenge_ben.Globals> {

    private static final Logger logger = LoggerFactory.getLogger("org.example.models.challenge_ben");

    public HashMap<Long, Double> pricesHistorical;

    @Variable(name = "Short-term moving average market price")
    public double movingAverage_ShortTerm;

    @Variable(name = "Long-term moving average market price")
    public double movingAverage_LongTerm;

    public MomentumTrader() { // used over default (hidden) constructor
        pricesHistorical= new HashMap<>();
    }

    public void storeMarketPriceLocal() {
        double currentPrice = getMessageOfType(Messages.Price.class).getBody();
        long currentTick = getContext().getTick();
        pricesHistorical.put(currentTick, currentPrice);
        // calculate short-term moving averages
        if (pricesHistorical.size() >= getGlobals().lockback_short) { // enough entries?
            double sumPriorPrices = 0;
            int counter = 0;
            for (long i=pricesHistorical.size()-1; i>pricesHistorical.size()-getGlobals().lockback_short; i--) {
                sumPriorPrices += pricesHistorical.get(i-1);
                counter++;
            }
            movingAverage_ShortTerm = sumPriorPrices / (double)counter;
        }
        // calculate long-term moving averages
        if (pricesHistorical.size() >= getGlobals().lockback_long) { // enough entries?
            double sumPriorPrices = 0;
            int counter = 0;
            for (long i=pricesHistorical.size()-1; i>pricesHistorical.size()-getGlobals().lockback_long; i--) {
                sumPriorPrices += pricesHistorical.get(i-1);
                counter++;
            }

            movingAverage_LongTerm = sumPriorPrices / (double)counter;
        }
        logger.trace("tick="+getContext().getTick()+
                ", price today="+currentPrice+
                ", moveAve_st="+movingAverage_ShortTerm+"" +
                ", moveAve_lt="+movingAverage_LongTerm);

    }

    public static Action<MomentumTrader> storeMarketPrice() {
        // calls local (non-static) functions for each mom trader
        return Action.create(MomentumTrader.class, momTrader -> momTrader.storeMarketPriceLocal());
    }

    public static Action<MomentumTrader> processInformation() {
        return Action.create(MomentumTrader.class, momTrader -> {
            // only engage in trading if enough LT info
            if (momTrader.getContext().getTick() > momTrader.getGlobals().lockback_long) {
                if (momTrader.getGlobals().probMomTrade > momTrader.getPrng().getRandom().nextDouble()) { // todo replace with global 0..1 probTrading
                    if (momTrader.movingAverage_ShortTerm < momTrader.movingAverage_LongTerm) {
                        momTrader.buy();
                    } else {
                        momTrader.sell();
                    }
                }
            }
        });
    }

    private void buy() {
        getLongAccumulator("buys").add(1);
        getLinks(Links.MomentumTraderMktLink.class).send(Messages.BuyOrderPlaced.class);
    }

    private void sell() {
        getLongAccumulator("sells").add(1);
        getLinks(Links.MomentumTraderMktLink.class).send(Messages.SellOrderPlaced.class);
    }
}
