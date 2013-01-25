package org.kmlFromXml;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;





public class CoordinateParser extends DefaultHandler{
	
	String file ="";
	String ID= "";
	private boolean isTheFoundNode =false;
	HashMap nodesAndCoordinates = new HashMap  ();
	String nodeIdTemp = "";

	private ArrayList list = new ArrayList();


	
	public	CoordinateParser (String xmlFile, ArrayList list) {
		this.list = list;
		file= xmlFile;
	//	ID = IDtoFind;
		parseDocument();
		
	}
	
private void parseDocument() {
		
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			//parse the file and also register this class for call backs
			sp.parse(file, this);
			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	
	
	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		
		
		if(qName.equalsIgnoreCase("node")&&(list.contains(attributes.getValue("id")))) {
			//create a new instance of employee
			isTheFoundNode = true;
			nodeIdTemp = attributes.getValue("id");
			//System.out.println ("found the node with ID "+ attributes.getValue("id") );
		
			String [] coordinates = new String [2];
			
			coordinates[0] = attributes.getValue("lat");
			coordinates[1] = attributes.getValue("lon");
		//	System.out.println(coordinates[0]);
			//list.add(coordinates);
			nodesAndCoordinates.put(nodeIdTemp, coordinates);
		}
	}
	
	
	
	public HashMap getHashMapNodesAndCoordinates () {
		return nodesAndCoordinates ;
	}
	
	/*
	public ArrayList getList () {
		return list;
	}*/

	public void characters(char[] ch, int start, int length) throws SAXException {
		new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase("node")) {
			//add it to the list
			//ArrayList  tempList = tempWay.getTags();
			if (isTheFoundNode ) {
				//ways.add(tempWay);
				
				isTheFoundNode =false;
			}
			
		}
		
	}	
	
	
}
