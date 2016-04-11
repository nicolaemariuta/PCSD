package com.certainfarm.Farm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;

public class CertainFieldStatus  implements FieldStatus{


	Map<Integer, FieldState> fieldState = null; 
	int nrFields;
	long expectedTimePeriod;
	File logFile = null;
	ReentrantReadWriteLock rwlock = null;
	
	
	
	public CertainFieldStatus(int nrFields, long expectedTimePeriod){
	
		this.nrFields = nrFields;
		this.expectedTimePeriod = expectedTimePeriod;
		fieldState = new HashMap<Integer, FieldState>();
		rwlock = new ReentrantReadWriteLock();
		
		logFile = new File ("log.txt");
		
	
	}
	
	
	public  void update(long timePeriod, List<Event> events)
			throws AttributeOutOfBoundsException, PrecisionFarmingException {
		
		//write lock placed on the function because it has to change data that is stored in memory
		rwlock.writeLock().lock();
		try
		{

		System.out.println("update receive");
		
		String writeToLog = "UPDATE received at " + new Date().toString() + " with values: ";
		
		//check for errors that should generate Exceptions
		if(timePeriod > expectedTimePeriod*2)
			throw new PrecisionFarmingException("Error at the temperature update timer");

		for(Event event: events){
			
			if(event.getFieldId() > nrFields || event.getFieldId() < 1)
				throw new AttributeOutOfBoundsException("Field number " + event.getFieldId() + " is invalid!" );
			
			if(event.getAvgTemperature() <-50 ||event.getAvgTemperature() >50 )
				throw new AttributeOutOfBoundsException("Temperature with value " + event.getAvgTemperature()+ " is invalid!" );
			
		
			if(event.getAvgHumidity() <0 ||event.getAvgHumidity() >100 )
				throw new AttributeOutOfBoundsException("Humidity with value " + event.getAvgHumidity() + " is invalid!" );
		
			//update the FieldStates that are stored
			fieldState.put(event.getFieldId(), new FieldState(event.getFieldId(), event.getAvgTemperature(), event.getAvgHumidity()));
			writeToLog = writeToLog + "FieldID=" + event.getFieldId() +"(Temperature=" + 
					event.getAvgTemperature() + "," +"Humidity="+ event.getAvgHumidity() + ")";
		}
		
		//log of operations
		//also write old information into log:
		writeToLog += "PrevFieldStates:(";
		for(Integer key: fieldState.keySet())
		{
			FieldState state = fieldState.get(key);
			writeToLog += "(FieldID=" + state.getFieldId() + ",Temperature=" 
					+ state.getTemperature() + ",Humidity=" + state.getHumidity() + ");";
			
		}
		writeToLog += ")";
		
		
		//write to file
		try {
			PrintWriter outFile;
		
			outFile = new PrintWriter(new FileWriter(logFile, true));
			outFile.println (writeToLog);
			outFile.close (); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
		}finally{
			rwlock.writeLock().unlock();
		}

		
	}

	@Override
	public  List<FieldState> query(List<Integer> fieldIds)
			throws AttributeOutOfBoundsException, PrecisionFarmingException {
		//place read lock on this function with role of reading data situated in memory
		rwlock.readLock().lock();
		List<FieldState> answer = new ArrayList<FieldState> ();
		try
		{
	
	
		//check if fieldIds that are reuqested by client are valid and 
		//if they are contained in the FieldState the values are returned
		for(Integer fieldID : fieldIds)
		{
			if(fieldID > nrFields || fieldID <1 )
				throw new AttributeOutOfBoundsException("Field number " + fieldID + " is invalid!" );
			
			if( fieldState.containsKey(fieldID))
				answer.add(fieldState.get(fieldID));
		}}finally{
			rwlock.readLock().unlock();
		}
		
		
		return answer;
	}
	
	
	
	
	
	
	

}
