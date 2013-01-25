package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmWay;

public class OsmRdfGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	String nodeUpdate = "PREFIX ogd: <http://linkedgeodata.org/ontology/>"
			+ "PREFIX dct: <http://purl.org/dc/elements/1.1/>"
			+ "PREFIX wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
			+ "PREFIX geo: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>"
			+ "INSERT DATA {"
			+ "<%s> a ogd:Node; dct:identifier \"%s\"^^<http://www.w3.org/2001/XMLSchema#string>;wgs:lat \"%s\"^^<http://www.w3.org/2001/XMLSchema#double>;wgs:long \"%s\"^^<http://www.w3.org/2001/XMLSchema#double>;geo:easting \"%s\"^^<http://www.w3.org/2001/XMLSchema#double>;geo:northing \"%s\"^^<http://www.w3.org/2001/XMLSchema#double>."
			+ "}";

	public Collection<String> generateUpdatesForNodes(
			Collection<OsmNode> nodes, String baseNs) {
		Collection<String> updates = new TreeSet<String>();
		for (OsmNode node : nodes) {
			updates.add(String.format(nodeUpdate, baseNs + node.getId(),
					node.getId(), node.getLat(), node.getLon(),
					node.getEasting(), node.getNorthing()));
		}
		return updates;
	}

	String wayUpdate = "PREFIX ogd: <http://linkedgeodata.org/ontology/>"
			+ "PREFIX dct: <http://purl.org/dc/elements/1.1/>"
			+ "INSERT DATA {"
			+ "<%s> a ogd:Way; dct:identifier \"%s\"^^<http://www.w3.org/2001/XMLSchema#string>.%s"
			+ "}";
	String wayHasNode = "<%s> irpinf:hasNode <%s>.";
	String osmOrderedNodeUpdate = "PREFIX irpinf: <http://www.dotrural.ac.uk/irp/uploads/ontologies/infrastructure/> " + "PREFIX ogd: <http://linkedgeodata.org/ontology/>"
			+ "INSERT DATA {"
			+ "<%s> a irpinf:OsmOrderedNode; irpinf:sequenceNumer \"%s\"^^<http://www.w3.org/2001/XMLSchema#integer>; ogd:hasNode <%s>. "
			+ "}";

	public Collection<String> generateUpdatesForWays(Collection<OsmWay> ways,
			String wayBaseNs, String nodeBaseNs) {
		Collection<String> updates = new TreeSet<String>();

		for (OsmWay way : ways) {
			StringBuilder nodeUpdates = new StringBuilder();
			String wayUri = wayBaseNs + way.getId();
			List<OsmNode> nodes = way.getNodes();
			for (int i = 0, j = nodes.size(); i < j; i++) {
				String uri = wayBaseNs + UUID.randomUUID().toString();
				nodeUpdates.append(String.format(wayHasNode, wayUri, uri));
				updates.add(String.format(osmOrderedNodeUpdate, uri, i, nodeBaseNs + nodes.get(i).getId()));
			}
			updates.add(String.format(wayUpdate, wayUri, way.getId(), nodeUpdates.toString()));
		}
		
		return updates;
	}

}
