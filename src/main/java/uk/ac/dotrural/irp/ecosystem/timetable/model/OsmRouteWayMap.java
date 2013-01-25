package uk.ac.dotrural.irp.ecosystem.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Stop;

public class OsmRouteWayMap extends OsmServiceMap {

	private List<OsmWay> ways;
	private OsmNode startNode;
	private OsmNode endNode;
	private Collection<Stop> additionalBusStops;

	public OsmRouteWayMap() {
		super();
		this.ways = new ArrayList<OsmWay>();
		this.additionalBusStops = new ArrayList<Stop>();
	}

	public void addWay(OsmWay way) {
		this.addWay(way, ways.size());
	}

	public int getNumberOfWays() {
		return this.ways.size();
	}

	public void addWay(OsmWay way, int index) {
		if (index > ways.size()) {
			throw new IllegalArgumentException(
					"trying to add segment at index " + index
							+ " but maximum is " + ways.size());
		}
		if (index < 0) {
			throw new IllegalArgumentException(
					"Cannot add segment with negative index");
		}
		if (index == ways.size()) {
			this.ways.add(way);
		} else {
			ways.add(index, way);
		}
	}

	public List<OsmWay> getWays() {
		return ways;
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

	public void setAdditionalBusStops(Collection<Stop> stops){
		this.additionalBusStops = stops;
	}
	
	public Collection<Stop> getAdditionalBusStops(){
		return this.additionalBusStops;
	}
	
	public void addAdditionalBusStop(Stop stop){
		this.additionalBusStops.add(stop);
	}
	
	public boolean containsStop(Stop stop){
		for (Stop s : additionalBusStops){
			if (s.getAtcoCode().equals(stop.getAtcoCode()))return true;
		}
		return false;
	}
	
	
}
