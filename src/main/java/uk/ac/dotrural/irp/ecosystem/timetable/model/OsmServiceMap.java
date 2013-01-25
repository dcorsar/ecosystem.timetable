package uk.ac.dotrural.irp.ecosystem.timetable.model;

public class OsmServiceMap {

	private String serviceUri;
	private String direction;

	public OsmServiceMap() {
		super();
	}

	public String getDirection() {
		return direction;
	}

	public String getServiceUri() {
		return serviceUri;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void setServiceUri(String serviceUri) {
		this.serviceUri = serviceUri;
	}

}