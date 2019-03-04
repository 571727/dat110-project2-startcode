package no.hvl.dat110.broker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import no.hvl.dat110.common.Logger;
import no.hvl.dat110.messages.PublishMsg;
import no.hvl.dat110.messagetransport.Connection;

public class Storage {

	protected ConcurrentHashMap<String, Set<String>> subscriptions;
	protected ConcurrentHashMap<String, ClientSession> clients;
	protected ConcurrentHashMap<String, Set<PublishMsg>> disconnectedCMsg;

	public Storage() {
		subscriptions = new ConcurrentHashMap<String, Set<String>>();
		clients = new ConcurrentHashMap<String, ClientSession>();
		disconnectedCMsg = new ConcurrentHashMap<String, Set<PublishMsg>>();
	}

	public Collection<ClientSession> getSessions() {
		return clients.values();
	}

	public Set<String> getTopics() {

		return subscriptions.keySet();

	}

	public ClientSession getSession(String user) {

		ClientSession session = clients.get(user);

		return session;
	}

	public Set<String> getSubscribers(String topic) {

		return (subscriptions.get(topic));

	}

	public void addClientSession(String user, Connection connection) {
		ClientSession session = new ClientSession(user, connection);
		clients.put(user, session);
		
	}

	public void removeClientSession(String user) {

		clients.remove(user);
		
	}

	public void createTopic(String topic) {
		subscriptions.put(topic, new HashSet<String>());
	
	}

	public void deleteTopic(String topic) {

		subscriptions.remove(topic);
		
	}

	public void addSubscriber(String user, String topic) {
		//Set<String> users = subscriptions.get(topic);
		
		//users.add(user);
		subscriptions.get(topic).add(user);
		//subscriptions.put(topic, users);
		
	}

	public void removeSubscriber(String user, String topic) {
		Set<String> users = subscriptions.get(topic);
		users.remove(user);
		subscriptions.put(topic, users);
	}

	public void addClientToBuffer(String user) {
		
		disconnectedCMsg.put(user, new HashSet<PublishMsg>());
		
	}

	public boolean isDisconnected(String s) {
		return !clients.containsKey(s);
	}

	public void addDiscMsg(String user, PublishMsg msg) {
		disconnectedCMsg.get(user).add(msg);
		
	}
}
