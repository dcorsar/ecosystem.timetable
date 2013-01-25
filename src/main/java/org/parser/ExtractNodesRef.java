package org.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
public class ExtractNodesRef extends DefaultHandler {
	
	private HashSet nodes = new HashSet();
 

	List ways;
	int count =0;
	
	private String tempVal;
	
	//to maintain context
	private Way tempWay;
	private boolean isRoad = false;
	 BufferedWriter bufferWritter;
	String  roadsXMLfile="";
	
	
	public ExtractNodesRef(String roadsXMLfile) throws IOException{
		ways = new ArrayList();
		this.roadsXMLfile=roadsXMLfile;
		
       
       
	}
	
	public void runExtractRef() throws IOException {
		
		

		//true = append file
		
		
		parseDocument();
		System.out.println("nodes: "+ count);
	//	printData();
		
		 
	}

	private void parseDocument() {
		
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			//parse the file and also register this class for call backs
			sp.parse(roadsXMLfile, this);
			
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
		//reset
		
		 if(qName.equalsIgnoreCase("ND")) {
			//create a new instance of employee
			String temp = attributes.getValue("ref");
			
				nodes.add(temp);
				
				count++;
			
		}
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException {
		
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		
	}
	
	public HashSet getNodes () {
		return nodes;
	}
	
}