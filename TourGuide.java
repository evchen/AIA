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
		public void onStart(){
			System.out.println(getAID()+" starting receive message behaviour");
		}
		public void action(){
			ACLMessage msg = myAgent.receive();
			if (msg!=null) processMessage(msg);
		}

		public void processMessage(ACLMessage msg){
			if (state == WAIT_FOR_PROFILER) {
				processProfilerMessage(msg);
			}
		}

		public void processProfilerMessage(ACLMessage msg){
			// send message to all curator
			AID[] aids = getAIDs("curator");
			for (int i = 0; i < aids.length; i++){
				ACLMessage send_msg = new ACLMessage(ACLMessage.INFORM);
				send_msg.addReceiver(aids[0]);
				send_msg.setLanguage("TG");
				send_msg.setContent(msg.getContent());
				send(send_msg);
			}
			state= WAIT_FOR_CURATOR;
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

