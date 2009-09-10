package com.markdrago.marioai.geneticagent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* import com.markdrago.marioai.geneticagent.GeneticAgent.Node; */

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
}
