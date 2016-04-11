package com.acertainfarm.workloads;

import com.certainfarm.Farm.FieldStatus;
import com.certainfarm.Farm.SensorAggregator;

public class WorkloadConfiguration {
	
	//includes parameters the workload
	private int numMeasurements = 500;
	private int nrFieldSensors = 5;
	private int numUpdates = 10;
	private int numQuerries = 55;
	private int numFieldToQuerry = 15;
	private int warmUpRuns = 10;
	private int numActualRuns =14;
	
	
	
	
	private FarmSetGenerator farmSetGenerator = null;
	private SensorAggregator sensorAggregator = null;
	private FieldStatus sender = null;
	private FieldStatus client = null;
	
	
	public WorkloadConfiguration(SensorAggregator sensorAggregator, FieldStatus sender, FieldStatus client){
		farmSetGenerator = new FarmSetGenerator();
		this.sensorAggregator = sensorAggregator;
		this.sender = sender;
		this.client = client;
	}


	public int getNumMeasurements() {
		return numMeasurements;
	}


	public void setNumMeasurements(int numMeasurements) {
		this.numMeasurements = numMeasurements;
	}


	public int getNumUpdates() {
		return numUpdates;
	}


	public void setNumUpdates(int numUpdates) {
		this.numUpdates = numUpdates;
	}


	public int getNumQuerries() {
		return numQuerries;
	}


	public void setNumQuerries(int numQuerries) {
		this.numQuerries = numQuerries;
	}


	public int getWarmUpRuns() {
		return warmUpRuns;
	}


	public void setWarmUpRuns(int warmUpRuns) {
		this.warmUpRuns = warmUpRuns;
	}


	public int getNumActualRuns() {
		return numActualRuns;
	}


	public void setNumActualRuns(int numActualRuns) {
		this.numActualRuns = numActualRuns;
	}



	public FarmSetGenerator getFarmSetGenerator() {
		return farmSetGenerator;
	}


	public void setFarmSetGenerator(FarmSetGenerator farmSetGenerator) {
		this.farmSetGenerator = farmSetGenerator;
	}


	public SensorAggregator getSensorAggregator() {
		return sensorAggregator;
	}


	public void setSensorAggregator(SensorAggregator sensorAggregator) {
		this.sensorAggregator = sensorAggregator;
	}


	public FieldStatus getSender() {
		return sender;
	}


	public void setSender(FieldStatus sender) {
		this.sender = sender;
	}


	public FieldStatus getClient() {
		return client;
	}


	public void setClient(FieldStatus client) {
		this.client = client;
	}


	public int getNumFieldToQuerry() {
		return numFieldToQuerry;
	}


	public void setNumFieldToQuerry(int numFieldToQuerry) {
		this.numFieldToQuerry = numFieldToQuerry;
	}


	public int getNrFieldSensors() {
		return nrFieldSensors;
	}


	public void setNrFieldSensors(int nrFieldSensors) {
		this.nrFieldSensors = nrFieldSensors;
	}

	
	
}
