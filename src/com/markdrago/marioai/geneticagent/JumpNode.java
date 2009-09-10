package com.markdrago.marioai.geneticagent;

import java.util.List;

import ch.idsia.mario.engine.sprites.Mario;

public class JumpNode extends ActionNode {
	public JumpNode(ActionHolder action_holder) {
		super(action_holder);
		this.name = "jump";
	}

	public Object execute(List<Object> args) {
		this.action_holder.button_action(Mario.KEY_JUMP, ((Boolean)args.get(0)).booleanValue());
		return args.get(0);
	}
}