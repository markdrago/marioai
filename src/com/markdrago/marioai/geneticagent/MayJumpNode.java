package com.markdrago.marioai.geneticagent;

import java.util.List;
import ch.idsia.mario.environments.Environment;

public class MayJumpNode extends EnvironmentNode {
	public MayJumpNode(EnvironmentHolder envholder) {
		super(envholder);
		this.name = "may_jump";
	}
	
	public Boolean execute(List<Boolean> args) {
		Environment env = envholder.get_environment();
		return new Boolean(env.mayMarioJump());
	}
}
