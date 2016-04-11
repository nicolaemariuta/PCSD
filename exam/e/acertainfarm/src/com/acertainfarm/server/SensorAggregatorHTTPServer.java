package com.acertainfarm.server;

/*
 * Server of SensorAggregator that initializes the CertainSensorAggregator that handles the data from messages
 * and the SensorAggregatorHTTPMessageHandler that is handling the requests from the client
 * ten, the server is started
 */
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.CertainSensorAggregator;
import com.certainfarm.utils.FarmConstants;

public class SensorAggregatorHTTPServer {
	
	public static void main(String[] args) throws PrecisionFarmingException {
		
		 CertainSensorAggregator sensorAggregator = new CertainSensorAggregator(FarmConstants.NUM_FIELDS, FarmConstants.INTERVAL);
		int listen_on_port = 8083;
		SensorAggregatorHTTPMessageHandler handler = new SensorAggregatorHTTPMessageHandler(
				sensorAggregator);
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
