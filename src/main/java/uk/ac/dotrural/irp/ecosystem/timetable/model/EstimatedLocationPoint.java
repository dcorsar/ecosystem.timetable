package uk.ac.dotrural.irp.ecosystem.timetable.model;

import java.util.UUID;

public class EstimatedLocationPoint extends TimingPoint {

	private static int counter = 1;
	private String id;
	
	public EstimatedLocationPoint(Point point, long time){
		super(point, time, time);
		this.id =  ""+(EstimatedLocationPoint.counter++);//UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

        @Override public String toString() {
            return "point: [" + super.getPoint() + "]" +
                    "; time: " + super.getArrivalTime() +
                    "; id: " + id;
        }

}
