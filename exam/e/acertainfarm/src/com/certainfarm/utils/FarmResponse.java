package com.certainfarm.utils;

import java.util.List;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.certainfarm.Farm.SensorAggregator;



public class FarmResponse {
	private Exception exception;
	private List<?> list;

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public FarmResponse(Exception exception, List<SensorAggregator> list) {
		this.setException(exception);
		this.setList(list);
	}

	public FarmResponse() {
		this.setException(null);
		this.setList(null);
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

}
