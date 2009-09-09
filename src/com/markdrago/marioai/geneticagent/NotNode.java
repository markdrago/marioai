package com.markdrago.marioai.geneticagent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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