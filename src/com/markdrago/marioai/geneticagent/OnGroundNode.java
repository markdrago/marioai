package com.markdrago.marioai.geneticagent;

import java.util.List;
import ch.idsia.mario.environments.Environment;

public class OnGroundNode extends EnvironmentNode {
	public OnGroundNode(EnvironmentHolder envholder) {
		super(envholder);
		this.name = "on_ground";
	}
	
	public Object execute(List<Object> args) {
		Environment env = envholder.get_environment();
		return (Object) new Boolean(env.isMarioOnGround());
	}
}
