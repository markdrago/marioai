package com.markdrago.marioai.geneticagent;

import java.util.List;

public class OrNode extends BinaryBooleanNode {
	public OrNode() {
		super();
		this.num_arguments = 2;
		this.name = "or";
	}
	
	public Boolean execute(List<Boolean> args) {
		return new Boolean(args.get(0).booleanValue() ||
				           args.get(1).booleanValue());
	}
}