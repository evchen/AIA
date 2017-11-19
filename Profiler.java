package hw1;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.core.AID;
import jade.lang.acl.*;
import jade.proto.states.MsgReceiver;

import java.util.Iterator;

public class Profiler extends Agent {

	DataStore ds = new DataStore();

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
		doWait(5000);
		System.out.println("Profiler is ready");
		AID[] tgAIDs = getAIDs("virtual-tour");
		AID[] cAIDs = getAIDs("curator");
		if (tgAIDs.length<=0){
			System.out.println("no tour guides");
			return;
		}
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setLanguage("P");
		msg.addReceiver(tgAIDs[0]);
		msg.setContent((String)getArguments()[0]);
		send(msg);
		System.out.println("message sent from profiler");
		SequentialBehaviour sb = new SequentialBehaviour();

		sb.addSubBehaviour(new MsgReceiver(this, null, MsgReceiver.INFINITE, ds, "tgReply"));
		sb.addSubBehaviour(new OneShotBehaviour(){
			public void action(){
				ACLMessage acl = (ACLMessage) ds.get("tgReply");
				System.out.println("profiler received response "+acl.getContent()+" from tour guide");
			}
		});

		ParallelBehaviour pb1 = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL){
			public void onStart(){
				System.out.println("Profiler starts to execute parallel behaviour");
				for (int i = 0 ; i < cAIDs.length; i ++){
					ACLMessage acl = (ACLMessage) ds.get("tgReply");
					addSubBehaviour(new SendMessageBehaviour(
								cAIDs[i],
								acl.getContent()));
				}

			}
		};
		sb.addSubBehaviour(pb1);
		sb.addSubBehaviour(new OneShotBehaviour(){
			public void action(){
				System.out.println("--------------------");
				System.out.println("Museum Tour");
			}
		});


		for (int i =0; i < cAIDs.length; i++){
			sb.addSubBehaviour(new MsgReceiver(this, null, MsgReceiver.INFINITE, ds, "cReply"){
				public int onEnd(){
					System.out.println(((ACLMessage)ds.get("cReply")).getContent());
					return super.onEnd();
				}
			});
		}
		sb.addSubBehaviour(new MsgReceiver(this, null, MsgReceiver.INFINITE, ds, "cReply"));
		addBehaviour(new TickerBehaviour(this, 1000){
			protected void onTick(){
				System.out.println("Profiler is still alive");
			}
		});
		addBehaviour(new WakerBehaviour(this,10000){
			protected void onWake(){
				System.out.println("It is time for this profiler to die");
				doDelete();
			}
		}	);
		addBehaviour(sb);
	}

	private class SendMessageBehaviour extends OneShotBehaviour{
		AID receiver;
		String content;

		public SendMessageBehaviour(AID receiver, String content){
			this.receiver = receiver;
			this.content = content;
		}

		public void action(){
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(receiver);
			msg.setLanguage("P");
			msg.setContent(content);
			send(msg);
		}

	}
}
