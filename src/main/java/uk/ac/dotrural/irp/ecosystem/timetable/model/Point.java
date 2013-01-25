package uk.ac.dotrural.irp.ecosystem.timetable.model;

public class Point implements GeographicFeature{
	private double easting, northing;

	public Point() {
		super();
	}

	public Point(double easting, double northing) {
		super();
		setEasting(easting);
		setNorthing(northing);
	}

	public double getEasting() {
		return easting;
	}

	public void setEasting(double easting) {
		this.easting = easting;
	}

	public double getNorthing() {
		return northing;
	}

	public void setNorthing(double northing) {
		this.northing = northing;
	}

	@Override
	public String toString() {
		return "Point [getEasting()=" + getEasting() + ", getNorthing()="
				+ getNorthing() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(easting);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(northing);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Point other = (Point) obj;
		if (Double.doubleToLongBits(easting) != Double
				.doubleToLongBits(other.easting))
			return false;
		if (Double.doubleToLongBits(northing) != Double
				.doubleToLongBits(other.northing))
			return false;
		return true;
	}

	public Point getPoint() {
		return this;
	}

	public void setPoint(Point point) {
		this.easting = point.easting;
		this.northing = point.northing;
	}

}
