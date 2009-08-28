package ch.idsia.scenarios;

import ch.idsia.ai.agents.ai.*;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.icegic.sergiolopez.AdaptiveAgent;
import ch.idsia.ai.agents.icegic.perez.Perez;
import ch.idsia.ai.agents.icegic.glenn.AIwesome;
import ch.idsia.ai.agents.icegic.michal.TutchekAgent;
import ch.idsia.ai.agents.icegic.peterlawford.SlowAgent;
import ch.idsia.ai.agents.icegic.robin.AStarAgent;
import ch.idsia.ai.agents.icegic.rafael.RjAgent;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.Evaluator;
import ch.idsia.utils.StatisticalSummary;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstName_at_idsia_dot_ch
 * Date: May 7, 2009
 * Time: 4:35:08 PM
 * Package: ch.idsia
 */

/*
For "quick and dirty" plays you can adjust the default parameters in ParameterContainer class, but we strongly encourage
you to use the API proposed. Because if in the first case you are mostlikely the only person who had become resposible for
the stability of the entire system, in the second case you can rely on our direct support as soon as possible. And(!)
If you encounter any trouble with using API proposed, please, e-mail us {sergey, julian} @ idsia . ch immediately. Because if
anybody encounters any trouble that implies some other person to encounter the same trouble and we cannot effort that.
Thank you for your kind assistance and productive collaboration!
Sergey Karakovskiy and Julian Togelius.
 */

public class MainRun 
{
    final static int numberOfTrials = 10;

    public static void main(String[] args) {
        CmdLineOptions cmdLineOptions = new CmdLineOptions(args);
        EvaluationOptions evaluationOptions = cmdLineOptions;  // if none options mentioned, all defalults are used.
        createNativeAgentsPool(cmdLineOptions);
//        score (cmdLineOptions.getAgent(), cmdLineOptions.getLevelRandSeed(), cmdLineOptions);

        Evaluator evaluator = new Evaluator(evaluationOptions);
        List<EvaluationInfo> evaluationSummary = evaluator.evaluate();
//        LOGGER.save("log.txt");

        if (cmdLineOptions.isExitProgramWhenFinished())
            System.exit(0);
    }

    private static boolean calledBefore = false;
    public static void createNativeAgentsPool(CmdLineOptions cmdLineOptions)
    {
        if (!calledBefore)
        {
            // Create an Agent here or mention the set of agents you want to be available for the framework.
            // All created agents by now are used here.
            // They can be accessed by just setting the commandline property -ag to the name of desired agent.
            calledBefore = true;
            new ForwardAgent();
            new HumanKeyboardAgent();
            new RandomAgent();
            new ForwardJumpingAgent();
            new SimpleMLPAgent();
//            new ServerAgent(cmdLineOptions.getServerAgentPort(), cmdLineOptions.isServerAgentEnabled());
            new ScaredAgent();
            new ScaredSpeedyAgent();
//            new Perez();
//            new AdaptiveAgent();
//            new AIwesome();
//            new TutchekAgent();
//            new SlowAgent();
            new AStarAgent();
//            new RjAgent();
        }
    }


    public static void score(Agent agent, int startingSeed, CmdLineOptions cmdLineOptions) {
        TimingAgent controller = new TimingAgent (agent);
        RegisterableAgent.registerAgent (controller);
//        EvaluationOptions options = new CmdLineOptions(new String[0]);
        EvaluationOptions options = cmdLineOptions;

        options.setMaxAttempts(1);
//        options.setVisualization(false);
//        options.setMaxFPS(true);
        System.out.println("Scoring controller " + agent.getName() + " with starting seed " + startingSeed);

        double competitionScore = 0;

        competitionScore += testConfig (controller, options, startingSeed, 0, false);
        competitionScore += testConfig (controller, options, startingSeed, 3, false);
        competitionScore += testConfig (controller, options, startingSeed, 5, false);
        competitionScore += testConfig (controller, options, startingSeed, 10, false);
        System.out.println("Competition score: " + competitionScore);
    }

    public static double testConfig (TimingAgent controller, EvaluationOptions options, int seed, int level, boolean paused) {
        options.setLevelDifficulty(level);
        options.setPauseWorld(paused);
        StatisticalSummary ss = test (controller, options, seed);
        double averageTimeTaken = controller.averageTimeTaken();
        System.out.printf("Difficulty %d score %.4f (avg time %.4f)\n",
                level, ss.mean(), averageTimeTaken);
        if (averageTimeTaken > 40) {
            System.out.println("Maximum allowed average time is 40 ms per time step.\n" +
                    "Controller disqualified");
            System.exit (0);
        }
        return ss.mean();
    }

    public static StatisticalSummary test (Agent controller, EvaluationOptions options, int seed) {
        StatisticalSummary ss = new StatisticalSummary ();
        for (int i = 0; i < numberOfTrials; i++) {
            options.setLevelRandSeed(seed + i);
            controller.reset();
            options.setAgent(controller);
            Evaluator evaluator = new Evaluator (options);
            EvaluationInfo result = evaluator.evaluate().get(0);
            ss.add (result.computeDistancePassed());
        }
        return ss;
    }
}
