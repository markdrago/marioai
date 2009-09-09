package com.markdrago.marioai.geneticagent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
