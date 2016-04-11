package com.acertainfarm.workloads;

import java.util.ArrayList;
import java.util.List;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.Measurement;
import com.certainfarm.Farm.SensorAggregator;
import com.certainfarm.utils.FarmConstants;

public class WorkloadFieldSensor extends Thread {
	/**
	 * Class that emulats the role of a Field Access Point, sending a random list of Measurements several times
	 * Implements Thread to make it able to send requests in parallel with other proccesses
	 * Sending the measurements is done by using the proxy saved in configuration
	 */
	
	
	List<Measurement> measurements;
	int nrMeasurements;

	SensorAggregator sensorAggregator;
	WorkloadConfiguration configuration;
	
	public WorkloadFieldSensor (WorkloadConfiguration configuration) {
		measurements = new ArrayList<Measurement>();
		this.configuration = configuration;
		nrMeasurements = configuration.getNumMeasurements();
		sensorAggregator = configuration.getSensorAggregator();
	
	}
	

	@Override
	public void run() {

		for (int i = 0; i < nrMeasurements; i++) {
		
			try {
				measurements = configuration.getFarmSetGenerator().nextSetOfMeasurements(FarmConstants.NUM_FIELDS);
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
