package uk.ac.dotrural.irp.ecosystem.timetable.model.cif;

import java.util.ArrayList;
import java.util.List;

public class Service {

	private String id;
	private List<Trip> trips;

	public Service(String id) {
		super();
		this.id = id.trim();
		this.trips = new ArrayList<Trip>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id.trim();
	}



	public void addTrip(Trip t) {
		this.trips.add(t);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Service other = (Service) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public List<Trip> getTrips() {
		return trips;
	}

	public void setTrips(List<Trip> trips) {
		this.trips = trips;
	}

}
