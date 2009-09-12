package com.markdrago.marioai.geneticagent;

import ch.idsia.ai.tasks.Task;

import java.util.ArrayList;

public class GenePool {
	ArrayList<Breedable> generation;
	private final Task task;
	private final int gen_size;
	
	public GenePool(Task task, Breedable adam, int gen_size) {
		this.task = task;
		this.gen_size = gen_size;
		this.generation = new ArrayList<Breedable>();
		fillGeneration(adam);
	}
	
	public void fillGeneration(Breedable adam) {
		while (generation.size() < gen_size) {
			add_agent(adam.get_random_breedable());
		}
	}
	
	public void nextGeneration() {
		/* TODO evolve next generation */
	}
	
	private void add_agent(Breedable agent) {
		this.generation.add(agent);
	}
}
