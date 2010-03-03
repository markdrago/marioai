package com.markdrago.marioai.geneticagent;

import java.util.List;
import ch.idsia.mario.environments.Environment;

public class LevelObservationNode extends ObservationNode {
	public LevelObservationNode(EnvironmentHolder envholder) {
		super(envholder);
		this.name = "level_observation";
	}
	
	public Boolean execute(List<Boolean> args) {
		int x, y, xmax, ymax;
		
		Environment env = envholder.get_environment();
		
		xmax = Environment.HalfObsWidth * 2;
		ymax = Environment.HalfObsHeight * 2;
		
		x = Math.abs(this.x) % xmax;
		y = Math.abs(this.y) % ymax;
		
		byte[][] enemyMap = env.getLevelSceneObservation();
		if (enemyMap[y][x] != 0)
			return new Boolean(true);
		return new Boolean(false);
	}
}
