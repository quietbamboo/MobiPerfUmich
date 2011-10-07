package com.mobiperf.PerfNearMe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ServerParser {
	 // Each array is of size 30
	 public static final int SIZE = 30;
	
	 private float[] latitudes = new float[SIZE], upTPs = new float[SIZE], downTPs = new float[SIZE];
	 private float[] longitudes = new float[SIZE];
	 private String[] networkTypes = new String[SIZE], localIPs = new String[SIZE], globalIPs= new String [SIZE], carriers = new String[SIZE];
	 private int[] cellIds = new int[SIZE], signalReadings = new int [SIZE];
	 private int count = 0;
	 private String content;
	 private static String parsedSite[];
	 
	 // Server parser
	 public ServerParser (double lat, double longi, double radius) throws Exception {
		 URL mobiperfreturn = 
			 new URL("http://mobiperf.com/php/gpsaround.php?format=xml&lat=" + lat + "&lng=" + longi + "&radius=" + radius);
		 URLConnection serverReturn = mobiperfreturn.openConnection();
		 BufferedReader in = new BufferedReader(new InputStreamReader(serverReturn.getInputStream()));
		 String inputLine;
		 while ((inputLine = in.readLine()) != null) {
		 		content = inputLine + "\n";
		 }
		 in.close();
	 }
	    
	 // This class function places the measurement into the correct array
	 private void placeData(String metric, String value) {
		 String temp = null;
		 if (count < SIZE){
			 if (value.equals("")){
				 temp = "Data not provided";
			 }
			 else {
				 temp = value;
			 }
			 if (metric.equalsIgnoreCase("networkType")) {
				 if (temp != null){
					 networkTypes[count] = temp;
				 }
			 }
			 else if (metric.equalsIgnoreCase("carrier")) {
				 if (temp != null){
					 carriers[count] = temp;
				 }
			 }
			 else if (metric.equalsIgnoreCase("cellid")) {
				 if (!temp.equalsIgnoreCase("Data not provided")){
					 cellIds[count] = Integer.parseInt(temp);
				 }
				 else{
					 cellIds[count] = -0;
				 }
			 }
			 else if (metric.equalsIgnoreCase("latitude")){
				 if (!temp.equalsIgnoreCase("Data not provided")){
					 latitudes[count] = Float.parseFloat(temp);
				 }
				 else{
					 latitudes[count] = (float) -0.0;
				 }
			 }
			 else if (metric.equalsIgnoreCase("longitude")) {
				 if (!temp.equalsIgnoreCase("Data not provided")){
					 longitudes[count] = Float.parseFloat(temp);
				 }
				 else{
					 longitudes[count] = (float) -0.0;
				 }
			 }
			 else if (metric.equalsIgnoreCase("signalReading")){
				 if (!temp.equalsIgnoreCase("Data not provided")){
					 signalReadings[count] = Integer.parseInt(temp);
				 }
				 else{
					 signalReadings[count] = -0;
				 }
			 }
			 else if (metric.equalsIgnoreCase("localIP")){
				 if (temp != null){
					 localIPs[count] = temp;	    		}
			 	}
			 else if (metric.equalsIgnoreCase("globalIP")){
				 if (temp != null){
					 globalIPs[count] = temp;
				 }
			 }
			 else if (metric.equalsIgnoreCase("upTP")) {
				 if (!temp.equalsIgnoreCase("Data not provided")){
	    				upTPs[count] = Float.parseFloat(temp);
				 }
				 else{
					 upTPs[count] = (float) -0.0;
				 }
			 }
			 else if (metric.equalsIgnoreCase("downTP")) {
				 if (!temp.equalsIgnoreCase("Data not provided")){
					 downTPs[count] = Float.parseFloat(temp);
				 }
				 else{
					 downTPs[count] = (float) -0.0;
				 }
				 count++;
			 }
		 }
		 else {
			 //Do nothing becuase I have attained 30 values
		 }
	 }
	    
	 // This is a list of getters
	 public float[] getLatitudes() {
		return latitudes;
	 }

	public float[] getUpTPs() {
		return upTPs;
	}

	public float[] getDownTPs() {
		return downTPs;
	}

	public float[] getLongitudes() {
		return longitudes;
	}

	public String[] getNetworkTypes() {
		return networkTypes;
	}

	public String[] getLocalIPs() {
		return localIPs;
	}

	public String[] getGlobalIPs() {
		return globalIPs;
	}

	public int[] getCellIds() {
		return cellIds;
	}

	public int[] getSignalReadings() {
		return signalReadings;
	}

	public String[] getCarriers() {
		return carriers;
	}
	
	public String getContent(){
		return content;
	}
	
	public int getParsedSiteLength(){
		return parsedSite.length;
	}
	 
	// This function parses the website into each of the various categories
	 public void splitSite (){
		 parsedSite = content.split("[></,><]");
		 for (int i = 7; i < parsedSite.length; i++){
			 if (!parsedSite[i].equals("")){
				 if (i+5 < parsedSite.length){
					 placeData(parsedSite[i], parsedSite[i+1]);
					 //It is += 3 because the data is in sets of 4 and it is already on the first element in the set
					 i+=3;
				 }
			 }
		 }
	 }
}
