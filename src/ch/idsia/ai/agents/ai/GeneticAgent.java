package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import java.util.List;
import java.util.ArrayList;
import java.lang.Number;
import java.util.Random;

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
    
    public class EnvironmentHolder {
    	Environment environ;
    	public void set_environment(Environment environ) {
    		this.environ = environ;
    	}
    	
    	public Environment get_environment() {
    		return this.environ;
    	}
    }
    
    public class ActionHolder {
    	boolean[] action;
    	
    	public void set_action(boolean[] action) {
    		this.action = action;
    	}
    	
    	public boolean[] get_action() {
    		return this.action;
    	}
    	
    	public void button_action(int button, boolean press) {
    		int buttonmod = button % 6;  /* 0 - 5 are valid keys */
    		this.action[buttonmod] = press;
    	}
    	
    }
    
    public class AddNode extends BinaryOpNode {
    	public AddNode() {
    		super();
    		this.name = "add";
    	}
    	
    	public int execute(int op1, int op2) {
    		return op1 + op2;
    	}
    }
    
    public class SubtractNode extends BinaryOpNode {
    	public SubtractNode() {
    		super();
    		this.name = "subtract";
    	}
    	
    	public int execute(int op1, int op2) {
    		return java.lang.Math.abs(op1 - op2);
    	}
    }
    
    public class MultiplyNode extends BinaryOpNode {
    	public MultiplyNode() {
    		super();
    		this.name = "multiply";
    	}
    	
    	public int execute(int op1, int op2) {
    		return op1 * op2;
    	}
    }
    
    public class DivideNode extends BinaryOpNode {
    	public DivideNode() {
    		super();
    		this.name = "divide";
    	}
    	
    	public int execute(int op1, int op2) {
    		if (op2 == 0) return 0;
    		return op1 / op2;
    	}
    }
    
    public class SpeedNode extends ActionNode {
    	public SpeedNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "speed";
    	}
    	
    	public boolean execute(boolean state) {
    		this.action_holder.button_action(Mario.KEY_SPEED, state);
    		return state;
    	}
    }
    
    public class LeftNode extends ActionNode {
    	public LeftNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "left";
    	}
    	
    	public boolean execute(boolean state) {
    		this.action_holder.button_action(Mario.KEY_LEFT, state);
    		this.action_holder.button_action(Mario.KEY_RIGHT, false);
    		return state;
    	}
    }
    
    public class RightNode extends ActionNode {
    	public RightNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "right";
    	}
    	
    	public boolean execute(boolean state) {
    		this.action_holder.button_action(Mario.KEY_RIGHT, state);
    		this.action_holder.button_action(Mario.KEY_LEFT, false);
    		return state;
    	}
    }
    
    public class JumpNode extends ActionNode {
    	public JumpNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "jump";
    	}
    	
    	public boolean execute(boolean state) {
    		this.action_holder.button_action(Mario.KEY_JUMP, state);
    		return state;
    	}
    }
    
    public abstract class ActionNode extends Node {
    	ActionHolder action_holder;
    	
    	public ActionNode(ActionHolder action_holder) {
    		super();
    		this.num_arguments = 1;
    		this.action_holder = action_holder;
    	}
    	
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		lst.add(NodeArgType.BOOLEAN);
    		return lst;
    	}
    	
    	public NodeArgType get_response_type() { return NodeArgType.BOOLEAN; }
    	
    	public abstract boolean execute(boolean state);
    }
    
    public class LevelObservationNode extends ObservationNode {
    	public LevelObservationNode(EnvironmentHolder envholder) {
    		super(envholder);
    		this.name = "level_observation";
    	}
    	
    	public boolean execute(Number x, Number y) {
    		Environment env = envholder.get_environment();
    		
    		byte[][] levelMap = env.getLevelSceneObservation();
    		if (levelMap[y.intValue()][x.intValue()] != 0)
    			return true;
    		return false;
    	}
    }
    
    public class EnemyObservationNode extends ObservationNode {
    	public EnemyObservationNode(EnvironmentHolder envholder) {
    		super(envholder);
    		this.name = "enemy_observation";
    	}
    	
    	public boolean execute(Number x, Number y) {
    		Environment env = envholder.get_environment();
    		
    		byte[][] enemyMap = env.getEnemiesObservation();
    		if (enemyMap[y.intValue()][x.intValue()] != 0)
    			return true;
    		return false;
    	}
    }
    
    public abstract class ObservationNode extends Node {
    	EnvironmentHolder envholder;
    	
    	public ObservationNode(EnvironmentHolder envholder) {
    		super();
    		this.num_arguments = 2;
    		this.envholder = envholder;
    	}
    	
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		lst.add(NodeArgType.INT);
    		lst.add(NodeArgType.INT);
    		return lst;
    	}
    	
    	public NodeArgType get_response_type() { return NodeArgType.BOOLEAN; }
    	
    	public abstract boolean execute(Number x, Number y);
    }
    
    public class GreaterThanNode extends BinaryConditionalNode {
    	public GreaterThanNode() {
    		super();
    		this.name = "greater_than";
    	}
    	
    	public boolean execute(int x, int y) {
    		return (x > y);
    	}
    }
    
    public class EqualNode extends BinaryConditionalNode {
    	public EqualNode() {
    		super();
    		this.name = "equal";
    	}
    	
    	public boolean execute(int x, int y) {
    		return (x == y);
    	}
    }
    
    public class NotNode extends Node {
    	public NotNode() {
    		super();
    		this.num_arguments = 1;
    	}
    	
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		lst.add(NodeArgType.BOOLEAN);
    		return lst;
    	}
    	
    	public NodeArgType get_response_type() { return NodeArgType.BOOLEAN; }
    	
    	public boolean execute(boolean state) {
    		return !state;
    	}
    	
    }
    
    public abstract class BinaryConditionalNode extends Node {
    	public BinaryConditionalNode() {
    		super();
    		this.num_arguments = 2;
    	}
    	
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		lst.add(NodeArgType.INT);
    		lst.add(NodeArgType.INT);
    		return lst;
    	}
    	
    	public NodeArgType get_response_type() { return NodeArgType.BOOLEAN; }
    	
    	public abstract boolean execute(int x, int y);
    }
    
    public class StaticIntNode extends IntNode {
    	int value;
    	
    	public StaticIntNode() {
    		super();
    		this.name = "static_int";
    	}
    	
    	public StaticIntNode(int value) {
    		super();
    		this.name = "static_int";
    		this.set_value(value);
    	}
    	
    	public void set_value(int value) {
    		this.value = value;
    	}
    	
    	public void set_random_value() {
    		Random rnd = new Random();
    		this.set_value(rnd.nextInt(100));
    	}
    	
    	public int execute() {
    		return this.value;
    	}
    }
    
    public abstract class IntNode extends Node {
    	public IntNode() {
    		super();
    		this.num_arguments = 0;
    	}
    	
    	public abstract int execute();
    	
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		return lst;
    	}
    	
    	public NodeArgType get_response_type() { return NodeArgType.INT; }
    }
    
    public abstract class BinaryOpNode extends Node {
    	public BinaryOpNode() {
    		super();
    		this.num_arguments = 2;
    	}
    	
    	public abstract int execute(int op1, int op2);
    	
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		lst.add(NodeArgType.INT);
    		lst.add(NodeArgType.INT);
    		return lst;
    	}
    	
    	public NodeArgType get_response_type() { return NodeArgType.INT; }
    }
    
    public abstract class Node {
    	ArrayList<Node> children;
    	Node parent;
    	int num_arguments;
    	String name;
    	
    	public Node() {
    		this.children = new ArrayList<Node>();
    		this.num_arguments = 0;
    		this.name = "unknown";
    	}
    	
    	public abstract List<NodeArgType> get_argument_types();
    	public abstract NodeArgType get_response_type();

    	public String toString() {
    		return this.name;
    	}
    	
    	public int get_num_arguments() { return this.num_arguments; }

    	List<Node> get_children() {
    		return this.children;
    	}
    	
    	void set_child(int index, Node child) {
    		this.children.ensureCapacity(index + 1);
    		this.children.set(index, child);
    		
    		child.set_parent(this);
    	}
    	
    	Node get_parent() {
    		return this.parent;
    	}
    	
    	void set_parent(Node parent) {
    		this.parent = parent;
    	}
    }

    public enum NodeArgType {
    	INT,
    	BOOLEAN;
    }
}
