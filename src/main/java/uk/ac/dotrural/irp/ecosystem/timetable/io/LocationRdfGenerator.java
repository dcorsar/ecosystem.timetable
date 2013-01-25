package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.dotrural.irp.ecosystem.timetable.model.EstimatedLocationPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.TimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Service;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.StopTimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Trip;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

/**
 * Responsible for generating stops for all the stops and estimated locations on
 * a service
 * 
 * @author David Corsar
 * 
 */
public class LocationRdfGenerator {
	private static String adminArea = "http://transport.data.gov.uk/doc/administrative-area/115";

	public Collection<String> generateSparqlUpdates(
			Map<String, Service> services) {
		Set<String> queries = new TreeSet<String>();
		for (Service s : services.values()) {
			for (Trip t : s.getTrips()) {
				List<TimingPoint> timingPoints = t.getStopPoints();
				for (int i = 0, j = timingPoints.size(); i < j; i++) {
					TimingPoint tp = timingPoints.get(i);
					if (tp.getClass().equals(StopTimingPoint.class)) {
						StopTimingPoint stp = (StopTimingPoint) tp;
						Point stopPoint = (stp.getStop()
								.getMapMatchedLocation() != null) ? stp
								.getStop().getMapMatchedLocation() : stp
								.getStop().getLocation();

						OSRef ref = new OSRef(stopPoint.getEasting(),
								stopPoint.getNorthing());
						LatLng ll = ref.toLatLng();

						queries.add(getStop(Constants.stopBase
								+ stp.getStop().getAtcoCode(), adminArea, stp
								.getStop().getPrefLabel(), (int) stopPoint
								.getEasting(), (int) stopPoint.getNorthing(),
								ll.getLat(), ll.getLng(), stp.getStop()
										.getAtcoCode(), ""));
					} else if (tp.getClass().equals(
							EstimatedLocationPoint.class)) {
						EstimatedLocationPoint elp = (EstimatedLocationPoint) tp;
						OSRef ref = new OSRef(tp.getPoint().getEasting(), tp
								.getPoint().getNorthing());
						LatLng ll = ref.toLatLng();
						queries.add(getEstimatedLocation(
								Constants.virtualStopBase + elp.getId(),
								adminArea, "estimated location based on timetable",
								(int) tp.getPoint().getEasting(), (int) tp
										.getPoint().getNorthing(), ll.getLat(),
								ll.getLng()));
					}
				}
			}
		}
		return queries;
	}

	public String getStop(String stopUri, String adminArea, String prefLabel,
			int easting, int northing, double lat, double lng, String atcoCode,
			String naptanCode) {
		String updateQuery = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
				+ "PREFIX naptan: <http://transport.data.gov.uk/def/naptan/> "
				+ "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> "
				+ "PREFIX spatial: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/> "
				+ "insert data {"
				+ "  <%s> a naptan:StopPoint; naptan:administrativeArea <%s>;"
				+ "skos:prefLabel \"%s\"@en; spatial:easting \"%s\"^^<http://www.w3.org/2001/XMLSchema#integer>	;"
				+ " spatial:northing \"%s\"^^<http://www.w3.org/2001/XMLSchema#integer> ;  geo:lat \"%s\" ;"
				+ " geo:long \"%s\" ;naptan:atcoCode \"%s\"^^naptan:AtcoCode; naptan:naptanCode  \"%s\"^^naptan:NaptanCode."
				+ "}";

		String query = String.format(updateQuery, stopUri, adminArea,
				prefLabel, easting, northing, lat, lng, atcoCode, naptanCode);
		return query;
	}

	public String getEstimatedLocation(String locationUri, String adminArea,
			String prefLabel, int easting, int northing, double lat, double lng) {
		String updateQuery = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
				+ "PREFIX naptan: <http://transport.data.gov.uk/def/naptan/> "
				+ "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> "
				+ " PREFIX irptt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/> "
				+ "PREFIX spatial: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/> "
				+ "insert data {"
				+ "  <%s> a irptt:VirtualStop; naptan:administrativeArea <%s>;"
				+ "skos:prefLabel \"%s\"@en; spatial:easting \"%s\"^^<http://www.w3.org/2001/XMLSchema#integer>	;"
				+ " spatial:northing \"%s\"^^<http://www.w3.org/2001/XMLSchema#integer> ;  geo:lat \"%s\" ;"
				+ " geo:long \"%s\"." + "}";

		String query = String.format(updateQuery, locationUri, adminArea,
				prefLabel, easting, northing, lat, lng);
		return query;
	}
}
