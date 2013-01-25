package uk.ac.dotrural.irp.ecosystem.timetable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class FakeLocationProvider {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println((new FakeLocationProvider()).getLocations().size());;
	}

	private Collection<GpsLocation> locations;

	public FakeLocationProvider() {
		super();
		locations = new LinkedList<GpsLocation>();
		try {
			parseLocations();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.err);
		}
	}

	private void parseLocations() throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(
				"resources/x95Journey5.js"));
		for (String s = reader.readLine(); s != null; s = reader.readLine()) {
			sb.append(s);
		}
		reader.close();

		String[] segs = sb.toString().split("},");
		for (String seg : segs) {
			double accuracy=-1, lat=-1, lng=-1;
			String[] elements = seg.split(",");
			for (String element : elements) {
				int index = element.indexOf("accuracy");
				if (index > -1) {
					accuracy = Double.parseDouble(element.substring(index + 13,
							element.lastIndexOf("\"")));
					continue;
				}
				index = element.indexOf("latitude");
				if (index > -1) {
					lat = Double.parseDouble(element.substring(index+13, element.lastIndexOf("\"")));
					continue;
				}
				index = element.indexOf("longitude");
				if (index > -1) {
					lng = Double.parseDouble(element.substring(index+14, element.lastIndexOf("\"")));
					continue;
				}
			}
			if (accuracy!=-1){
				locations.add(new GpsLocation(accuracy,lng,lat));
			}
		}

	}

	public Collection<GpsLocation> getLocations() {
		return locations;
	}

}

class GpsLocation {
	double accuracy, lng, lat;

	public GpsLocation() {
		super();
	}

	public GpsLocation(double accuracy, double lng, double lat) {
		super();
		this.accuracy = accuracy;
		this.lng = lng;
		this.lat = lat;
	}

}
