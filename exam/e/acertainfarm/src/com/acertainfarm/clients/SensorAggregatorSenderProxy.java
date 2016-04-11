package com.acertainfarm.clients;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.Event;
import com.certainfarm.Farm.FieldState;
import com.certainfarm.Farm.FieldStatus;
import com.certainfarm.utils.FarmMessageTag;
import com.certainfarm.utils.FarmUtility;

public class SensorAggregatorSenderProxy implements FieldStatus{
	
	
	protected HttpClient client;
	protected String serverAddress;

	/**
	 * Initialize the client object
	 */
	public SensorAggregatorSenderProxy(String serverAddress) throws Exception {

		setServerAddress(serverAddress);
		client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		client.setMaxConnectionsPerAddress(FarmClientConstants.CLIENT_MAX_CONNECTION_ADDRESS); // max
																									// concurrent
																									// connections
																									// to
																									// every
																									// address
		client.setThreadPool(new QueuedThreadPool(
				FarmClientConstants.CLIENT_MAX_THREADSPOOL_THREADS)); // max
																			// threads
		client.setTimeout(FarmClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS); // seconds
																					// timeout;
																					// if
																					// no
																					// server
																					// reply,
																					// the
																					// request
																					// expires
		client.start();
	
	}
	
	
	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	

	/*
	 * Send new Event to the FieldStatusHTTPServer
	 * A map containing two elements: 1.the time period and  2.the list of Event that is sent to server
	 * It waits a response from the server based on the RPC schema
	 */

	@Override
	public void update(long timePeriod, List<Event> events)
			throws AttributeOutOfBoundsException, PrecisionFarmingException {

		ContentExchange exchange = new ContentExchange();
		
		String urlString = serverAddress + "/" + FarmMessageTag.UPDATE;
		
		//	String urlString = serverAddress + "/" + FarmMessageTag.BUYBOOKS;
		
		Map<Integer,Object> sendMap = new HashMap<Integer,Object>();
		sendMap.put(1, timePeriod);
		sendMap.put(2, events);

		String listISBNsxmlString = FarmUtility
				.serializeObjectToXMLString(sendMap);
		exchange.setMethod("POST");
		exchange.setURL(urlString);
		Buffer requestContent = new ByteArrayBuffer(listISBNsxmlString);
		exchange.setRequestContent(requestContent);

		FarmUtility.SendAndRecv(this.client, exchange);
	}

	//method is not implemented since the SensorAggregator does not have to querry the server
	@Override
	public List<FieldState> query(List<Integer> fieldIds)
			throws AttributeOutOfBoundsException, PrecisionFarmingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	public void stop() {
		try {
			client.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
