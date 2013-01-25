package uk.ac.dotrural.irp.ecosystem.timetable.model.cif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.ac.dotrural.irp.ecosystem.timetable.model.TimingPoint;

public class Trip {

	public static final String OUTBOUND = "outbound";
	public static final String INBOUND = "inbound";
	
	private String id, altLabel;
	private String startDate, endDate, direction;
	private List<TimingPoint> stopPoints;
	private boolean mo, tu, we, th, fr, sa, su;
	private Operator operator;
	
	public Trip(String id) {
		super();
		this.id = id.trim();
		this.id = this.id.trim();
		this.stopPoints = new ArrayList<TimingPoint>();
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id.trim();
	}

	public void addStopPoint(StopTimingPoint sp) {
		this.stopPoints.add(sp);
	}

	public List<TimingPoint> getStopPoints() {
		return stopPoints;
	}

	public void setStopPoints(List<TimingPoint> stopPoints) {
		this.stopPoints = stopPoints;
	}

	public boolean isMo() {
		return mo;
	}

	public void setMo(boolean mo) {
		this.mo = mo;
	}

	public boolean isTu() {
		return tu;
	}

	public void setTu(boolean tu) {
		this.tu = tu;
	}

	public boolean isWe() {
		return we;
	}

	public void setWe(boolean we) {
		this.we = we;
	}

	public boolean isTh() {
		return th;
	}

	public void setTh(boolean th) {
		this.th = th;
	}

	public boolean isFr() {
		return fr;
	}

	public void setFr(boolean fr) {
		this.fr = fr;
	}

	public boolean isSa() {
		return sa;
	}

	public void setSa(boolean sa) {
		this.sa = sa;
	}

	public boolean isSu() {
		return su;
	}

	public void setSu(boolean su) {
		this.su = su;
	}

	public String getDirection() {
		if ("I".equals(direction)){
			return INBOUND;
		}
		if ("O".equals(direction)){
			return OUTBOUND;
		}
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getAltLabel() {
		return altLabel;
	}

	public void setAltLabel(String altLabel) {
		this.altLabel = altLabel;
	}

	public Collection<Stop> getStops() {
		Collection<Stop> stops = new ArrayList<Stop>(this.stopPoints.size());
		for (TimingPoint tp : this.stopPoints){
			if (tp.getClass().equals(StopTimingPoint.class)){
				stops.add(((StopTimingPoint)tp).getStop());
			}
		}
		return stops;
	}

}


