package org.parser;

import java.io.BufferedWriter;
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

public class ExtractNodesAndWriteToXML extends DefaultHandler{

	HashSet list;
	int count = 0;
	String id;
	String lat;
	String lon;
	String ref;
	String originalOSMfile;
	 String nodesXMLfile;
	
	BufferedWriter bufferWritter;

	public ExtractNodesAndWriteToXML(HashSet list, String originalOSMfile, String nodesXMLfile ) throws IOException {
		this.list = list;
		this.originalOSMfile = originalOSMfile;
		this.nodesXMLfile = nodesXMLfile;
	}

	public void runExtraction() throws IOException {

		FileWriter fileWritter = new FileWriter(
				nodesXMLfile);
		bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write("<?xml version='1.0' encoding='UTF-8'?>\n");
		bufferWritter.write("<document>\n");
		
		parseDocument();
		
		System.out.println("extracted nodes: " + count);
		// printData();
		bufferWritter.write("</document>\n");
		bufferWritter.close();

	}

	private void parseDocument() {
		
		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			// parse the file and also register this class for call backs
			sp.parse(originalOSMfile, this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	
	private void writeData() throws IOException {

		// System.out.println("ID: "+ way.getID() );
		bufferWritter.write("<node id=\""+ ref +"\" lat=\""+ lat +"\" lon=\""+ lon +"\"/>\n");
		count ++;
		

	}

	// Event Handlers
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// reset
		 if (qName.equalsIgnoreCase("NODE") ) {
			// create a new instance of employee
			 if ( list.contains(attributes.getValue("id"))) {
				 
			ref = attributes.getValue("id");
			lat = attributes.getValue("lat");
			lon = attributes.getValue("lon");
			try {
				writeData();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 }
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

	}

}
