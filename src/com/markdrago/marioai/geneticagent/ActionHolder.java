package com.markdrago.marioai.geneticagent;

public class ActionHolder {
	boolean[] action;
	
	public void set_action(boolean[] action) {
		this.action = action;
	}
	
	public boolean[] get_action() {
		return this.action;
	}
	
	public void button_action(int button, boolean press) {
		int buttonmod = button % 6;  /* 0 - 5 are valid keys */
		this.action[buttonmod] = press;
	}
	
}
