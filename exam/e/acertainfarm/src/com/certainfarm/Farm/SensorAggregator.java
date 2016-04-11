package com.certainfarm.Farm;

import java.util.List;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;

/**
 * The SensorAggregator service assigns measurements to time periods and
 * averages them by time period and field. These averaged measurements are
 * packaged into events at the end of a time period and sent to the FieldStatus
 * service.
 * 
 * @author vmarcos
 */
public interface SensorAggregator {

	/**
	 * Accepts a batch of measurements from the farm fields, originating from
	 * field sensors and relayed by a field access point.
	 * 
	 * @param measurements
	 *            - a list of measurements relayed by a field access point
	 */
	public void newMeasurements(List<Measurement> measurements)
			throws AttributeOutOfBoundsException, PrecisionFarmingException;

}
