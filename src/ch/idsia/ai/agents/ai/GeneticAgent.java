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
import java.lang.reflect.Type;
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
        
    	NodeFactory node_factory;
    	
        action = new boolean[Environment.numberOfButtons];

        this.actionholder = new ActionHolder();
        this.actionholder.set_action(action);
        this.envholder = new EnvironmentHolder();
        this.node = null;
        
        node_factory = new NodeFactory(this.envholder, this.actionholder);
        
        /* guarantee we have at least one action node in the tree */
        while (this.node == null || !tree_contains_action_node(node)) {
        	this.node = get_random_agent(node_factory);
        }
        
        System.out.println(get_dot_for_tree(this.node));
        
        reset();
    }

    public void reset()
    {

        
        /* this.node = get_forward_agent(this.envholder, this.actionholder); */
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
    
    public Object execute_node(Node node) {
    	List<Node> children;
    	ArrayList<Object> child_results;
    	Node child;
    	int child_count;
    	
    	/* get results for all child nodes */
    	child_count = node.get_num_children();
    	child_results = new ArrayList<Object>();
    	if (child_count > 0) {
    		children = node.get_children();
    		for (int i = 0; i < child_count; i++) {
    			child = children.get(i);
    			child_results.add(this.execute_node(child));
    		}
    	}
    	
    	/* return result for this node */
		return node.execute(child_results);
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
    
    public Node get_random_agent(NodeFactory node_factory) {
    	return get_random_tree(node_factory, null, 0);
    }
    
    public Node get_random_tree(NodeFactory node_factory, Node parent, int level) {
    	ArrayList<Node> nodes = new ArrayList<Node>();
    	List<Type> arguments;
    	Type arg;
    	Node child = null;
    	
    	if (parent == null) {
    		/* this iteration just makes a parent */
    		parent = node_factory.get_random_node_weighted();
    		nodes.add(parent);
    	} else {
    		/* make all of the children that this node needs */
    		arguments = parent.get_argument_types();
    		for (int i = 0; i < arguments.size(); i++) {
    			arg = arguments.get(i);
    			
    			if (level < 5) {
    				if (arg == NodeType.get_type("boolean")) {
    					child = node_factory.get_boolean_node_weighted();
    				} else if (arg == NodeType.get_type("int")) {
    					child = node_factory.get_integer_node_weighted();
    				}
    			} else {
    				if (arg == NodeType.get_type("boolean")) {
    					child = node_factory.get_boolean_leaf_node();
    				} else if (arg == NodeType.get_type("int")) {
    					child = node_factory.get_integer_leaf_node();
    				}
    			}

    			/* add this child to the list of nodes we should make children for */
    			nodes.add(child);
    			
        		/* attach this node to the parent node */
    			parent.set_child(i, child);
    		}
    	}
    	
    	/* get random children for all of these nodes */
    	for (Node loopchild: nodes) {
    		get_random_tree(node_factory, loopchild, level + 1);
    	}
    	
    	return parent;
    }
    
    public boolean tree_contains_action_node(Node node) {
    	/* if this node is an action node, return true */
    	if (node instanceof ActionNode)
    		return true;
    	
    	/* if any of the trees beneath this one contain an action node return true */
    	for (Node child: node.get_children()) {
    		if (tree_contains_action_node(child))
    			return true;
    	}
    	
    	return false;
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
    
    public Node get_jumping_agent(EnvironmentHolder envholder, ActionHolder actionholder) {
    	Node obs, num1, num2, jump, mayjump, and;
    	
    	num1 = new StaticIntNode(12);
    	num2 = new StaticIntNode(11);
    	obs = new EnemyObservationNode(envholder);
    	obs.set_child(0, num1);
    	obs.set_child(1, num2);
    	
    	mayjump = new MayJumpNode(envholder);
    	and = new AndNode();
    	and.set_child(0, obs);
    	and.set_child(1, mayjump);
    	
    	jump = new JumpNode(actionholder);
    	jump.set_child(0, and);
    	
    	return jump;
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
    	
    	public Object execute(List<Object> args) {
    		return new Integer(
    				((Integer)args.get(0)).intValue() +
    				((Integer)args.get(1)).intValue());
    	}
    }
    
    public class SubtractNode extends BinaryOpNode {
    	public SubtractNode() {
    		super();
    		this.name = "subtract";
    	}
    	
    	public Object execute(List<Object> args) {
    		return new Integer(java.lang.Math.abs(
    				((Integer)args.get(0)).intValue() -
    				((Integer)args.get(1)).intValue()));
    	}
    }
    
    public class MultiplyNode extends BinaryOpNode {
    	public MultiplyNode() {
    		super();
    		this.name = "multiply";
    	}
    	
    	public Object execute(List<Object> args) {
    		return new Integer(
    				((Integer)args.get(0)).intValue() *
    				((Integer)args.get(1)).intValue());
    	}
    }
    
    public class DivideNode extends BinaryOpNode {
    	public DivideNode() {
    		super();
    		this.name = "divide";
    	}
    	
    	public Object execute(List<Object> args) {
    		int op1 = ((Integer)args.get(0)).intValue();
    		int op2 = ((Integer)args.get(1)).intValue();
    		
    		if (op2 == 0) { return new Integer(0); }
    		
    		return new Integer(op1 / op2);
    	}
    }
    
    public class SpeedNode extends ActionNode {
    	public SpeedNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "speed";
    	}
    	
    	public Object execute(List<Object> args) {
    		this.action_holder.button_action(Mario.KEY_SPEED, ((Boolean)args.get(0)).booleanValue());
    		return args.get(0);
    	}
    }
    
    public class LeftNode extends ActionNode {
    	public LeftNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "left";
    	}
    	
    	public Object execute(List<Object> args) {
    		this.action_holder.button_action(Mario.KEY_LEFT, ((Boolean)args.get(0)).booleanValue());
    		this.action_holder.button_action(Mario.KEY_RIGHT, false);
    		return args.get(0);
    	}
    }
    
    public class RightNode extends ActionNode {
    	public RightNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "right";
    	}
    	
    	public Object execute(List<Object> args) {
    		this.action_holder.button_action(Mario.KEY_RIGHT, ((Boolean)args.get(0)).booleanValue());
    		this.action_holder.button_action(Mario.KEY_LEFT, false);
    		return args.get(0);
    	}
    }
    
    public class JumpNode extends ActionNode {
    	public JumpNode(ActionHolder action_holder) {
    		super(action_holder);
    		this.name = "jump";
    	}

    	public Object execute(List<Object> args) {
    		this.action_holder.button_action(Mario.KEY_JUMP, ((Boolean)args.get(0)).booleanValue());
    		return args.get(0);
    	}
    }
    
    public abstract class ActionNode extends Node {
    	ActionHolder action_holder;
    	
    	public ActionNode(ActionHolder action_holder) {
    		super();
    		this.num_arguments = 1;
    		this.action_holder = action_holder;
    	}
    	
    	public List<Type> get_argument_types() {
    		ArrayList<Type> lst = new ArrayList<Type>();
    		lst.add(NodeType.get_type("boolean"));
    		return lst;
    	}
    	
    	public Type get_response_type() { return NodeType.get_type("boolean"); }
    }
    
    public class OnGroundNode extends EnvironmentNode {
    	public OnGroundNode(EnvironmentHolder envholder) {
    		super(envholder);
    		this.name = "on_ground";
    	}
    	
    	public Object execute(List<Object> args) {
    		Environment env = envholder.get_environment();
    		return (Object) new Boolean(env.isMarioOnGround());
    	}
    }
    
    public class MayJumpNode extends EnvironmentNode {
    	public MayJumpNode(EnvironmentHolder envholder) {
    		super(envholder);
    		this.name = "may_jump";
    	}
    	
    	public Object execute(List<Object> args) {
    		Environment env = envholder.get_environment();
    		return (Object) new Boolean(env.mayMarioJump());
    	}
    }
    
    public abstract class EnvironmentNode extends Node {
    	EnvironmentHolder envholder;
    	
    	public EnvironmentNode(EnvironmentHolder envholder) {
    		super();
    		this.num_arguments = 0;
    		this.envholder = envholder;
    	}
    	
    	public List<Type> get_argument_types() {
    		ArrayList<Type> lst = new ArrayList<Type>();
    		return lst;
    	}
    	
    	public Type get_response_type() { return NodeType.get_type("boolean"); }
    }
    
    public class LevelObservationNode extends ObservationNode {
    	public LevelObservationNode(EnvironmentHolder envholder) {
    		super(envholder);
    		this.name = "level_observation";
    	}
    	
    	public Object execute(List<Object> args) {
    		int x, y, xmax, ymax;
    		
    		Environment env = envholder.get_environment();
    		
    		xmax = Environment.HalfObsWidth * 2;
    		ymax = Environment.HalfObsHeight * 2;
    		
    		x = ((Integer)args.get(0)).intValue() % xmax;
    		y = ((Integer)args.get(1)).intValue() % ymax;
    		
    		byte[][] enemyMap = env.getLevelSceneObservation();
    		if (enemyMap[y][x] != 0)
    			return (Object) new Boolean(true);
			return (Object) new Boolean(false);
    	}
    }
    
    public class EnemyObservationNode extends ObservationNode {
    	public EnemyObservationNode(EnvironmentHolder envholder) {
    		super(envholder);
    		this.name = "enemy_observation";
    	}
    	
    	public Object execute(List<Object> args) {
    		int x, y, xmax, ymax;
    		
    		Environment env = envholder.get_environment();
    		
    		xmax = Environment.HalfObsWidth * 2;
    		ymax = Environment.HalfObsHeight * 2;
    		
    		x = ((Integer)args.get(0)).intValue() % xmax;
    		y = ((Integer)args.get(1)).intValue() % ymax;
    		
    		byte[][] enemyMap = env.getEnemiesObservation();
    		if (enemyMap[y][x] != 0)
    			return (Object) new Boolean(true);
			return (Object) new Boolean(false);
    	}
    }
    
    public abstract class ObservationNode extends Node {
    	EnvironmentHolder envholder;
    	
    	public ObservationNode(EnvironmentHolder envholder) {
    		super();
    		this.num_arguments = 2;
    		this.envholder = envholder;
    	}
    	
    	public List<Type> get_argument_types() {
    		ArrayList<Type> lst = new ArrayList<Type>();
    		lst.add(NodeType.get_type("int"));
    		lst.add(NodeType.get_type("int"));
    		return lst;
    	}
    	
    	public Type get_response_type() { return NodeType.get_type("boolean"); }
    }
    
    public class GreaterThanNode extends BinaryConditionalNode {
    	public GreaterThanNode() {
    		super();
    		this.name = "greater_than";
    	}
    	
    	public Object execute(List<Object> args) {
    		return new Boolean(
    				((Integer)args.get(0)).intValue() >
    				((Integer)args.get(1)).intValue());
    	}
    }
    
    public class EqualNode extends BinaryConditionalNode {
    	public EqualNode() {
    		super();
    		this.name = "equal";
    	}
    	
    	public Object execute(List<Object> args) {
    		return new Boolean(
    				((Integer)args.get(0)).intValue() ==
    				((Integer)args.get(1)).intValue());
    	}
    }
    
    public class NotNode extends Node {
    	public NotNode() {
    		super();
    		this.num_arguments = 1;
    		this.name = "not";
    	}
    	
    	public List<Type> get_argument_types() {
    		ArrayList<Type> lst = new ArrayList<Type>();
    		lst.add(NodeType.get_type("boolean"));
    		return lst;
    	}
    	
    	public Type get_response_type() { return NodeType.get_type("boolean"); }
    	
    	public Object execute(List<Object> args) {
    		return (Object) new Boolean( ! ((Boolean)args.get(0)).booleanValue());
    	}
    }

    public class AndNode extends BinaryBooleanNode {
    	public AndNode() {
    		super();
    		this.num_arguments = 2;
    		this.name = "and";
    	}
    	
    	public Object execute(List<Object> args) {
    		return (Object) new Boolean(
    				((Boolean)args.get(0)).booleanValue() &&
    				((Boolean)args.get(1)).booleanValue());
    	}
    }
    
    public class OrNode extends BinaryBooleanNode {
    	public OrNode() {
    		super();
    		this.num_arguments = 2;
    		this.name = "or";
    	}
    	
    	public Object execute(List<Object> args) {
    		return (Object) new Boolean(
    				((Boolean)args.get(0)).booleanValue() ||
    				((Boolean)args.get(1)).booleanValue());
    	}
    }
    
    public abstract class BinaryConditionalNode extends Node {
    	public BinaryConditionalNode() {
    		super();
    		this.num_arguments = 2;
    	}
    	
    	public List<Type> get_argument_types() {
    		ArrayList<Type> lst = new ArrayList<Type>();
    		lst.add(NodeType.get_type("int"));
    		lst.add(NodeType.get_type("int"));
    		return lst;
    	}
    	
    	public Type get_response_type() { return NodeType.get_type("boolean"); }
    }
    
    public abstract class BinaryBooleanNode extends Node {
    	public BinaryBooleanNode() {
    		super();
    		this.num_arguments = 2;
    	}
    	
    	public List<Type> get_argument_types() {
    		ArrayList<Type> lst = new ArrayList<Type>();
    		lst.add(NodeType.get_type("boolean"));
    		lst.add(NodeType.get_type("boolean"));
    		return lst;
    	}
    	
    	public Type get_response_type() { return NodeType.get_type("boolean"); }
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
    	
    	public Object execute(List<Object> args) {
    		return (Object) new Boolean(this.value);
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
    	
    	public List<Type> get_argument_types() {
    		ArrayList<Type> lst = new ArrayList<Type>();
    		return lst;
    	}
    	
    	public Type get_response_type() { return NodeType.get_type("boolean"); }
    }
    
    public class StaticIntNode extends IntNode {
    	int value;
    	
    	public StaticIntNode() {
    		super();
    		this.set_random_value();
    		this.set_name_by_value();
    	}
    	
    	public StaticIntNode(int value) {
    		super();
    		this.set_value(value);
    		this.set_name_by_value();
    	}
    	
    	public void set_value(int value) {
    		this.value = value;
    	}
    	
    	public void set_random_value() {
    		Random rnd = new Random();
    		this.set_value(rnd.nextInt(22));
    	}
    	
    	private void set_name_by_value() {
    		this.name = String.format("static_int %d", this.value);    		
    	}
    	
    	public Object execute(List<Object> args) {
    		return (Object) new Integer(this.value);
    	}
    }
    
    public abstract class IntNode extends Node {
    	public IntNode() {
    		super();
    		this.num_arguments = 0;
    	}
    	
    	public List<Type> get_argument_types() {
    		ArrayList<Type> lst = new ArrayList<Type>();
    		return lst;
    	}
    	
    	public Type get_response_type() { return NodeType.get_type("int"); }
    }
    
    public abstract class BinaryOpNode extends Node {
    	public BinaryOpNode() {
    		super();
    		this.num_arguments = 2;
    	}
    	
    	public List<Type> get_argument_types() {
    		ArrayList<Type> lst = new ArrayList<Type>();
    		lst.add(NodeType.get_type("int"));
    		lst.add(NodeType.get_type("int"));
    		return lst;
    	}
    	
    	public Type get_response_type() { return NodeType.get_type("int"); }
    }
    
    public static class NodeType {
    	public static Type get_type(String nickname) {
    		Type result = null;
    		
    		try {
    			if (nickname == "int") {
    				result = Class.forName("java.lang.Integer");
    			} else if (nickname == "boolean") {
    				result = Class.forName("java.lang.Boolean");
    			}
    		} catch(Exception e) {
    			System.out.println(e.getMessage());
    		}
    		
    		if (result == null) {
    			System.out.println("Unable to find class for nickname: " + nickname);
    		}
    		
    		return result;
    	}
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
    	
    	public abstract List<Type> get_argument_types();
    	public abstract Type get_response_type();

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
    	
    	public Object execute(List<Object> args) {
    		return null;
    	}
    }
    
    public class NodeFactory {
    	ArrayList<String> boolean_nodes, int_nodes, leaf_boolean_nodes;
    	ArrayList<String> leaf_int_nodes, all_nodes, action_nodes;
    	ArrayList<String> observation_nodes, leaf_observation_nodes;
    	ActionHolder action_holder;
    	EnvironmentHolder env_holder;
    	Random rnd;
    	
    	public NodeFactory(EnvironmentHolder env_holder, ActionHolder action_holder) {
    		boolean_nodes = new ArrayList<String>();
    		int_nodes = new ArrayList<String>();
    		leaf_boolean_nodes = new ArrayList<String>();
    		leaf_int_nodes = new ArrayList<String>();
    		all_nodes = new ArrayList<String>();
    		action_nodes = new ArrayList<String>();
    		observation_nodes = new ArrayList<String>();
    		leaf_observation_nodes = new ArrayList<String>();
    		this.action_holder = action_holder;
    		this.env_holder = env_holder;
    		rnd = new Random();

    		/* all boolean nodes */
    		action_nodes.add("SpeedNode");
    		action_nodes.add("LeftNode");
    		action_nodes.add("RightNode");
    		action_nodes.add("JumpNode");
    		leaf_observation_nodes.add("OnGroundNode");
    		leaf_observation_nodes.add("MayJumpNode");
    		observation_nodes.add("LevelObservationNode");
    		observation_nodes.add("EnemyObservationNode");
    		leaf_boolean_nodes.add("StaticBooleanNode");
    		/*
    		boolean_nodes.add("GreaterThanNode");
    		boolean_nodes.add("EqualNode");
    		*/
    		boolean_nodes.add("NotNode");
    		boolean_nodes.add("AndNode");
    		boolean_nodes.add("OrNode");

    		/* collect all booleans in to boolean_nodes */
    		boolean_nodes.addAll(action_nodes);
    		boolean_nodes.addAll(leaf_observation_nodes);
    		boolean_nodes.addAll(observation_nodes);
    		boolean_nodes.addAll(leaf_boolean_nodes);
    		
    		/* leaf obs are both leaf and obs */
    		leaf_boolean_nodes.addAll(leaf_observation_nodes);
    		observation_nodes.addAll(leaf_observation_nodes);
    		
    		/* all int nodes */
    		/*
    		int_nodes.add("AddNode");
    		int_nodes.add("SubtractNode");
    		int_nodes.add("MultiplyNode");
    		int_nodes.add("DivideNode");
    		*/
    		leaf_int_nodes.add("StaticIntNode");
    		
    		/* add leaf int nodes to int_nodes */
    		int_nodes.addAll(leaf_int_nodes);
    		
    		/* combine boolean and int nodes in to all_nodes */
    		all_nodes.addAll(boolean_nodes);
    		all_nodes.addAll(int_nodes);
    	}
    	
    	public Node get_random_node() {
    		return get_random_node_from_array(all_nodes);
    	}
    	
    	public Node get_random_node_weighted() {
    		double chance = rnd.nextDouble();
    		
    		if (chance < 0.25) {
    			return get_integer_node_weighted();
    		} else {
    			return get_boolean_node_weighted();
    		}
    	}
    	
    	public Node get_boolean_node_weighted() {
    		double chance = rnd.nextDouble();
    		
    		if (chance < 0.33) {
    			return get_action_node();
    		} else if (chance < 0.66) {
    			return get_observation_node();
    		} else {
    			return get_boolean_node();
    		}
    	}
    	
    	public Node get_integer_node_weighted() {
    		double chance = rnd.nextDouble();
    		
    		if (chance < 0.5) {
    			return get_integer_leaf_node();
    		}
    		return get_integer_node();
    	}
    	
    	public Node get_integer_node() {
    		return get_random_node_from_array(int_nodes);
    	}
    	
    	public Node get_integer_leaf_node() {
    		return get_random_node_from_array(leaf_int_nodes);
    	}
    	
    	public Node get_boolean_node() {
    		return get_random_node_from_array(boolean_nodes);
    	}
    	
    	public Node get_boolean_leaf_node() {
    		return get_random_node_from_array(leaf_boolean_nodes);
    	}
    	
    	public Node get_action_node() {
    		return get_random_node_from_array(action_nodes);
    	}
    	
    	public Node get_observation_node() {
    		return get_random_node_from_array(observation_nodes);
    	}
    	
    	private Node get_random_node_from_array(ArrayList<String> namearray) {
    		int choice;
    		String name;
    		
    		choice = rnd.nextInt(namearray.size());
    		name = namearray.get(choice);
			
			return get_node_with_name(name);    		
    	}
    	
    	private Node get_node_with_name(String nodename) {
    		if (nodename.equals("StaticBooleanNode")) {
    			return new StaticBooleanNode();
    		} else if (nodename.equals("StaticIntNode")) {
    			return new StaticIntNode();
    		} else if (nodename.equals("AddNode")) {
    		    return new AddNode();
    		} else if (nodename.equals("SubtractNode")) {
    		    return new SubtractNode();
    		} else if (nodename.equals("MultiplyNode")) {
    		    return new MultiplyNode();
    		} else if (nodename.equals("DivideNode")) {
    		    return new DivideNode();
    		} else if (nodename.equals("SpeedNode")) {
    		    return new SpeedNode(this.action_holder);
    		} else if (nodename.equals("LeftNode")) {
    		    return new LeftNode(this.action_holder);
    		} else if (nodename.equals("RightNode")) {
    		    return new RightNode(this.action_holder);
    		} else if (nodename.equals("JumpNode")) {
    		    return new JumpNode(this.action_holder);
    		} else if (nodename.equals("OnGroundNode")) {
    		    return new OnGroundNode(this.env_holder);
    		} else if (nodename.equals("MayJumpNode")) {
    		    return new MayJumpNode(this.env_holder);
    		} else if (nodename.equals("LevelObservationNode")) {
    		    return new LevelObservationNode(this.env_holder);
    		} else if (nodename.equals("EnemyObservationNode")) {
    		    return new EnemyObservationNode(this.env_holder);
    		} else if (nodename.equals("GreaterThanNode")) {
    		    return new GreaterThanNode();
    		} else if (nodename.equals("EqualNode")) {
    		    return new EqualNode();
    		} else if (nodename.equals("NotNode")) {
    		    return new NotNode();
    		} else if (nodename.equals("AndNode")) {
    		    return new AndNode();
    		} else if (nodename.equals("OrNode")) {
    		    return new OrNode();
    		} else {
    			return null;
    		}
    	}
    }
}
