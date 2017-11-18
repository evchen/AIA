package hw1;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;

public class Curator extends Agent{
	
	final String[][] ALL_ARTIFACTS = {
	{"Twenty Love Poems and a Song of Despair, Pablo Neruda, 1924","The Solitary Reaper, William Wordsworth, 1807","The Fugitive, Alec Brock Stevenson, 1922"},
	{"Banana, Yellow, Spain", "Apple, Red, Italy","Grape, Green, Chile"},
	{"Piano Sonata No. 14, Ludwig van Beethoven, 1801","Toccata and Fugue in D Minor, Johann Sebastian Bach, 1801","Hotline Bling, Drake, 2016"}
	};
	
	private String S;

	protected void setup(){
		addBehaviour(new RegisterOnDF(this));
		addBehaviour(new ReceiveMessageBehaviour());
		
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
					processTGMessage(msg.getContent(),msg.getSender());
				}
			}
		}
		private void processTGMessage(String msg, AID sender){
			
		}

	}
}	
