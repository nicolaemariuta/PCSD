package com.acertainfarm.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.CertainFieldStatus;
import com.certainfarm.Farm.CertainSensorAggregator;
import com.certainfarm.Farm.Event;
import com.certainfarm.Farm.FieldState;
import com.certainfarm.Farm.FieldStatus;
import com.certainfarm.Farm.Measurement;
import com.certainfarm.Farm.SensorAggregator;

public class FieldStatusTest {
	
	private static final int NUM_FIELDS =16;
	private static final long TEST_INTERVAL = 5;

	public static FieldStatus fieldStatus;
	
	
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			fieldStatus = new CertainFieldStatus(NUM_FIELDS,TEST_INTERVAL);						
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Tests basic update() functionality
	 */
	@Test
	public void testUpdate()
	{
		long period = 5;
		
		List<Event> events = new ArrayList<Event>();
		//int fieldId, int avgTemperature, int avgHumidity
		events.add(new Event(3,-50,20));
		events.add(new Event(7,-20,30));
		events.add(new Event(5,-10,50));
		
		try {
			fieldStatus.update(period,events);
		} catch (AttributeOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrecisionFarmingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Tests basic query() functionality
	 */
	@Test
	public void testQuery()
	{
	
		
		List<Integer> fieldids = new ArrayList<Integer>();
		//int fieldId, int avgTemperature, int avgHumidity
		fieldids.add(1);
		fieldids.add(2);
		fieldids.add(3);
		
		List<FieldState> fieldStates = null;
		
		try {
			 fieldStates = fieldStatus.query(fieldids);
		} catch (AttributeOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrecisionFarmingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		assertTrue(fieldStates.size() ==0);
	}
	
	
	
	
	
	
	
	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}
	


}
