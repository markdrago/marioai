package com.markdrago.marioai.geneticagent;

import java.util.List;

public class EqualNode extends BinaryConditionalNode {
	public EqualNode() {
		super();
		this.name = "equal";
	}
	
	public Object execute(List<Object> args) {
		return new Boolean(
				((Integer)args.get(0)).intValue() ==
				((Integer)args.get(1)).intValue());
	}
}