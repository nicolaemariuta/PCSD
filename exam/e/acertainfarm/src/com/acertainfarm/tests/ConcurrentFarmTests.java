package com.acertainfarm.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainfarm.clients.FarmClientProxy;
import com.acertainfarm.clients.FieldAccessPointProxy;
import com.acertainfarm.clients.SensorAggregatorSenderProxy;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.CertainFieldStatus;
import com.certainfarm.Farm.CertainSensorAggregator;
import com.certainfarm.Farm.Event;
import com.certainfarm.Farm.FieldState;
import com.certainfarm.Farm.FieldStatus;
import com.certainfarm.Farm.Measurement;
import com.certainfarm.Farm.SensorAggregator;
import com.certainfarm.utils.FarmConstants;

public class ConcurrentFarmTests {
	
	/* Tests for cheking how the clients interraction works with the server in the system
	 * I have created this class to only run it with localTest = false
	 */

	private static boolean localTest = false;
	private static SensorAggregator sensorAggregator;
	private static FieldStatus fieldStatusClient;
	private static FieldStatus fieldStatusUpdater;
	
	

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			String localTestProperty = System
					.getProperty(FarmConstants.PROPERTY_KEY_LOCAL_TEST);
			localTest = (localTestProperty != null) ? Boolean
					.parseBoolean(localTestProperty) : localTest;
			if (localTest) {
				sensorAggregator = new CertainSensorAggregator(FarmConstants.NUM_FIELDS,FarmConstants.INTERVAL);
				fieldStatusClient  = new CertainFieldStatus(FarmConstants.NUM_FIELDS,FarmConstants.INTERVAL);
				fieldStatusUpdater  = new CertainFieldStatus(FarmConstants.NUM_FIELDS,FarmConstants.INTERVAL);
			} else {
				sensorAggregator = new FieldAccessPointProxy("http://localhost:8083/sensor");
				fieldStatusClient = new FarmClientProxy("http://localhost:8081/status");
				fieldStatusUpdater = new SensorAggregatorSenderProxy("http://localhost:8081/status");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Tests full system if it works properly with sensors sending measurements, aggregator sending updates to FieldStatus
	 * and clients querry the FieldStatus
	 */
	
	@Test
	public void testFullSystem()
	{
		List<Measurement> measurements = new ArrayList<Measurement>();
		//int sensorId, int fieldId, int currentTemperature, int currentHumidity
		measurements.add(new Measurement(1,1,-11,21));
		measurements.add(new Measurement(2,2,-12,22));
		measurements.add(new Measurement(3,3,-13,23));
		measurements.add(new Measurement(4,4,-14,24));
		measurements.add(new Measurement(5,5,-15,25));
		measurements.add(new Measurement(6,6,-16,26));
		
		
		FieldSensorEmulator sensorEmulator = new FieldSensorEmulator(measurements,15, sensorAggregator);
		
		
		List<Integer> fieldIds = new ArrayList<Integer>();
		fieldIds.add(1);
		fieldIds.add(2);
		fieldIds.add(3);
		fieldIds.add(4);
		fieldIds.add(5);
		fieldIds.add(6);
		
		FarmClientEmulator clientEmmulator = new FarmClientEmulator(fieldIds,1, fieldStatusClient);
		
		
		sensorEmulator.start();
		try {
			sensorEmulator.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		clientEmmulator.start();
		
		try {
			sensorEmulator.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		List<FieldState> clientQueryAnswer = clientEmmulator.getFieldStates();
		System.out.println("thread" + clientQueryAnswer.size());
	
		
		for(int i = 0; i< clientQueryAnswer.size(); i++)
		{
			Measurement measurement = measurements.get(i);
			FieldState fieldState = clientQueryAnswer.get(i);
	
			assertTrue(measurement.getFieldId() == fieldState.getFieldId()
					&& measurement.getCurrentHumidity() == fieldState.getHumidity()
					&& measurement.getCurrentTemperature() ==  fieldState.getTemperature());
		}
		
		
	}
	
	
	/**
	 * Tests how updates sent to FieldStatus and queries are working when FieldStatus is alsone, without having the
	 * Sensoraggregator
	 */
	
		@Test
	public void testFieldStatus()
	{
		List<Event> events =  new ArrayList<Event>();

		//int sensorId, int fieldId, int currentTemperature, int currentHumidity
		events.add(new Event(1,-11,21));
		events.add(new Event(2,-12,22));
		events.add(new Event(3,-13,23));
		events.add(new Event(4,-14,24));
		events.add(new Event(5,-15,25));
		events.add(new Event(6,-16,26));
		
		
		SensorSenderEmulator sensorEmulator = new SensorSenderEmulator(events,1,2, fieldStatusUpdater);
		
		
		List<Integer> fieldIds = new ArrayList<Integer>();
		fieldIds.add(1);
		fieldIds.add(2);
		fieldIds.add(3);
		fieldIds.add(4);
		fieldIds.add(5);
		fieldIds.add(6);
		
		FarmClientEmulator clientEmmulator = new FarmClientEmulator(fieldIds,1, fieldStatusClient);
		
		
		sensorEmulator.start();
		
		try {
			sensorEmulator.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		clientEmmulator.start();
		

		List<FieldState> clientQueryAnswer = clientEmmulator.getFieldStates();
		System.out.println(clientQueryAnswer.size());
	
		assertTrue(clientQueryAnswer.size() == fieldIds.size());

		
		for(int i = 0; i< clientQueryAnswer.size(); i++)
		{
			Event event = events.get(i);
			FieldState fieldState = clientQueryAnswer.get(i);
			
			assertTrue(event.getFieldId() == fieldState.getFieldId()
					&& event.getAvgHumidity() == fieldState.getHumidity()
					&& event.getAvgTemperature() ==  fieldState.getTemperature());
		}
		
		
	}


	
	
	
	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		if (!localTest) {
			((FieldAccessPointProxy) sensorAggregator).stop();
			((FarmClientProxy) fieldStatusClient).stop();
			((SensorAggregatorSenderProxy) fieldStatusUpdater).stop();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


}
