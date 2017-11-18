package hw1;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

public class Curator extends Agent{
	private String S;

	protected void setup(){
		Object[] args = getArguments();
		if (args == null || args.length<= 0) return;
		for (int i = 0; i<args.length; i++){
			System.out.println("argument "+ (i+1) +" is " + (String)args[i]);
		}
	}

	private class ReceiveMessageBehaviour extends CyclicBehaviour {
		public void action(){
			ACLMessage msg = myAgent.receive();
			if (msg!=null) {
				if (msg.getLanguage() == "TG"){
					processTGMessage(msg.getContent());
				}
			}
		}
		private void processTGMessage(String msg){
			System.out.println("Curator " + getAID() + " got message" + msg);
		}

	}
}	
