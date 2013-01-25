package uk.ac.dotrural.irp.ecosystem.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Stop;

public class OsmRouteNodeMap extends OsmServiceMap {

	public List<OsmNode> nodes;
	private OsmNode startNode;
	private OsmNode endNode;
	private Collection<Stop> additionalBusStops;

	public OsmRouteNodeMap() {
		super();
		this.nodes = new ArrayList<OsmNode>();
		this.additionalBusStops = new ArrayList<Stop>();
	}

	public void addNode(OsmNode node) {
		this.addNode(node, nodes.size());
	}

	public int getNumberOfNodes() {
		return this.nodes.size();
	}

	public void addNode(OsmNode node, int index) {
		if (index > nodes.size()) {
			throw new IllegalArgumentException(
					"trying to add segment at index " + index
							+ " but maximum is " + nodes.size());
		}
		if (index < 0) {
			throw new IllegalArgumentException(
					"Cannot add segment with negative index");
		}
		if (index == nodes.size()) {
			this.nodes.add(node);
		} else {
			nodes.add(index, node);
		}
	}

	public List<OsmNode> getNodes() {
		return nodes;
	}

	public void addNodes(List<OsmNode> subList) {
		this.nodes.addAll(subList);
	}

	public OsmNode getStartNode() {
		return startNode;
	}

	public void setStartNode(OsmNode startNode) {
		this.startNode = startNode;
	}

	public OsmNode getEndNode() {
		return endNode;
	}

	public void setEndNode(OsmNode endNode) {
		this.endNode = endNode;
	}

	public void setAdditionalBusStops(Collection<Stop> stops) {
		this.additionalBusStops = stops;
	}

	public Collection<Stop> getAdditionalBusStops() {
		return this.additionalBusStops;
	}

	public void addAdditionalBusStop(Stop stop) {
		this.additionalBusStops.add(stop);
	}

	public boolean containsStop(Stop stop) {
		for (Stop s : additionalBusStops) {
			if (s.getAtcoCode().equals(stop.getAtcoCode()))
				return true;
		}
		return false;
	}

}
