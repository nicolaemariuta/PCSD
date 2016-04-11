	package com.acertainfarm.clients;

import java.util.List;

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

public class FarmClientProxy implements FieldStatus{
	
	protected HttpClient client;
	protected String serverAddress;

	/**
	 * Initialize the client object
	 */
	public FarmClientProxy(String serverAddress) throws Exception {
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
	

	
	
	
	//Method updated is not implemented because the FarmClientProxy does not have this functionality
	@Override
	public void update(long timePeriod, List<Event> events)
			throws AttributeOutOfBoundsException, PrecisionFarmingException {
		// TODO Auto-generated method stub
		//it is not needed that the farm client has to update the fieldstatus
		
	}
	
	
	/*
	 * Send new measurements to the FieldStatusHTTPServer
	 * List of Integers corresponding to FieldIds is serialized and sent to the server address 
	 * It waits a response from the server based on the RPC schema
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FieldState> query(List<Integer> fieldIds)
			throws AttributeOutOfBoundsException, PrecisionFarmingException {

		ContentExchange exchange = new ContentExchange();
		
		String urlString = serverAddress + "/" + FarmMessageTag.QUERY;
		


		String listISBNsxmlString = FarmUtility
				.serializeObjectToXMLString(fieldIds);
		exchange.setMethod("POST");
		exchange.setURL(urlString);
		Buffer requestContent = new ByteArrayBuffer(listISBNsxmlString);
		exchange.setRequestContent(requestContent);

		return (List<FieldState>) FarmUtility.SendAndRecv(this.client, exchange);

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
