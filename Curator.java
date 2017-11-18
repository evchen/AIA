package hw1;

import jade.core.Agent;

public class Curator extends Agent{
	private String S;

	protected void setup(){
		Object[] args = getArguments();
		if (args == null || args.length<= 0) return;
		for (int i = 0; i<args.length; i++){
			System.out.println("argument "+ (i+1) +" is " + (String)args[i]);
		}
	}
}	
