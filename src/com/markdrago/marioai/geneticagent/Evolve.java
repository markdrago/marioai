package com.markdrago.marioai.geneticagent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 4, 2009
 * Time: 4:33:25 PM
 */
public class Evolve {

    final static int generations = 250;
    final static int population_size = 32;
    
    public static void main(String[] args) {
        EvaluationOptions options = new CmdLineOptions(args);
        options.setNumberOfTrials(1);
        options.setPauseWorld(true);
        
        Breedable initial = new GeneticAgent();
        
       	System.out.println("New Evolve phase started.");

       	options.setLevelDifficulty(3);
       	options.setAgent((Agent)initial);
       	options.setMaxFPS(true);
       	options.setVisualization(false);

       	Task task = new ProgressTask(options);
       	GenePool pool = new GenePool(task, initial, population_size);

       	for (int gen = 0; gen < generations; gen++) {
       		pool.nextGeneration();
       	}
                
/*
                double bestResult = es.getBestFitnesses()[0];
//                LOGGER.println("Generation " + gen + " best " + bestResult, LOGGER.VERBOSE_MODE.INFO);
                System.out.println("Generation " + gen + " best " + bestResult);
                options.setVisualization(gen % 5 == 0 || bestResult > 4000);
                options.setMaxFPS(true);
                Agent a = (Agent) es.getBests()[0];
                a.setName(((Agent)initial).getName() + df.format(gen));
//                AgentsPool.setCurrentAgent(a);
                bestAgents.add(a);
                double result = task.evaluate(a)[0];
//                LOGGER.println("trying: " + result, LOGGER.VERBOSE_MODE.INFO);
                options.setVisualization(false);
                options.setMaxFPS(true);
                Easy.save (es.getBests()[0], "evolved.xml");
                if (result > 4000)
                    break; // Go to next difficulty.
*/
        
        /*// TODO: log dir / log dump dir option
        // TODO: reduce number of different
        // TODO: -fq 30, -ld 1:15, 8 
        //LOGGER.println("Saving bests... ", LOGGER.VERBOSE_MODE.INFO);

        options.setVisualization(true);
        int i = 0;
        for (Agent bestAgent : bestAgents) {
            Easy.save(bestAgent, "bestAgent" +  df.format(i++) + ".xml");
        }

        LOGGER.println("Saved! Press return key to continue...", LOGGER.VERBOSE_MODE.INFO);
        try {System.in.read();        } catch (IOException e) {            e.printStackTrace();        }

//        for (Agent bestAgent : bestAgents) {
//            task.evaluate(bestAgent);
//        }


        LOGGER.save("log.txt");*/
        System.exit(0);
    }
}
