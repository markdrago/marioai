package com.markdrago.marioai.geneticagent;

import java.util.List;

public class AndNode extends BinaryBooleanNode {
	public AndNode() {
		super();
		this.num_arguments = 2;
		this.name = "and";
	}
	
	public Object execute(List<Object> args) {
		return (Object) new Boolean(
				((Boolean)args.get(0)).booleanValue() &&
				((Boolean)args.get(1)).booleanValue());
	}
}
