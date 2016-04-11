package com.acertainfarm.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;





import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.certainfarm.Farm.CertainFieldStatus;
import com.certainfarm.Farm.CertainSensorAggregator;
import com.certainfarm.Farm.Event;
import com.certainfarm.Farm.FieldState;
import com.certainfarm.Farm.Measurement;
import com.certainfarm.utils.FarmMessageTag;
import com.certainfarm.utils.FarmResponse;
import com.certainfarm.utils.FarmUtility;

public class FieldStatusHTTPMessageHandler extends AbstractHandler {
	
	private CertainFieldStatus myFieldStatus = null;

	public FieldStatusHTTPMessageHandler(CertainFieldStatus farmStatus) {
		myFieldStatus = farmStatus;
	}

	@SuppressWarnings("unchecked")
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		
		FarmMessageTag messageTag;
	
		System.out.println("handle");
		
	
		String requestURI;
		FarmResponse farmResponse = null;

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		requestURI = request.getRequestURI();

		// Need to do request multi-plexing
		if (!FarmUtility.isEmpty(requestURI)
				&& requestURI.toLowerCase().startsWith("/status")) {
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
			//handle the update message by deserializing the Map cotaining
			//the two items: the time and list of events
			//it does also call the update() method of CertainFieldStatus that handles the data in the request
			case UPDATE:
				String xml = FarmUtility
						.extractPOSTDataFromRequest(request);
				Map<Integer,Object> answer = (Map<Integer, Object>) FarmUtility
						.deserializeXMLStringToObject(xml);
				long timePeriod = (Long) answer.get(1);
				List<Event> events =  (List<Event>) answer.get(2);
	
				
				farmResponse = new FarmResponse();
				
				try {
		
					myFieldStatus.update(timePeriod, events);
				} catch (AttributeOutOfBoundsException ex) {
					// TODO Auto-generated catch block
					farmResponse.setException(ex);
				} catch (PrecisionFarmingException ex) {
					// TODO Auto-generated catch block
					farmResponse.setException(ex);
				}
				
				String listMeasurementsxmlString = FarmUtility
						.serializeObjectToXMLString(farmResponse);
				response.getWriter().println(listMeasurementsxmlString);
				break;
			//handle the query message by deserializing the list of FieldIDs store as Integers
			//it does also call the query() method of CertainFieldStatus that handles the data in the request	
			//the method returns the list of FieldStates that is sent back to the client
			case QUERY:
				 xml = FarmUtility.extractPOSTDataFromRequest(request);
				 System.out.println("query");
				 List<Integer> fieldIds = (List<Integer>) FarmUtility
							.deserializeXMLStringToObject(xml);
				 
				 
				farmResponse = new FarmResponse();
				
				
				try {
				
					farmResponse.setList(myFieldStatus.query(fieldIds));
					
				} catch (AttributeOutOfBoundsException ex) {
					// TODO Auto-generated catch block
					farmResponse.setException(ex);
					
					
					
					
				} catch (PrecisionFarmingException ex) {
					// TODO Auto-generated catch block
					farmResponse.setException(ex);
				}
				
				listMeasurementsxmlString = FarmUtility
						.serializeObjectToXMLString(farmResponse);

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
