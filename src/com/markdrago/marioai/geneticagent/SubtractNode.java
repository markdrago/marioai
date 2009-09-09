package com.markdrago.marioai.geneticagent;

import java.util.List;

public class SubtractNode extends BinaryOpNode {
	public SubtractNode() {
		super();
		this.name = "subtract";
	}
	
	public Object execute(List<Object> args) {
		return new Integer(java.lang.Math.abs(
				((Integer)args.get(0)).intValue() -
				((Integer)args.get(1)).intValue()));
	}
}
