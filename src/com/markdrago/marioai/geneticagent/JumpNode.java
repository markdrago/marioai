package com.markdrago.marioai.geneticagent;

import java.util.List;
import java.util.Random;

import ch.idsia.mario.engine.sprites.Mario;

public class JumpNode extends ActionNode {
	
	private Random rnd;
	private int orig_jump_duration = 0;
	private int orig_jump_gap = 0;
	private int jump_duration = 0;
	private int jump_gap = 0;
	
	private static final int max_jump_duration = 25;
	private static final int max_jump_gap = 50;
	
	public JumpNode(ActionHolder action_holder) {
		super(action_holder);
		this.name = "jump";
		rnd = new Random();
		this.orig_jump_duration = rnd.nextInt() % max_jump_duration;
		this.orig_jump_gap = rnd.nextInt() % max_jump_gap;
		reset_jump_timers();
	}

	private void reset_jump_timers() {
		this.jump_duration = this.orig_jump_duration;
		this.jump_gap = this.orig_jump_gap;
	}
	
	public Boolean execute(List<Boolean> args) {
		if (args.get(0).booleanValue()) {
			if (this.jump_duration > 0) {
				this.action_holder.button_action(Mario.KEY_JUMP, true);
				this.jump_duration--;
			} else {
				if (this.jump_gap > 0) {
					this.action_holder.button_action(Mario.KEY_JUMP, false);
					this.jump_gap--;
				} else {
					reset_jump_timers();
				}
			}
		} else {
			this.action_holder.button_action(Mario.KEY_JUMP, false);
		}

		return args.get(0);
	}
}
