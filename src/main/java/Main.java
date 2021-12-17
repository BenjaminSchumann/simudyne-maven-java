import org.example.models.factory.Factory;
import org.example.models.factory.Globals;
import simudyne.core.exec.runner.ModelRunner;
import simudyne.core.exec.runner.RunnerBackend;
import simudyne.core.exec.runner.definition.BatchDefinitionsBuilder;
import simudyne.nexus.Server;

import java.util.Arrays;

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

    /*
    try {
      if (true) { // bypass console
        RunnerBackend runnerBackend = RunnerBackend.create();
        ModelRunner modelRunner = runnerBackend.forModel(Factory.class);
        BatchDefinitionsBuilder runDefinitionBuilder =
                BatchDefinitionsBuilder.create()
                        .forRuns(1) // a required field, must be greater than 0.
                        .forTicks(183); // a required field, must be greater than 0.
        modelRunner.forRunDefinitionBuilder(runDefinitionBuilder);
        // To run the model and wait for it to complete
        modelRunner.run();
      }
    }
    catch (RuntimeException e) {
      System.out.println(Arrays.toString(e.getStackTrace()));
      e.printStackTrace();
  }
  */
  }
}
