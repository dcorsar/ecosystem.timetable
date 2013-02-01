package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.dotrural.irp.ecosystem.timetable.RouteWayMapBuilder;
import uk.ac.dotrural.irp.ecosystem.timetable.model.EstimatedLocationPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteNodeMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteWayMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmWay;
import uk.ac.dotrural.irp.ecosystem.timetable.model.TimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Service;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Stop;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Trip;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

/**
 * Generates RDF for: OSM nodes and ways, bus stops, timetable,
 * 
 * @author david
 * 
 */
public class BulkRdfGenerator {

	private static String adminArea = "http://transport.data.gov.uk/doc/administrative-area/115";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String base = "resources/bordersTest/";
		String osmMapFile = base + "osm/planet_-2.974_55.273_82f07edd.osm";
		String mappingFile = base + "RouteWayMappings.txt";
		String atcoDirectory = base + "cif";

		BulkTimetableImporter importer = new BulkTimetableImporter();
//		RouteWayMapBuilder mapBuilder = new RouteWayMapBuilder();
//		Map<Integer, OsmRouteWayMap> tripRouteMappings = importer
//				.readRouteMappings(mappingFile, mapBuilder);
//		AtcoCifParser atcoParser = new AtcoCifParser();
//		// borders area code is 115
//		importer.importTimetables(tripRouteMappings, atcoDirectory, atcoParser,
//				osmMapFile, "115", Constants.routeBase, mapBuilder);

		BulkRdfGenerator brg = new BulkRdfGenerator();

//		brg.generateBusStopRdf("timetable",
//				"resources/bordersTest/rdf/timetable");
//		System.out.println("...");
//		brg.generateVirtualStopRdf("timetable",
//				"resources/bordersTest/rdf/timetable", atcoParser.getRoutes());
//		System.out.println("...");
//		brg.generateTimetableRdf("timetable",
//				"resources/bordersTest/rdf/timetable", atcoParser);

		brg.generateKmlRdf("timetable", "resources/bordersTest/rdf/timetable",
				"resources/bordersTest/RouteKmlMappings.txt",
				"http://107.20.159.169/GetThere/kml/");
		System.out.println("...");

//		brg.generateOsmRdf("mapnodes", "resources/bordersTest/rdf/nodes",
//				importer.getNodeMaps());
//		System.out.println("...");
//		brg.generateMapRdf("mapnodes", "resources/bordersTest/rdf/nodes",
//				importer.getNodeMaps());
//		System.out.println("...");

	}

	private void displayUpdates(Collection<String> updates) {
		for (String s : updates) {
			System.out.println(s);
		}
	}

	private LocationRdfGenerator lrg;

	public BulkRdfGenerator() {
		super();
		this.lrg = new LocationRdfGenerator();
	}

	public void generateOsmRdf(String modelName, String tdbStoreLocation,
			List<OsmRouteNodeMap> maps) {
		OsmRdfGenerator generator = new OsmRdfGenerator();
		Set<OsmNode> nodes = new HashSet<OsmNode>();
		for (OsmRouteNodeMap map : maps) {
			nodes.addAll(map.getNodes());
		}
		Collection<String> updates = generator.generateUpdatesForNodes(nodes,
				Constants.nodesBase);
		// updates.addAll(generator.generateUpdatesForWays(OsmWay.getWays(),
		// Constants.waysBase, Constants.nodesBase));
		createModel(modelName, updates, tdbStoreLocation);
	}

	private void createModel(String modelName, Collection<String> updates,
			String location) {
		TripleStoreUtils.getTripleStoreUtils().createModel(modelName);
		TripleStoreUtils.getTripleStoreUtils().performUpdates(modelName,
				updates);
		TripleStoreUtils.getTripleStoreUtils().toTdbModel(modelName, location);
	}

	public void generateBusStopRdf(String modelName, String tdbStoreLocation) {
		Collection<Stop> stops = Stop.getStops();
		Collection<String> updates = new ArrayList<String>(stops.size());
		for (Stop stop : stops) {
			OSRef ref = new OSRef(stop.getPoint().getEasting(), stop.getPoint()
					.getNorthing());
			LatLng ll = ref.toLatLng();
			updates.add(lrg.getStop(
					// stopUri, adminArea, prefLabel, easting, northing, lat,
					// lng, atcoCode, naptanCode
					Constants.stopBase + stop.getAtcoCode(), adminArea,
					stop.getPrefLabel(), (int) stop.getPoint().getEasting(),
					(int) stop.getPoint().getNorthing(), ll.getLat(),
					ll.getLng(), stop.getAtcoCode(), ""));
		}
		// displayUpdates(updates);
		createModel(modelName, updates, tdbStoreLocation);
	}

	public void generateVirtualStopRdf(String modelName,
			String tdbStoreLocation, Map<String, Service> services) {
		Collection<String> updates = new ArrayList<String>();

		for (Service s : services.values()) {
			for (Trip t : s.getTrips()) {
				List<TimingPoint> timingPoints = t.getStopPoints();
				for (int i = 0, j = timingPoints.size(); i < j; i++) {
					TimingPoint tp = timingPoints.get(i);
					if (tp.getClass().equals(EstimatedLocationPoint.class)) {
						EstimatedLocationPoint elp = (EstimatedLocationPoint) tp;
						OSRef ref = new OSRef(tp.getPoint().getEasting(), tp
								.getPoint().getNorthing());
						LatLng ll = ref.toLatLng();
						updates.add(lrg.getEstimatedLocation(
								Constants.virtualStopBase + elp.getId(),
								adminArea,
								"estimated location based on timetable",
								(int) tp.getPoint().getEasting(), (int) tp
										.getPoint().getNorthing(), ll.getLat(),
								ll.getLng()));
					}
				}
			}
		}

		// displayUpdates(updates);
		createModel(modelName, updates, tdbStoreLocation);
	}

	public void generateMapRdf(String modelName, String tdbStoreLocation,
			List<OsmRouteNodeMap> nodeMaps) {
		ServiceMapRdfGenerator smrg = new ServiceMapRdfGenerator();
		Collection<String> updates = new ArrayList<String>();
		for (OsmRouteNodeMap map : nodeMaps) {
			updates.addAll(smrg.generateSparqlUpdates(map, Constants.mapsBase,
					Constants.nodesBase, map.getServiceUri(),
					map.getDirection()));
		}
		// displayUpdates(updates);
		createModel(modelName, updates, tdbStoreLocation);
	}

	public void generateTimetableRdf(String modelName, String tdbStoreLocation,
			AtcoCifParser parser) {
		Collection<String> updates = new ArrayList<String>();
		CifToRdfUpdates generator = new CifToRdfUpdates();
		for (Service service : parser.getRoutes().values()) {
			updates.addAll(generator.generateSparqlUpdates(
					parser.getOperators(), service));
		}
		// displayUpdates(updates);
		createModel(modelName, updates, tdbStoreLocation);
	}

	public void generateKmlRdf(String modelName, String tdbStoreLocation,
			String mappingFile, String remoteKmlBase) throws IOException {
		KmlRdfGenerator gen = new KmlRdfGenerator();
		Collection<String> updates = gen.generatedUpdatesFor(mappingFile,
				Constants.kmlBase, Constants.routeBase, remoteKmlBase);
		displayUpdates(updates);
//		createModel(modelName, updates, tdbStoreLocation);
	}

}
