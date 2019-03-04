package no.hvl.dat110.broker;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import no.hvl.dat110.common.Logger;
import no.hvl.dat110.common.Stopable;
import no.hvl.dat110.messages.*;
import no.hvl.dat110.messagetransport.Connection;

public class Dispatcher extends Stopable {

	private Storage storage;

	public Dispatcher(Storage storage) {
		super("Dispatcher");
		this.storage = storage;

	}

	@Override
	public void doProcess() {

		Collection<ClientSession> clients = storage.getSessions();

		Logger.lg(".");
		for (ClientSession client : clients) {

			Message msg = null;

			if (client.hasData()) {
				msg = client.receive();
			}

			if (msg != null) {
				dispatch(client, msg);
			}
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void dispatch(ClientSession client, Message msg) {

		MessageType type = msg.getType();

		switch (type) {

		case DISCONNECT:
			onDisconnect((DisconnectMsg) msg);
			break;

		case CREATETOPIC:
			onCreateTopic((CreateTopicMsg) msg);
			break;

		case DELETETOPIC:
			onDeleteTopic((DeleteTopicMsg) msg);
			break;

		case SUBSCRIBE:
			onSubscribe((SubscribeMsg) msg);
			break;

		case UNSUBSCRIBE:
			onUnsubscribe((UnsubscribeMsg) msg);
			break;

		case PUBLISH:
			onPublish((PublishMsg) msg);
			break;

		default:
			Logger.log("broker dispatch - unhandled message type");
			break;

		}
	}

	// called from Broker after having established the underlying connection
	public void onConnect(ConnectMsg msg, Connection connection) {

		String user = msg.getUser();

		Logger.log("onConnect:" + msg.toString());
		storage.addClientSession(user, connection);
		if(storage.disconnectedCMsg.containsKey(user)) {
			Set<PublishMsg> messages = storage.disconnectedCMsg.get(user);
			ClientSession session = storage.getSession(user);
			for(PublishMsg s : messages) {
				session.send(s);
			}
			storage.subscriptions.remove(user);
		}

	}


	// called by dispatch upon receiving a disconnect message 
	public void onDisconnect(DisconnectMsg msg) {

		String user = msg.getUser();

		Logger.log("onDisconnect:" + msg.toString());

		storage.removeClientSession(user);
		storage.addClientToBuffer(user);

	}

	public void onCreateTopic(CreateTopicMsg msg) {

		Logger.log("onCreateTopic:" + msg.toString());

		// TODO: create the topic in the broker storage
		String topic = msg.getTopic();
		storage.createTopic(topic);

	}

	public void onDeleteTopic(DeleteTopicMsg msg) {

		Logger.log("onDeleteTopic:" + msg.toString());

		// TODO: delete the topic from the broker storage
		String topic = msg.getTopic();
		storage.deleteTopic(topic);
	}

	public void onSubscribe(SubscribeMsg msg) {

		Logger.log("onSubscribe:" + msg.toString());

		// TODO: subscribe user to the topic
		String user = msg.getUser();
		String topic = msg.getTopic();
		storage.addSubscriber(user, topic);
		
	}

	public void onUnsubscribe(UnsubscribeMsg msg) {

		Logger.log("onUnsubscribe:" + msg.toString());

		// TODO: unsubscribe user to the topic
		String user = msg.getUser();
		String topic = msg.getTopic();
		storage.removeSubscriber(user, topic);

	}

	public void onPublish(PublishMsg msg) {

		Logger.log("onPublish:" + msg.toString());

		// TODO: publish the message to clients subscribed to the topic
		String topic = msg.getTopic();
		
		ConcurrentHashMap<String, Set<String>> subscriptions = storage.subscriptions;
		Set<String> clients = subscriptions.get(topic);
		for(String s : clients) {
			if(storage.isDisconnected(s)) {
				storage.addDiscMsg(s, msg);
			}else {
				ClientSession session = storage.getSession(s);
				session.send(msg);
			}
			
		}
		
	}
}
