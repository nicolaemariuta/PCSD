package com.acertainfarm.tests;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.CertainSensorAggregator;
import com.certainfarm.Farm.Measurement;
import com.certainfarm.Farm.SensorAggregator;
import com.certainfarm.utils.FarmConstants;



public class SensorAggregatorTests {
	
	private static final int NUM_FIELDS =16;
	private static final long TEST_INTERVAL = 5;

	public static SensorAggregator sensorAggregator;
	
	
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			sensorAggregator = new CertainSensorAggregator(NUM_FIELDS,TEST_INTERVAL);						
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests basic newMeasurements() functionality
	 */
	@Test
	public void testMeasurements()
	{
		
		List<Measurement> measurements = new ArrayList<Measurement>();
		//int sensorId, int fieldId, int currentTemperature, int currentHumidity
		measurements.add(new Measurement(1,3,-20,30));
		measurements.add(new Measurement(2,2,-20,30));
		measurements.add(new Measurement(8,1,-20,30));
		measurements.add(new Measurement(2,3,-20,30));
		measurements.add(new Measurement(3,5,-20,30));
		measurements.add(new Measurement(7,3,-20,30));

		
		try {
			sensorAggregator.newMeasurements(measurements);
		} catch (AttributeOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrecisionFarmingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests if exception is thrown in case of wrong fieldID 
	 */
	@Test
	public void testUpdateFieldID()
	{
		
		List<Measurement> measurements = new ArrayList<Measurement>();
		//int sensorId, int fieldId, int currentTemperature, int currentHumidity
		measurements.add(new Measurement(1,3,-20,30));
		measurements.add(new Measurement(8,18,-20,30));
		measurements.add(new Measurement(2,3,-20,30));
	

		
		try {
			sensorAggregator.newMeasurements(measurements);
			
		
		} catch (AttributeOutOfBoundsException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrecisionFarmingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Tests if exception is thrown in case of wrong temperature
	 */
	@Test
	public void testUpdateTemperature()
	{
		
		List<Measurement> measurements = new ArrayList<Measurement>();
		//int sensorId, int fieldId, int currentTemperature, int currentHumidity
		measurements.add(new Measurement(1,3,-20,30));
		measurements.add(new Measurement(8,18,-20,30));
		measurements.add(new Measurement(2,3,-20,30));
	

		try {
			sensorAggregator.newMeasurements(measurements);
			fail();
		
		} catch (AttributeOutOfBoundsException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrecisionFarmingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Tests if exception is thrown in case of wrong humidity
	 */
	@Test
	public void testUpdateHumidity()
	{
		
		List<Measurement> measurements = new ArrayList<Measurement>();
		//int sensorId, int fieldId, int currentTemperature, int currentHumidity
		measurements.add(new Measurement(1,3,-20,110));
		measurements.add(new Measurement(8,18,-20,30));
		measurements.add(new Measurement(2,3,-20,30));
	

		try {
			sensorAggregator.newMeasurements(measurements);
			fail();
		
		} catch (AttributeOutOfBoundsException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrecisionFarmingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}
	

}
