/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Trip;

/**
 * Generates updates for associating a KML file with a route in a specific
 * direction
 * 
 * @author david
 * 
 */
public class KmlRdfGenerator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		KmlRdfGenerator gen = new KmlRdfGenerator();
		Collection<String> updates = gen.generatedUpdatesFor(
				"resources/bordersTest/RouteKmlMappings.txt",
				Constants.kmlBase, Constants.routeBase,
				"http://homepages.abdn.ac.uk/dcorsar/pages/GetThere/kml/");
		for (String s : updates) {
			System.out.println(s);
		}

	}

	String update = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ " PREFIX transit: <http://vocab.org/transit/terms/>"
			+ " PREFIX irptt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/>"
			+ " insert data {<%s> a irptt:KmlFile;  "
			+ "transit:direction \"%s\"^^xsd:string;" + "transit:route <%s>;"
			+ "irptt:location \"%s\"^^xsd:string." + "}";

	public String createUpdateFor(String kmlUri, String routeUri,
			String direction, String location) {
		return String.format(update, kmlUri, direction, routeUri, location);
	}

	public Collection<String> generatedUpdatesFor(String routeMappingsFile,
			String kmlBaseUri, String routeBaseUri, String kmlWebBase)
			throws IOException {
		Collection<String> updates = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new FileReader(
				routeMappingsFile));
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			// assume line is format: routeId, direction, kmlFile
			String[] mappings = line.split(" ");
			String routeUri = routeBaseUri + mappings[0];
			String direction = ("inbound".equalsIgnoreCase(mappings[1])) ? Trip.INBOUND
					: Trip.OUTBOUND;
			String kmlLocation = kmlWebBase + mappings[2];
			String kmlUri = kmlBaseUri + mappings[0] + direction;
			updates.add(createUpdateFor(kmlUri, routeUri, direction,
					kmlLocation));
		}
		reader.close();
		return updates;
	}

}
