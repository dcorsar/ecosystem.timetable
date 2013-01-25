package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import uk.ac.dotrural.irp.ecosystem.timetable.RouteWayMapBuilder;
import uk.ac.dotrural.irp.ecosystem.timetable.Utils;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteNodeMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteWayMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Trip;

public class KmlGenerator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String osmMapFile = "resources/borders/osm/planet_-2.974_55.273_82f07edd.osm";
		new OsmXmlMapWayExtractor().extractOsmWayDetails(osmMapFile);
		new OsmXmlMapNodeExtractor().extractOsmNodeDetails(osmMapFile);
		
		RouteWayMapBuilder rmb = new RouteWayMapBuilder();
		OsmRouteWayMap map = rmb.buildOsmRouteMapFor(
				"resources/borders/maps/397InboundWaysA.txt");
		map.setDirection(Trip.OUTBOUND);
		map.setServiceUri("http://www.example.com/route/1");
		// no longer required as extracted once
		// rmb.extractWayDetailsFrom(osmMapFile, map);
		// rmb.extractNodeDetailsFrom(osmMapFile, map);
		OsmRouteNodeMap nodeMap = rmb.inferRouteNodeMapFor(map);
		OsmRouteWayMap wayMap = Utils.createOsmWayMapFor(nodeMap);
		KmlGenerator gen = new KmlGenerator();
		gen.generateKml(nodeMap, "resources/borders/maps/397InboundA.kml");

	}

	public void generateKml(OsmRouteNodeMap map, String toFile)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		addHeader(sb);
		appendNodes(map.getNodes(), sb);
		appendFooter(sb);

		FileWriter fileWritter = new FileWriter(new File(toFile));
		fileWritter.append(sb.toString());
		fileWritter.flush();
		fileWritter.close();
	}

	private void addHeader(StringBuffer bufferWritter)
			throws UnsupportedEncodingException {
		String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));

		data = "<kml xmlns=\"http://www.opengis.net/kml/2.2\">";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));

		data = "<Document>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<name>Paths</name>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<description>X95 route.</description>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<Style id=\"yellowLineGreenPoly\">\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<LineStyle>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<color>7f0000ff</color>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<width>4</width>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = " </LineStyle>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "  <PolyStyle>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<color>7f0000ff</color>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "</PolyStyle>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "</Style>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<Placemark>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<name>Absolute Extruded</name>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<description>X95 route</description>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "<styleUrl>#yellowLineGreenPoly</styleUrl>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = " <LineString>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = " <extrude>1</extrude>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = " <tessellate>1</tessellate>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = " <altitudeMode>absolute</altitudeMode>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "  <coordinates>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
	}

	private void appendNodes(List<OsmNode> nodes, StringBuffer bufferWritter)
			throws UnsupportedEncodingException {
		// code here
		int i = 0;
		for (OsmNode node : nodes) {
			System.out.println(String.format(
					"{node:\"%s\", lng:\"%s\", lat:\"%s\"},", i++,
					node.getLon(), node.getLat()));
			String data = node.getLon() + "," + node.getLat() + "\n";
			bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		}
	}

	private void appendFooter(StringBuffer bufferWritter)
			throws UnsupportedEncodingException {
		String data = "</coordinates>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "</LineString>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "</Placemark>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "</Document>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));
		data = "</kml>\n";
		bufferWritter.append(new String(data.getBytes(), "UTF-8"));

	}
}
