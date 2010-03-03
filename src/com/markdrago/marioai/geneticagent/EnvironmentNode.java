package com.markdrago.marioai.geneticagent;

public abstract class EnvironmentNode extends Node {
	EnvironmentHolder envholder;
	
	public EnvironmentNode(EnvironmentHolder envholder) {
		super();
		this.num_arguments = 0;
		this.envholder = envholder;
	}
}
