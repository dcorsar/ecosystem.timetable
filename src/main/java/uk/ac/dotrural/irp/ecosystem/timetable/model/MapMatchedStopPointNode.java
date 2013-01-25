package uk.ac.dotrural.irp.ecosystem.timetable.model;

import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.Stop;

public class MapMatchedStopPointNode extends OsmNode {

	private Stop stop;
	
	
	

	public MapMatchedStopPointNode(Stop stop) {
		super("BusStop" + stop.getAtcoCode());
//		System.out.println("map matched " + stop.getAtcoCode());
		this.stop = stop;
		setEasting(stop.getMapMatchedLocation().getEasting());
		setNorthing(stop.getMapMatchedLocation().getNorthing());
		setPoint(stop.getMapMatchedLocation());
	}

	public Stop getStop() {
		return stop;
	}
	
	

}
