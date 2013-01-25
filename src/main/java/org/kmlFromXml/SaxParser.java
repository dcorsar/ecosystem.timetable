package org.kmlFromXml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxParser extends DefaultHandler{

String file ="";
ArrayList listOfways;
private boolean isTheFoundRoad =false;
HashMap <String,ArrayList> waysAndNodes = new HashMap <String , ArrayList> ();
String wayIDtemp = "";
ArrayList listOfnodesTemp = new ArrayList ();
Boolean readingNodes = false;

//to maintain context

private ArrayList list = new ArrayList();
	
public	SaxParser (String xmlFile, ArrayList list) {
	file= xmlFile;
	this.listOfways = list;
	parseDocument();
	
	System.out.println ("WAYS: --- "+listOfways);
	
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
		
		
		//if way read by parser appears in the list of ways from csv file then remember the list of nodes
		
		
		
		if(qName.equalsIgnoreCase("WAY")&&(listOfways.contains(attributes.getValue("id")))) {
			
			//mark we are inside the identified way
			isTheFoundRoad = true;
			
			wayIDtemp = attributes.getValue("id");
			
			
		}
		
		else if(qName.equalsIgnoreCase("ND")&&(isTheFoundRoad)) {
			//create a new instance of employee
		//	System.out.println ("found the node "+ attributes.getValue("ref"));
			//empty temp array list for nodes if its first node in the way
			
			
			String id = attributes.getValue("ref");
			
			list.add (id);
			listOfnodesTemp.add(id);
			
			
			
		}
		
		
		/*if(qName.equalsIgnoreCase("WAY")&&(attributes.getValue("id").equals(ID))) {
			//create a new instance of employee
			isTheFoundRoad = true;
			System.out.println ("found the way with ID "+ attributes.getValue("id") );
		}
		
		else if(qName.equalsIgnoreCase("ND")&&(isTheFoundRoad)) {
			//create a new instance of employee
		//	System.out.println ("found the node "+ attributes.getValue("ref"));
			list.add((attributes.getValue("ref")));
		}*/
	}
	
	
	// returns a list of all identified node ids
	public ArrayList getNodesList () {
		return list;
	}
	
	public HashMap getHasMapWithWaysAndNodes () {
		return waysAndNodes;
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase("WAY")) {
			//add it to the list
			//ArrayList  tempList = tempWay.getTags();
			if (isTheFoundRoad ) {
				//ways.add(tempWay);
				
				isTheFoundRoad =false;
				readingNodes = false;
				
				waysAndNodes.put (wayIDtemp,(ArrayList) listOfnodesTemp.clone());
				//System.out.println(waysAndNodes);
				
				listOfnodesTemp = new ArrayList ();
			}
			
		}
		
	}
	
}
