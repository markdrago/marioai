package com.markdrago.marioai.geneticagent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class BinaryBooleanNode extends Node {
	public BinaryBooleanNode() {
		super();
		this.num_arguments = 2;
	}
	
	public List<Type> get_argument_types() {
		ArrayList<Type> lst = new ArrayList<Type>();
		lst.add(NodeType.get_type("boolean"));
		lst.add(NodeType.get_type("boolean"));
		return lst;
	}
	
	public Type get_response_type() { return NodeType.get_type("boolean"); }
}