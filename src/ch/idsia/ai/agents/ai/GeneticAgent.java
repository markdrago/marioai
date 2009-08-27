package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import java.util.List;
import java.util.ArrayList;
import java.lang.Number;

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
    
    public class AddNode extends BinaryOpNode {
    	public int execute(Number op1, Number op2) {
    		return op1.intValue() + op2.intValue();
    	}
    }
    
    public abstract class BinaryOpNode implements Node {
    	abstract int execute(Number op1, Number op2);
    	public int get_num_arguments() { return 2; }
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		lst.add(NodeArgType.NUMERIC);
    		lst.add(NodeArgType.NUMERIC);
    		return lst;
    	}
    	public NodeArgType get_response_type() { return NodeArgType.INT; }
    }
    
    public interface Node {
    	int get_num_arguments();
    	List<NodeArgType> get_argument_types();
    	NodeArgType get_response_type();
    	String toString();
    }

    public enum NodeArgType {
    	INT,
    	FLOAT,
    	STRING,
    	BOOLEAN,
    	NUMERIC;
    
    	boolean is_numeric() {
    		return (this == INT || this == FLOAT || this == BOOLEAN || this == NUMERIC);
    	}
    }
}
