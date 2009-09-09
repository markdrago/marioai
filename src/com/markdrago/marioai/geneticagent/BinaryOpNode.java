package com.markdrago.marioai.geneticagent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class BinaryOpNode extends Node {
	public BinaryOpNode() {
		super();
		this.num_arguments = 2;
	}
	
	public List<Type> get_argument_types() {
		ArrayList<Type> lst = new ArrayList<Type>();
		lst.add(NodeType.get_type("int"));
		lst.add(NodeType.get_type("int"));
		return lst;
	}
	
	public Type get_response_type() { return NodeType.get_type("int"); }
}
