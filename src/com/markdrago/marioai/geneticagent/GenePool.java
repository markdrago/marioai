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
		ArrayList<Breedable> nextgen = new ArrayList<Breedable>();

		/* top 8 move on */
		for (int i = 0; i < 8; i++) {
			nextgen.set(done++, generation.get(i));
		}
		
		/* top 4 move on mutated */
		for (int i = 0; i < 4; i++) {
			nextgen.set(done++, generation.get(i).mutate());
		}
		
		/* random 4 out of 8 b/w 4-11 move on mutated */
		int rand[] = random_from_range(4, 11, 4);
		for (int i = 0; i < 4; i++) {
			nextgen.set(done++, generation.get(rand[i]).mutate());
		}
		
		/* top 8 randomly breed creating 4 */
		rand = random_from_range(0, 7, 8);
		for (int i = 0; i < 4; i++) {
			Breedable lucky1, lucky2;
			lucky1 = generation.get(rand[i]);
			lucky2 = generation.get(rand[i + 4]);
			nextgen.set(done++, lucky1.breed(lucky2));
		}
		
		/* top 16 randomly breed creating 8 */
		rand = random_from_range(0, 15, 16);
		for (int i = 0; i < 8; i++) {
			Breedable lucky1, lucky2;
			lucky1 = generation.get(rand[i]);
			lucky2 = generation.get(rand[i + 8]);
			nextgen.set(done++, lucky1.breed(lucky2));
		}
		
		/* bottom 16 randomly breed creating 4 */
		rand = random_from_range(16, 31, 8);
		for (int i = 0; i < 4; i++) {
			Breedable lucky1, lucky2;
			lucky1 = generation.get(rand[i]);
			lucky2 = generation.get(rand[i + 4]);
			nextgen.set(done++, lucky1.breed(lucky2));
		}
		
		this.generation = nextgen;
		this.scores.clear();
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
