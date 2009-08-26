package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

/**
 * User: Mark Drago
 * Date: Aug 26, 2009
 * Time: 08:14:00 AM
 * Package: ch.idsia.ai.agents.ai;
 */

public class GeneticAgent extends RegisterableAgent implements Agent {

    static final boolean superslow = false;

    public GeneticAgent()
    {
        super("GeneticAgent");
        reset();
    }

    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_SPEED] = true;
    }

    public boolean[] getAction(Environment observation)
    {
        try {Thread.sleep (39);}
        catch (Exception e){}
        action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] =  observation.mayMarioJump() || !observation.isMarioOnGround();
        return action;
    }
}
