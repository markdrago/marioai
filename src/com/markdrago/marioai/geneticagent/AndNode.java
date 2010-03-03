package com.markdrago.marioai.geneticagent;

import java.util.List;

public class AndNode extends BinaryBooleanNode {
	public AndNode() {
		super();
		this.num_arguments = 2;
		this.name = "and";
	}
	
	public Boolean execute(List<Boolean> args) {
		return new Boolean(args.get(0).booleanValue() &&
						   args.get(1).booleanValue());
	}
}
