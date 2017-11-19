package hw1;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import java.util.Random;

public class Curator extends Agent{
	
	final String[][] ALL_ARTIFACTS = {
	{"Twenty Love Poems and a Song of Despair, Pablo Neruda, 1924","The Solitary Reaper, William Wordsworth, 1807","The Fugitive, Alec Brock Stevenson, 1922"},
	{"Banana, Yellow, Spain", "Apple, Red, Italy","Grape, Green, Chile"},
	{"Piano Sonata No. 14, Ludwig van Beethoven, 1801","Toccata and Fugue in D Minor, Johann Sebastian Bach, 1801","Hotline Bling, Drake, 2016"}
	};

	String agentNo;
	String[] local_artifacts;
	
	private String S;

	protected void setup(){
		
		agentNo = (String)getArguments()[0];
		System.out.println("Curator "+ getAID() + " has number " + agentNo + " is ready");
		local_artifacts = ALL_ARTIFACTS[Integer.valueOf(agentNo)%3];
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
					//System.out.println("Curator " + agentNo + " received "+msg.getContent() + " from tour guide");
					processTGMessage(msg.getContent(),msg.getSender());
				}
				if (msg.getLanguage() == "P"){
					//System.out.println("Curator " + agentNo + " received "+msg.getContent() + " from profiler");
					processProfilerMessage(msg.getContent(), msg.getSender());
				}
			}
		}
		private void processProfilerMessage(String msg, AID sender){
			String[] artifacts = msg.split(";");
			String response = "";
			for (int i =0; i<artifacts.length; i ++){
				String aNo = (artifacts[i].split(":"))[0];
				if (aNo.equals(agentNo)){
					response = response + findArtifact((artifacts[i].split(":"))[1])+"\n";
				}
			}
			ACLMessage response_msg = new ACLMessage(ACLMessage.INFORM);
			response_msg.addReceiver(sender);
			response_msg.setContent(response);
			send(response_msg);
						
		}
		private String findArtifact(String s){
			for (int i = 0; i<local_artifacts.length; i ++){
				if (local_artifacts[i].contains(s)){
					return local_artifacts[i];
				}
				
			}
			return "";
		}

		private void processTGMessage(String msg, AID sender){
			String[] parts = msg.split(" ");
			int artifactID = (parts[0].hashCode() + parts[1].hashCode())%2+1;
			String reply = "";
			for (int i =(new Random()).nextInt(2); i<artifactID; i++){
				reply =reply + agentNo+":"+local_artifacts[i].split(", ")[0]+";";
			}
			ACLMessage response_msg = new ACLMessage(ACLMessage.INFORM);
			response_msg.addReceiver(sender);
			response_msg.setContent(reply);
			response_msg.setLanguage("C");
			send(response_msg);
		}

	}
}	
