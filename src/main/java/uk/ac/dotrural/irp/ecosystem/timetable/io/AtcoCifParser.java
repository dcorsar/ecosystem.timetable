package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.dotrural.irp.ecosystem.timetable.Utils;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.TimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Operator;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Service;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Stop;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.StopTimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Trip;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

/**
 * Converts an ATCO cif file
 * 
 * @author david
 * 
 */
public class AtcoCifParser {

	public static void main(String[] args) throws IOException {
		AtcoCifParser p = new AtcoCifParser();
		// change this to the ATCO cif file
		// p.parseFile("/Users/david/Documents/Projects/dotrural/dotruralProjects/InformedRuralPassenger/data/timetable/nptdr2011cif/Admin_Area_690/Admin_Area_690/ATCO_690_BUS.CIF");
		p.parseFile("resources/borders/62/SVRBOAO062.cif", "115");
		p.parseFile("resources/borders/62/SVRBOAO062A.cif", "115");
		p.parseFile("resources/borders/62/SVRBOAO062B.cif", "115");

		p.parseFile("resources/borders/396397/SVRBOAO397.cif", "115");
		p.parseFile("resources/borders/396397/SVRBOAO396.cif", "115");
		p.parseFile("resources/borders/73/SVRBOAO073.cif", "115");
		p.parseFile("resources/borders/72/SVRBOAO072.cif", "115");
		p.parseFile("resources/borders/95X9595A/SVRBOAO095.cif", "115");
		p.parseFile("resources/borders/95X9595A/SVRBOAO095A.cif", "115");
		p.parseFile("resources/borders/95X9595A/SVRBOAX095.cif", "115");

		Set<String> distinctRoutes = new TreeSet<String>();
		for (String key : p.routes.keySet()) {
			Service service = p.routes.get(key);
			System.out.println(key + "," + service.getId());
			for (Trip trip : service.getTrips()) {
				String sum = "";
				String stops = "";
				for (Stop s : trip.getStops()) {
					sum += s.getAtcoCode();
					OSRef ref = new OSRef(s.getLocation().getEasting(), s
							.getLocation().getNorthing());
					LatLng ll = ref.toLatLng();
					stops += String.format(
							"{id:\"%s\", lat:\"%s\", lng:\"%s\"},",
							s.getAtcoCode(), ll.getLat(), ll.getLng());

				}
				String route = String.format(
						"{route:\"%s\",sum:\"%s\", hash:\"%s\", stops:[%s]}",
						service.getId() + " " + trip.getDirection(), sum,
						sum.hashCode(), stops.substring(0, stops.length() - 1));
				distinctRoutes.add(route);
				// distinctRoutes.add(sum);
				// System.out.println(trip.getStopPoints().size());
			}
		}

		String json = new String();

		System.out.println("var routes=[");
		for (String r : distinctRoutes) {
			System.out.println(r + ", ");
		}
		System.out.println("];");
		// System.out.println(distinctRoutes.size());

	}

	private Map<String, Operator> operators;
	private Map<String, Service> routes;
	// mapping between the operator code in the CIF file and the code used in
	// the Traveline National Operator Code dataset
	private String ops = "WAI:WAIS,HOG:HOGG,MUJ:MNRO,SNA:SNAI,TVS:TVSR,PER:PERY,BKR:BSKR,MCE:MCEW,GVT:GVT,RBN:RBT,TEL:TELF,IDM:GVTR,SBC:SBC,FED:FSCE,FAB:FAB,BLB:BLB";

	public AtcoCifParser() {
		super();
		this.operators = new HashMap<String, Operator>();
		this.routes = new HashMap<String, Service>();
		for (String o : ops.split(",")) {
			String[] details = o.split(":");
			this.operators.put(details[0], new Operator(details[1]));
		}
	}

	public void parseFile(String filepath, String areaCode) throws IOException {
		parseFile(new File(filepath), areaCode);
	}

	public void parseFile(File file, String areaCode) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		this.areaCode = areaCode;

		int stopNumber = 0;
		Trip currentTrip = null;
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			String code = line.substring(0, 2);
			if ("QS".equals(code)) {
				currentTrip = parseJourneyHeader(line);
			} else if ("QE".equals(code)) {
				parseJourneyDateRunningRecord(line);
			} else if ("QO".equals(code)) {
				stopNumber = 0;
				parseJourneyOriginRecord(line, currentTrip);
			} else if ("QI".equals(code)) {
				parseJourneyIntermediateRecord(line, ++stopNumber, currentTrip);
			} else if ("QT".equals(code)) {
				parseJourneyDestinationRecord(line, ++stopNumber, currentTrip);
			} else if ("QB".equals(code)) {
				parseAdditionalLocationInformation(line);
			} else if ("QL".equals(code)) {
				parseLocationRecord(line);
			} else if ("ZS".equals(code)) {
				parseCustomSectionHeader(line);
			} else if ("QA".equals(code)) {
				parserLocationNameRectord(line);
			}
		}
		reader.close();
		// TimetableLoader tl = new TimetableLoader();
		// tl.load(operators, routes);
	}

	private String currentJourneyLabel;
	private String areaCode;

	private void parseCustomSectionHeader(String line) {
		currentJourneyLabel = line.substring(14, 114).trim();
	}

	private void parseLocationRecord(String line) {
		String stopId = line.substring(3, 15).trim();
		String fullLocation = line.substring(15, 63).trim();
		Stop s = Stop.getStop(stopId);
		s.setPrefLabel(fullLocation);
	}

	private void parserLocationNameRectord(String line) {
		String stopId = line.substring(3, 15).trim();
		String fullLocation = line.substring(15).trim();
		Stop s = Stop.getStop(stopId);
		if (!s.isLabelAdded()) {
			s.setPrefLabel(s.getPrefLabel() + ", " + fullLocation);
			s.setLabelAdded(true);
		}

	}

	private void parseAdditionalLocationInformation(String line) {
		String stopId = line.substring(3, 15).trim();
		String easting = line.substring(15, 23).trim();
		String northing = line.substring(23, 31).trim();
		Stop s = Stop.getStop(stopId);
		s.setLocation(new Point(Double.parseDouble(easting), Double
				.parseDouble(northing)));
	}

	public Map<String, Operator> getOperators() {
		return operators;
	}

	public Map<String, Service> getRoutes() {
		return routes;
	}

	public Collection<Stop> getStopsFor(String service, String direction) {
		return getStopsFor(routes.get(service), direction);
	}

	public Collection<Stop> getStopsFor(Service service, String direction) {
		Map<String, Stop> stopPoints = new TreeMap<String, Stop>();
		for (Trip trip : service.getTrips()) {
			if (trip.getDirection().equals(direction)) {
				for (TimingPoint tp : trip.getStopPoints()) {
					StopTimingPoint sp = (StopTimingPoint) tp;
					stopPoints.put(sp.getStop().getAtcoCode(), sp.getStop());
				}
			}
		}

		return stopPoints.values();
	}

	private void parseFileHeader(String line) {

	}

	private Trip parseJourneyHeader(String line) {
		String operator = line.substring(3, 7).trim();
		String jId = line.substring(7, 13).trim();
		String start = line.substring(13, 21);
		String end = line.substring(21, 29);
		char mo = line.charAt(29);
		char tu = line.charAt(30);
		char we = line.charAt(31);
		char th = line.charAt(32);
		char fr = line.charAt(33);
		char sa = line.charAt(34);
		char su = line.charAt(35);
		char term = line.charAt(36);
		char bank = line.charAt(37);
		String routeNo = line.substring(37, 42).trim();
		char dir = line.charAt(64);

		Service r = getService(routeNo);
		Trip trip = new Trip(areaCode + "/" + routeNo + "/" + jId);
		trip.setOperator(this.operators.get(operator));
		trip.setMo(('1' == mo));
		trip.setTu(('1' == tu));
		trip.setWe(('1' == we));
		trip.setTh(('1' == th));
		trip.setFr(('1' == fr));
		trip.setSa(('1' == sa));
		trip.setSu(('1' == su));
		r.addTrip(trip);
		trip.setDirection(('I' == dir) ? Trip.INBOUND : Trip.OUTBOUND);
		trip.setAltLabel(currentJourneyLabel);
		return trip;
	}

	private Service getService(String routeNo) {
		routeNo = routeNo.trim();
		Service r = this.routes.get(routeNo);
		if (r == null) {
			r = new Service(routeNo);
			this.routes.put(routeNo, r);
		}
		return r;
	}

	private void parseJourneyDateRunningRecord(String line) {

	}

	private void parseJourneyOriginRecord(String line, Trip currentTrip) {
		String loc = line.substring(2, 14);
		// String deptime = toXsdTime(line.substring(14, 18));
		long deptime = Utils.parseAtcoTime(line.substring(14, 18));
		StopTimingPoint sp = new StopTimingPoint(Stop.getStop(loc),
				Long.MIN_VALUE, deptime, 0);
		currentTrip.addStopPoint(sp);
	}

	private void parseJourneyIntermediateRecord(String line, int stopNumber,
			Trip currentTrip) {
		String loc = line.substring(2, 14);
		// String arrTime = toXsdTime(line.substring(14, 18));
		// String depTime = toXsdTime(line.substring(18, 22));
		long arrTime = Utils.parseAtcoTime(line.substring(14, 18));
		long depTime = Utils.parseAtcoTime(line.substring(18, 22));
		if (arrTime != depTime) {
			// System.out.println("difference in times " +line.substring(14,
			// 22));
		}
		char activity = line.charAt(22);
		StopTimingPoint sp = new StopTimingPoint(Stop.getStop(loc), arrTime,
				depTime, stopNumber);
		currentTrip.addStopPoint(sp);
	}

	private void parseJourneyDestinationRecord(String line, int stopNumber,
			Trip currentTrip) {
		String loc = line.substring(2, 14);
		// String arrTime = toXsdTime(line.substring(14, 18));
		long arrTime = Utils.parseAtcoTime(line.substring(14, 18));
		StopTimingPoint sp = new StopTimingPoint(Stop.getStop(loc), arrTime,
				Long.MIN_VALUE, stopNumber);
		currentTrip.addStopPoint(sp);
	}

	private void parseJourneyRepititionRecord(String line) {

	}

	// private void parseLocationRecord(String line) {
	//
	// }

	private void parseAdditionalLocationInfoRecord(String line) {

	}

	private void parseAltLocationRecord(String line) {

	}

}
