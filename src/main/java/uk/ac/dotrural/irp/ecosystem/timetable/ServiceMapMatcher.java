package uk.ac.dotrural.irp.ecosystem.timetable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.dotrural.irp.ecosystem.core.util.Util;
import uk.ac.dotrural.irp.ecosystem.timetable.io.Constants;
import uk.ac.dotrural.irp.ecosystem.timetable.io.OsmRdfGenerator;
import uk.ac.dotrural.irp.ecosystem.timetable.io.ServiceMapRdfGenerator;
import uk.ac.dotrural.irp.ecosystem.timetable.io.TripleStoreUtils;
import uk.ac.dotrural.irp.ecosystem.timetable.model.GeographicFeature;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteNodeMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteWayMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Segment;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Trip;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class ServiceMapMatcher {

	private static final String MAPNODES_TS = "mapnodes";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// quick and simple test

		// lets build a route map
		RouteWayMapBuilder rmb = new RouteWayMapBuilder();
		OsmRouteWayMap map = rmb.buildOsmRouteMapFor(
				"resources/wayIds15Outbound.txt");
		map.setDirection(Trip.OUTBOUND);
		map.setServiceUri("http://cops-022382.uoa.abdn.ac.uk:8086/timetable/route/X95");
		map.setStartNode(OsmNode.getNode("283038463"));
		map.setEndNode(OsmNode.getNode("1278622982"));
		rmb.extractWayDetailsFrom(
				"resources/planet_-3.31,54.82_-2.27,55.99.osm", map);
		rmb.extractNodeDetailsFrom(
				"resources/planet_-3.31,54.82_-2.27,55.99.osm", map);
		ServiceMapRdfGenerator gen = new ServiceMapRdfGenerator();
		OsmRouteNodeMap nodeMap = rmb.inferRouteNodeMapFor(map);

		// add the rdf for the service map to the store
		Collection<String> updates = gen.generateSparqlUpdates(nodeMap,
				Constants.mapsBase, Constants.nodesBase, nodeMap.getServiceUri(), nodeMap.getDirection());

		TripleStoreUtils.getTripleStoreUtils().createModel(MAPNODES_TS);
		TripleStoreUtils.getTripleStoreUtils().performUpdates(MAPNODES_TS,
				updates);

		// add the rdf for the nodes
		OsmRdfGenerator osmRdf = new OsmRdfGenerator();
		Collection<String> osmUpdates = osmRdf.generateUpdatesForNodes(
				nodeMap.getNodes(), Constants.nodesBase);
		TripleStoreUtils.getTripleStoreUtils().performUpdates(MAPNODES_TS,
				osmUpdates);

		TripleStoreUtils.getTripleStoreUtils().toTdbModel(MAPNODES_TS,
				"resources/mapnodes/");
		if (1 == 1) {
			System.exit(0);
		}

		// try to do some mapping
		ServiceMapMatcher matcher = new ServiceMapMatcher();

		FakeLocationProvider flp = new FakeLocationProvider();
		Collection<GpsLocation> locs = flp.getLocations();
		System.out.println("go");
		for (GpsLocation loc : locs) {
			LatLng ll = new LatLng(loc.lat, loc.lng);
			OSRef ref = ll.toOSRef();
			Point p = new Point(ref.getEasting(), ref.getNorthing());
			System.out.println(String.format("{acc:\"%s\", lon:%s,lat:%s},",
					loc.accuracy, loc.lng, loc.lat));

			SegmentDistance sd = matcher.mapToRoute(p, 500, Constants.routeBase
					+ "X95/", Trip.OUTBOUND, "http://localhost:8093");
			if (sd != null) {
				ref = new OSRef(sd.mappedPoint.getEasting(),
						sd.mappedPoint.getNorthing());
				ll = ref.toLatLng();
				System.out.println(String.format(
						" {id:\"%s\", lon:%s,lat:%s},", sd.distance,
						ll.getLng(), ll.getLat()));
			} else {
				System.out.println("   unable to map point");
			}
			// try {
			// // Thread.sleep(500);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace(System.err);
			// }
		}
	}

	public SegmentDistance mapToRouteFromJourney(Point point,
			double marginOfError, String journeyUri, String journeyEndpoint,
			String nodesEndpoint) {
		//System.out.println("starting mapping");
		Collection<Segment> candidateSegments = getSegmentsWithinFromJourney(
				point, marginOfError, journeyUri, journeyEndpoint,
				nodesEndpoint);
		if (candidateSegments.size() == 0) {
			System.out.println("no candidate segments");
			return null;
		}
	//	System.out.println("candidate segments " + candidateSegments);
		MapMatcher matcher = new MapMatcher();
		return matcher.mapSegments(point, candidateSegments);
	}

	private Collection<Segment> getSegmentsWithinFromJourney(Point point,
			double marginOfError, String journeyUri, String journeyEndpoint,
			String nodesEndpoint) {
		String journeyQ = String.format(getJourneyQuery, journeyUri);
		String lineUri, direction;

		ResultSet results = performRemoteQuery(journeyEndpoint, journeyQ);
		List<String> vars = results.getResultVars();
		if (results.hasNext()) {
			QuerySolution solution = results.next();
			lineUri = Util.getNodeValue(solution.get(vars.get(0))).trim();
			direction = Util.getNodeValue(solution.get(vars.get(1))).trim();
			return getSegmentsWithin(point, marginOfError, lineUri, direction,
					nodesEndpoint);
		} else {
			return new ArrayList<Segment>();
		}
	}

	private ResultSet performRemoteQuery(String endpoint, String query) {
		System.out.println(query + " => " + endpoint);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				endpoint, query);
		ResultSet results = queryExecution.execSelect();
		return results;
	}

	private String getJourneyQuery = "PREFIX irpuser: <http://www.dotrural.ac.uk/irp/uploads/ontologies/user/> PREFIX transport: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/>  PREFIX transit: <http://vocab.org/transit/terms/> SELECT ?line ?direction ?device ?user {<%s> a irpuser:BusJourney; transport:line ?line; transit:direction ?direction; irpuser:withDevice ?device; irpuser:user ?user.}";

	public SegmentDistance mapToRoute(Point point, double marginOfError,
			String serviceUri, String direction, String nodesEndpoint) {
		Collection<Segment> candidateSegments = getSegmentsWithin(point,
				marginOfError, serviceUri, direction, nodesEndpoint);
		MapMatcher matcher = new MapMatcher();
		return matcher.mapSegments(point, candidateSegments);
	}

	private void echoNode(GeographicFeature to) {
		OSRef ref = new OSRef(to.getPoint().getEasting(), to.getPoint()
				.getNorthing());
		LatLng ll = ref.toLatLng();
		System.out.println(String.format(" {id:\"%s\", lon:%s,lat:%s},", "-",
				ref.getEasting(), ref.getNorthing()));
		// System.out.println(String.format(" {id:\"%s\", lon:%s,lat:%s},", "-",
		// // ref.getEasting(), ref.getNorthing()));
		// ll.getLng(), ll.getLat()));
	}

	private Collection<Segment> getSegmentsWithin(Point point,
			double marginOfError, String serviceUri, String direction,
			String nodesEndpoint) {
		Map<Integer, OsmNode> nodes = getNodesWtihin(point.getEasting(),
				point.getNorthing(), marginOfError, serviceUri, direction,
				nodesEndpoint);
		Collection<Segment> candidateSegments = new LinkedList<Segment>();
		Collection<String> madeSegments = new HashSet<String>();
		// now need to create segments for each of the candidate nodes, both to
		// the node
		// and from the node
		for (Integer sequenceNumber : nodes.keySet()) {
			// create the node before this sequence number if we haven't already
			if (sequenceNumber > 0) {
				createSegment(serviceUri, direction, nodes, candidateSegments,
						madeSegments, sequenceNumber - 1, (sequenceNumber - 1)
								+ " " + sequenceNumber, nodesEndpoint);
			}

			// create the node after
			createSegment(serviceUri, direction, nodes, candidateSegments,
					madeSegments, sequenceNumber, sequenceNumber + " "
							+ (sequenceNumber + 1), nodesEndpoint);
		}

		return candidateSegments;
	}

	private void createSegment(String serviceUri, String direction,
			Map<Integer, OsmNode> nodes, Collection<Segment> candidateSegments,
			Collection<String> madeSegments, int fromSequenceNumber, String id,
			String nodesEndpoint) {
		if (!madeSegments.contains(id)) {
			Segment s = createSegement(fromSequenceNumber, nodes, serviceUri,
					direction, nodesEndpoint);
			if (s != null) {
				candidateSegments.add(s);
			}
			madeSegments.add(id);
		}
	}

	/**
	 * Attempts to create a segment from the nodes at index
	 * nodeSequenceNumberStart to (nodeSequenceNumberStart+1)
	 * 
	 * @param nodeSequenceNumberStart
	 * @param nodes
	 *            Nodes that we already know about, with the key being their
	 *            sequence number in the service map
	 * @return
	 */
	private Segment createSegement(int nodeSequenceNumberStart,
			Map<Integer, OsmNode> nodes, String serviceUri, String direction,
			String nodesEndpoint) {
		OsmNode fromNode = nodes.get(nodeSequenceNumberStart);
		if (fromNode == null) {
			fromNode = getNode(nodeSequenceNumberStart, serviceUri, direction,
					nodesEndpoint);
		}
		// start of the line
		if (fromNode == null) {
			return null;
		}
		OsmNode toNode = nodes.get(nodeSequenceNumberStart + 1);
		if (toNode == null) {
			// need to query and get it
			toNode = getNode(nodeSequenceNumberStart + 1, serviceUri,
					direction, nodesEndpoint);
		}
		// if still null, then its the end of the line
		if (toNode == null) {
			return null;
		}
		return new Segment(fromNode, toNode);
	}

	String nodeQuery = "PREFIX irpt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/> "
			+ "PREFIX irpinf: <http://www.dotrural.ac.uk/irp/uploads/ontologies/infrastructure/>"
			+ "PREFIX geo: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>"
			+ "PREFIX trans: <http://vocab.org/transit/terms/>"
			+ "PREFIX ogd: <http://linkedgeodata.org/ontology/>"
			+ "PREFIX dct: <http://purl.org/dc/elements/1.1/>"
			+ "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>"
			+ "SELECT  ?sequenceNumber ?node ?nodeId ?easting ?northing {"
			+ "  ?serviceMap a irpt:BusServiceMap; irpt:service <%s>; trans:direction \"%s\"^^<http://www.w3.org/2001/XMLSchema#string>; irpinf:hasNode ?mapNode."
			+ "?mapNode irpinf:sequenceNumber \"%s\"^^xsd:integer; irpinf:hasNode ?node."
			+ "?node dct:identifier ?nodeId; geo:easting ?easting;geo:northing ?northing."
			+ "}";

	private OsmNode getNode(int sequenceNumber, String serviceUri,
			String direction, String nodesEndpoint) {
		String query = String.format(nodeQuery, serviceUri, direction,
				sequenceNumber);
		ResultSet results = performRemoteQuery(nodesEndpoint, query);
		// assuming will only have one result - if not, someone has messed up
		if (results.hasNext()) {
			QuerySolution sol = results.next();
			OsmNode node = OsmNode
					.getNode(sol.getLiteral("nodeId").getString());
			node.setEasting(sol.getLiteral("easting").getDouble());
			node.setNorthing(sol.getLiteral("northing").getDouble());
			return node;
		}
		return null;
	}

	String candidateNodesQuery = "PREFIX irpt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/> "
			+ "PREFIX irpinf: <http://www.dotrural.ac.uk/irp/uploads/ontologies/infrastructure/>"
			+ "PREFIX geo: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>"
			+ "PREFIX trans: <http://vocab.org/transit/terms/>"
			+ "PREFIX ogd: <http://linkedgeodata.org/ontology/>"
			+ "PREFIX dct: <http://purl.org/dc/elements/1.1/>"
			+ "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>"
			+ "SELECT  ?sequenceNumber ?node ?nodeId ?easting ?northing {"
			+ "  ?serviceMap a irpt:BusServiceMap; irpt:service <%s>; trans:direction \"%s\"^^<http://www.w3.org/2001/XMLSchema#string>; irpinf:hasNode ?mapNode."
			+ "?mapNode irpinf:sequenceNumber ?sequenceNumber; irpinf:hasNode ?node."
			+ "?node dct:identifier ?nodeId; geo:easting ?easting;geo:northing ?northing."
			+ "filter (((abs(\"%s\"^^xsd:double - ?easting)) <= \"%s\"^^xsd:double) && ((abs(\"%s\"^^xsd:double  - ?northing)) <=\"%s\"^^xsd:double) ) ."
			+ "} order by ?sequenceNumber";

	private Map<Integer, OsmNode> getNodesWtihin(double easting,
			double northing, double marginOfError, String serviceUri,
			String direction, String nodesEndpoint) {
		Map<Integer, OsmNode> nodes = new TreeMap<Integer, OsmNode>();
		String query = String.format(candidateNodesQuery, serviceUri,
				direction, easting, marginOfError, northing, marginOfError);
		ResultSet results = performRemoteQuery(nodesEndpoint, query);
		while (results.hasNext()) {
			QuerySolution sol = results.next();
			OsmNode node = OsmNode
					.getNode(sol.getLiteral("nodeId").getString());
			node.setEasting(sol.getLiteral("easting").getDouble());
			node.setNorthing(sol.getLiteral("northing").getDouble());
			nodes.put(sol.getLiteral("sequenceNumber").getInt(), node);
		}
		return nodes;
	}
}
