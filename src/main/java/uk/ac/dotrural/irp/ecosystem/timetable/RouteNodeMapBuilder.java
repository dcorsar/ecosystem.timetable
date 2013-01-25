package uk.ac.dotrural.irp.ecosystem.timetable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.dotrural.irp.ecosystem.timetable.io.OsmRdfGenerator;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteNodeMap;

public class RouteNodeMapBuilder {

	public static void main(String[] args) {
		RouteNodeMapBuilder rmb = new RouteNodeMapBuilder();

		OsmRouteNodeMap map = rmb.buildOsmRouteMapFor(
				"resources/x95outboundNodes.txt", "http://www.whocares.com",
				"outbound");
		System.out.println("1");
		rmb.extractNodeDetailsFrom(
				"resources/planet_-3.31,54.82_-2.27,55.99.osm", map);
		for (OsmNode node : map.getNodes()) {
//			System.out.println("   " + node.getId() + " " + node.getEasting()
//					+ " " + node.getNorthing());
		}
		
		
	}

	public OsmRouteNodeMap buildOsmRouteMapFor(String routeNodeIdFile,
			String routeUri, String routeDirection) {
		OsmRouteNodeMap map = new OsmRouteNodeMap();
		map.setDirection(routeDirection);
		map.setServiceUri(routeUri);
		BufferedReader reader = null;
		// OsmWay way = OsmWay.getWay(UUID.randomUUID().toString());
		// map.addWay(way);
		try {
			reader = new BufferedReader(new FileReader(routeNodeIdFile));
			for (String line = reader.readLine(); line != null; line = reader
					.readLine()) {
				map.addNode(OsmNode.getNode(line.trim()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return map;
	}

	public void extractNodeDetailsFrom(String xmlFile, OsmRouteNodeMap forMap) {
		Map<String, OsmNode> requiredNodes = new HashMap<String, OsmNode>();
		for (OsmNode node : forMap.getNodes()) {
			requiredNodes.put(node.getId(), node);
		}
		OsmNodeExtractor extractor = new OsmNodeExtractor();
		extractor.extractOsmNodeDetails(xmlFile, requiredNodes);
	}
	
	

}
