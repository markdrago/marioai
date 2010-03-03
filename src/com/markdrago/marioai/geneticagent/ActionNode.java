package com.markdrago.marioai.geneticagent;

public abstract class ActionNode extends Node {
	ActionHolder action_holder;
	
	public ActionNode(ActionHolder action_holder) {
		super();
		this.num_arguments = 1;
		this.action_holder = action_holder;
	}
}
