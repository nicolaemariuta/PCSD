package com.acertainfarm.tests;

import java.util.List;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.Event;
import com.certainfarm.Farm.FieldStatus;

public class SensorSenderEmulator extends Thread {
	
	/**
	 * Class that emulats the role of a Sender of updates to FieldStatus, sending the list of Event several times
	 * Implements Thread to make it able to send requests in parallel with other proccesses
	 * Used in ConcurrentFarmTess
	 */
	
	
	List<Event> events;
	int nrSends;
	long interval;
	FieldStatus fieldStatusUpdater;
	
	public SensorSenderEmulator (List<Event> events, int nrSends, long interval, FieldStatus fieldStatus) {
		this.events = events;
		this.nrSends = nrSends;
		this.interval = interval;
		this.fieldStatusUpdater = fieldStatus; 
	}
	

	@Override
	public void run() {

		for (int i = 0; i < nrSends; i++) {

			try {
				
				fieldStatusUpdater.update(interval, events);
				System.out.println("try update");

			} catch (AttributeOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PrecisionFarmingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}

	}
	

}
