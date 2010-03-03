package com.markdrago.marioai.geneticagent;

import java.util.List;

public class NotNode extends Node {
	public NotNode() {
		super();
		this.num_arguments = 1;
		this.name = "not";
	}
	
	public Boolean execute(List<Boolean> args) {
		return new Boolean( ! args.get(0).booleanValue());
	}
}