package com.markdrago.marioai.geneticagent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class BooleanNode extends Node {
	public BooleanNode() {
		super();
		this.num_arguments = 0;
	}
	
	public List<Type> get_argument_types() {
		ArrayList<Type> lst = new ArrayList<Type>();
		return lst;
	}
	
	public Type get_response_type() { return NodeType.get_type("boolean"); }
}