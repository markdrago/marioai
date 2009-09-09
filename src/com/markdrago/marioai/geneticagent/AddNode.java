package com.markdrago.marioai.geneticagent;

import java.util.List;

public class AddNode extends BinaryOpNode {
	public AddNode() {
		super();
		this.name = "add";
	}
	
	public Object execute(List<Object> args) {
		return new Integer(
				((Integer)args.get(0)).intValue() +
				((Integer)args.get(1)).intValue());
	}
}
