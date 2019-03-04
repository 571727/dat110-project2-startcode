package no.hvl.dat110.iotsystem;

import no.hvl.dat110.client.Client;
import no.hvl.dat110.messages.Message;
import no.hvl.dat110.messages.PublishMsg;

public class DisplayDevice {
	
	private static final int COUNT = 10;
		
	public static void main (String[] args) {
		
		System.out.println("Display starting ...");
		
		/*
		 * Connect to the same broker as the temperaturedevice. Make a temperature topic
		 */
		String topic = "temperature";
		Client client = new Client("DisplayClient", "localhost", 8080);
		
		client.connect();
		client.createTopic(topic);
		client.subscribe(topic);
		
		for(int i = 0; i < COUNT; i++) {
			Message msg = client.receive();
			System.err.println("DISPLAYDEVICE: " + msg.toString());
		}
		
		client.disconnect();
		System.out.println("Display stopping ... ");
		
		
	}
}
