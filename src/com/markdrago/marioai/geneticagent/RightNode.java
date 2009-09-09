package com.markdrago.marioai.geneticagent;

import java.util.List;
import ch.idsia.mario.engine.sprites.Mario;

public class RightNode extends ActionNode {
	public RightNode(ActionHolder action_holder) {
		super(action_holder);
		this.name = "right";
	}
	
	public Object execute(List<Object> args) {
		this.action_holder.button_action(Mario.KEY_RIGHT, ((Boolean)args.get(0)).booleanValue());
		this.action_holder.button_action(Mario.KEY_LEFT, false);
		return args.get(0);
	}
}
