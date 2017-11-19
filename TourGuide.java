package hw1;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;

public class TourGuide extends Agent {

	final int WAIT_FOR_PROFILER = 0;
	final int WAIT_FOR_CURATOR = 1;

	int state = WAIT_FOR_PROFILER;

	int numberOfCurators;

	protected void setup(){
		System.out.println("Tour guide " + getAID() +" is ready");
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
			sd.setName("virtual tour guide");
			sd.setType("virtual-tour");
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

		String combinedResponse = "";
		AID profilerAID=null;

		public void localReset(){
			combinedResponse = "";
			profilerAID = null;
			state = WAIT_FOR_PROFILER;
		}

		public void action(){
			ACLMessage msg = myAgent.receive();
			if (msg!=null) {processMessage(msg);}
		}

		public void processMessage(ACLMessage msg){
			switch (msg.getLanguage()){
				case "P": processProfilerMessage(msg);
					  break;
				case "C": processCuratorMessage(msg);
					  break;
			}
//			if (state == WAIT_FOR_PROFILER) {
//				processProfilerMessage(msg);
//			}
//			if (state == WAIT_FOR_CURATOR){
//				processCuratorMessage(msg);
//			}
		}

		public void processProfilerMessage(ACLMessage msg){
			// send message to all curator
			System.out.println("Tour Guide received "+(String)msg.getContent()+" from profiler");
			profilerAID = msg.getSender();
			AID[] aids = getAIDs("curator");
			for (int i = 0; i < aids.length; i++){
				ACLMessage send_msg = new ACLMessage(ACLMessage.INFORM);
				send_msg.addReceiver(aids[i]);
				send_msg.setLanguage("TG");
				send_msg.setContent(msg.getContent());
				send(send_msg);
			}
			state= WAIT_FOR_CURATOR;
			numberOfCurators = aids.length;
		}

		public void processCuratorMessage(ACLMessage msg){
			System.out.println("Tour Guide received "+(String)msg.getContent()+" from Curator");
			combinedResponse= combinedResponse + (String)msg.getContent();
			numberOfCurators --;
			if (numberOfCurators <= 0){
				ACLMessage response_msg = new ACLMessage(ACLMessage.INFORM);
				response_msg.addReceiver(profilerAID);
				response_msg.setContent(combinedResponse);
				System.out.println("Tour Guide sent "+combinedResponse + " to profiler");
				send(response_msg);
				localReset();
			}
		}

		private AID[] getAIDs(String service){
			AID[] aids = null;
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription templateSd = new ServiceDescription();
			templateSd.setType(service);
			template.addServices(templateSd);
			try{
				DFAgentDescription[] results = DFService.search(myAgent, template);
				aids = new AID[results.length];
				for (int i = 0; i<results.length; i ++){
					aids[i] = results[i].getName();
				}
			}
			catch (FIPAException fe){

				fe.printStackTrace();
			}
			return aids;
		}	

	}
}

