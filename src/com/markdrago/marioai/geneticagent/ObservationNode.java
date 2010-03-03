package com.markdrago.marioai.geneticagent;

import java.util.Random;

import ch.idsia.mario.environments.Environment;

public abstract class ObservationNode extends Node {
	EnvironmentHolder envholder;
	int x, y;
	
	public ObservationNode(EnvironmentHolder envholder) {
		super();
		this.num_arguments = 0;
		this.envholder = envholder;
		pickRandomXY();
	}
	
	public void pickRandomXY() {
		Random rnd = new Random();
		this.x = rnd.nextInt() % Environment.HalfObsWidth;
		this.y = rnd.nextInt() % Environment.HalfObsHeight;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
