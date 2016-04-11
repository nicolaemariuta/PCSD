package com.certainfarm.Farm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.acertainfarm.clients.SensorAggregatorSenderProxy;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;

public class CertainSensorAggregator implements SensorAggregator{
	
	Map<Integer, FieldMeasurements> fieldMeasurementsMap = null; 
	int nrFields;
	long interval;
	SensorAggregatorSenderProxy  sender;
	Date firstMeasurement = null;
	ReentrantReadWriteLock rwlock = null;
	
	
	public CertainSensorAggregator(int nrFields, long interval){
		fieldMeasurementsMap = new HashMap<Integer, FieldMeasurements>();
		this.nrFields = nrFields;
		this.interval = interval;
		rwlock = new ReentrantReadWriteLock();
		
		//initialize the SensorAggregatorSenderProxy that will send updates to the proxies
		try {
			sender = new SensorAggregatorSenderProxy("http://localhost:8081/status");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Method that handles the measurements received from the Field Access Points by storing the values and 
	 * send updates when the timerstamp is over
	 */

	@Override
	public  void newMeasurements(List<Measurement> measurements)
			throws AttributeOutOfBoundsException, PrecisionFarmingException {
		
		//check if the time to send updates has come
		//places readlock because data is checked
		rwlock.readLock().lock();
		try
		{
		if(firstMeasurement == null)
			firstMeasurement = new Date();
		
		long timer = getDateDiff(new Date(), TimeUnit.SECONDS);

		if(timer >= interval){


			//create list of Events that has to be sent to FieldStatus server 
			//with average temperatures and humidities
			List<Event> events = new ArrayList<Event>();
			for (Integer key : fieldMeasurementsMap.keySet())
			{
				events.add(new Event(key, fieldMeasurementsMap.get(key).getAvgTemperature(),
						fieldMeasurementsMap.get(key).getAvgHumidity()));
			}
			//write lock placed on part of the code that updates the date that is stored
			//and sends update to the other server
			rwlock.readLock().unlock();
			rwlock.writeLock().lock();
			try {
				fieldMeasurementsMap.clear();

				sender.update(timer, events);
				firstMeasurement = new Date();
			}finally{
				rwlock.writeLock().unlock();
				rwlock.readLock().lock();
			}


		}
		}finally{
			rwlock.readLock().unlock();
		}
		//readLock placed on the part of code where measurements received are checked for errors
		rwlock.readLock().lock();
		try
		{
		
			for (Measurement measurement : measurements) {
				if (measurement.getFieldId() > nrFields || measurement.getFieldId() < 1)
					throw new AttributeOutOfBoundsException("Field number " + measurement.getFieldId() + " is invalid!");

				if (measurement.getSensorId() < 1)
					throw new AttributeOutOfBoundsException("Sensor id " + measurement.getSensorId() + " is invalid!");


				if (measurement.getCurrentTemperature() < -50 || measurement.getCurrentTemperature() > 50)
					throw new AttributeOutOfBoundsException("Temperature with value " + measurement.getCurrentTemperature() + " is invalid!");


				if (measurement.getCurrentHumidity() < 0 || measurement.getCurrentHumidity() > 100)
					throw new AttributeOutOfBoundsException("Humidity with value " + measurement.getCurrentHumidity() + " is invalid!");

			}
		}finally{
				rwlock.readLock().unlock();
			}
		//writeLock placed on the loop that updates measurements hold by SensorAggregator

		rwlock.writeLock().lock();
		try {
			for (Measurement measurement : measurements) {


					FieldMeasurements fieldMeasurements = null;

					if (fieldMeasurementsMap.containsKey(measurement.getFieldId())) {
						fieldMeasurements = fieldMeasurementsMap.get(measurement.getFieldId());

					} else {
						fieldMeasurements = new FieldMeasurements();
					}

					fieldMeasurements.addHumidity(measurement.getCurrentHumidity());
					fieldMeasurements.addTemperature(measurement.getCurrentTemperature());
					
				
					fieldMeasurementsMap.put(measurement.getFieldId(), fieldMeasurements);
					}

				}

		finally{
			rwlock.writeLock().unlock();
			}
			
		
	
	
		
	}
	
	//function to calculate difference between two Dates, based on code found on Stackoverlow.com
	private  long getDateDiff( Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - firstMeasurement.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}

	
}
