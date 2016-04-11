package com.certainfarm.Farm;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FieldMeasurements {
	
	
	/**
	 * The identifier for the sensor that originated the measurement.
	 */

	
	private ArrayList<Integer> temperatures = null;
	private ArrayList<Integer> humidities = null;

	
	public FieldMeasurements(){
		temperatures = new ArrayList<Integer>();
		humidities = new ArrayList<Integer>();
	}
	
	/**
	 * Functions that returns the average temperature and humidity calulated over all values that are accumulated 
	 * and clears the array of temperatures that are stored so new temperatures and humidity will be accumulated
	 */
	public int getAvgTemperature()
	{
		int sum = 0;

		for(Integer temp: temperatures)
			sum += temp;
		
		
	//	temperatures.clear();
		
	
		return (sum/temperatures.size());

	}
	
	public int getAvgHumidity()
	{
		int sum = 0;

		for(Integer hum: humidities)
			sum += hum;

		return (sum/humidities.size());
	
	}
	
	
	/**
	 * Functions to add new temperatures and humidities to the lists
	 */
	
	
	public void addTemperature(int temperature)
	{
		temperatures.add(temperature);
	
	}
	
	public void addHumidity(int humidity)
	{
		humidities.add(humidity);
	}
	
	


	
	
	

}
