package ch.idsia.scenarios;

import ch.idsia.ai.agents.ai.*;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.icegic.robin.AStarAgent;
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

public class MainRun 
{
    final static int numberOfTrials = 10;

    public static void main(String[] args) {
        CmdLineOptions cmdLineOptions = new CmdLineOptions(args);
        EvaluationOptions evaluationOptions = cmdLineOptions;  // if none options mentioned, all defalults are used.
        createAgentsPool();
//        score (cmdLineOptions.getAgent(), cmdLineOptions.getLevelRandSeed(), cmdLineOptions);

        scoreAllAgents(cmdLineOptions);
        Evaluator evaluator = new Evaluator(evaluationOptions);
        List<EvaluationInfo> evaluationSummary = evaluator.evaluate();
//        LOGGER.save("log.txt");

        if (cmdLineOptions.isExitProgramWhenFinished())
            System.exit(0);
    }

    private static boolean calledBefore = false;
    public static void createAgentsPool()
    {
        if (!calledBefore)
        {
            // Create an Agent here or mention the set of agents you want to be available for the framework.
            // All created agents by now are used here.
            // They can be accessed by just setting the commandline property -ag to the name of desired agent.
            calledBefore = true;
            //addAgentToThePool
            AgentsPool.addAgent(new ForwardAgent());
            AgentsPool.addAgent(new ForwardJumpingAgent());
            AgentsPool.addAgent(new RandomAgent());
//            AgentsPool.addAgent(new HumanKeyboardAgent());
            AgentsPool.addAgent(new SimpleMLPAgent());
            AgentsPool.addAgent(new ScaredAgent());
            AgentsPool.addAgent(new ScaredSpeedyAgent());
//            new Perez();
//            new AdaptiveAgent();
//            new AIwesome();
//            new TutchekAgent();
//            new SlowAgent();
            AgentsPool.addAgent(new AStarAgent());
//            new RjAgent();
        }
    }

    public static void scoreAllAgents(CmdLineOptions cmdLineOptions)
    {
        for (Agent agent : AgentsPool.getAgentsCollection())
        {
            score(agent, 0, cmdLineOptions);
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
