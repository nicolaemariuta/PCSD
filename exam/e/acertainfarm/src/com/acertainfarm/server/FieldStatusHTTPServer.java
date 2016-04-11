package com.acertainfarm.server;

import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.CertainFieldStatus;
import com.certainfarm.Farm.CertainSensorAggregator;
import com.certainfarm.utils.FarmConstants;

/**
 * Server of FieldStatus that initializes the CertainFieldStatus that handles the data from messages
 * and the FieldStatusHTTPMessageHandler that is handling the requests from the client
 * ten, the server is started
 */
public class FieldStatusHTTPServer {
	
	public static void main(String[] args) throws PrecisionFarmingException {
		
		 CertainFieldStatus fieldStatus = new CertainFieldStatus(FarmConstants.NUM_FIELDS, FarmConstants.INTERVAL);
		int listen_on_port = 8081;
		FieldStatusHTTPMessageHandler handler = new FieldStatusHTTPMessageHandler(
				fieldStatus);
		String server_port_string = System.getProperty(FarmConstants.PROPERTY_KEY_SERVER_PORT);
	
	if (server_port_string != null) {
			try {
				listen_on_port = Integer.parseInt(server_port_string);
			} catch (NumberFormatException ex) {
				System.err.println(ex);
			}
		}
		
		if (FarmHTTPServerUtility.createServer(listen_on_port, handler)) {
			;
		}
		
		
	}

}
