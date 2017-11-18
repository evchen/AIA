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
	protected void setup(){
		System.out.println("Profiler is ready");
		try{
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription templateSd = new ServiceDescription();
			templateSd.setType("virtural-tour");
			template.addServices(templateSd);
			DFAgentDescription[] results = DFService.search(this, template);

			for (int i =0; i < results.length; i ++){
				System.out.println("virtural tour guide " + i +" with AID " +results[i].getName().getName());
				Iterator services = results[i].getAllServices();
				while(services.hasNext()){
					ServiceDescription sd = (ServiceDescription)services.next();
					System.out.println("tour guide " + i + " has service " + sd.getName());
				}
				
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(results[i].getName());
				msg.setContent("a message from the profiler to tour guide");
				send(msg);
				System.out.println("message sent from profiler");
			}
		}
		catch(FIPAException fe) {
			fe.printStackTrace();
		}

	}
}
