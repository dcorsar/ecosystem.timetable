package uk.ac.dotrural.irp.ecosystem.timetable.model;

public class TimingPoint implements GeographicFeature   {

	private Point point;
	private long arrivalTime, departureTime;
	
	public TimingPoint(Point point, long arrivalTime, long departureTime) {
		super();
		this.point = point;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.dotrural.irp.ecosystem.timetable.model.GeographicFeature#getPoint()
	 */
	public Point getPoint() {
		return point;
	}

	/* (non-Javadoc)
	 * @see uk.ac.dotrural.irp.ecosystem.timetable.model.GeographicFeature#setPoint(uk.ac.dotrural.irp.ecosystem.timetable.model.Point)
	 */
	public void setPoint(Point point) {
		this.point = point;
	}



	public long getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(long arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public long getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(long departureTime) {
		this.departureTime = departureTime;
	}
	
	
	
}
