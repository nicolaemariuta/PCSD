package com.acertainfarm.tests;

import java.util.ArrayList;
import java.util.List;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.FieldState;
import com.certainfarm.Farm.FieldStatus;

public class FarmClientEmulator extends Thread {
	/**
	 * Class that emulats the role of a Farm client, sending the list of fieldIds several times
	 * Implements Thread to make it able to send requests in parallel with other proccesses
	 * Used in ConcurrentFarmTess
	 */
	
	
	List<Integer> fieldIds;
	List<FieldState> fieldStates;
	int nrSends;
	FieldStatus fieldStatusClient;

	
	public FarmClientEmulator (List<Integer> fieldIds, int nrSends, FieldStatus fieldStatus) {
		this.fieldIds = fieldIds;
		this.nrSends = nrSends;
		fieldStates = new ArrayList<FieldState>();
		this.fieldStatusClient = fieldStatus;
	}
	

	@Override
	public void run() {

		for (int i = 0; i < nrSends; i++) {
		
			try {
				
				fieldStates = fieldStatusClient.query(fieldIds);
				System.out.println(fieldStates.size());
				
				System.out.println("query");
			} catch (AttributeOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PrecisionFarmingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}

	}


	public List<FieldState> getFieldStates() {
		return fieldStates;
	}


	public void setFieldStates(List<FieldState> fieldStates) {
		this.fieldStates = fieldStates;
	}
	

}
