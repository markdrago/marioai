package com.markdrago.marioai.geneticagent;

import java.util.List;
import ch.idsia.mario.engine.sprites.Mario;

public class SpeedNode extends ActionNode {
	public SpeedNode(ActionHolder action_holder) {
		super(action_holder);
		this.name = "speed";
	}
	
	public Object execute(List<Object> args) {
		this.action_holder.button_action(Mario.KEY_SPEED, ((Boolean)args.get(0)).booleanValue());
		return args.get(0);
	}
}
