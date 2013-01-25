package uk.ac.dotrural.irp.ecosystem.timetable.model.cif;

import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.TimingPoint;

public class StopTimingPoint extends TimingPoint{

	private Stop stop;
	private int stopNumber = -1;
	
	public StopTimingPoint(Stop stop, long arrivalTime, long departureTime, int stopNumber) {
		super(stop.getLocation(), arrivalTime, departureTime);
		this.stop = stop;
		this.stopNumber = stopNumber;
	}
	@Override
	public Point getPoint() {
		return this.stop.getLocation();
	}
	/**
	 * Sets the point for this TimingPoint along with the location of the associated stop
	 */
	@Override
	public void setPoint(Point point) {
		super.setPoint(point);
		this.stop.setLocation(point);
	}
	public int getStopNumber() {
		return stopNumber;
	}
	public void setStopNumber(int stopNumber) {
		this.stopNumber = stopNumber;
	}
	public Stop getStop() {
		return stop;
	}
	public void setStop(Stop stop) {
		this.stop = stop;
	}
}


