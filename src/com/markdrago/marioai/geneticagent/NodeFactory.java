package com.markdrago.marioai.geneticagent;

import java.util.ArrayList;
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
}