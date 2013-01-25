package uk.ac.dotrural.irp.ecosystem.timetable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.dotrural.irp.ecosystem.timetable.io.AtcoCifParser;
import uk.ac.dotrural.irp.ecosystem.timetable.io.CifToRdfUpdates;
import uk.ac.dotrural.irp.ecosystem.timetable.io.Constants;
import uk.ac.dotrural.irp.ecosystem.timetable.io.KmlGenerator;
import uk.ac.dotrural.irp.ecosystem.timetable.io.LocationRdfGenerator;
import uk.ac.dotrural.irp.ecosystem.timetable.io.OsmRdfGenerator;
import uk.ac.dotrural.irp.ecosystem.timetable.io.OsmXmlMapNodeExtractor;
import uk.ac.dotrural.irp.ecosystem.timetable.io.OsmXmlMapWayExtractor;
import uk.ac.dotrural.irp.ecosystem.timetable.io.ServiceMapRdfGenerator;
import uk.ac.dotrural.irp.ecosystem.timetable.io.TripleStoreUtils;
import uk.ac.dotrural.irp.ecosystem.timetable.model.EstimatedLocationPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.MapMatchedStopPointNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteNodeMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteWayMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmWay;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Segment;
import uk.ac.dotrural.irp.ecosystem.timetable.model.TimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Service;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Stop;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.StopTimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Trip;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

public class TimetableBuilder {

	StopToRouteMatcher matcher;

	public TimetableBuilder() {
		super();
		matcher = new StopToRouteMatcher();
		vle = new VehicleLocationEstimator();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		TimetableBuilder tb = new TimetableBuilder();

		Set<String> updates = new TreeSet<String>();
		tb.extractOsmMap("resources/borders/osm/planet_-3.072_54.88_50d36d17.osm");

		// // route 72
		updates.addAll(tb.buildTimetable("resources/borders/72/SVRBOAO072.cif",
				"72", Constants.routeBase + "72",
				"resources/borders/72/72InboundLinksMain.txt",
				"resources/borders/72/72OutboundLinksMain.txt", "72",
				"resources/borders/72/72.kml"));
		// // route 73
		updates.addAll(tb.buildTimetable("resources/borders/73/SVRBOAO073.cif",
				"73", Constants.routeBase + "73",
				"resources/borders/73/73InboundLinks.txt",
				"resources/borders/73/73OutboundLinks.txt", "73",
				"resources/borders/73/73.kml"));
		// // // route 396
		// updates.addAll(tb.buildTimetable(
		// "resources/borders/396397/SVRBOAO396.cif", "396",
		// Constants.routeBase + "396",
		// "resources/borders/396397/396397InboundLinks.txt",
		// "resources/borders/396397/396397OutboundLinks.txt", "396",
		// "resources/borders/396397/396.kml"));
		// // // route 397
		// updates.addAll(tb.buildTimetable(
		// "resources/borders/396397/SVRBOAO397.cif", "397",
		// Constants.routeBase + "397",
		// "resources/borders/396397/396397InboundLinks.txt",
		// "resources/borders/396397/396397OutboundLinks.txt", "397",
		// "resources/borders/396397/397.kml"));
		// route 95A
		// updates.addAll(tb.buildTimetable(
		// "resources/borders/95X9595A/SVRBOAO095A.cif", "95A",
		// Constants.routeBase + "95A",
		// "resources/borders/95X9595A/95AInboundWays.txt",
		// "resources/borders/95X9595A/95AOutboundWays.txt", "95A",
		// "resources/borders/95X9595A/95A.kml"));
		for (String s : updates) {
			System.out.println(s);
		}

		// route 63
		// List<String> updates =
		// tb.buildTimetable("resources/63/SVRABAO063.cif",
		// "63", Constants.routeBase + "63",
		// "resources/63/planet_-2.248,57.1_-1.676,57.548.osm",
		// "resources/63/route63North.txt",
		// "resources/63/route63South.txt"
		// // , "493576364", "1326292263","1326292263", "493576364"
		// , "63");

		// northstart 1907675208
		// northend 1326292413

		// route x95
		// List<String> updates=
		// tb.buildTimetable("resources/x95Ways/SVRBOAX095.cif","115",
		// Constants.routeBase + "X95",
		// "resources/planet_-3.31,54.82_-2.27,55.99.osm",
		// "resources/x95Ways/x95InboundWays.txt",
		// "resources/x95Ways/x95OutboundWays.txt", "254141210",
		// "283038461", "283038461", "254141210", "X95");

		// route 17
		// List<String> updates =
		// tb.buildTimetable("resources/17/SVRABAO017.cif","111",
		// Constants.routeBase +"17",
		// "resources/17/planet_-2.2125,57.1047_-2.0299,57.1961.osm",
		// "resources/17/route17WaysInbound.txt",
		// "resources/17/route17WaysOutbound.txt", "1168853593",
		// "57049101", "57049101", "1168853593", "17");
		// //Utils.update("resources/17/17tt", updates);
		// // route 9U
		// updates.addAll(tb.buildTimetable("resources/9U/SVRABAO009U.cif","111",
		// Constants.routeBase +"9U",
		// "resources/17/planet_-2.2125,57.1047_-2.0299,57.1961.osm",
		// "resources/9U/ways9UInbound.txt",
		// "resources/9U/ways9UOutbound.txt", "1907675208",
		// "60858883", "60858883", "1907675208", "9U"));

		// for (String update : updates) {
		// System.out.println(update);
		// }
		//
		Utils.update("resources/borders/rdf/timetable", updates);
		// Utils.update("resources/x95/tt", updates);

		// add the route nodes
		TripleStoreUtils.getTripleStoreUtils().toTdbModel(MAPNODES_TS,
				"resources/borders/rdf/nodes/");
		// TripleStoreUtils.getTripleStoreUtils().toTdbModel(MAPNODES_TS,
		// "resources/x95/nodes/");

		// AtcoCifParser p = new AtcoCifParser();
		// p.parseFile("resources/SVRBOAX095.cif");

		// OsmRouteMap outboundMap = tb.buildRouteMap(
		// "resources/x95outboundNodes.txt",
		// "resources/planet_-3.27,54.8_-2.46,56.05.osm",
		// CifToRdfUpdates.routeBase + "X95", Trip.OUTBOUND);
		// OsmRouteMap inboundMap = tb.reverseRouteMap(outboundMap,
		// outboundMap.getRouteUri(), Trip.INBOUND);

		// Collection<Stop> stops = p.getStopsFor("X95", Trip.INBOUND);
		// int count = 0;
		// for (Stop stop : stops) {
		// OSRef ref = new OSRef(stop.getLocation().getEasting(), stop
		// .getLocation().getNorthing());
		// LatLng ll = ref.toLatLng();
		// System.out.println(String.format(
		// "{id:\"Stop %s<br>  %s\", lng:%s,lat:%s},",
		// stop.getAtcoCode(), count++, ll.getLng(), ll.getLat()));
		// }

		// Map<String, SegmentDistance> mappedStops = tb.mapStopsToRoute(
		// inboundMap, stops);
		// tb.mergeStopsWithMap(inboundMap, mappedStops);
		// tb.estimateLocations(p.getRoutes(), Trip.INBOUND, inboundMap);

	}

	private static final String MAPNODES_TS = "mapnodes";

	public void extractOsmMap(String osmMapFile) {
		new OsmXmlMapWayExtractor().extractOsmWayDetails(osmMapFile);
		new OsmXmlMapNodeExtractor().extractOsmNodeDetails(osmMapFile);
	}

	public List<String> buildTimetable(String atcoCifFile, String areaCode,
			String routeUri, String inboundWaysFile, String outboundWaysFile,
			String routeId, String kmlFile) throws IOException {
		AtcoCifParser p = new AtcoCifParser();
		p.parseFile(atcoCifFile, areaCode);

		RouteWayMapBuilder rmb = new RouteWayMapBuilder();
		OsmRouteNodeMap inboundNodeMap = buildMapTimetableEstimateLocations(
				routeUri, inboundWaysFile, p, rmb, Trip.INBOUND);
		KmlGenerator writer = new KmlGenerator();
		writer.generateKml(inboundNodeMap, kmlFile + "in.kml");
		// OsmWay.clearWays();
		// OsmNode.clearNodes();

		System.out.println("OUTBOUND");
		OsmRouteNodeMap outboundWayMap = buildMapTimetableEstimateLocations(
				routeUri, outboundWaysFile, p, rmb, Trip.OUTBOUND);

		writer.generateKml(outboundWayMap, kmlFile + "out.kml");

		// trips in p should have actual and estimated locations

		// generate map rdf
		ServiceMapRdfGenerator gen = new ServiceMapRdfGenerator();
		String nodeUri = "http://dtp-24.sncs.abdn.ac.uk:8095/osm/nodes/";
		Collection<String> updates = gen.generateSparqlUpdates(inboundNodeMap,
				"http://dtp-24.sncs.abdn.ac.uk:8095/maps/", nodeUri, inboundNodeMap.getServiceUri(), inboundNodeMap.getDirection());
		updates.addAll(gen.generateSparqlUpdates(outboundWayMap,
				"http://dtp-24.sncs.abdn.ac.uk:8095/maps/", nodeUri, outboundWayMap.getServiceUri(), outboundWayMap.getDirection()));

		TripleStoreUtils.getTripleStoreUtils().createModel(MAPNODES_TS);
		TripleStoreUtils.getTripleStoreUtils().performUpdates(MAPNODES_TS,
				updates);

		// add the rdf for the nodes
		OsmRdfGenerator osmRdf = new OsmRdfGenerator();
		Collection<String> osmUpdates = osmRdf.generateUpdatesForNodes(
				inboundNodeMap.getNodes(), nodeUri);
		osmUpdates.addAll(osmRdf.generateUpdatesForNodes(
				outboundWayMap.getNodes(), nodeUri));

		// generate timetable rdf stuff
		List<String> allUpdates = new LinkedList<String>();
		// lets generate some RDF for the timetable
		CifToRdfUpdates rdfGenerator = new CifToRdfUpdates();
		updates = rdfGenerator.generateSparqlUpdates(p.getOperators(), p
				.getRoutes().get(routeId));
		// System.out
		// .println("--------------------------------------------------------");
		// // for (String update : updates) {
		// System.out.println(update);
		// }
		allUpdates.addAll(updates);
		// System.out
		// .println("--------------------------------------------------------");
		// now lets generate some for the stops and estimated locations
		LocationRdfGenerator locGen = new LocationRdfGenerator();
		Collection<String> updates2 = locGen.generateSparqlUpdates(p
				.getRoutes());
		// for (String string : updates2) {
		// System.out.println(string);
		// }
		allUpdates.addAll(updates2);

		return allUpdates;

	}

	private OsmRouteNodeMap buildMapTimetableEstimateLocations(String routeUri,
			String inboundWaysFile, AtcoCifParser p, RouteWayMapBuilder rmb,
			String direction) {
		OsmRouteWayMap map = rmb.buildOsmRouteMapFor(inboundWaysFile);
		map.setServiceUri(routeUri);
		map.setDirection(direction);
		// no longer required as extracted once
		// rmb.extractWayDetailsFrom(osmMapFile, map);
		// rmb.extractNodeDetailsFrom(osmMapFile, map);
		OsmRouteNodeMap nodeMap = rmb.inferRouteNodeMapFor(map);
		OsmRouteWayMap wayMap = Utils.createOsmWayMapFor(nodeMap);
		for (String service : p.getRoutes().keySet()) {
			Map<String, SegmentDistance> mappedStops = mapStopsToRoute(wayMap,
					p.getStopsFor(p.getRoutes().get(service), direction));
			mergeStopsWithMap(wayMap, mappedStops);
		}
		estimateLocations(p.getRoutes(), direction, wayMap);
		return nodeMap;
	}

	private OsmRouteWayMap determineMapFor(Collection<OsmRouteWayMap> wayMaps,
			Trip t) {
		// TODO Auto-generated method stub
		return null;
	}

	private Collection<OsmRouteNodeMap> inferNodeMaps(
			Collection<OsmRouteWayMap> maps, RouteWayMapBuilder rmb) {
		Collection<OsmRouteNodeMap> nodeMaps = new ArrayList<OsmRouteNodeMap>(
				maps.size());
		for (OsmRouteWayMap map : maps) {
			OsmRouteNodeMap nodeMap = rmb.inferRouteNodeMapFor(map);
			nodeMap.setDirection(map.getDirection());
			nodeMap.setServiceUri(map.getServiceUri());
			nodeMap.setStartNode(map.getStartNode());
			nodeMap.setEndNode(map.getEndNode());
			nodeMap.setAdditionalBusStops(map.getAdditionalBusStops());
			nodeMaps.add(nodeMap);
		}
		return nodeMaps;
	}

	private Collection<OsmRouteWayMap> createOsmWayMapsFor(
			Collection<OsmRouteNodeMap> nodeMaps) {
		Collection<OsmRouteWayMap> wayMaps = new ArrayList<OsmRouteWayMap>(
				nodeMaps.size());
		for (OsmRouteNodeMap map : nodeMaps) {
			OsmRouteWayMap wayMap = Utils.createOsmWayMapFor(map);
			wayMap.setDirection(map.getDirection());
			wayMap.setServiceUri(map.getServiceUri());
			wayMap.setStartNode(map.getStartNode());
			wayMap.setEndNode(map.getEndNode());
			wayMap.setAdditionalBusStops(map.getAdditionalBusStops());
			wayMaps.add(wayMap);
		}
		return wayMaps;
	}

	public OsmRouteNodeMap buildRouteMap(String nodeListFile,
			String nodeDetailsFiles, String routeUri, String direction) {
		RouteNodeMapBuilder rmb = new RouteNodeMapBuilder();
		OsmRouteNodeMap map = rmb.buildOsmRouteMapFor(nodeListFile, routeUri,
				direction);
		rmb.extractNodeDetailsFrom(nodeDetailsFiles, map);
		return map;
	}

	public OsmRouteWayMap reverseRouteMap(OsmRouteWayMap map, String routeUri,
			String direction) {
		OsmRouteWayMap reverse = new OsmRouteWayMap();
		reverse.setServiceUri(routeUri);
		reverse.setDirection(direction);

		List<OsmWay> ways = map.getWays();
		for (int i = ways.size() - 1; i >= 0; i--) {
			OsmWay way = ways.get(i);
			OsmWay reverseWay = OsmWay.getWay(way.getId());
			List<OsmNode> wayNodes = way.getNodes();
			for (int j = wayNodes.size() - 1; j >= 0; j--) {
				reverseWay.addNode(wayNodes.get(j));
			}
			reverse.addWay(reverseWay);
		}

		return reverse;
	}

	public Map<String, SegmentDistance> mapStopsToRoute(OsmRouteWayMap map,
			Collection<Stop> stops) {
		Map<String, SegmentDistance> mappedStops = matcher.mapMatchStops(map,
				stops);
		// sysout the details of the mapped stops and the stops
		int count = 0;
		for (String stopId : mappedStops.keySet()) {
			SegmentDistance sd = mappedStops.get(stopId);
			// System.out.println(stopId);
			OSRef ref = new OSRef(sd.mappedPoint.getEasting(),
					sd.mappedPoint.getNorthing());
			LatLng ll = ref.toLatLng();
			// System.out.println(String.format(
			// "{id:\"MappedStop %s<br/>%s\", lng:%s,lat:%s},", stopId,
			// count, ll.getLng(), ll.getLat()));

			Stop s = Stop.getStop(stopId);
			ref = new OSRef(s.getLocation().getEasting(), s.getLocation()
					.getNorthing());
			ll = ref.toLatLng();
			// System.out.println(String.format(
			// "{id:\"Stop %s<br>  %s\", lng:%s,lat:%s},", stopId,
			// count++, ll.getLng(), ll.getLat()));

		}
		return mappedStops;
	}

	public OsmRouteWayMap mergeStopsWithMap(OsmRouteWayMap map,
			Map<String, SegmentDistance> mappedStops) {
		return matcher.mergeStopsAsNodesNewMap(map, mappedStops);
	}
	VehicleLocationEstimator vle;
	
	public void estimateLocations(Trip trip,
			String direction, OsmRouteWayMap map) {
		if (trip.getDirection() != direction) {
			return;
		}
		List<TimingPoint> newTimingPoints = new ArrayList<TimingPoint>();

		// associate the trip to the map
		List<TimingPoint> timingPoints = trip.getStopPoints();
		// if we don't have a location for a timing point (e.g. a stop)
		// remove it from the list
		for (int i = 0, j = timingPoints.size(); i < j; i++) {
			if (timingPoints.get(i).getPoint() == null) {
				timingPoints.remove(i);
				--i;
				--j;
			}
		}
		if (timingPoints.size() > 1) {
			for (int stopIndex = 0, j = timingPoints.size() - 1; stopIndex < j; stopIndex++) {
				// estimate the locations between two timing points
				StopTimingPoint from = (StopTimingPoint) timingPoints
						.get(stopIndex);
				StopTimingPoint to = (StopTimingPoint) timingPoints
						.get(stopIndex + 1);
				Stop origin = from.getStop(), destination = to
						.getStop();

				List<OsmNode> nodes = new ArrayList<OsmNode>();
				boolean startFound = false, endFound = false;
				int startFromWayIndex = 0, startFromNodeIndex = 0;
				for (int end = map.getWays().size(); startFromWayIndex < end; startFromWayIndex++) {
					OsmWay way = map.getWays().get(startFromWayIndex);
					for (int endNodes = way.getNumberOfNodes(); startFromNodeIndex < endNodes; startFromNodeIndex++) {
						OsmNode node = way.getNodes().get(
								startFromNodeIndex);
						if (startFound) {
							nodes.add(node);
							if (isNodeForStop(destination, node)) {
								endFound = true;
								break;
							}
						} else if (isNodeForStop(origin, node)) {
							nodes.add(node);
							startFound = true;
						}
					}
					if (endFound) {
						break;
					}
					startFromNodeIndex = 0;
				}

				List<Segment> segments = convertToSegments(nodes);
				List<EstimatedLocationPoint> estimates = vle
						.estimateLocationsBetween(from, to, segments,
								60);

				newTimingPoints.add(from);
				newTimingPoints.addAll(estimates);
			}
			// add the very final timing point
			newTimingPoints
					.add(timingPoints.get(timingPoints.size() - 1));
		}

		// displayingTimingPoints
		for (int i = 0, j = newTimingPoints.size(); i < j; i++) {
			TimingPoint tp = newTimingPoints.get(i);
			if (tp.getClass().equals(StopTimingPoint.class)) {
				StopTimingPoint stp = (StopTimingPoint) tp;
				Point stopPoint = (stp.getStop()
						.getMapMatchedLocation() != null) ? stp
						.getStop().getMapMatchedLocation() : stp
						.getStop().getLocation();

				OSRef ref = new OSRef(stopPoint.getEasting(),
						stopPoint.getNorthing());
				LatLng ll = ref.toLatLng();
				// System.out
				// .println(String
				// .format("{type:\"%s\", id:\"%s\", arrTime:\"%s\", depTime:\"%s\", lng:%s,lat:%s},",
				// tp.getClass().getName(), stp
				// .getStop()
				// .getAtcoCode(),
				// Utils.atcoTimeToString(tp
				// .getArrivalTime()),
				// Utils.atcoTimeToString(tp
				// .getDepartureTime()),
				// ll.getLng(), ll.getLat()));
			} else {
				OSRef ref = new OSRef(tp.getPoint().getEasting(), tp
						.getPoint().getNorthing());
				LatLng ll = ref.toLatLng();
				// System.out
				// .println(String
				// .format("{type:\"%s\",id:\"%s\", arrTime:\"%s\", depTime:\"%s\", lng:%s,lat:%s},",
				// tp.getClass().getName(),
				// ((EstimatedLocationPoint) tp)
				// .getId(),
				// Utils.atcoTimeToString(tp
				// .getArrivalTime()),
				// Utils.atcoTimeToString(tp
				// .getDepartureTime()),
				// ll.getLng(), ll.getLat()));
			}
		}
		trip.setStopPoints(newTimingPoints);
	}

	public void estimateLocations(Map<String, Service> services,
			String direction, OsmRouteWayMap map) {
		for (String key : services.keySet()) {
			Service service = services.get(key);
			for (Trip trip : service.getTrips()) {
				estimateLocations(trip, direction, map);
			}
		}
	}

	private List<Segment> convertToSegments(List<OsmNode> nodes) {
		List<Segment> segments = new ArrayList<Segment>();
		for (int i = 0, j = nodes.size() - 1; i < j; i++) {
			segments.add(new Segment(nodes.get(i), nodes.get(i + 1)));
		}
		return segments;
	}

	private boolean isNodeForStop(Stop stop, OsmNode node) {
		if (node.getClass().equals(MapMatchedStopPointNode.class)) {
			MapMatchedStopPointNode mmspn = (MapMatchedStopPointNode) node;
			return stop.equals(mmspn.getStop());
		}
		return false;
	}

}
