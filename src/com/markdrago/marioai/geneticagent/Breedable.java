package com.markdrago.marioai.geneticagent;

public interface Breedable {
	
	public Breedable get_random_breedable();
	
    public Breedable mutate();
    
    public Breedable breed(GeneticAgent parent2);
    
}
