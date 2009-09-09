package com.markdrago.marioai.geneticagent;

import java.util.List;
import java.util.Random;

public class StaticIntNode extends IntNode {
	int value;
	
	public StaticIntNode() {
		super();
		this.set_random_value();
		this.set_name_by_value();
	}
	
	public StaticIntNode(int value) {
		super();
		this.set_value(value);
		this.set_name_by_value();
	}
	
	public void set_value(int value) {
		this.value = value;
	}
	
	public void set_random_value() {
		Random rnd = new Random();
		this.set_value(rnd.nextInt(22));
	}
	
	private void set_name_by_value() {
		this.name = String.format("static_int %d", this.value);    		
	}
	
	public Object execute(List<Object> args) {
		return (Object) new Integer(this.value);
	}
}
