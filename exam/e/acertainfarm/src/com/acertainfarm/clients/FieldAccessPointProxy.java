package com.acertainfarm.clients;

import java.util.List;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;


import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.Measurement;
import com.certainfarm.Farm.SensorAggregator;
import com.certainfarm.utils.FarmMessageTag;
import com.certainfarm.utils.FarmUtility;

public class FieldAccessPointProxy implements SensorAggregator {
	
	
	protected HttpClient client;
	protected String serverAddress;

	/**
	 * Initialize the client object
	 */
	
	public FieldAccessPointProxy(String serverAddress) throws Exception {
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
	 * Send new measurements to the SensorAggregatorHTTPServer
	 * List of Measurement if serialized and sent to the Server
	 * It waits a response from the server based on the RPC schema
	 */

	@Override
	public void newMeasurements(List<Measurement> measurements)
			throws AttributeOutOfBoundsException, PrecisionFarmingException {

		ContentExchange exchange = new ContentExchange();
		
		String urlString = serverAddress + "/" + FarmMessageTag.NEWMEASUREMENTS;
		
		String listISBNsxmlString = FarmUtility
				.serializeObjectToXMLString(measurements);
		exchange.setMethod("POST");
		exchange.setURL(urlString);
		Buffer requestContent = new ByteArrayBuffer(listISBNsxmlString);
		exchange.setRequestContent(requestContent);

		FarmUtility.SendAndRecv(this.client, exchange);
		
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
