package com.markdrago.marioai.geneticagent;

import java.util.List;

public class MultiplyNode extends BinaryOpNode {
	public MultiplyNode() {
		super();
		this.name = "multiply";
	}
	
	public Object execute(List<Object> args) {
		return new Integer(
				((Integer)args.get(0)).intValue() *
				((Integer)args.get(1)).intValue());
	}
}
