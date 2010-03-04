package com.markdrago.marioai.geneticagent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.ai.BasicAIAgent;
import ch.idsia.mario.environments.Environment;
import wox.serial.Easy;

import java.util.Random;
import java.util.UUID;

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
	NodeFactory node_factory;
	String uuid;

    public GeneticAgent()
    {
        super("GeneticAgent");
        
        action = new boolean[Environment.numberOfButtons];

        this.actionholder = new ActionHolder();
        this.actionholder.set_action(action);
        this.envholder = new EnvironmentHolder();
        this.node = null;
        this.node_factory = new NodeFactory(this.envholder, this.actionholder);
        this.uuid = UUID.randomUUID().toString();
        
        /* guarantee we have at least one action node in the random tree */
        while (this.node == null || !this.node.tree_contains_action_node()) {
        	this.node = this.node_factory.get_random_agent();
        }
        
        //System.out.println(this.node.get_dot_for_tree());
        //Easy.save(this.node, "node.xml");
        
        reset();
    }
    
    public void reset()
    {
    }

    public boolean[] getAction(Environment observation)
    {
    	this.envholder.set_environment(observation);
    	this.node.execute_node();
        return this.actionholder.get_action();
    }

    /* methods for Breedable interface */
    public Breedable breed(Breedable parent2) {
    	Random rnd = new Random();
    	GeneticAgent child, copyfrom;
    	Node remove, add;

    	if (rnd.nextDouble() < 0.5) {
    		child = this.copy();
    		copyfrom = (GeneticAgent)parent2;
    	} else {
    		child = ((GeneticAgent)parent2).copy();
    		copyfrom = this;
    	}

    	remove = child.node.pick_random_branch();
    	add = copyfrom.node.pick_random_branch();

    	/* if remove is the head of child, just replace the child nodes with add */
    	if (remove.parent == null) {
    		child.node = add;
    	}
    	
    	return child;
    }
    
    public Breedable mutate() {
    	GeneticAgent freak = this.copy();
    	
    	freak.node = freak.node.mutate_tree(this.node_factory);
    	
    	return freak;
    }
    
    public Breedable get_random_breedable() {
    	return new GeneticAgent();
    }
    
    private GeneticAgent copy() {
    	GeneticAgent cp = new GeneticAgent();
    	cp.node = this.node.copy(cp.node_factory);
    	return cp;
    }
}
