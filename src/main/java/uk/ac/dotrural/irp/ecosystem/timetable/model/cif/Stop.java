package uk.ac.dotrural.irp.ecosystem.timetable.model.cif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.dotrural.irp.ecosystem.timetable.model.GeographicFeature;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;

public class Stop implements GeographicFeature{

	private String atcoCode;
	private String prefLabel;
	private Point location;
	private Point mapMatchedLocation;
	private boolean labelAdded;

	private static Map<String, Stop> stops = new HashMap<String, Stop>();

	public static void printNumberOfStops(){
		System.out.println(stops.size());
	}
	
	public static Collection<Stop> getStops(){
		Collection<Stop> stops = new ArrayList<Stop>(Stop.stops.size());
		for (Stop s : Stop.stops.values()){
			stops.add(s);
		}
		return stops;
	}
	
	
	public static Stop getStop(String atcoCode) {
		atcoCode = atcoCode.trim();
		if (stops.containsKey(atcoCode)) {
			return stops.get(atcoCode);
		}
		Stop stop = new Stop(atcoCode);
		stops.put(atcoCode, stop);
		return stop;
	}

	private Stop(String atcoCode) {
		super();
		this.mapMatchedLocation = null;
		this.atcoCode = atcoCode;
		this.labelAdded = false;
	}

	
	
	public String getPrefLabel() {
		return prefLabel;
	}

	public void setPrefLabel(String prefLabel) {
		this.prefLabel = prefLabel;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Point getMapMatchedLocation() {
		return mapMatchedLocation;
	}

	public void setMapMatchedLocation(Point mapMatchedLocation) {
		this.mapMatchedLocation = mapMatchedLocation;
	}

	public String getAtcoCode() {
		return atcoCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((atcoCode == null) ? 0 : atcoCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stop other = (Stop) obj;
		if (atcoCode == null) {
			if (other.atcoCode != null)
				return false;
		} else if (!atcoCode.equals(other.atcoCode))
			return false;
		return true;
	}


//	@Override
	public Point getPoint() {
		return getLocation();
	}
//
////	@Override
	public void setPoint(Point point) {
		setLocation(point);
	}

	public boolean isLabelAdded() {
		return labelAdded;
	}

	public void setLabelAdded(boolean labelAdded) {
		this.labelAdded = labelAdded;
	}

}
