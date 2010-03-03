package com.markdrago.marioai.geneticagent;

import java.util.List;
import ch.idsia.mario.engine.sprites.Mario;

public class LeftNode extends ActionNode {
	public LeftNode(ActionHolder action_holder) {
		super(action_holder);
		this.name = "left";
	}
	
	public Boolean execute(List<Boolean> args) {
		this.action_holder.button_action(Mario.KEY_LEFT, args.get(0).booleanValue());
		this.action_holder.button_action(Mario.KEY_RIGHT, false);
		return args.get(0);
	}
}
