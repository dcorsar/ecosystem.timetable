package org.parser;

import java.io.IOException;

public class RunRoadsAndNodesExtraction {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		String originalOSMfile = "/Users/milan/OSM/borders.osm";
		String roadsXMLfile = "/Users/milan/OSM/bordersRoads.xml";
		String nodesXMLfile ="/Users/milan/OSM/bordersNodes.xml";
		
		System.out.println("Extracting roads from OSM");
		ExtractRoadsFromOSMtoXML spe = new ExtractRoadsFromOSMtoXML(originalOSMfile,roadsXMLfile);
		spe.runExample();
		System.out.println("Finished roads from OSM");
		
		ExtractNodesRef extractNodesRef = new ExtractNodesRef (roadsXMLfile) ;
		System.out.println("Start looking for node's ids");
		extractNodesRef.runExtractRef();
		
		System.out.println ("Extracted ids:" + extractNodesRef.getNodes().size());
		System.out.println("Finished looking for id's");
		
		
		System.out.println("Start writing in XML file");
		ExtractNodesAndWriteToXML extractNodesAndWrite = new ExtractNodesAndWriteToXML (extractNodesRef.getNodes(),originalOSMfile,nodesXMLfile) ;
		extractNodesAndWrite.runExtraction();
		System.out.println("Finished writing in XML file");

	}

}
