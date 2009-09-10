package com.markdrago.marioai.geneticagent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	
    public Node get_random_agent() {
    	return this.get_random_tree(null, 0);
    }
    
    public Node get_random_tree(Node parent, int level) {
    	ArrayList<Node> nodes = new ArrayList<Node>();
    	List<Type> arguments;
    	Type arg;
    	Node child = null;
    	
    	if (parent == null) {
    		/* this iteration just makes a parent */
    		parent = this.get_random_node_weighted();
    		nodes.add(parent);
    	} else {
    		/* make all of the children that this node needs */
    		arguments = parent.get_argument_types();
    		for (int i = 0; i < arguments.size(); i++) {
    			arg = arguments.get(i);
    			
    			if (level < 5) {
    				if (arg == NodeType.get_type("boolean")) {
    					child = this.get_boolean_node_weighted();
    				} else if (arg == NodeType.get_type("int")) {
    					child = this.get_integer_node_weighted();
    				}
    			} else {
    				if (arg == NodeType.get_type("boolean")) {
    					child = this.get_boolean_leaf_node();
    				} else if (arg == NodeType.get_type("int")) {
    					child = this.get_integer_leaf_node();
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
    		this.get_random_tree(loopchild, level + 1);
    	}
    	
    	return parent;
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
}