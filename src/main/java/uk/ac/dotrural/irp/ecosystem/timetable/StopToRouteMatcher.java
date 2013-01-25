package uk.ac.dotrural.irp.ecosystem.timetable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uk.ac.dotrural.irp.ecosystem.timetable.model.MapMatchedStopPointNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteNodeMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteWayMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmWay;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Segment;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Stop;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

public class StopToRouteMatcher {

	public static void main(String[] args) throws IOException {
		StopPointExtractor spe = new StopPointExtractor();
		Collection<Stop> stops = spe.extractStopsFor("95", "outbound");
		RouteNodeMapBuilder rmb = new RouteNodeMapBuilder();

		OsmRouteNodeMap map = rmb.buildOsmRouteMapFor(
				"resources/x95outboundNodes.txt", "http://www.whocares.com",
				"outbound");
		System.out.println("1");
		// rmb.extractWayDetailsFrom(
		// "resources/planet_-3.27,54.8_-2.46,56.05.osm", map);
		// System.out.println("2");
		rmb.extractNodeDetailsFrom(
				"resources/planet_-3.27,54.8_-2.46,56.05.osm", map);

		int count = 0;
		// for (OsmWay way : map.getWays()) {
		// for (OsmNode node : way.getNodes()) {
		// count++;
		// OSRef ref = new OSRef(node.getEasting(), node.getNorthing());
		// LatLng ll = ref.toLatLng();
		// System.out.println(String.format("{id: %s, lon:%s,lat:%s},",
		// node.getId(), ll.getLng(), ll.getLat()));
		// }
		// }

		StopToRouteMatcher matcher = new StopToRouteMatcher();
		OsmRouteWayMap wayMap = Utils.createOsmWayMapFor(map);
		Map<String, SegmentDistance> mappedStops = matcher.mapMatchStops(
				wayMap, stops);
		matcher.mergeStopsAsNodes(wayMap, mappedStops);
		System.out.println(count + " "
				+ wayMap.getWays().get(0).getNumberOfNodes() + " "
				+ (229 + count));

		for (OsmWay way : wayMap.getWays()) {
			for (OsmNode node : way.getNodes()) {
				OSRef ref = new OSRef(node.getEasting(), node.getNorthing());
				LatLng ll = ref.toLatLng();
				System.out.println(String.format(
						" {id:\"%s\", lon:%s,lat:%s},", node.getId(),
						ll.getLng(), ll.getLat()));
			}
		}
	}

	public Map<String, SegmentDistance> mapMatchStops(OsmRouteWayMap map,
			Collection<Stop> stops) {

		Map<String, SegmentDistance> mappedStops = new HashMap<String, SegmentDistance>();
		MapMatcher matcher = new MapMatcher();
		for (Stop stop : stops) {
			if (stop.getLocation() != null) {
				// insert oldMapMatchStops here if desired

				// List<SegmentDistance> candidateNodes =
				// getCandidateNearestNodes(
				// stop.getAtcoCode(), stop.getLocation(), map, 5);
				SegmentDistance candidateNode = matcher.mapWays(
						stop.getLocation(), map.getWays());
				// Segment closestSegment = null;
				// double distance = Double.MAX_VALUE;
				// for (SegmentDistance sd : candidateNodes) {
				// Segment s = sd.segment;
				// double d = calculateDistanceTo(s.getFrom().getPoint(), s
				// .getTo().getPoint(), stop.getLocation());
				// if (d < distance) {
				// distance = d;
				// closestSegment = s;
				// }
				// }
				// Point mappedPoint = mapPointToLine(closestSegment.getFrom()
				// .getPoint(), closestSegment.getTo().getPoint(),
				// stop.getLocation());
				// Segment closestSegment = candidateNodes.get(0).segment;
				// double distance = candidateNodes.get(0).distance;
				// Point mappedPoint = candidateNodes.get(0).mappedPoint;

				// mappedStops.put(stop.getAtcoCode(), candidateNodes.get(0));
				mappedStops.put(stop.getAtcoCode(), candidateNode);

				// System.out.println("{stop_code: " + stop.getAtcoCode()
				// + ", stope: " + stop.getLocation().getEasting()
				// + ", stopn: " + stop.getLocation().getNorthing()
				// + ", closeSe:  "
				// + closestSegment.getFrom().getPoint().getEasting()
				// + ", closeSn: "
				// + closestSegment.getFrom().getPoint().getNorthing()
				// + ", closeEe: "
				// + closestSegment.getTo().getPoint().getEasting()
				// + ", closeEn: "
				// + closestSegment.getTo().getPoint().getNorthing()
				// + ", distance: " + distance + ", closePe: "
				// + mappedPoint.getEasting() + ", closePn: "
				// + mappedPoint.getNorthing() + "},");

			}
		}
		return mappedStops;
	}

//	private void oldMapMachStops(OsmRouteWayMap map, Collection<Stop> stops) {
//		for (Stop stop : stops) {
//			{ // remove this line if putting back
//				Point closestStart = null, closestEnd = null, mappedPoint = null;
//				double distance = Double.MAX_VALUE;
//				double weight = 0;
//				for (OsmWay way : map.getWays()) {
//					List<OsmNode> nodes = way.getNodes();
//					for (int i = 1, j = nodes.size(); i < j; i++) {
//						//
//						// if (isStopPossiblyOnLine(nodes.get(i - 1),
//						// nodes.get(i), stop.getLocation())) {
//						// Point p = mapPointToLine(
//						// nodes.get(i - 1),
//						// nodes.get(i),
//						// stop.getLocation());
//						// if (isPointPossiblyOnLine(nodes.get(i - 1),
//						// nodes.get(i), p)) {
//						double d = calculateDistanceTo(nodes.get(i - 1),
//								nodes.get(i), stop.getLocation());
//						double w = (80 - d) / 80;
//						// System.out.println(d);
//						if (weight < w) {
//							// if (d < distance) {
//							weight = w;
//							distance = d;
//							closestStart = nodes.get(i - 1);
//							closestEnd = nodes.get(i);
//							mappedPoint = mapPointToLine(nodes.get(i - 1),
//									nodes.get(i), stop.getLocation());
//						}
//						// }
//						// }
//					}
//				}
//				if (closestStart == null) {
//					System.out.println("unable to map stop "
//							+ stop.getAtcoCode());
//				} else
//					System.out.println("{stop_code: " + stop.getAtcoCode()
//							+ ", stope: " + stop.getLocation().getEasting()
//							+ ", stopn: " + stop.getLocation().getNorthing()
//							+ ", closeSe:  " + closestStart.getEasting()
//							+ ", closeSn: " + closestStart.getNorthing()
//							+ ", closeEe: " + closestEnd.getEasting()
//							+ ", closeEn: " + closestEnd.getNorthing()
//							+ ", distance: " + distance + ", closePe: "
//							+ mappedPoint.getEasting() + ", closePn: "
//							+ mappedPoint.getNorthing() + ", weight: " + weight
//							+ "},");
//			}
//
//		}
//	}

	

	/**
	 * @deprecated
	 * @param lineStart
	 * @param lineEnd
	 * @param point
	 * @return
	 */
//	private boolean isStopPossiblyOnLine(Point lineStart, Point lineEnd,
//			Point point) {
//		if ((lineStart.getEasting() == 339121.6368975959 && lineStart
//				.getNorthing() == 566096.0221172781)
//				|| (lineEnd.getEasting() == 339121.6368975959 && lineEnd
//						.getNorthing() == 566096.0221172781)) {
//			int i = 0;
//		}
//		boolean easting = true, northing = true; // lets be optimists
//		if (lineStart.getEasting() > lineEnd.getEasting()) {
//			easting = lineStart.getEasting() >= point.getEasting()
//					&& point.getEasting() >= lineEnd.getEasting();
//		} else {
//			easting = lineStart.getEasting() <= point.getEasting()
//					&& point.getEasting() <= lineEnd.getEasting();
//		}
//		if (lineStart.getNorthing() > lineEnd.getNorthing()) {
//			northing = lineStart.getNorthing() >= point.getNorthing()
//					&& point.getNorthing() >= lineEnd.getNorthing();
//		} else {
//			northing = lineStart.getNorthing() <= point.getNorthing()
//					&& point.getNorthing() <= lineEnd.getNorthing();
//		}
//		return (easting || northing);
//	}

	

	
	/**
	 * Calculates the perpendicular distance from point to the line segStart to
	 * segEnd
	 * 
	 * @param segStart
	 * @param segEnd
	 * @param point
	 * @return
	 */
	private double calculateDistanceTo(Point segStart, Point segEnd, Point point) {
		double E1 = segStart.getEasting(), N1 = segStart.getNorthing(), E2 = segEnd
				.getEasting(), N2 = segEnd.getNorthing(), Eh = point
				.getEasting(), Nk = point.getNorthing();
		double d = Math.abs((Eh * (N1 - N2)) - (Nk * (E1 - E2))
				+ ((E1 * N2) - (E2 * N1)))
				/ Math.sqrt(Math.pow((E1 - E2), 2) + Math.pow((N1 - N2), 2));
		return d;
	}

	public void mergeStopsAsNodes(OsmRouteWayMap map,
			Map<String, SegmentDistance> mappedStops) {
		for (OsmWay way : map.getWays()) {
			List<Segment> mapSegments = way.getSegments();

			List<OsmNode> newNodes = new ArrayList<OsmNode>();
			// really horrible way to do it - should be much more efficient....
			for (Segment s : mapSegments) {
				newNodes.add((OsmNode) s.getFrom());
				for (String stopId : mappedStops.keySet()) {
					SegmentDistance sd = mappedStops.get(stopId);
					if (sd.segment == s
							|| (s.getFrom().equals(sd.segment.getFrom()) && s
									.getTo().equals(sd.segment.getTo()))) {
						Stop stop = Stop.getStop(stopId);
						stop.setMapMatchedLocation(sd.mappedPoint);
						newNodes.add(new MapMatchedStopPointNode(stop));
					}
				}
			}
			// add the to node of the final segment
			newNodes.add((OsmNode) mapSegments.get(mapSegments.size() - 1)
					.getTo());
			way.setNodes(newNodes);
		}
	}
	
	public OsmRouteWayMap mergeStopsAsNodesNewMap(OsmRouteWayMap map,
			Map<String, SegmentDistance> mappedStops) {
		OsmRouteWayMap newMap = new OsmRouteWayMap();
		newMap.setAdditionalBusStops(map.getAdditionalBusStops());
		newMap.setDirection(map.getDirection());
		newMap.setServiceUri(map.getServiceUri());
		newMap.setStartNode(map.getStartNode());
		newMap.setEndNode(map.getEndNode());
		
		for (OsmWay way : map.getWays()) {
			List<Segment> mapSegments = way.getSegments();

			List<OsmNode> newNodes = new ArrayList<OsmNode>();
			// really horrible way to do it - should be much more efficient....
			for (Segment s : mapSegments) {
				newNodes.add((OsmNode) s.getFrom());
				for (String stopId : mappedStops.keySet()) {
					SegmentDistance sd = mappedStops.get(stopId);
					if (sd.segment == s
							|| (s.getFrom().equals(sd.segment.getFrom()) && s
									.getTo().equals(sd.segment.getTo()))) {
						Stop stop = Stop.getStop(stopId);
						stop.setMapMatchedLocation(sd.mappedPoint);
						newNodes.add(new MapMatchedStopPointNode(stop));
					}
				}
			}
			// add the to node of the final segment
			newNodes.add((OsmNode) mapSegments.get(mapSegments.size() - 1)
					.getTo());
			OsmWay newWay = OsmWay.getWay(way.getId() + "_b");
			newWay.setNodes(newNodes);
			newMap.addWay(newWay);
		}
		return newMap;
	}
}

