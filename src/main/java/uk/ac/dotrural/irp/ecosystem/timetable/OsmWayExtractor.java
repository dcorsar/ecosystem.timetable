package uk.ac.dotrural.irp.ecosystem.timetable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmWay;

/**
 * Extracts the details of a number of ways from an OSM XML file.
 * 
 * @author David Corsar, Milan Markovic
 * 
 */
public class OsmWayExtractor extends DefaultHandler {

	private Map<String, OsmWay> requiredWays;
	private String tempVal;
	private int count;
	private OsmWay currentWay;

	public OsmWayExtractor() {
		super();
		this.requiredWays = new HashMap<String, OsmWay>();
	}

	public void extractOsmWayDetails(String xmlFile, List<OsmWay> ways) {
		extractRequiredWays(ways);
		count = 0;
		parseDocument(xmlFile);
	}

	private void extractRequiredWays(List<OsmWay> ways) {
		this.requiredWays.clear();
		for (OsmWay way : ways) {
			this.requiredWays.put(way.getId(), way);
		}
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

		tempVal = "";
		if (qName.equalsIgnoreCase("WAY")) {
			// will return null if we don't require this way
			currentWay = this.requiredWays.get(attributes.getValue("id"));
		} else if (qName.equalsIgnoreCase("TAG") && (currentWay != null)) {
			if ((!attributes.getValue("k").equals("note"))
					&& (!attributes.getValue("k").equals("width"))
					&& (!attributes.getValue("k").equals("incline"))) {
				String[] touple = new String[2];
				touple[0] = attributes.getValue("k");
				touple[1] = attributes.getValue("v");

				if (touple[0] == null) {
					touple[0] = "";
				}

				if (touple[1] == null) {
					touple[1] = "";
				}
				currentWay.addTag(touple[0], touple[1]);
			}
		} else if (qName.equalsIgnoreCase("ND") && (currentWay != null)) {
			OsmNode node = OsmNode.getNode(attributes.getValue("ref"));
			currentWay.addNode(node);
			node.addOsmWay(currentWay);
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("WAY")) {
			count++;
		}
	}
}
