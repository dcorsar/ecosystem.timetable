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

        @Override public String toString() {
            return "from: [" + from + "]" +
                   "; to: [" + to + "]";
        }

        @Override public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Segment other = (Segment) obj;
            if (this.from != other.from && (this.from == null || !this.from.equals(other.from))) {
                return false;
            }
            if (this.to != other.to && (this.to == null || !this.to.equals(other.to))) {
                return false;
            }
            return true;
        }

        @Override public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.from != null ? this.from.hashCode() : 0);
            hash = 97 * hash + (this.to != null ? this.to.hashCode() : 0);
            return hash;
        }

}
