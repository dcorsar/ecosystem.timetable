package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteNodeMap;

public class ServiceMapRdfGenerator {

	public Collection<String> generateSparqlUpdates(OsmRouteNodeMap map,
			String baseNs, String osmNodeBaseUri, String serviceUri, String direction) {
		Collection<String> updates = new TreeSet<String>(), nodeUris = new TreeSet<String>();
		List<OsmNode> nodes = map.getNodes();
		for (int i = 0, j = nodes.size(); i < j; i++) {
			String nodeUri = baseNs + UUID.randomUUID().toString();
			nodeUris.add(nodeUri);
			updates.add(getRouteMapNode(nodes.get(i), i, nodeUri,
					osmNodeBaseUri));
		}
		updates.add(getBusRouteMapQuery(baseNs + UUID.randomUUID().toString(), serviceUri, direction,
				nodeUris));
		return updates;
	}

	String busRouteMapUpdate = "PREFIX irpt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/>"
			+ "PREFIX irpinf: <http://www.dotrural.ac.uk/irp/uploads/ontologies/infrastructure/> "
			+ "PREFIX trans: <http://vocab.org/transit/terms/>"
			+ "INSERT DATA {"
			+ " <%s> a irpt:BusServiceMap; irpt:service <%s>; trans:direction \"%s\"^^<http://www.w3.org/2001/XMLSchema#string>. %s "
			+ "}";
	String hasNodesUpdate = "<%s> irpinf:hasNode <%s>.";

	private String getBusRouteMapQuery(String mapUri, String serviceUri,
			String direction, Collection<String> nodeUris) {
		StringBuilder sb = new StringBuilder();
		for (String nodeUri : nodeUris) {
			sb.append(String.format(hasNodesUpdate, mapUri, nodeUri));
		}

		return String.format(String.format(busRouteMapUpdate, mapUri,
				serviceUri, direction, sb.toString()));
	}

	String routeMapNodeUpdate = "PREFIX irpt: <http://www.dotrural.ac.uk/irp/uploads/ontologies/transport/> "
			+ "PREFIX irpinf: <http://www.dotrural.ac.uk/irp/uploads/ontologies/infrastructure/>"
			+ "INSERT DATA {"
			+ "<%s> a irpt:ServiceMapNode; irpinf:sequenceNumber \"%s\"^^<http://www.w3.org/2001/XMLSchema#integer>; irpinf:hasNode <%s>."
			+ "}";

	private String getRouteMapNode(OsmNode osmNode, int sequenceNumber,
			String nodeUri, String osmNodeBaseUri) {
		return String.format(routeMapNodeUpdate, nodeUri, sequenceNumber,
				osmNodeBaseUri + osmNode.getId());
	}

}
