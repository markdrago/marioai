package com.markdrago.marioai.geneticagent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.ai.BasicAIAgent;
import ch.idsia.mario.environments.Environment;
import wox.serial.Easy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * User: Mark Drago
 * Date: Aug 26, 2009
 * Time: 08:14:00 AM
 * Package: ch.idsia.ai.agents.ai;
 */

public class GeneticAgent extends BasicAIAgent implements Agent, Breedable {

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
        while (this.node == null || !this.node.tree_contains_action_node()) {
        	this.node = node_factory.get_random_agent();
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
    
    /* methods for Breedable interface */
    public Breedable getNewInstance() {
    	return this;
    }
    
    public void mutate() {
    }
    
    public Breedable copy() {
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

        return (Breedable)cp;
    }
    
    public void breedWith(Breedable spouse) {
    }
    
    public void execute_node_tree(Node node, Environment observation) {
    	this.envholder.set_environment(observation);
    	this.node.execute_node();
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
}
