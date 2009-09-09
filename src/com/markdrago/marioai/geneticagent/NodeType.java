package com.markdrago.marioai.geneticagent;

import java.lang.reflect.Type;

public class NodeType {
	public static Type get_type(String nickname) {
		Type result = null;
		
		try {
			if (nickname == "int") {
				result = Class.forName("java.lang.Integer");
			} else if (nickname == "boolean") {
				result = Class.forName("java.lang.Boolean");
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		if (result == null) {
			System.out.println("Unable to find class for nickname: " + nickname);
		}
		
		return result;
	}
}
