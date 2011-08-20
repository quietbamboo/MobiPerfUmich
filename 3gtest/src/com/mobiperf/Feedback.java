/****************************
 * This file is part of the MobiPerf project (http://mobiperf.com). 
 * We make it open source to help the research community share our efforts.
 * If you want to use all or part of this project, please give us credit and cite MobiPerf's official website (mobiperf.com).
 * The package is distributed under license GPLv3.
 * If you have any feedbacks or suggestions, don't hesitate to send us emails (3gtest@umich.edu).
 * The server suite source code is not included in this package, if you have specific questions related with servers, please also send us emails
 * 
 * Contact: 3gtest@umich.edu
 * Development Team: Junxian Huang, Birjodh Tiwana, Zhaoguang Wang, Zhiyun Qian, Cheng Chen, Yutong Pei, Feng Qian, Qiang Xu
 * Copyright: RobustNet Research Group led by Professor Z. Morley Mao, (Department of EECS, University of Michigan, Ann Arbor) and Microsoft Research
 *
 ****************************/
/****************************
*
* @Date: May 19, 2011
* @Time: 4:23:53 PM
* @Author: Junxian Huang
*
****************************/
package com.mobiperf;

public class Feedback {
	
	public static enum TYPE {
		DEVICE_ID,
		RUN_ID,
		NEW_TEST,
		AIRPLANE_MODE_CHECKING,
		AIRPLANE_MODE_ENABLED,
		GPS_CHECKING,
		GPS_VALUE,
		NETWORK_CONNECTION_CHECKING,
		NETWORK_CONNECTION_DOWN,
		CARRIER_NAME,
		NETWORK_TYPE,
		CELL_ID,
		SIGNAL_STRENGTH,
		LAC
	};
	
	/**
	 * 
	 * @param experiment something like AIRPLANE_MODE_CHECKING
	 * @param parameters 
	 * @return
	 */
	public static String getMessage(Feedback.TYPE type, String[] parameters){
		
		String message;
		switch(type){
			case DEVICE_ID:
				return "Device ID: " + InformationCenter.getDeviceID();
			case RUN_ID:
				return "Run ID: " + InformationCenter.getRunId();
			case NEW_TEST:
				return "Press \"Run\" to start a new test";
			case AIRPLANE_MODE_CHECKING:
				return "Checking airplane mode ...";
			case AIRPLANE_MODE_ENABLED:
				return "Airplane mode is enabled, MobiPerf can not be run";
			case GPS_CHECKING:
				return "Determining GPS info ...";
			case GPS_VALUE:
				if(GPS.latitude < -400 || GPS.longitude < -400){
					return "GPS: unknown";
				}
				message = "GPS: " + Math.abs(GPS.latitude);
				if(GPS.latitude > 0)
					message += "N";
				else if(GPS.latitude < 0)
					message += "S";
				message+=(", " + Math.abs(GPS.longitude));
				if(GPS.longitude > 0)
					message += "E";
				else if(GPS.longitude < 0)
					message += "W";
				return message;
			case NETWORK_CONNECTION_CHECKING:
				return "Checking network connection ...";
			case NETWORK_CONNECTION_DOWN:
				return "Seems your network is down";
			case CARRIER_NAME:
				if(parameters == null || parameters[0] == null)
					return "Carrier: unknown";
				return "Carrier: " + parameters[0];
			case NETWORK_TYPE:
				if(parameters == null || parameters[0] == null)
					return "Network type: unknown";
				return "Network type: " + parameters[0];
			case CELL_ID:
				return "Cell ID: " + parameters[0];
			case LAC:
				return "Location Area Code (LAC): " + parameters[0];
			case SIGNAL_STRENGTH:
				return "Signal strength (asu): " + parameters[0];
				
		}
		return null;
	}

}
