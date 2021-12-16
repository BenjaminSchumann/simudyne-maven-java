package org.example.models.challenge_ben;

import simudyne.core.abm.AgentBasedModel;
import simudyne.core.abm.GlobalState;
import simudyne.core.abm.Group;
import simudyne.core.abm.Split;
import simudyne.core.annotations.Constant;
import simudyne.core.annotations.Input;
import simudyne.core.annotations.ModelSettings;
import simudyne.core.rng.SeededRandom;


@ModelSettings(macroStep = 60)
public class trading_challenge_ben extends AgentBasedModel<trading_challenge_ben.Globals> {

    public SeededRandom rng = SeededRandom.create(42);

    public static final class Globals extends GlobalState {
        @Input(name = "Update Frequency")
        public double updateFrequency = 0.01;

        @Constant(name = "Number of Traders") // so it is visible on setup (BS)
        public long nbTraders = 200;

        @Input(name = "Number of momentum Traders") // so it is visible on setup (BS)
        public long nbMomentumTraders = 50;

        @Input(name = "Lambda")
        public double lambda = 10;

        @Input(name = "Volatility of Information Signal")
        public double volatilityInfo = 0.001;

        public double informationSignal;

        @Input(name = "short-term moving average lock-back period (ticks)")
        public long lockback_short = 7;

        @Input(name = "long-term moving average lock-back period (ticks)")
        public long lockback_long = 21;

        @Input(name = "prob for trading for mom-traders (0..1)")
        public double probMomTrade = 0.5;

    }

    { // empty class initializer, should be picked up by overridden "init"
        createLongAccumulator("buys", "Number of buy orders");
        createLongAccumulator("sells", "Number of sell orders");
        createDoubleAccumulator("price", "Price");

        registerAgentTypes(Market.class, Trader.class, MomentumTrader.class);
        registerLinkTypes(Links.MktTraderLink.class, Links.TraderMktLink.class, Links.MomentumTraderMktLink.class, Links.MktMomentumTraderLink.class);

    }

    @Override
    public void setup() {
        getGlobals().informationSignal = rng.gaussian(0.0, getGlobals().volatilityInfo).sample();

        Group<Trader> traderGroup =
                generateGroup(Trader.class, getGlobals().nbTraders);
        Group<Market> marketGroup =
                generateGroup(Market.class, 1, market -> {
                    market.price = 4.0;
                });
        Group<MomentumTrader> momentumTraderGroup =
                generateGroup(MomentumTrader.class, getGlobals().nbMomentumTraders);

        traderGroup.fullyConnected(marketGroup, Links.TraderMktLink.class);
        marketGroup.fullyConnected(traderGroup, Links.MktTraderLink.class);
        // link momentum traders as well
        momentumTraderGroup.fullyConnected(marketGroup, Links.MomentumTraderMktLink.class);
        marketGroup.fullyConnected(momentumTraderGroup, Links.MktMomentumTraderLink.class);

        super.setup();
    }

    @Override
    public void step() {
        super.step();

        getGlobals().informationSignal = rng.gaussian(0.0, getGlobals().volatilityInfo).sample();

        run(
                Split.create(
                        Trader.processInformation(),
                        MomentumTrader.processInformation()),
                Market.calcPriceImpact(),
                Split.create(
                        Trader.updateThreshold(),
                        MomentumTrader.storeMarketPrice())

        );
    }
}
