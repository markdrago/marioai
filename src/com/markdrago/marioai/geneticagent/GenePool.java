package com.markdrago.marioai.geneticagent;

import ch.idsia.ai.tasks.Task;

import java.util.ArrayList;
import java.util.Random;

public class GenePool {
	ArrayList<Breedable> generation;
	ArrayList<Double> scores;
	private final Task task;
	private final int gen_size;
	
	private static final int iterations = 5;
	
	public GenePool(Task task, Breedable adam, int gen_size) {
		this.task = task;
		this.gen_size = gen_size;
		this.generation = new ArrayList<Breedable>();
		this.scores = new ArrayList<Double>(gen_size);
		fill_generation(adam);
	}
	
	public void fill_generation(Breedable adam) {
		while (generation.size() < gen_size) {
			add_agent(adam.get_random_breedable());
		}
	}
	
	public void score_generation() {
		scores.clear();
		scores.ensureCapacity(generation.size());
		
		for (int i = 0; i < generation.size(); i++) {
			scores.set(i, score_agent(generation.get(i)));
		}
	}
	
	public double score_agent(Breedable agent) {
		double sum = 0;
		for (int i = 0; i < iterations; i++) {
			agent.reset();
			sum += task.evaluate(agent)[0];
		}
		return (sum / iterations);
	}
	
	public void order_generation() {
		Breedable temp;
		for (int i = 0; i < generation.size(); i++) {
			for (int j = i; j < generation.size() - 1; j++) {
				if (scores.get(i) < scores.get(j)) {
					temp = generation.get(i);
					generation.set(i, generation.get(j));
					generation.set(j, temp);
				}
			}
		}
	}
	
	public void evolve_generation() {
		int done = 0;
		int offset = 0;
		ArrayList<Breedable> nextgen = new ArrayList<Breedable>();

		/* top 8 move on */
		for (int i = 0; i < 8; i++) {
			nextgen.set(done + i, generation.get(i));
		}
		done += 8;
		
		/* top 4 move on mutated */
		for (int i = 0; i < 4; i++) {
			nextgen.set(done + i, generation.get(i).mutate());
		}
		done += 4;
		
		/* random 4 from 4-12 move on mutated */
		offset = 4;
		for (int i = 0; i < 4; i++) {
			
		}
	}
	
	public int[] random_from_range(int start, int end, int num) {
		Random rnd = new Random();
		boolean is_unique;
		int result[] = new int[num];
		
		int range = (end - start) + 1;
		
		/* loop until we have all requested numbers */
		for (int i = 0; i < num; i++) {
			is_unique = false;
			
			/* keep picking until we have a unique number */
			while (!is_unique) {
				/* pick a random number, hope it is unique */
				int choice = rnd.nextInt(range) + start;
				is_unique = true;
				
				/* check if this random number is unique */
				for (int j = 0; j < i; j++) {
					if (choice == result[j]) {
						is_unique = false;
						break;
					}
				}
			}
		}
		
		return result;
	}
	
	public void nextGeneration() {
		score_generation();
		order_generation();
		evolve_generation();
	}
	
	private void add_agent(Breedable agent) {
		this.generation.add(agent);
	}
}
