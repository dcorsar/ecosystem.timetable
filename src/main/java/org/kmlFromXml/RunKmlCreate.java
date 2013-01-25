package org.kmlFromXml;

import java.io.IOException;
import java.util.ArrayList;

public class RunKmlCreate {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
//		String listOfwayIDs = "resources/x95inboundways.csv";
		String listOfwayIDs = "resources/63/route63North.txt";
		String startNodeID = "493576364";//"23467610";//"283038461";
		String endNodeID = "";
		String xmlWays ="resources/63/planet_-2.248,57.1_-1.676,57.548.osm";// "resources/planet_-3.27,54.8_-2.46,56.05.osm";
		String xmlNodes = "resources/63/planet_-2.248,57.1_-1.676,57.548.osm";
		String kmlPath ="resources/63/north.kml";
		
		
	
		
		System.out.println("Filtering nodes");
		FilterNodes nodes =  new FilterNodes (listOfwayIDs,xmlWays,xmlNodes, startNodeID,endNodeID,kmlPath);
		ArrayList <String> nodesList = nodes.getNodesList ();
		System.out.println("---------------------------------");
		for (String s : nodesList){
			System.out.println(s);
		}
		System.out.println("Finished filteringNodes");
/*		System.out.println ("Nodes filtered:" + nodes.size());
		
		System.out.println("Extracting Coordinates");
		ArraiList <String []> coordinates = new ExtractCoordinates (nodes,scotlandNodes,englandNodes);
		
		System.out.println ("Extracted coordinates:" + coordinates.size());
		System.out.println("Finished looking for id's");
		
		
		System.out.println("Start writing in KMLL file");
		
		System.out.println("Finished writing in XML file");*/

	}

	

}
