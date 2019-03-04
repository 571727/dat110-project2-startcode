package no.hvl.dat110.iotsystem;

import no.hvl.dat110.client.Client;

public class TemperatureDevice {
	
	private static final int COUNT = 10;
	
	public static void main(String[] args) {
		
		TemperatureSensor sn = new TemperatureSensor();
		
		/*
		 * Connect to a broker. The broker sits and waits for an incoming connection.
		 * This has to connect to that. Then run the TemperatureSensor COUNT times and publish
		 * temperature topic whatever that is(probs read()). Then disconnect.
		 */
		Client client = new Client("TemperatureDevice","localhost", 8080);
		
		client.connect();
		
		
		for(int i = 0; i < COUNT; i++) {
			client.publish("temperature", String.valueOf(sn.read()));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("Could not sleep");
				e.printStackTrace();
			}
		}
		
		client.disconnect();
		
		System.out.println("Temperature device stopping ... ");
		
		
	}
}
