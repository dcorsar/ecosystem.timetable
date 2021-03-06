package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;

/**
 * Extracts all nodes from an OSM XML file
 * @author david
 *
 */
public class OsmXmlMapNodeExtractor extends DefaultHandler {


	public void extractOsmNodeDetails(String xmlFile) {
		parseDocument(xmlFile);
	}

	private void parseDocument(String xmlFile) {

		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			// parse the file and also register this class for call backs
			sp.parse(xmlFile, this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	// Event Handlers
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// reset
		if (qName.equalsIgnoreCase("NODE")) {
			OsmNode node =OsmNode.getNode(attributes.getValue("id"));
			if (node != null) {
				node.setLat(Double.parseDouble(attributes.getValue("lat")));
				node.setLon(Double.parseDouble(attributes.getValue("lon")));
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
