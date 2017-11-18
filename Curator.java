package hw1;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;

public class Curator extends Agent{
	private String S;

	protected void setup(){
		addBehaviour(new RegisterOnDF(this));
		addBehaviour(new ReceiveMessageBehaviour());
		
		Object[] args = getArguments();
		if (args == null || args.length<= 0) return;
		for (int i = 0; i<args.length; i++){
			System.out.println("argument "+ (i+1) +" is " + (String)args[i]);
		}
	}
	
	private class RegisterOnDF extends OneShotBehaviour {
		Agent a;
		public RegisterOnDF(Agent a){
			this.a = a;
		}

		public void action(){
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName("curator");
			sd.setType("curator");
			dfd.addServices(sd);

			try{
				DFService.register(a, dfd);
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}
			System.out.println(getAID()+" registered on DF.");
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
			System.out.println("Curator " + getAID() + " got message " + msg);
		}

	}
}	
