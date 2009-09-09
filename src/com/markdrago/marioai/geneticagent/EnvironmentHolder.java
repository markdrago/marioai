package com.markdrago.marioai.geneticagent;

import ch.idsia.mario.environments.Environment;

public class EnvironmentHolder {
	Environment environ;
	public void set_environment(Environment environ) {
		this.environ = environ;
	}
	
	public Environment get_environment() {
		return this.environ;
	}
}
