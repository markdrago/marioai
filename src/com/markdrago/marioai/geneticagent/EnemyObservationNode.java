package com.markdrago.marioai.geneticagent;

import java.util.List;
import ch.idsia.mario.environments.Environment;

public class EnemyObservationNode extends ObservationNode {
	public EnemyObservationNode(EnvironmentHolder envholder) {
		super(envholder);
		this.name = "enemy_observation";
	}
	
	public EnemyObservationNode(EnvironmentHolder envholder, int x, int y) {
		super(envholder);
		this.name = "enemy_observation";
		setX(x);
		setY(y);
	}
	
	public Boolean execute(List<Boolean> args) {
		int x, y, xmax, ymax;
		
		Environment env = envholder.get_environment();
		
		xmax = Environment.HalfObsWidth * 2;
		ymax = Environment.HalfObsHeight * 2;
		
		x = this.x % xmax;
		y = this.y % ymax;
		
		byte[][] enemyMap = env.getEnemiesObservation();
		if (enemyMap[y][x] != 0)
			return new Boolean(true);
		return new Boolean(false);
	}
}
