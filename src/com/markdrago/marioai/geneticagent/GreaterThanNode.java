package com.markdrago.marioai.geneticagent;

import java.util.List;

public class GreaterThanNode extends BinaryConditionalNode {
	public GreaterThanNode() {
		super();
		this.name = "greater_than";
	}
	
	public Object execute(List<Object> args) {
		return new Boolean(
				((Integer)args.get(0)).intValue() >
				((Integer)args.get(1)).intValue());
	}
}
