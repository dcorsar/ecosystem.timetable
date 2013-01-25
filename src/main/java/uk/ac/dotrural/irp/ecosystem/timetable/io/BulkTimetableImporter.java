package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.dotrural.irp.ecosystem.timetable.RouteWayMapBuilder;
import uk.ac.dotrural.irp.ecosystem.timetable.SegmentDistance;
import uk.ac.dotrural.irp.ecosystem.timetable.TimetableBuilder;
import uk.ac.dotrural.irp.ecosystem.timetable.Utils;
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

public class BulkTimetableImporter {

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
		RouteWayMapBuilder mapBuilder = new RouteWayMapBuilder();
		Map<Integer, OsmRouteWayMap> tripRouteMappings = importer
				.readRouteMappings(mappingFile, mapBuilder);
		AtcoCifParser atcoParser = new AtcoCifParser();
		// borders area code is 115
		importer.importTimetables(tripRouteMappings, atcoDirectory, atcoParser,
				osmMapFile, "115", Constants.routeBase, mapBuilder);

		// now just to display it in json
		System.out.println("var services = [");
		for (Service s : atcoParser.getRoutes().values()) {
			for (Trip t : s.getTrips()) {
				String stops = "";
				for (TimingPoint tp : t.getStopPoints()) {
					OSRef ref = new OSRef(tp.getPoint().getEasting(), tp
							.getPoint().getNorthing());
					LatLng ll = ref.toLatLng();
					stops += String
							.format("{type:\"%s\",arr:\"%s\", dep:\"%s\", lat:\"%s\", lng:\"%s\"},",
									tp.getClass().getName(), Utils
											.atcoTimeToString(tp
													.getArrivalTime()), Utils
											.atcoTimeToString(tp
													.getDepartureTime()), ll
											.getLat(), ll.getLng());
				}
				System.out.format("{trip:\"%s\", stops:[%s]},\n", t.getId()
						+ " " + t.getDirection(), stops);
			}
		}
		System.out.println("];");

	}

	public BulkTimetableImporter() {
	}

	public Map<Integer, OsmRouteWayMap> readRouteMappings(String mappingFile,
			RouteWayMapBuilder mapBuilder) throws IOException {
		Map<String, String> mappings = readRouteMappingsFile(mappingFile);
		Map<Integer, OsmRouteWayMap> maps = new TreeMap<Integer, OsmRouteWayMap>();

		for (String key : mappings.keySet()) {
			OsmRouteWayMap map = mapBuilder.buildOsmRouteMapFor(mappings
					.get(key));
			maps.put(Integer.parseInt(key), map);
		}
		return maps;
	}

	private Map<String, String> readRouteMappingsFile(String mappingFile)
			throws IOException {
		Map<String, String> mappings = new TreeMap<String, String>();
		BufferedReader reader = new BufferedReader(new FileReader(mappingFile));
		for (String s = reader.readLine(); s != null; s = reader.readLine()) {
			String[] pair = s.split("=");
			mappings.put(pair[0].trim(), pair[1].trim());
		}
		reader.close();
		return mappings;
	}

	private List<OsmRouteNodeMap> nodeMaps;
	private Map<Integer, String> tripService;

	/**
	 * 
	 * @param tripRouteMappings
	 *            Map of unique ID for a trip (based on, e.g. the bus stops
	 *            visited) to the map for that trip
	 * @param atcoDirectory
	 *            The directory containing atco cif files that will be imported
	 * @param osmXmlFile
	 *            The XML file containing the OSM maps
	 * @throws IOException
	 */
	public void importTimetables(
			Map<Integer, OsmRouteWayMap> tripRouteMappings,
			String atcoDirectory, AtcoCifParser atcoParser, String osmXmlFile,
			String areaCode, String serviceUriBase, RouteWayMapBuilder rwmb)
			throws IOException {

		TimetableBuilder tb = new TimetableBuilder();

		// have to determine the id for every trip individual, then use that to
		// find the map
		// which is then used to estimate locations

		File[] atcoFiles = getAtcoDirectoryList(atcoDirectory);
		for (File atcoFile : atcoFiles) {
			atcoParser.parseFile(atcoFile, areaCode);
		}
		this.tripService = new HashMap<Integer, String>();
		this.nodeMaps = new ArrayList<OsmRouteNodeMap>();
		Map<Integer, List<Trip>> groupedTrips = new TreeMap<Integer, List<Trip>>();
		// group the trips to allow batch processing later
		for (Service service : atcoParser.getRoutes().values()) {
			for (Trip trip : service.getTrips()) {
				String tripStopList = "";
				for (Stop stop : trip.getStops()) {
					tripStopList += stop.getAtcoCode();
				}
				int tripId = tripStopList.hashCode();
				List<Trip> trips = groupedTrips.get(tripId);
				if (trips == null) {
					trips = new ArrayList<Trip>();
				}
				trips.add(trip);
				this.tripService.put(tripId, service.getId());
				groupedTrips.put(tripId, trips);
			}
		}

		new OsmXmlMapExtractor().extract(osmXmlFile);
		nodeMaps = new ArrayList<OsmRouteNodeMap>();
		for (Integer tripId : groupedTrips.keySet()) {
			List<Trip> trips = groupedTrips.get(tripId);
			System.out.printf("%s %s\n", tripId, trips.size());
			// extract the ways and nodes from the OSM XML file
			Trip t = trips.get(0);
			OsmRouteWayMap map = tripRouteMappings.get(tripId);
			if (map == null) {
				System.out.println("no map for " + tripId + " " + t.getId());
			}

			// create a map with the bus stops integraed
			OsmRouteNodeMap nodeMap = rwmb.inferRouteNodeMapFor(map);
			OsmRouteWayMap wayMap = Utils.createOsmWayMapFor(nodeMap);

			Map<String, SegmentDistance> mappedStops = tb.mapStopsToRoute(
					wayMap, t.getStops());
			OsmRouteWayMap mergedMap = tb
					.mergeStopsWithMap(wayMap, mappedStops);

			nodeMap.setDirection(t.getDirection());
			nodeMap.setServiceUri(serviceUriBase
					+ this.tripService.get(tripId));
			
			this.nodeMaps.add(nodeMap);
			if (tripId == -1568453423) {
				System.out.println("blah");
			}

			// estimate the locations for all trips
			for (Trip trip : trips) {
				if (trip.getId().equals("115/95/10440")) {
					System.out.println("*************** " + tripId);
				}
				tb.estimateLocations(trip, trip.getDirection(), mergedMap);

			}
			// OsmNode.clearNodes();
			// OsmWay.clearWays();
		}
	}

	public List<OsmRouteNodeMap> getNodeMaps(){
		return this.nodeMaps;
	}
	
	/**
	 * Gets all the files in the specified directory that have extension .cif
	 * 
	 * @param atcoDirectory
	 * @return
	 */
	private File[] getAtcoDirectoryList(String atcoDirectory) {
		File f = new File(atcoDirectory);
		if (f.isDirectory()) {
			return f.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.getName().endsWith(".cif");
				}
			});
		}
		return new File[0];
	}

}
