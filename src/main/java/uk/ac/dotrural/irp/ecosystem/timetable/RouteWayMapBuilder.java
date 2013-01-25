package uk.ac.dotrural.irp.ecosystem.timetable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kmlFromXml.KmlWriter;

import uk.ac.dotrural.irp.ecosystem.timetable.io.OsmRdfGenerator;
import uk.ac.dotrural.irp.ecosystem.timetable.io.ServiceMapRdfGenerator;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteNodeMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteWayMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmWay;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Stop;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Trip;

public class RouteWayMapBuilder {

	public static void main(String[] args) throws IOException {
		RouteWayMapBuilder rmb = new RouteWayMapBuilder();

		OsmRouteWayMap map = rmb.buildOsmRouteMapFor(
				"resources/wayIds15Outbound.txt");
		
		map.setDirection(Trip.OUTBOUND);
		map.setServiceUri("http://www.example.com/route/1");
		map.setStartNode(OsmNode.getNode("283038463"));
		map.setEndNode(OsmNode.getNode("1278622982"));
		System.out.println("1");
		rmb.extractWayDetailsFrom(
				"resources/17/planet_-2.2125,571047_-2.0299,571961.osm", map);
		System.out.println("2");
		rmb.extractNodeDetailsFrom(
				"resources/planet_-3.31,54.82_-2.27,55.99.osm", map);
		// for (OsmWay way : map.getWays()) {
		// System.out.println(way.getId());
		// for (OsmNode node : way.getNodes()) {
		// System.out.println("   " + node.getId() + " "
		// + node.getEasting() + " " + node.getNorthing());
		// }
		// }

		// inbound map
		// OsmRouteNodeMap nodeMap = rmb.inferRouteNodeMapFor(map, "1278622982",
		// "283038463");
		// outbound map
		OsmRouteNodeMap nodeMap = rmb.inferRouteNodeMapFor(map);
		ArrayList list = new ArrayList();
		int count = 0;
		for (OsmNode node : nodeMap.getNodes()) {
			// System.out.println(String.format("{id: \"%s\", lng: %s, lat: %s },",
			// node.getId() + "  " + count++, node.getLon(), node.getLat()));

			list.add(new String[] { "" + node.getLat(), "" + node.getLon() });
		}
		// System.out.println(nodeMap.getNumberOfNodes());
		// new KmlWriter(list, "resources/mykml.kml");

		ServiceMapRdfGenerator gen = new ServiceMapRdfGenerator();
		Collection<String> updates = gen.generateSparqlUpdates(nodeMap,
				"http://www.abdn.ac.uk/map/", "http://www.abdn.ac.uk/node/", nodeMap.getServiceUri(), nodeMap.getDirection());
		for (Iterator iterator = updates.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println(string);
		}

	}

	public OsmRouteWayMap buildOsmRouteMapFor(
			String routeWayIdFile) {

		OsmRouteWayMap map = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(routeWayIdFile));
			for (String line = reader.readLine(); line != null; line = reader
					.readLine()) {
				if (line.startsWith("// start route")) {
					map = new OsmRouteWayMap();
				} else if (line.startsWith("// start node ")) {
					map.setStartNode(OsmNode.getNode(line.substring(14).trim()));
				} else if (line.startsWith("// end node ")) {
					map.setEndNode(OsmNode.getNode(line.substring(12).trim()));
				} else if (line.startsWith("// stop list ")) {
					String[] list = line.substring(13).split(" ");
					for (String s : list) {
						map.addAdditionalBusStop(Stop.getStop(s));
					}
				} else if (line.startsWith("//") || "".equals(line.trim())) {
					// ignore
				} else {
					map.addWay(OsmWay.getWay(line.trim()));
				}
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

	public void extractWayDetailsFrom(String xmlFile, OsmRouteWayMap forMap) {
		OsmWayExtractor extractor = new OsmWayExtractor();
		extractor.extractOsmWayDetails(xmlFile, forMap.getWays());
	}

	public void extractNodeDetailsFrom(String xmlFile, OsmRouteWayMap forMap) {
		Map<String, OsmNode> requiredNodes = new HashMap<String, OsmNode>();
		for (OsmWay way : forMap.getWays()) {
			for (OsmNode node : way.getNodes()) {
				requiredNodes.put(node.getId(), node);
			}
		}
		OsmNodeExtractor extractor = new OsmNodeExtractor();
		extractor.extractOsmNodeDetails(xmlFile, requiredNodes);
	}

	public OsmRouteNodeMap inferRouteNodeMapFor(OsmRouteWayMap wayMap) {
		OsmRouteNodeMap map = new OsmRouteNodeMap();
		map.setDirection(wayMap.getDirection());
		map.setServiceUri(wayMap.getServiceUri());
		map.setStartNode(wayMap.getStartNode());
		map.setEndNode(wayMap.getEndNode());

		List<OsmWay> ways = wayMap.getWays();
		if (ways.size() >= 2) {
			int startWay = 0;
			// add the nodes from startNode to the first intersection
			int start = ways.get(startWay++)
					.getNodeIndex(wayMap.getStartNode());
			while (start == -1) {
				start = ways.get(startWay++)
						.getNodeIndex(wayMap.getStartNode());
			}

//			System.out.println(ways.get(startWay - 1).getId() + " to "
//					+ ways.get(1).getId());
			if ("25945784".equals(ways.get(startWay - 1).getId())
					&& "25945785".equals(ways.get(startWay).getId())) {
				System.out.println("blah");
			}
			int end = findIntersectionInFirst(ways.get(startWay - 1),
					ways.get(startWay), start);
			addNodesToMap(map, ways.get(startWay - 1), start, end);

			// first the first intersection in the second way
			start = // findIntersectionInNext(ways.get(0),
			ways.get(startWay)
					.getNodeIndex(ways.get(startWay - 1).getNode(end));

			// find all subsequent intersections
			for (int i = startWay, j = ways.size() - 1; i < j; i++) {
				end = findIntersectionInFirst(ways.get(i), ways.get(i + 1),
						start);
//				 System.out.println(ways.get(i).getId() + " to "
//				 + ways.get(i + 1).getId());
				if ("25945784".equals(ways.get(i).getId())
						&& "25945785".equals(ways.get(i + 1).getId())) {
					System.out.println("blah");
				}
				addNodesToMap(map, ways.get(i), start, end);
				start = ways.get(i + 1).getNodeIndex(ways.get(i).getNode(end));
			}

			// add the nodes for the last way from the intersection to endNode
			end = ways.get(ways.size() - 1).getNodeIndex(wayMap.getEndNode());
			addNodesToMap(map, ways.get(ways.size() - 1), start, end);
		}

		return map;
	}

	private void addNodesToMap(OsmRouteNodeMap map, OsmWay way, int start,
			int end) {
		if (start < end) {
			for (int k = start; k < end; k++) {
				map.addNode(way.getNodes().get(k));
			}
		} else {
			// end before start so add in reverse
			for (int k = start; k > end; k--) {
				map.addNode(way.getNodes().get(k));
			}
		}
	}

	/*
	 * Returns the index of the node n in first.nodes that is also in next.nodes
	 * or -1 if no such node exists
	 */
	private int findIntersectionInFirst(OsmWay first, OsmWay next, int fromIndex) {
		List<OsmNode> firstNodes = first.getNodes(), nextNodes = next
				.getNodes();

		// look for node forward from fromIndex
		for (int i = fromIndex, j = firstNodes.size(), l = nextNodes.size(); i < j; i++) {
			OsmNode node = firstNodes.get(i);
			for (int k = 0; k < l; k++) {
				if (nextNodes.get(k) == node) {
					return i;
				}
			}
		}

		// didn't find it, so look back
		for (int i = fromIndex, l = nextNodes.size(); i >= 0; i--) {
			OsmNode node = firstNodes.get(i);
			for (int k = 0; k < l; k++) {
				if (nextNodes.get(k) == node) {
					return i;
				}
			}
		}

		return -1;
	}

	/*
	 * Returns the index of the node n in next.nodes that is also in first.nodes
	 * or -1 if no such node exists
	 */
	private int findIntersectionInNext(OsmWay first, OsmWay next) {
		List<OsmNode> firstNodes = first.getNodes(), nextNodes = next
				.getNodes();

		for (int i = 0, j = firstNodes.size(), l = nextNodes.size(); i < l; i++) {
			OsmNode node = nextNodes.get(i);
			for (int k = 0; k < j; k++) {
				if (firstNodes.get(k) == node) {
					return i;
				}
			}
		}

		return -1;
	}

}
