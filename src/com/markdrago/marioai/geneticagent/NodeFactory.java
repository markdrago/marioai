package com.markdrago.marioai.geneticagent;

import java.util.ArrayList;
import java.util.Random;

public class NodeFactory {
	ArrayList<String> all_nodes, leaf_nodes, action_nodes;
	ArrayList<String> observation_nodes, leaf_observation_nodes;
	ActionHolder action_holder;
	EnvironmentHolder env_holder;
	Random rnd;
	
	public NodeFactory(EnvironmentHolder env_holder, ActionHolder action_holder) {
		all_nodes = new ArrayList<String>();
		leaf_nodes = new ArrayList<String>();
		action_nodes = new ArrayList<String>();
		observation_nodes = new ArrayList<String>();
		leaf_observation_nodes = new ArrayList<String>();
		this.action_holder = action_holder;
		this.env_holder = env_holder;
		rnd = new Random();

		action_nodes.add("SpeedNode");
		action_nodes.add("LeftNode");
		action_nodes.add("RightNode");
		action_nodes.add("JumpNode");
		leaf_observation_nodes.add("OnGroundNode");
		leaf_observation_nodes.add("MayJumpNode");
		observation_nodes.add("LevelObservationNode");
		observation_nodes.add("EnemyObservationNode");
		leaf_nodes.add("StaticBooleanNode");
		all_nodes.add("NotNode");
		all_nodes.add("AndNode");
		all_nodes.add("OrNode");

		/* collect all nodes in to all_nodes */
		all_nodes.addAll(action_nodes);
		all_nodes.addAll(leaf_observation_nodes);
		all_nodes.addAll(observation_nodes);
		all_nodes.addAll(leaf_nodes);
		
		/* leaf obs are both leaf and obs */
		leaf_nodes.addAll(leaf_observation_nodes);
		observation_nodes.addAll(leaf_observation_nodes);
	}
	
	public Node get_random_node() {
		return get_random_node_from_array(all_nodes);
	}
	
	public Node get_random_node_weighted() {
		double chance = rnd.nextDouble();
		
		if (chance < 0.33) {
			return get_action_node();
		} else if (chance < 0.66) {
			return get_observation_node();
		} else {
			return get_random_node();
		}
	}
	
	public Node get_leaf_node() {
		return get_random_node_from_array(leaf_nodes);
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
    	Node child = null;
    	
    	if (parent == null) {
    		/* this iteration just makes a parent */
    		parent = this.get_random_node_weighted();
    		nodes.add(parent);
    	} else {
    		/* make all of the children that this node needs */
    		for (int i = 0; i < parent.get_num_arguments(); i++) {
    			if (level < 5) {
   					child = this.get_random_node_weighted();
    			} else {
   					child = this.get_leaf_node();
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
    	Node obs, jump, mayjump, and;
    	
    	obs = new EnemyObservationNode(envholder, 12, 11);
    	
    	mayjump = new MayJumpNode(envholder);
    	and = new AndNode();
    	and.set_child(0, obs);
    	and.set_child(1, mayjump);
    	
    	jump = new JumpNode(actionholder);
    	jump.set_child(0, and);
    	
    	return jump;
    }
}