package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uk.ac.dotrural.irp.ecosystem.timetable.Utils;
import uk.ac.dotrural.irp.ecosystem.timetable.model.EstimatedLocationPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.TimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Operator;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Service;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.StopTimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Trip;
import static uk.ac.dotrural.irp.ecosystem.timetable.io.Constants.*;

/**
 * Creates a set of SPARQL updates for services
 * 
 * @author david
 * 
 */

public class CifToRdfUpdates {

	

	public List<String> generateSparqlUpdates(Map<String, Operator> operators,
			Service service) {
		Map<String, Service> services = new HashMap<String, Service>();
		services.put(service.getId(), service);
		return generateSparqlUpdates(operators, services);
	}

	public List<String> generateSparqlUpdates(Map<String, Operator> operators,
			Map<String, Service> services) {
		int counter = 1;
		// a list of sparql update queries - one for each route, trip, and
		// service
		List<String> updates = new ArrayList<String>();
		for (Service s : services.values()) {
			String routeUri = routeBase + s.getId();
			updates.add(getRoute(routeUri, s.getId(), s.getId()));
			for (Trip t : s.getTrips()) {
				String tripUri = tripBase + t.getId();
				String serviceCal = servCalBase + t.getId();
				updates.add(getTrip(tripUri, t.getDirection(), s.getId() + " "
						+ t.getDirection(), t.getAltLabel(), operatorBase
						+ t.getOperator().getId(), routeUri, serviceCal));
				updates.add(getServiceCalendar(serviceCal, t.isMo(), t.isTu(),
						t.isWe(), t.isTh(), t.isFr(), t.isSa(), t.isSu()));

				List<TimingPoint> timingPoints = t.getStopPoints();
				for (int i = 0, j = timingPoints.size(); i < j; i++) {
					TimingPoint tp = timingPoints.get(i);
					if (tp.getClass().equals(StopTimingPoint.class)) {
						StopTimingPoint stp = (StopTimingPoint) tp;
						// Point stopPoint = (stp.getStop()
						// .getMapMatchedLocation() != null) ? stp
						// .getStop().getMapMatchedLocation() : stp
						// .getStop().getLocation();
						updates.add(getStopTime(stopTimeBase + t.getId() +"/" +counter++,
								Utils.atcoTimeToString(stp.getArrivalTime()),
								Utils.atcoTimeToString(stp.getDepartureTime()),
								i, stopBase + stp.getStop().getAtcoCode(),
								tripUri));
					} else if (tp.getClass().equals(
							EstimatedLocationPoint.class)) {
						updates.add(getVirtualStopTime(
								stopTimeBase + t.getId() +"/" +counter++,
								Utils.atcoTimeToString(tp.getArrivalTime()),
								Utils.atcoTimeToString(tp.getDepartureTime()),
								i,
								virtualStopBase
										+ ((EstimatedLocationPoint) tp).getId(),
								tripUri));
					}
				}
			}
		}
		return updates;
	}

	private String getRoute(String uri, String label, String shortName) {
		String query = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ " PREFIX transit: <http://vocab.org/transit/terms/>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX sprel: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>"
				+ " PREFIX irptt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/>"
				+ "PREFIX naptan: <http://transport.data.gov.uk/def/naptan/>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "PREFIX wgs84: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
				+ " insert data  {<%s> a transit:Route;  "
				+ "rdfs:label \"%s\"^^xsd:string; skos:altLabel \"%s\"^^xsd:string;"
				+ "naptan:administrativeArea <http://transport.data.gov.uk/id/administrative-area/111>;"
				+ "transit:routeShortName \"%s\"^^xsd:string." + "}";
		return String.format(query, uri, label, label, shortName);
	}

	private String getTrip(String uri, String direction, String label,
			String altLabel, String operatorUri, String routeUri,
			String servCalUri) {
		String query = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ " PREFIX transit: <http://vocab.org/transit/terms/>"
				+ "PREFIX sprel: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>"
				+ " PREFIX irptt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/>"
				+ "PREFIX wgs84: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
				+ " insert data {<%s> a transit:Trip;  "
				+ "transit:direction \"%s\"^^xsd:string;"
				+ "rdfs:label \"%s\"^^xsd:string; transit:description \"%s\"^^xsd:string;"
				+ "transit:operator <%s>;" + "transit:route <%s>;"
				+ "transit:serviceCalendar <%s>.}";
		return String.format(query, uri, direction, label, altLabel,
				operatorUri, routeUri, servCalUri);
	}

	private String getServiceCalendar(String uri, boolean m, boolean tu,
			boolean w, boolean th, boolean f, boolean sa, boolean su) {

		String query = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ " PREFIX transit: <http://vocab.org/transit/terms/>"
				+ "PREFIX sprel: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>"
				+ "PREFIX irptt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/>"
				+ "PREFIX wgs84: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
				+ "insert data  { <%s> a transit:ServiceCalendar;"
				+ " transit:monday \"%s\"^^xsd:boolean;"
				+ "transit:tuesday \"%s\"^^xsd:boolean;"
				+ "transit:wednesday \"%s\"^^xsd:boolean;"
				+ "transit:thursday \"%s\"^^xsd:boolean;"
				+ "transit:friday \"%s\"^^xsd:boolean;"
				+ "transit:saturday \"%s\"^^xsd:boolean;"
				+ "transit:sunday \"%s\"^^xsd:boolean.}";

		return String.format(query, uri, m, tu, w, th, f, sa, su);

	}

	private String getStopTime(String uri, String arrivalTime,
			String departureTime, int sequence, String stopUri, String tripUri) {
		String query = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ " PREFIX transit: <http://vocab.org/transit/terms/>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX sprel: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>"
				+ " PREFIX irptt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/>"
				+ "PREFIX wgs84: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
				+ " insert data {	<%s> a transit:StopTime; ";
		if (arrivalTime != null && (!("".equals(arrivalTime))))
			query += "transit:arrivalTime \"" + arrivalTime + "\"^^xsd:time;";
		if (departureTime != null&& (!("".equals(departureTime))))
			query += "transit:departureTime \"" + departureTime
					+ "\"^^xsd:time;";

		query += "transit:stopSequenceNumber \"%s\"^^xsd:integer;"
				+ "transit:stop <%s>;" + "transit:trip <%s>.}";

		return String.format(query, uri, sequence, stopUri, tripUri);
	}

	private String getVirtualStopTime(String uri, String arrivalTime,
			String departureTime, int sequence, String stopUri, String tripUri) {
		String query = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ " PREFIX transit: <http://vocab.org/transit/terms/>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX sprel: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>"
				+ " PREFIX irptt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/>"
				+ "PREFIX wgs84: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
				+ " insert data {	<%s> a irptt:VirtualStopTime; ";
		if (arrivalTime != null)
			query += "transit:arrivalTime \"" + arrivalTime + "\"^^xsd:time;";
		if (departureTime != null)
			query += "transit:departureTime \"" + departureTime
					+ "\"^^xsd:time;";

		query += "transit:stopSequenceNumber \"%s\"^^xsd:integer;"
				+ "transit:stop <%s>;" + "transit:trip <%s>.}";

		return String.format(query, uri, sequence, stopUri, tripUri);
	}

}
