import org.example.models.factory.Factory;
import simudyne.nexus.Server;

public class Main {
  public static void main(String[] args) {
    Server.register("Factory floor", Factory.class);
    /*
    Server.register("Trading Model", TradingModel.class);
    Server.register("Trading challenge", trading_challenge_ben.class);
    Server.register("Mortgage Model", MortgageModel.class);
    Server.register("Credit Card Model", CreditCardModel.class);
    Server.register("Continuous Double Auction Model", CDAModel.class);
    Server.register("Volatility Model", VolatilityModel.class);
    Server.register("Chain Bankruptcy Model", TokyoModel.class);
    Server.register("S.I.R. Model", SimudyneSIR.class);
    Server.register("Tumor Growth Model", TumorGrowthModel.class);
    Server.register("Schelling Segregation Model", SchellingModel.class);
    Server.register("Forest Fire Model", ForestFireModel.class);
    Server.register("Gai-Kapadia Model", GaiKapadiaModel.class);
    */
    Server.run(args);
  }
}
