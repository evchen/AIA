package hw1;

import jade.core.Agent;
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
			sd.setName("virtural tour guide");
			sd.setType("virtural-tour");
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
			if (msg!=null) {
				System.out.println("Received ACL message in Tour Guide: "+ msg.getContent());
			}
		}
	}
}

