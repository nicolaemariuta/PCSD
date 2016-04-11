package com.acertainfarm.workloads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.Event;
import com.certainfarm.Farm.FieldState;
import com.certainfarm.Farm.Measurement;
import com.certainfarm.utils.FarmConstants;

public class Worker implements Callable<WorkerRunResult>{

	private WorkloadConfiguration configuration = null;
	private int numSuccessfulFarmSensorInteraction = 0;
	private int numTotalFarmInteraction = 0;

	public Worker(WorkloadConfiguration config) {
		configuration = config;
	}

	
	/**
	 * Run the appropriate interaction while trying to maintain the configured
	 * distributions
	 * 
	 * Updates the counts of total runs and successful runs for customer
	 * interaction
	 * 
	 * @param chooseInteraction
	 * @return
	 */
	
	private boolean runInteraction() {
		try {
			
			runFarmClientInteraction();
			
			numTotalFarmInteraction++;
			//	runSendorSenderInteraction();
			numSuccessfulFarmSensorInteraction++;
			
		} catch (PrecisionFarmingException ex) {
			return false;
		}
		catch (AttributeOutOfBoundsException ex) {
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * Run the workloads trying to respect the distributions of the interactions
	 * and return result in the end
	 */
	public WorkerRunResult call() throws Exception {
		int count = 1;
		long startTimeInNanoSecs = 0;
		long endTimeInNanoSecs = 0;
		int successfulInteractions = 0;
		long timeForRunsInNanoSecs = 0;


		//start field sensor threads
		for(int i = 0; i < configuration.getNrFieldSensors(); i++)
			new WorkloadFieldSensor(configuration).start();
		

		// Perform the warmup runs for client requests
		while (count++ <= configuration.getWarmUpRuns()) {
			runInteraction();
		}

		count = 1;
		numTotalFarmInteraction = 0;
		numSuccessfulFarmSensorInteraction = 0;

		// Perform the actual runs
		startTimeInNanoSecs = System.nanoTime();
		while (count++ <= configuration.getNumActualRuns()) {
		
			if (runInteraction()) {
				successfulInteractions++;
			}
		}
		endTimeInNanoSecs = System.nanoTime();
		timeForRunsInNanoSecs += (endTimeInNanoSecs - startTimeInNanoSecs);
		return new WorkerRunResult(successfulInteractions,
				timeForRunsInNanoSecs, configuration.getNumActualRuns(),
				numTotalFarmInteraction,
				numSuccessfulFarmSensorInteraction);
	}
	
	
	
	
	/**
	 * Runs the sensor sending field states to sensoaggregator
	 * 
	 * @throws PrecisionFarmingException, AttributeOutOfBoundsException
	 */
	
	private void runFarmSensorInteraction() throws PrecisionFarmingException, AttributeOutOfBoundsException {
		
		List<Measurement> measurements = configuration.getFarmSetGenerator().nextSetOfMeasurements(configuration.getNumMeasurements());
		configuration.getSensorAggregator().newMeasurements(measurements);
		
	}
	
	/**
	 * Runs the farm client sending querris to FieldStatus server
	 * 
	 * @throws PrecisionFarmingException, AttributeOutOfBoundsException
	 */
	
	private void runFarmClientInteraction() throws PrecisionFarmingException, AttributeOutOfBoundsException {
		
		List<Integer> fieldIds = new ArrayList<Integer>();
		for(int i = 0; i < FarmConstants.NUM_FIELDS; i++)
			fieldIds.add(i+1);
		
		
		List<Integer> fieldIdsToSend = configuration.getFarmSetGenerator()
				.sampleFromFieldIss(fieldIds, configuration.getNumFieldToQuerry());
		
		configuration.getClient().query(fieldIdsToSend);
		
		
		
	}
	
	/**
	 * Runs the sender from SensorAggregator; sending updates to the FieldStatus server
	 * 
	 * @throws PrecisionFarmingException, AttributeOutOfBoundsException
	 */
	
	private void runSendorSenderInteraction() throws PrecisionFarmingException, AttributeOutOfBoundsException {
		
		List<Event> events = configuration.getFarmSetGenerator().nextSetOfUpdates(configuration.getNumUpdates());
		configuration.getSender().update(FarmConstants.INTERVAL, events);
		
	}
	
	
	
	
	
	
	
	
	
	
	

}
