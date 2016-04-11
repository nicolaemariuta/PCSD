package com.acertainfarm.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.CertainSensorAggregator;
import com.certainfarm.Farm.Measurement;
import com.certainfarm.utils.FarmMessageTag;
import com.certainfarm.utils.FarmResponse;
import com.certainfarm.utils.FarmUtility;


public class SensorAggregatorHTTPMessageHandler extends AbstractHandler{

	private CertainSensorAggregator mySensorAggregator = null;

	public SensorAggregatorHTTPMessageHandler(CertainSensorAggregator sensorAggregator) {
		mySensorAggregator = sensorAggregator;
	}

	@SuppressWarnings("unchecked")
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		
		FarmMessageTag messageTag;

		
		String requestURI;
		FarmResponse farmSensorResponse = null;

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		requestURI = request.getRequestURI();

		// Need to do request multi-plexing
		if (!FarmUtility.isEmpty(requestURI)
				&& requestURI.toLowerCase().startsWith("/sensor")) {
			messageTag = FarmUtility.convertURItoMessageTag(requestURI
					.substring(7)); // the request is from store
			// manager, more
			// sophisticated security
			// features could be added
			// here
		} else {
			messageTag = FarmUtility.convertURItoMessageTag(requestURI);
		}
		// the RequestURI before the switch
		if (messageTag == null) {
			System.out.println("Unknown message tag");
		} else {
			switch (messageTag) {
			//Handle the message newMeasurements from the field access points
			//The list of Measurement is deserialized and 
			case NEWMEASUREMENTS:
		
				String xml = FarmUtility
						.extractPOSTDataFromRequest(request);

				List<Measurement> measurements = (List<Measurement>) FarmUtility
						.deserializeXMLStringToObject(xml);

				farmSensorResponse = new FarmResponse();
				
				try {
					mySensorAggregator.newMeasurements(measurements);
				} catch (AttributeOutOfBoundsException ex) {
					// TODO Auto-generated catch block
					farmSensorResponse.setException(ex);
				} catch (PrecisionFarmingException ex) {
					// TODO Auto-generated catch block
					farmSensorResponse.setException(ex);
				}
			
				String listMeasurementsxmlString = FarmUtility
						.serializeObjectToXMLString(farmSensorResponse);
				response.getWriter().println(listMeasurementsxmlString);
				break;

			
				
			default:
				System.out.println("Unhandled message tag");
				break;
			}
		}
		// Mark the request as handled so that the HTTP response can be sent
		baseRequest.setHandled(true);

	}

}
