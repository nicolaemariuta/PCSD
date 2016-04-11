package com.acertainfarm.workloads;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.acertainfarm.clients.FarmClientProxy;
import com.acertainfarm.clients.FieldAccessPointProxy;
import com.acertainfarm.clients.SensorAggregatorSenderProxy;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.CertainFieldStatus;
import com.certainfarm.Farm.CertainSensorAggregator;
import com.certainfarm.Farm.FieldStatus;
import com.certainfarm.Farm.SensorAggregator;
import com.certainfarm.utils.FarmConstants;

/**
 * 
 * CertainWorkload class runs the workloads by different workers concurrently.
 * It configures the environment for the workers using WorkloadConfiguration
 * objects and reports the metrics
 * 
 */

public class CertainWorkload {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int numConcurrentWorkloadThreads = 1;
		// String serverAddress = "http://localhost:8081";
		boolean localTest = false;
		List<WorkerRunResult> workerRunResults = new ArrayList<WorkerRunResult>();
		List<Future<WorkerRunResult>> runResults = new ArrayList<Future<WorkerRunResult>>();

		// Initialize the RPC interfaces if its not a localTest, the variable is
		// overriden if the property is set
		String localTestProperty = System
				.getProperty(FarmConstants.PROPERTY_KEY_LOCAL_TEST);
		// localTest = (localTestProperty != null) ? Boolean
		// .parseBoolean(localTestProperty) : localTest;

		SensorAggregator sensorAggregator = null;
		FieldStatus aggregatorSender = null;
		FieldStatus farmClient = null;

		if (localTest) {
			sensorAggregator = new CertainSensorAggregator(
					FarmConstants.NUM_FIELDS, FarmConstants.INTERVAL);
			CertainFieldStatus fieldStatus = new CertainFieldStatus(
					FarmConstants.NUM_FIELDS, FarmConstants.INTERVAL);
			aggregatorSender = fieldStatus;
			farmClient = fieldStatus;
		} else {
			sensorAggregator = new FieldAccessPointProxy(
					"http://localhost:8083/sensor");
			farmClient = new FarmClientProxy("http://localhost:8081/status");
			aggregatorSender = new SensorAggregatorSenderProxy(
					"http://localhost:8081/status");

		}

		// Generate data in the bookstore before running the workload
		initializeFarmData(sensorAggregator, farmClient, aggregatorSender);

		ExecutorService exec = Executors
				.newFixedThreadPool(numConcurrentWorkloadThreads);

		for (int i = 0; i < numConcurrentWorkloadThreads; i++) {
			WorkloadConfiguration config = new WorkloadConfiguration(
					sensorAggregator, aggregatorSender, farmClient);
			Worker workerTask = new Worker(config);
			// Keep the futures to wait for the result from the thread
			runResults.add(exec.submit(workerTask));
		}

		// Get the results from the threads using the futures returned
		for (Future<WorkerRunResult> futureRunResult : runResults) {
			WorkerRunResult runResult = futureRunResult.get(); // blocking call
			workerRunResults.add(runResult);
		}

		exec.shutdownNow(); // shutdown the executor

		// Finished initialization, stop the clients if not localTest
		if (!localTest) {
			System.out.println("stop");
			((FieldAccessPointProxy) sensorAggregator).stop();
			((FarmClientProxy) farmClient).stop();
			((SensorAggregatorSenderProxy) aggregatorSender).stop();
		}

		reportMetric(workerRunResults);
	}

	/**
	 * Computes the metrics and prints them
	 * 
	 * @param workerRunResults
	 */
	public static void reportMetric(List<WorkerRunResult> workerRunResults) {
		// TODO: You should aggregate metrics and output them for plotting here

		double totalSuccFreq = 0;
		double totalFreq = 0;
		double totalTime = 0;
		for (WorkerRunResult workerRunResult : workerRunResults) {
			totalSuccFreq += workerRunResult.getSuccessfulInteractions();
			totalFreq += workerRunResult.getTotalFarmInteractionRuns();
			totalTime += workerRunResult.getElapsedTimeInNanoSecs();
		}
		// average seconds elapsed for workers
		double averageTimeSeconds = (totalTime / workerRunResults.size()) / 1000000000; // 10^9
		double goodput = totalSuccFreq / averageTimeSeconds;
		double throughput = totalFreq / averageTimeSeconds;
		double latency = averageTimeSeconds
				/ (totalSuccFreq / workerRunResults.size());

		System.out.println("goodput=" + goodput + "throughput=" + throughput
				+ "latency=" + latency);
		
		
		
		try {
			
			File file = new File("metric.txt");
			PrintWriter outFile = new PrintWriter(new FileWriter(file));
			String write = 14+"," + goodput + "," + throughput
					+ "," + latency; 
			outFile.println (write);
			outFile.close (); 
			System.out.println(write);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	

	}

	public static void initializeFarmData(SensorAggregator sensorAggregator,
			FieldStatus sender, FieldStatus client)
			throws PrecisionFarmingException, AttributeOutOfBoundsException {

		// TODO: Init some data for fieldstatus
		FarmSetGenerator generator = new FarmSetGenerator();
		sender.update(FarmConstants.INTERVAL, generator.nextSetOfUpdates(10));
	}

}
