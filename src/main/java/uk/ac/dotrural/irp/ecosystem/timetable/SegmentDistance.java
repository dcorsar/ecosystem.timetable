package uk.ac.dotrural.irp.ecosystem.timetable;

import java.util.Comparator;

import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Segment;

public class SegmentDistance implements Comparator<SegmentDistance>,
		Comparable<SegmentDistance> {
	Segment segment;
	double distance;
	Point mappedPoint;

	public String toString() {
		return Double.toString(distance);
	}

	public int compare(SegmentDistance o1, SegmentDistance o2) {
		return (o1.distance == o2.distance) ? 0
				: (o1.distance - o2.distance) > 0 ? 1 : -1;
	}

	public int compareTo(SegmentDistance o2) {
		return (distance == o2.distance) ? 0 : (distance - o2.distance) > 0 ? 1
				: -1;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Point getMappedPoint() {
		return mappedPoint;
	}

	public void setMappedPoint(Point mappedPoint) {
		this.mappedPoint = mappedPoint;
	}
}
