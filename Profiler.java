package hw1;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.core.AID;
import jade.lang.acl.*;

import java.util.Iterator;

public class Profiler extends Agent {

	private AID[] getAIDs(String service){
		AID[] aids = null;
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription templateSd = new ServiceDescription();
		templateSd.setType(service);
		template.addServices(templateSd);
		try{
			DFAgentDescription[] results = DFService.search(this, template);
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
	
	protected void setup(){
		System.out.println("Profiler is ready");
		AID[] aids = getAIDs("virtual-tour");			
		if (aids.length<=0){
			System.out.println("no tour guides");
			return;
		}
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(aids[0]);
		msg.setContent((String)getArguments()[0]);
		send(msg);
		System.out.println("message sent from profiler");

	}
}
