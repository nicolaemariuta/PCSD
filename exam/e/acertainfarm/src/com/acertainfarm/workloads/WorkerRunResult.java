package com.acertainfarm.workloads;

/**
 * 
 * WorkerRunResult class represents the result returned by a worker class after
 * running the workload interactions
 * 
 */
public class WorkerRunResult {
	private int successfulInteractions; // total number of successful interactions
	private int totalRuns; // total number of interactions run 
	private long elapsedTimeInNanoSecs; // total time taken to run all
										// interactions
	private int successfulFarmSensorInteractionRuns; // number of
															// successful
															// frequent book
															// store interaction
															// runs
	private int totalFarmInteractionRuns; // total number of
														// farm interaction
														// runs

	public WorkerRunResult(int successfulInteractions, long elapsedTimeInNanoSecs,
			int totalRuns, int successfulFarmSensorInteractionRuns,
			int totalFarmInteractionRuns) {
		this.setSuccessfulInteractions(successfulInteractions);
		this.setElapsedTimeInNanoSecs(elapsedTimeInNanoSecs);
		this.setTotalRuns(totalRuns);
		this.setSuccessfulFarmSensorInteractionRuns(successfulFarmSensorInteractionRuns);
		this.setTotalFarmInteractionRuns(totalFarmInteractionRuns);
	}

	public int getTotalRuns() {
		return totalRuns;
	}

	public void setTotalRuns(int totalRuns) {
		this.totalRuns = totalRuns;
	}

	public int getSuccessfulInteractions() {
		return successfulInteractions;
	}

	public void setSuccessfulInteractions(int successfulInteractions) {
		this.successfulInteractions = successfulInteractions;
	}

	public long getElapsedTimeInNanoSecs() {
		return elapsedTimeInNanoSecs;
	}

	public void setElapsedTimeInNanoSecs(long elapsedTimeInNanoSecs) {
		this.elapsedTimeInNanoSecs = elapsedTimeInNanoSecs;
	}

	public int getSuccessfulFarmSensorInteractionRuns() {
		return successfulFarmSensorInteractionRuns;
	}

	public void setSuccessfulFarmSensorInteractionRuns(
			int successfulFarmSensorInteractionRuns) {
		this.successfulFarmSensorInteractionRuns = successfulFarmSensorInteractionRuns;
	}

	public int getTotalFarmInteractionRuns() {
		return totalFarmInteractionRuns;
	}

	public void setTotalFarmInteractionRuns(int totalFarmInteractionRuns) {
		this.totalFarmInteractionRuns = totalFarmInteractionRuns;
	}




}

