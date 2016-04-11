package com.acertainfarm.tests;

import java.util.List;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.Measurement;
import com.certainfarm.Farm.SensorAggregator;

public class FieldSensorEmulator extends Thread {
	/**
	 * Class that emulats the role of a Field Access Point, sending the list of Measurements several times
	 * and there is also a sleep to simulate the sensor that sends measurements at defined periods of time
	 * Used in ConcurrentFarmTess
	 */
	
	
	List<Measurement> measurements;
	int nrSends;
	SensorAggregator sensorAggregator;
	
	public FieldSensorEmulator (List<Measurement> measurements, int nrSends, SensorAggregator sensorAggregator) {
		this.measurements = measurements;
		this.nrSends = nrSends;
		this.sensorAggregator = sensorAggregator;
	}
	

	@Override
	public void run() {

		for (int i = 0; i < nrSends; i++) {
		
			try {
		//		System.out.println(measurements.size());
				sensorAggregator.newMeasurements(measurements);
				Thread.sleep(500);

			} catch (AttributeOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PrecisionFarmingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
