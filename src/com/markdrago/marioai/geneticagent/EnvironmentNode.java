package com.markdrago.marioai.geneticagent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class EnvironmentNode extends Node {
	EnvironmentHolder envholder;
	
	public EnvironmentNode(EnvironmentHolder envholder) {
		super();
		this.num_arguments = 0;
		this.envholder = envholder;
	}
	
	public List<Type> get_argument_types() {
		ArrayList<Type> lst = new ArrayList<Type>();
		return lst;
	}
	
	public Type get_response_type() { return NodeType.get_type("boolean"); }
}
