package com.markdrago.marioai.geneticagent;

import java.util.List;

public class DivideNode extends BinaryOpNode {
	public DivideNode() {
		super();
		this.name = "divide";
	}
	
	public Object execute(List<Object> args) {
		int op1 = ((Integer)args.get(0)).intValue();
		int op2 = ((Integer)args.get(1)).intValue();
		
		if (op2 == 0) { return new Integer(0); }
		
		return new Integer(op1 / op2);
	}
}
