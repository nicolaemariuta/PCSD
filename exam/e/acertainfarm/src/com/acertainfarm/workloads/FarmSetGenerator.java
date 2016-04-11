package com.acertainfarm.workloads;

import java.util.ArrayList;
import java.util.List;

import com.certainfarm.Farm.Event;
import com.certainfarm.Farm.Measurement;
import com.certainfarm.utils.FarmConstants;

public class FarmSetGenerator {
	
	public FarmSetGenerator(){
		
	}
	

	/**
	 * Returns num randomly selected FieldIds from inpus set
	 * 
	 * @param num
	 * @return
	 */
	public List<Integer> sampleFromFieldIss(List<Integer> fileldIds, int num) {
		
		if(num>fileldIds.size())
		{
			return fileldIds;
		}
		else 
		{
			
			
			List<Integer> randoms = new ArrayList<Integer>();
			for(int i =0; i<num; i++)
			{
				while(true)
				{
				int rand = (int) (Math.random()*fileldIds.size());
					if(!randoms.contains(rand))
					{
						randoms.add(fileldIds.get(rand));
						break;
					}
				}
			}
			
			return randoms;
		}
		
	}
	
	/**
	 * Return num Measurements for newMeasurements
	 * 
	 */
	
	
	public List<Measurement> nextSetOfMeasurements(int num) {
		List<Measurement> measurements =  new ArrayList<Measurement>();
		List<Integer> fieldIds = new ArrayList<Integer>();
		
		for(int i = 0; i < num; i++)
		{
			Integer randomFieldId;
			while(true)
			{
				randomFieldId = (int) (Math.random()*FarmConstants.NUM_FIELDS)+1;
				if(!fieldIds.contains(randomFieldId))
				{
					fieldIds.add(randomFieldId);
					break;
				}
			}
		//	int sensorId, int fieldId, int currentTemperature, int currentHumidity
			measurements.add(new Measurement((int) (Math.random()*10)+1
					, randomFieldId
					, (int) (Math.random()*100)-50
					, (int) (Math.random()*100)+1 ));
			
		}
			
		return measurements;
		}
	
	
	
	/**
	 * Return num Events for update
	 * 
	 */
	
	
	public List<Event> nextSetOfUpdates(int num) {
		List<Event> events =  new ArrayList<Event>();
		List<Integer> fieldIds = new ArrayList<Integer>();
		
		for(int i = 0; i < num; i++)
		{
			Integer randomFieldId;
			while(true)
			{
				randomFieldId = (int) (Math.random()*FarmConstants.NUM_FIELDS)+1;
				if(!fieldIds.contains(randomFieldId))
				{
					fieldIds.add(randomFieldId);
					break;
				}
			}
		//	int sensorId, int fieldId, int currentTemperature, int currentHumidity
			events.add(new Event( randomFieldId
					, (int) (Math.random()*100)-50
					, (int) (Math.random()*100)+1 ));
			
		}
			
		return events;
		}
	
	


	
	

}
