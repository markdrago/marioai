package com.markdrago.marioai.geneticagent;

import java.util.List;
import ch.idsia.mario.environments.Environment;

public class LevelObservationNode extends ObservationNode {
	public LevelObservationNode(EnvironmentHolder envholder) {
		super(envholder);
		this.name = "level_observation";
	}
	
	public Object execute(List<Object> args) {
		int x, y, xmax, ymax;
		
		Environment env = envholder.get_environment();
		
		xmax = Environment.HalfObsWidth * 2;
		ymax = Environment.HalfObsHeight * 2;
		
		x = ((Integer)args.get(0)).intValue() % xmax;
		y = ((Integer)args.get(1)).intValue() % ymax;
		
		byte[][] enemyMap = env.getLevelSceneObservation();
		if (enemyMap[y][x] != 0)
			return (Object) new Boolean(true);
		return (Object) new Boolean(false);
	}
}
