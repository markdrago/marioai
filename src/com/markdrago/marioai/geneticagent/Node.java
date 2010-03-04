package com.markdrago.marioai.geneticagent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Random;

public abstract class Node {
	ArrayList<Node> children;
	Node parent;
	int num_arguments;
	String name;
	String uuid;
	
	public Node() {
		this.children = new ArrayList<Node>();
		this.num_arguments = 0;
		this.name = "unknown";
		this.uuid = UUID.randomUUID().toString();
		this.parent = null;
	}
	
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
		if (this.children.size() <= index)
			this.children.add(child);
		else
			this.children.set(index, child);
		
		child.set_parent(this);
	}
	
	public void replace_child(Node child, Node newchild) {
		int index;
		
		index = this.children.indexOf(child);
		this.set_child(index, newchild);
	}
	
	public Node get_parent() {
		return this.parent;
	}
	
	private void set_parent(Node parent) {
		this.parent = parent;
	}
	
	public Boolean execute(List<Boolean> args) {
		return null;
	}
	
    public boolean tree_contains_action_node() {
    	/* if this node is an action node, return true */
    	if (this instanceof ActionNode)
    		return true;
    	
    	/* if any of the trees beneath this one contain an action node return true */
    	for (Node child: this.get_children()) {
    		if (child.tree_contains_action_node())
    			return true;
    	}
    	
    	return false;
    }
    
    public Boolean execute_node() {
    	List<Node> children;
    	ArrayList<Boolean> child_results;
    	Node child;
    	int child_count;
    	
    	/* get results for all child nodes */
    	child_count = this.get_num_children();
    	child_results = new ArrayList<Boolean>();
    	if (child_count > 0) {
    		children = this.get_children();
    		for (int i = 0; i < child_count; i++) {
    			child = children.get(i);
    			child_results.add(child.execute_node());
    		}
    	}
    	
    	/* return result for this node */
		return this.execute(child_results);
    }
    
    public Node pick_random_branch() {
    	Node choice = null;
    	Random rnd = new Random();
    	
    	while (choice == null)
    		choice = pick_random_node(rnd);
    	
    	return choice;
    }
    
    public Node pick_random_node(Random rnd) {
    	Node choice = null;
    	
    	if (rnd.nextDouble() > 0.1) {
    		/* randomly pick a child */
    		for (Node child: this.children) {
    			choice = child.pick_random_node(rnd);
    			if (choice != null) {
    				return choice;
    			}
    		}
    	}
    	
    	return this;
    }
    
    public Node mutate_tree(NodeFactory factory) {
    	Random rnd = new Random();
    	Node newtree = null;
    	
    	while (newtree == null) {
    		newtree = this.mutate_node(factory, rnd);
    	}
    	
    	return newtree;
    }
    
    public Node mutate_node(NodeFactory factory, Random rnd) {
    	Node newtree = null;
    	
    	if (rnd.nextDouble() > 0.1) {
    		/* mutate the children */
    		for (Node child: this.children) {
    			newtree = child.mutate_node(factory, rnd);
    			if (newtree != null)
    				break;
    		}
    		return (newtree == null) ? null : this;
    	}
    	
    	if (this.parent == null) {
    		newtree = factory.get_random_tree(null, 0);
    		return newtree;
    	} else {
    		newtree = factory.get_random_tree(this.parent, 0);
    		return this;
    	}
    }
    
    public Node copy(NodeFactory factory) {
    	Node cp = factory.get_node_with_name(this.name);

    	for (Node child: this.children) {
    		Node childcp = child.copy(factory);
    		cp.children.add(childcp);
    		childcp.parent = cp;
    	}
    	
    	return cp;
    }
    
    public String get_dot_for_tree() {
    	StringBuffer result = new StringBuffer("digraph geneticagent {\n");
    	this.get_dot_for_node(result, 0);
    	result.append("}");
    	return result.toString();
    }
    
    /* this function adds all of the dot-notation needed to draw the graph of
     * its tree, and returns the highest numbered nodenum used in the process*/
    public int get_dot_for_node(StringBuffer result, int nodenum) {
    	List<Node> children;
    	Node child;
    	int child_count, max_used, child_num;
    	String nodename, nodelabel, childname;
    	
    	/* create name and label for this node */
    	nodename = String.format("node%d", nodenum);
    	nodelabel = this.toString();
    	
    	/* add name for this node to output */
    	result.append(nodename + " [label=\"" + nodelabel + "\"]\n");
    	
    	/* add links to children and get results for children */
    	child_count = this.get_num_children();
    	max_used = nodenum;
    	if (child_count > 0) {
    		children = this.get_children();
    		for (int i = 0; i < child_count; i++) {
    			child = children.get(i);
    			child_num = max_used + 1;
    			
    			/* draw links from this node to children */
    			childname = String.format("node%d", child_num);
    			result.append(nodename + " -> " + childname + "\n"); 
    			
    			/* add results from the child tree */
    			max_used = child.get_dot_for_node(result, child_num);
    		}
    	}
    	
    	return max_used;
    }
}
