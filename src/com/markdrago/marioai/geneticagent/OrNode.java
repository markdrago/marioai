package com.markdrago.marioai.geneticagent;

import java.util.List;

public class OrNode extends BinaryBooleanNode {
	public OrNode() {
		super();
		this.num_arguments = 2;
		this.name = "or";
	}
	
	public Object execute(List<Object> args) {
		return (Object) new Boolean(
				((Boolean)args.get(0)).booleanValue() ||
				((Boolean)args.get(1)).booleanValue());
	}
}