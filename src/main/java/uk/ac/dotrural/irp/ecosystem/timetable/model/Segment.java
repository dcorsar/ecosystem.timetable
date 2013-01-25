package uk.ac.dotrural.irp.ecosystem.timetable.model;

public class Segment {
	private GeographicFeature from, to;

	public Segment(GeographicFeature from, GeographicFeature to) {
		super();
		setFrom(from);
		setTo(to);
	}

	public GeographicFeature getFrom() {
		return from;
	}

	public void setFrom(GeographicFeature from) {
		this.from = from;
	}

	public GeographicFeature getTo() {
		return to;
	}

	public void setTo(GeographicFeature to) {
		this.to = to;
	}

	
	
}
