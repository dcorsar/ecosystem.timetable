package uk.ac.dotrural.irp.ecosystem.timetable;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uk.ac.dotrural.irp.ecosystem.timetable.io.AtcoCifParser;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.TimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Service;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Stop;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.StopTimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Trip;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class StopPointExtractor {

	public static void main(String[] args) throws IOException {
		StopPointExtractor spe = new StopPointExtractor();
		spe.extractStopsFor("95", "outbound");
	}
	
	
	
	

	public Collection<Stop> extractStopsFor(String route, String direction)
			throws IOException {
		AtcoCifParser p = new AtcoCifParser();
		p.parseFile("resources/SVRBOAX095.cif", "115");

		Map<String, Stop> stopPoints = new TreeMap<String, Stop>();
		for (String key : p.getRoutes().keySet()) {
			Service service = p.getRoutes().get(key);
			for (Trip trip : service.getTrips()) {
				if (trip.getDirection().equals(direction)) {
					for (TimingPoint tp : trip.getStopPoints()) {
						StopTimingPoint sp = (StopTimingPoint) tp;
						stopPoints
								.put(sp.getStop().getAtcoCode(), sp.getStop());
					}
				}
			}
		}
		addLocationInformation(stopPoints.values());

		for (String key : p.getRoutes().keySet()) {
			Service service = p.getRoutes().get(key);
			for (Trip trip : service.getTrips()) {
				for (TimingPoint tp : trip.getStopPoints()) {
					StopTimingPoint sp = (StopTimingPoint) tp;
					if (sp.getStop().getLocation() == null) {
						System.out.println(sp.getStop().getAtcoCode());
					}
				}
			}
		}

		return stopPoints.values();
	}

	private static String queryEndpoint = "http://gov.tso.co.uk/transport/sparql";
	private static String stopQuery = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
			+ "PREFIX naptan: <http://transport.data.gov.uk/def/naptan/>"
			+ "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
			+ "PREFIX spatial: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>"
			+ "select distinct ?s ?easting  ?northing ?lat  ?long ?atcoCode ?naptanCode where {"
			+ "  ?s a naptan:StopPoint; "
			+ "skos:prefLabel ?label; spatial:easting ?easting ."
			+ "?s spatial:northing ?northing .  ?s geo:lat ?lat ."
			+ "?s geo:long ?long; naptan:atcoCode \"%s\"^^<http://transport.data.gov.uk/def/naptan/AtcoCode>; naptan:naptanCode ?naptanCode."
			+ "}";

	private void addLocationInformation(Collection<Stop> stops) {
		System.out.println(stops.size());
		for (Stop stop : stops) {
			String query = String.format(stopQuery, stop.getAtcoCode());
			ResultSet results = query(query);
			while (results.hasNext()) {
				QuerySolution sol = results.next();
				if (stop.getLocation() == null) {
					stop.setLocation(new Point());
				}
				stop.getLocation().setEasting(
						sol.getLiteral("easting").getDouble());
				stop.getLocation().setNorthing(
						sol.getLiteral("northing").getDouble());
			}
		}
	}

	public ResultSet query(String sQuery) {

		// System.out.format(" INFO: %s => %s\n", sQuery, queryEndpoint);

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				queryEndpoint, sQuery);
		ResultSet results = queryExecution.execSelect();

		return results;
	}
}
