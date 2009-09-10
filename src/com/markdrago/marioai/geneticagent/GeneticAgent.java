package com.markdrago.marioai.geneticagent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.ai.BasicAIAgent;
import ch.idsia.ai.Evolvable;
import ch.idsia.mario.environments.Environment;
import wox.serial.Easy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;

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
        Easy.save(this.node, "node.xml");
        
        reset();
    }

    public void reset()
    {
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
    

}
