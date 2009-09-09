package com.markdrago.marioai.geneticagent;

import java.util.List;
import ch.idsia.mario.environments.Environment;

public class MayJumpNode extends EnvironmentNode {
	public MayJumpNode(EnvironmentHolder envholder) {
		super(envholder);
		this.name = "may_jump";
	}
	
	public Object execute(List<Object> args) {
		Environment env = envholder.get_environment();
		return (Object) new Boolean(env.mayMarioJump());
	}
}
