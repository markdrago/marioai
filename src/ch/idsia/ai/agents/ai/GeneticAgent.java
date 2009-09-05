package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.ai.BasicAIAgent;
import ch.idsia.ai.Evolvable;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Mark Drago
 * Date: Aug 26, 2009
 * Time: 08:14:00 AM
 * Package: ch.idsia.ai.agents.ai;
 */

public class GeneticAgent extends BasicAIAgent implements Agent, Evolvable {

	Node node;
	ActionHolder actionholder;
	EnvironmentHolder envholder;

    public GeneticAgent()
    {
        super("GeneticAgent");
        reset();
    }

    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];

        this.actionholder = new ActionHolder();
        this.actionholder.set_action(action);
        this.envholder = new EnvironmentHolder();
        
        this.node = get_forward_agent(this.envholder, this.actionholder);
    }

    public boolean[] getAction(Environment observation)
    {
    	this.execute_node_tree(this.node, observation);
    	
        return this.actionholder.get_action();
    }
    
    /* methods for Evolvable interface */
    public Evolvable getNewInstance() {
    	return this;
    }
    
    public void mutate() {
    }
    
    public Evolvable copy() {
    	Object cp = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bos.toByteArray()));
            cp = in.readObject();
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }

        return (Evolvable)cp;
    }
    
    public void execute_node_tree(Node node, Environment observation) {
    	this.envholder.set_environment(observation);
    	this.execute_node(node);
    }
    
    public NodeArg execute_node(Node node) {
    	List<Node> children;
    	ArrayList<NodeArg> child_results;
    	Node child;
    	NodeArg result = null;
    	int child_count;
    	
    	/* get results for all child nodes */
    	child_count = node.get_num_children();
    	child_results = new ArrayList<NodeArg>();
    	if (child_count > 0) {
    		children = node.get_children();
    		for (int i = 0; i < child_count; i++) {
    			child = children.get(i);
    			child_results.add(this.execute_node(child));
    		}
    	}
    	
    	/* get result for this node */
    	if (child_count == 0) {
			result = node.execute();
    	} else if (child_count == 1) {
    		result = node.execute(child_results.get(0));
    	} else if (child_count == 2) {
    		result = node.execute(child_results.get(0), child_results.get(1));
    	}
    	
    	return result;
    }
    
    public String get_dot_for_tree(Node node) {
    	StringBuffer result = new StringBuffer("digraph geneticagent {\n");
    	get_dot_for_node(result, node, 0);
    	result.append("}");
    	return result.toString();
    }
    
    /* this function adds all of the dot-notation needed to draw the graph of
     * its tree, and returns the highest numbered nodenum used in the process*/
    public int get_dot_for_node(StringBuffer result, Node node, int nodenum) {
    	List<Node> children;
    	Node child;
    	int child_count, max_used, child_num;
    	String nodename, nodelabel, childname;
    	
    	/* create name and label for this node */
    	nodename = String.format("node%d", nodenum);
    	nodelabel = node.toString();
    	
    	/* add name for this node to output */
    	result.append(nodename + " [label=\"" + nodelabel + "\"]\n");
    	
    	System.out.println(result);
    	
    	/* add links to children and get results for children */
    	child_count = node.get_num_children();
    	max_used = nodenum;
    	if (child_count > 0) {
    		children = node.get_children();
    		for (int i = 0; i < child_count; i++) {
    			child = children.get(i);
    			child_num = max_used + 1;
    			
    			/* draw links from this node to children */
    			childname = String.format("node%d", child_num);
    			result.append(nodename + " -> " + childname + "\n"); 
    			
    			/* add results from the child tree */
    			max_used = get_dot_for_node(result, child, child_num);
    		}
    	}
    	
    	return max_used;
    }
    
    /* manually create some test trees */
    public Node get_forward_agent(EnvironmentHolder envholder, ActionHolder actionholder) {
    	Node onground, not, or, mayjump, jump, speed, right, truenode, and;
    	
    	/* this tree is assembled children-first for ease of assembly */
    	
    	/* not on ground */
    	onground = new OnGroundNode(envholder);
    	not = new NotNode();
    	not.set_child(0, onground);
    	
    	/* or may jump */
    	mayjump = new MayJumpNode(envholder);
    	or = new OrNode();
    	or.set_child(0, mayjump);
    	or.set_child(1, not);
    	
    	/* jump & speed */
    	jump = new JumpNode(actionholder);
    	speed = new SpeedNode(actionholder);
    	jump.set_child(0, or);
    	speed.set_child(0, jump);
    	
    	/* separate branch for true & right */
    	truenode = new StaticBooleanNode(true);
    	right = new RightNode(actionholder);
    	right.set_child(0, truenode);
    	
    	/* combine right and speed */
    	and = new AndNode();
    	and.set_child(0, right);
    	and.set_child(1, speed);
    	
    	return and;
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
    	
    	public NodeArg execute(NodeArg op1, NodeArg op2) {
    		return new NodeArg(op1.get_int_value() + op2.get_int_value());
    	}
    }
    
    public class SubtractNode extends BinaryOpNode {
    	public SubtractNode() {
    		super();
    		this.name = "subtract";
    	}
    	
    	public NodeArg execute(NodeArg op1, NodeArg op2) {
    		return new NodeArg(java.lang.Math.abs(op1.get_int_value() - op2.get_int_value()));
    	}
    }
    
    public class MultiplyNode extends BinaryOpNode {
    	public MultiplyNode() {
    		super();
    		this.name = "multiply";
    	}
    	
    	public NodeArg execute(NodeArg op1, NodeArg op2) {
    		return new NodeArg(op1.get_int_value() * op2.get_int_value());
    	}
    }
    
    public class DivideNode extends BinaryOpNode {
    	public DivideNode() {
    		super();
    		this.name = "divide";
    	}
    	
    	public NodeArg execute(NodeArg op1, NodeArg op2) {
    		if (op2.get_int_value() == 0) return new NodeArg(0);
    		return new NodeArg(op1.get_int_value() / op2.get_int_value());
    	}
    }
    
    public class SpeedNode extends ActionNode {
    	public SpeedNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "speed";
    	}
    	
    	public NodeArg execute(NodeArg state) {
    		this.action_holder.button_action(Mario.KEY_SPEED, state.get_bool_value());
    		return state;
    	}
    }
    
    public class LeftNode extends ActionNode {
    	public LeftNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "left";
    	}
    	
    	public NodeArg execute(NodeArg state) {
    		this.action_holder.button_action(Mario.KEY_LEFT, state.get_bool_value());
    		this.action_holder.button_action(Mario.KEY_RIGHT, false);
    		return state;
    	}
    }
    
    public class RightNode extends ActionNode {
    	public RightNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "right";
    	}
    	
    	public NodeArg execute(NodeArg state) {
    		this.action_holder.button_action(Mario.KEY_RIGHT, state.get_bool_value());
    		this.action_holder.button_action(Mario.KEY_LEFT, false);
    		return state;
    	}
    }
    
    public class JumpNode extends ActionNode {
    	public JumpNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "jump";
    	}
    	
    	public NodeArg execute(NodeArg state) {
    		this.action_holder.button_action(Mario.KEY_JUMP, state.get_bool_value());
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
    	
    	public abstract NodeArg execute(NodeArg state);
    }
    
    public class OnGroundNode extends EnvironmentNode {
    	public OnGroundNode(EnvironmentHolder envholder) {
    		super(envholder);
    		this.name = "on_ground";
    	}
    	
    	public NodeArg execute() {
    		Environment env = envholder.get_environment();
    		return new NodeArg(env.isMarioOnGround());
    	}
    }
    
    public class MayJumpNode extends EnvironmentNode {
    	public MayJumpNode(EnvironmentHolder envholder) {
    		super(envholder);
    		this.name = "may_jump";
    	}
    	
    	public NodeArg execute() {
    		Environment env = envholder.get_environment();
    		return new NodeArg(env.mayMarioJump());
    	}
    }
    
    public abstract class EnvironmentNode extends Node {
    	EnvironmentHolder envholder;
    	
    	public EnvironmentNode(EnvironmentHolder envholder) {
    		super();
    		this.num_arguments = 0;
    		this.envholder = envholder;
    	}
    	
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		return lst;
    	}
    	
    	public NodeArgType get_response_type() { return NodeArgType.BOOLEAN; }
    	
    	public abstract NodeArg execute();
    }
    
    public class LevelObservationNode extends ObservationNode {
    	public LevelObservationNode(EnvironmentHolder envholder) {
    		super(envholder);
    		this.name = "level_observation";
    	}
    	
    	public NodeArg execute(NodeArg x, NodeArg y) {
    		Environment env = envholder.get_environment();
    		
    		byte[][] levelMap = env.getLevelSceneObservation();
    		if (levelMap[y.get_int_value()][x.get_int_value()] != 0)
    			return new NodeArg(true);
    		return new NodeArg(false);
    	}
    }
    
    public class EnemyObservationNode extends ObservationNode {
    	public EnemyObservationNode(EnvironmentHolder envholder) {
    		super(envholder);
    		this.name = "enemy_observation";
    	}
    	
    	public NodeArg execute(NodeArg x, NodeArg y) {
    		Environment env = envholder.get_environment();
    		
    		byte[][] enemyMap = env.getEnemiesObservation();
    		if (enemyMap[y.get_int_value()][x.get_int_value()] != 0)
    			return new NodeArg(true);
    		return new NodeArg(false);
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
    	
    	public abstract NodeArg execute(NodeArg x, NodeArg y);
    }
    
    public class GreaterThanNode extends BinaryConditionalNode {
    	public GreaterThanNode() {
    		super();
    		this.name = "greater_than";
    	}
    	
    	public NodeArg execute(NodeArg x, NodeArg y) {
    		return new NodeArg(x.get_int_value() > y.get_int_value());
    	}
    }
    
    public class EqualNode extends BinaryConditionalNode {
    	public EqualNode() {
    		super();
    		this.name = "equal";
    	}
    	
    	public NodeArg execute(NodeArg x, NodeArg y) {
    		return new NodeArg(x.get_int_value() == y.get_int_value());
    	}
    }
    
    public class NotNode extends Node {
    	public NotNode() {
    		super();
    		this.num_arguments = 1;
    		this.name = "not";
    	}
    	
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		lst.add(NodeArgType.BOOLEAN);
    		return lst;
    	}
    	
    	public NodeArgType get_response_type() { return NodeArgType.BOOLEAN; }
    	
    	public NodeArg execute(NodeArg state) {
    		return new NodeArg(!state.get_bool_value());
    	}
    	
    }

    public class AndNode extends BinaryBooleanNode {
    	public AndNode() {
    		super();
    		this.num_arguments = 2;
    		this.name = "and";
    	}
    	
    	public NodeArg execute(NodeArg op1, NodeArg op2) {
    		return new NodeArg(op1.get_bool_value() && op2.get_bool_value());
    	}
    }
    
    public class OrNode extends BinaryBooleanNode {
    	public OrNode() {
    		super();
    		this.num_arguments = 2;
    		this.name = "or";
    	}
    	
    	public NodeArg execute(NodeArg op1, NodeArg op2) {
    		return new NodeArg((op1.get_bool_value() || op2.get_bool_value()));
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
    	
    	public abstract NodeArg execute(NodeArg x, NodeArg y);
    }
    
    public abstract class BinaryBooleanNode extends Node {
    	public BinaryBooleanNode() {
    		super();
    		this.num_arguments = 2;
    	}
    	
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		lst.add(NodeArgType.BOOLEAN);
    		lst.add(NodeArgType.BOOLEAN);
    		return lst;
    	}
    	
    	public NodeArgType get_response_type() { return NodeArgType.BOOLEAN; }
    	
    	public abstract NodeArg execute(NodeArg op1, NodeArg op2);
    }
    
    public class StaticBooleanNode extends BooleanNode {
    	boolean value;
    	
    	public StaticBooleanNode() {
    		super();
    		this.num_arguments = 0;
    		this.set_random_value();
    		this.set_name_by_value();
    	}
    	
    	public StaticBooleanNode(boolean value) {
    		this.num_arguments = 0;
    		this.set_value(value);
    		this.set_name_by_value();
    	}
    	
    	private void set_name_by_value() {
    		this.name = (this.value == true) ? "true" : "false";    		
    	}
    	
    	public NodeArg execute() {
    		return new NodeArg(this.value);
    	}
    	
    	public void set_value(boolean value) {
    		this.value = value;
    	}
    	
    	public void set_random_value() {
    		Random rnd = new Random();
    		this.set_value(rnd.nextBoolean());
    	}
    }
    
    public abstract class BooleanNode extends Node {
    	public BooleanNode() {
    		super();
    		this.num_arguments = 0;
    	}
    	
    	public abstract NodeArg execute();
    	
    	public List<NodeArgType> get_argument_types() {
    		ArrayList<NodeArgType> lst = new ArrayList<NodeArgType>();
    		return lst;
    	}
    	
    	public NodeArgType get_response_type() { return NodeArgType.BOOLEAN; }
    }
    
    public class StaticIntNode extends IntNode {
    	int value;
    	
    	public StaticIntNode() {
    		super();
    		this.name = String.format("static_int %d", this.value);
    		this.set_random_value();
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
    	
    	public NodeArg execute() {
    		return new NodeArg(this.value);
    	}
    }
    
    public abstract class IntNode extends Node {
    	public IntNode() {
    		super();
    		this.num_arguments = 0;
    	}
    	
    	public abstract NodeArg execute();
    	
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
    	
    	public abstract NodeArg execute(NodeArg op1, NodeArg op2);
    	
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
    	
    	public int get_num_children() {
    		return this.children.size();
    	}

    	public List<Node> get_children() {
    		return this.children;
    	}
    	
    	public void set_child(int index, Node child) {
    		this.children.ensureCapacity(index + 1);
    		this.children.add(index, child);
    		
    		child.set_parent(this);
    	}
    	
    	public Node get_parent() {
    		return this.parent;
    	}
    	
    	private void set_parent(Node parent) {
    		this.parent = parent;
    	}
    	
    	public NodeArg execute() {
    		return null;
    	}
    	
    	public NodeArg execute(NodeArg op1) {
    		return null;
    	}
    	
    	public NodeArg execute(NodeArg op1, NodeArg op2) {
    		return null;
    	}
    }

    public class NodeArg {
    	NodeArgType type;
    	int int_value;
    	boolean bool_value;
    	
    	public NodeArg(int value) {
    		this.type = NodeArgType.INT;
    		this.int_value = value;
    	}
    	
    	public NodeArg(boolean value) {
    		this.type = NodeArgType.BOOLEAN;
    		this.bool_value = value;
    	}
    	
    	public int get_int_value() {
    		return this.int_value;
    	}
    	
    	public boolean get_bool_value() {
    		return this.bool_value;
    	}
    }
    
    public enum NodeArgType {
    	INT,
    	BOOLEAN;
    }
}
