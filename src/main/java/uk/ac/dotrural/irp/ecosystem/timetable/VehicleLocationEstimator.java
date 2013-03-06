package uk.ac.dotrural.irp.ecosystem.timetable;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dotrural.irp.ecosystem.timetable.model.EstimatedLocationPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Segment;
import uk.ac.dotrural.irp.ecosystem.timetable.model.TimingPoint;
import uk.ac.dotrural.irp.ecosystem.timetable.model.cif.StopTimingPoint;

public class VehicleLocationEstimator {

        /**
         *
         * @param
         * @return
         */
        public EstimatedLocationPoint estimateLocationAtTime(Point observedPoint, Segment observedSegment, List<Segment> journeySegments, double observedSpeed, long observedTime, long targetTime) {
            final long time = targetTime - observedTime;
            final double distanceTravelled = observedSpeed * (time/1000);

            if (distanceTravelled == 0)
                return new EstimatedLocationPoint(observedPoint, targetTime);

            System.out.println("\t distanceTravelled: " + distanceTravelled);
            System.out.println("\t observedSpeed:     " + observedSpeed);

            List<Segment> remainingSegments = journeySegments.subList(journeySegments.indexOf(observedSegment), journeySegments.size());
            Point currentPoint = observedPoint;
            double remainingDistance = distanceTravelled;
            for (Segment currentSegment : remainingSegments) {
                double remainingThisSegment = Utils.calculateDistanceBetween(currentPoint, currentSegment.getTo().getPoint());

                System.out.println("\t\t remainingDistance:    " + remainingDistance);
                System.out.println("\t\t remainingThisSegment: " + remainingThisSegment);
                System.out.println();
                
                if (remainingThisSegment > remainingDistance) {
                    Point p = calculatePointFromAlongSegment(remainingDistance, currentPoint, currentSegment);
                    return new EstimatedLocationPoint(p, targetTime);
                } else {
                    remainingDistance = remainingDistance - remainingThisSegment;
                    currentPoint = currentSegment.getTo().getPoint();
                }
            }

            throw new IllegalStateException("Bus has past end of route");
        }
        
	/**
	 * Estimates
	 * 
	 * @param from
	 * @param to
	 * @param segments
	 *            
	 * @param frequency
	 *            The frequency (in seconds) that locations should be estimated
	 *            for
	 * @return
	 */
	public List<EstimatedLocationPoint> estimateLocationsBetween(
			TimingPoint from, TimingPoint to, List<Segment> segments,
			long frequency) {
//		if (from.getPoint() != segments.get(0).getFrom().getPoint()) {
//			throw new IllegalArgumentException(
//					"First point in segments must equal from");
//		}
//		if (to != segments.get(segments.size() - 1).getTo()) {
//			throw new IllegalArgumentException(
//					"Last point in segments must equal to");
//		}
		if (frequency <= 0) {
			throw new IllegalArgumentException(
					"Frequency must be greater than 0");
		}

		// total time between stops
		long t = to.getArrivalTime() - from.getDepartureTime();
		// average speed
		double v = calculateTotalLength(segments) / t;
		// distance travelled between estimated locations
		double d = v * frequency;
		// number of locations to be estimated
		int numEstLocs = (int) (t / frequency);
		// may end up estimating for the final point, so check and decrement
		// numEstLocs if necessary
		if ((from.getDepartureTime() + (numEstLocs * frequency)) == to
				.getArrivalTime()) {
			numEstLocs--;
		}
		
		System.out.println(((StopTimingPoint)from).getStop().getAtcoCode());
		List<EstimatedLocationPoint> estimates = new ArrayList<EstimatedLocationPoint>();
		if (numEstLocs>0 && v==0){
			System.out.println("moving nowhere for " + numEstLocs);
			for (int i =1;i<=numEstLocs;i++){
				estimates.add(new EstimatedLocationPoint(from.getPoint(), from.getDepartureTime()+(frequency*i)));
			}
			return estimates;
		}

		// start estimating locations
		Point p = segments.get(0).getFrom().getPoint(); //from.getPoint();
		int segIndex = 0;
		for (int i = 0; i < numEstLocs; i++) {
			double toGo = d;
			double remainingThisSegment = Utils.calculateDistanceBetween(p,
					segments.get(segIndex).getTo().getPoint());
			while (remainingThisSegment < toGo) {
				toGo -= remainingThisSegment;
				p = segments.get(segIndex++).getTo().getPoint();
				remainingThisSegment = Utils.calculateDistanceBetween(p,
						segments.get(segIndex).getTo().getPoint());
			}
			long estimateTime = from.getDepartureTime() + (frequency * (i + 1));
			if (toGo == 0) {
				estimates.add(new EstimatedLocationPoint(segments
						.get(segIndex - 1).getTo().getPoint(), estimateTime));
			} else if (remainingThisSegment == toGo) {
				estimates.add(new EstimatedLocationPoint(segments.get(segIndex)
						.getTo().getPoint(), estimateTime));
				p = segments.get(segIndex++).getTo().getPoint();
			} else {
				p = calculatePointFromAlongSegment(toGo, p,
						segments.get(segIndex));
				estimates.add(new EstimatedLocationPoint(p, estimateTime));
			}
		}
		return estimates;
	}

	private Point calculatePointFromAlongSegment(double distance,
			Point from, Segment along) {
		double segLength = Utils.calculateDistanceBetween(along.getFrom(), along.getTo());
		if (distance > segLength) {
			throw new IllegalArgumentException("Cannot go " + distance
					+ " along a segment that is only " + segLength + " long");
		}
		double ratio = distance / segLength;
		double easting = (along.getTo().getPoint().getEasting() == along
				.getFrom().getPoint().getEasting()) ? along.getTo().getPoint()
				.getEasting() : from.getEasting()
				+ (ratio * (along.getTo().getPoint().getEasting() - along
						.getFrom().getPoint().getEasting()));
		double northing = (along.getTo().getPoint().getNorthing() == along
				.getFrom().getPoint().getNorthing()) ? along.getTo().getPoint()
				.getNorthing() : from.getNorthing()
				+ (ratio * (along.getTo().getPoint().getNorthing() - along
						.getFrom().getPoint().getNorthing()));

		return new Point(easting, northing);
	}

	private double calculateTotalLength(List<Segment> segments) {
		double length = 0;
		for (Segment segment : segments) {
			length += Utils.calculateDistanceBetween(segment.getFrom().getPoint(), segment
					.getTo().getPoint());
		}
		return length;
	}



}
