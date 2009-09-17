package com.markdrago.marioai.geneticagent;

import ch.idsia.ai.agents.Agent;

public interface Breedable extends Agent {
	
	public Breedable get_random_breedable();
	
    public Breedable mutate();
    
    public Breedable breed(Breedable parent2);
    
}
