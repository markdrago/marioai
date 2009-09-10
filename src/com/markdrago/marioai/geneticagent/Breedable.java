package com.markdrago.marioai.geneticagent;

import ch.idsia.ai.Evolvable;

public interface Breedable extends Evolvable {
	/* crossover some genetic material from the spouse in to this Breedable */
	public void breedWith(Breedable spouse);
}
