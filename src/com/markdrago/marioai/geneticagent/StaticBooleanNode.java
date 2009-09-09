package com.markdrago.marioai.geneticagent;

import java.util.List;
import java.util.Random;

public class StaticBooleanNode extends BooleanNode {
	boolean value;
	
	public StaticBooleanNode() {
		super();
		this.num_arguments = 0;
		this.set_random_value();
		this.set_name_by_value();
	}
	
	public StaticBooleanNode(boolean value) {
		this.num_arguments = 0;
		this.set_value(value);
		this.set_name_by_value();
	}
	
	private void set_name_by_value() {
		this.name = (this.value == true) ? "true" : "false";    		
	}
	
	public Object execute(List<Object> args) {
		return (Object) new Boolean(this.value);
	}
	
	public void set_value(boolean value) {
		this.value = value;
	}
	
	public void set_random_value() {
		Random rnd = new Random();
		this.set_value(rnd.nextBoolean());
	}
}
