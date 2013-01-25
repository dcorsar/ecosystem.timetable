package uk.ac.dotrural.irp.ecosystem.timetable.io;

import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmWay;

public class OsmXmlMapExtractor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * Extracts ways and node from the specified osm xml file. Extracted nodes
	 * and ways can be accessed by factory methods in OsmNode and {@link OsmWay}
	 * 
	 * @param osmFile
	 */
	public void extract(String osmFile) {
		new OsmXmlMapWayExtractor().extractOsmWayDetails(osmFile);
		new OsmXmlMapNodeExtractor().extractOsmNodeDetails(osmFile);
	}
}
