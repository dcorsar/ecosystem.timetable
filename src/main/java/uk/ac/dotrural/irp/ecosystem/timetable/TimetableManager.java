package uk.ac.dotrural.irp.ecosystem.timetable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.dotrural.irp.ecosystem.timetable.io.AtcoCifParser;
import uk.ac.dotrural.irp.ecosystem.timetable.io.CifToRdfUpdates;
import uk.ac.dotrural.irp.ecosystem.timetable.io.Constants;
import uk.ac.dotrural.irp.ecosystem.timetable.io.LocationRdfGenerator;
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

public class TimetableManager {
	private String destDir = "resources/temp/";

	public static void main(String[] args) throws IOException {
		TimetableManager tm = new TimetableManager();
		List<String> updates = tm.doIt();
		Utils.update("resources/x95tt", updates);
	}

	public List<String> doIt() throws IOException {

		// get the timetable
		AtcoCifParser p = new AtcoCifParser();
		p.parseFile("resources/SVRBOAX095.cif", "115");

		// get the bus stops
		Collection<Stop> stops = p.getStopsFor("X95", Trip.OUTBOUND);
		System.out.println("2");

		// build the route map for the outbound trip
		RouteNodeMapBuilder rmb = new RouteNodeMapBuilder();
		OsmRouteNodeMap map = rmb.buildOsmRouteMapFor(
				"resources/x95outboundNodes.txt", Constants.routeBase
						+ "X95", Trip.OUTBOUND);

		// System.out.println("3");
		// extract the node details
		// rmb.extractWayDetailsFrom(
		// "resources/planet_-3.27,54.8_-2.46,56.05.osm", map);
		// System.out.println("2");
		rmb.extractNodeDetailsFrom(
				"resources/planet_-3.27,54.8_-2.46,56.05.osm", map);

		// System.out.println("4");
		// map the bus stops to the route
		StopToRouteMatcher matcher = new StopToRouteMatcher();
		OsmRouteWayMap wayMap = Utils.createOsmWayMapFor(map);
		Map<String, SegmentDistance> mappedStops = matcher.mapMatchStops(
				wayMap, stops);
		// sysout the details of the mapped stops and the stops
		int count = 0;
		for (String stopId : mappedStops.keySet()) {
			SegmentDistance sd = mappedStops.get(stopId);
			OSRef ref = new OSRef(sd.mappedPoint.getEasting(),
					sd.mappedPoint.getNorthing());
			LatLng ll = ref.toLatLng();
			System.out.println(String.format(
					"{id:\"MappedStop %s<br/>%s\", lng:%s,lat:%s},", stopId,
					count, ll.getLng(), ll.getLat()));

			Stop s = Stop.getStop(stopId);
			ref = new OSRef(s.getLocation().getEasting(), s.getLocation()
					.getNorthing());
			ll = ref.toLatLng();
			System.out.println(String.format(
					"{id:\"Stop %s<br>  %s\", lng:%s,lat:%s},", stopId,
					count++, ll.getLng(), ll.getLat()));

		}
		// for (OsmWay way : map.getWays())
		// for (OsmNode node : way.getNodes()) {
		// OSRef ref = new OSRef(node.getEasting(), node.getNorthing());
		// LatLng ll = ref.toLatLng();
		// System.out.println(String.format("{id:\"%s     %s %s %s\", lng:%s,lat:%s},",
		// node.getId(), count++, node.getEasting(), node.getNorthing(),
		// ll.getLng(), ll.getLat()));
		// }
		//

		System.out.println("5");
		// add the mapped stops as nodes in the route
		matcher.mergeStopsAsNodes(wayMap, mappedStops);

		// for (OsmWay way : map.getWays()) {
		// for (OsmNode node : way.getNodes()) {
		// OSRef ref = new OSRef(node.getEasting(), node.getNorthing());
		// LatLng ll = ref.toLatLng();
		// System.out.println(String.format(
		// " {id:\"%s\", lon:%s,lat:%s},", node.getId(),
		// ll.getLng(), ll.getLat()));
		// }
		// }

		// need to swap stops 6200209560 and 6200209570 as
		// 6200209570 should be before 6200209560 for outbound trips
		// but the current ATCO cif file doesn't have it that way!!!
		for (String key : p.getRoutes().keySet()) {
			Service service = p.getRoutes().get(key);
			for (Trip trip : service.getTrips()) {
				// ignore inbound trips
				if (trip.getDirection() == Trip.INBOUND) {
					continue;
				}
				List<TimingPoint> timingPoints = trip.getStopPoints();
				for (int i = 0, j = timingPoints.size(); i < j; i++) {
					StopTimingPoint stp = (StopTimingPoint) timingPoints.get(i);
					if ("6200209570".equals(stp.getStop().getAtcoCode())) {
						StopTimingPoint next = (StopTimingPoint) timingPoints
								.get(i + 1);
						if ("6200209560".equals(next.getStop().getAtcoCode())) {
							stp.setStop(Stop.getStop("6200209560"));
							next.setStop(Stop.getStop("6200209570"));
							break;
						}
					}
				}
			}
		}

		// the map now has the stops mapped to road and included as nodes
		// estimate the location between stops
		VehicleLocationEstimator vle = new VehicleLocationEstimator();
		List<String> allUpdates = new ArrayList<String>();
		for (String key : p.getRoutes().keySet()) {
			Service service = p.getRoutes().get(key);
			for (Trip trip : service.getTrips()) {
				if (trip.getDirection() == Trip.INBOUND) {
					continue;
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
						for (int end = wayMap.getWays().size(); startFromWayIndex < end; startFromWayIndex++) {
							OsmWay way = wayMap.getWays()
									.get(startFromWayIndex);
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
						System.out
								.println(String
										.format("{type:\"%s\", stop id:\"%s\", arrTime:\"%s\", depTime:\"%s\", lng:%s,lat:%s},",
												tp.getClass().getName(), stp
														.getStop()
														.getAtcoCode(),
												Utils.atcoTimeToString(tp
														.getArrivalTime()),
												Utils.atcoTimeToString(tp
														.getDepartureTime()),
												ll.getLng(), ll.getLat()));
					} else {
						OSRef ref = new OSRef(tp.getPoint().getEasting(), tp
								.getPoint().getNorthing());
						LatLng ll = ref.toLatLng();
						System.out
								.println(String
										.format("{type:\"%s\", stop id:\"%s\", arrTime:\"%s\", depTime:\"%s\", lng:%s,lat:%s},",
												tp.getClass().getName(),
												((EstimatedLocationPoint) tp)
														.getId(),
												Utils.atcoTimeToString(tp
														.getArrivalTime()),
												Utils.atcoTimeToString(tp
														.getDepartureTime()),
												ll.getLng(), ll.getLat()));
					}
				}
				trip.setStopPoints(newTimingPoints);
			}

			System.out
					.println("--------------------------------------------------------");
			// lets generate some RDF for the timetable
			CifToRdfUpdates rdfGenerator = new CifToRdfUpdates();
			List<String> updates = rdfGenerator.generateSparqlUpdates(
					p.getOperators(), p.getRoutes().get("X95"));
			for (String update : updates) {
				System.out.println(update);
			}
			allUpdates.addAll(updates);
			System.out
					.println("--------------------------------------------------------");
			// now lets generate some for the stops and estimated locations
			LocationRdfGenerator locGen = new LocationRdfGenerator();
			Collection<String> updates2 = locGen.generateSparqlUpdates(p
					.getRoutes());
			for (String string : updates2) {
				System.out.println(string);
			}
			allUpdates.addAll(updates2);
		}
		return allUpdates;
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

	public void updateTimetable() {
		createTempDirectory();
		downloadTrapezeTimetableZip();
	}

	/**
	 * Creates a temp directory if one doesn't already exist
	 */
	private boolean createTempDirectory() {
		File dir = new File(destDir);
		if (!(dir.exists())) {
			return dir.mkdir();
		}
		if (dir.isDirectory())
			return true;
		else {
			System.err.println("Unable to create directory "
					+ createTempDirectory()
					+ " as file already exists with that name");
			return false;
		}
	}

	/**
	 * Attempts to download the Trapeze timetable zip file via SFTP
	 * 
	 * @return
	 */
	private boolean downloadTrapezeTimetableZip() {
		SftpDownloader downloader = new SftpDownloader();
		String remoteFileName = "timetables.zip";
		return downloader.download("uoa", "ftp.trapezegroup.co.uk", "",
				remoteFileName, destDir + remoteFileName);
		// is it possible to check date of creation/update
		// to ensure its a new file?
	}

	private boolean extractZipFile() {
		ZipExtractor extractor = new ZipExtractor();
		String remoteFileName = "timetables.zip";
		boolean success = false;
		try {
			success = extractor.unzip(destDir + remoteFileName, destDir);
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	private void convertAtcoFile() {

	}

	private void uploadRdfTimetable() {

	}
}
