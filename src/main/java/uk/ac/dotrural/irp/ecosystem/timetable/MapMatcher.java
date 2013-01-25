package uk.ac.dotrural.irp.ecosystem.timetable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmWay;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Segment;

/**
 * Implements a basic map matching algorithm
 * 
 * @author david corsar
 * 
 */
public class MapMatcher {

	public SegmentDistance mapWays(Point p, Collection<OsmWay> candidateWays) {
		Collection<Segment> segments = new ArrayList<Segment>();
		for (OsmWay way : candidateWays) {
			segments.addAll(way.getSegments());
		}
		return mapSegments(p, segments);
	}

	public SegmentDistance mapSegments(Point p,
			Collection<Segment> candidateSegments) {
		if (candidateSegments.size() == 0) {
			return null;
		}

		List<SegmentDistance> list = new ArrayList<SegmentDistance>();
		for (Segment segment : candidateSegments) {
			SegmentDistance ond = new SegmentDistance();
			ond.segment = segment;
			Point projection = mapPointToLine(segment.getFrom().getPoint(),
					segment.getTo().getPoint(), p);
			ond.distance = Utils.calculateDistanceBetween(p, projection);
			if (isPointPossiblyOnLine(segment.getFrom().getPoint(), segment
					.getTo().getPoint(), projection)) {
				ond.mappedPoint = projection;
			} else {
				double fromD = Utils.calculateDistanceBetween(projection,
						segment.getFrom().getPoint());
				double toD = Utils.calculateDistanceBetween(projection, segment
						.getTo().getPoint());
				if (fromD < toD) {
					ond.distance += fromD;
					ond.mappedPoint = segment.getFrom().getPoint();
				} else {
					ond.distance += toD;
					ond.mappedPoint = segment.getTo().getPoint();
				}
			}
			list.add(ond);
		}

		SegmentDistance closest = list.get(0);
		for (SegmentDistance sd : list) {
			if (sd.distance < closest.distance) {
				closest = sd;
			}
		}
		// }
		// Collections.sort(list);
		return closest;
		// return list.subList(0, number);
	}

	private Point mapPointToLine(Point lineStart, Point lineEnd, Point point) {
		double E1 = lineStart.getEasting(), N1 = lineStart.getNorthing(), E2 = lineEnd
				.getEasting(), N2 = lineEnd.getNorthing(), Eh = point
				.getEasting(), Nk = point.getNorthing();
		double x = ((E2 - E1) * ((Eh * (E2 - E1)) + (Nk * (N2 - N1))) + (N2 - N1)
				* ((E1 * N2) - (E2 * N1)))
				/ (Math.pow((E2 - E1), 2) + Math.pow((N2 - N1), 2));
		double y = ((N2 - N1) * ((Eh * (E2 - E1) + Nk * (N2 - N1))) - (E2 - E1)
				* ((E1 * N2) - (E2 * N1)))
				/ (Math.pow((E2 - E1), 2) + Math.pow((N2 - N1), 2));
		return new Point(x, y);
	}

	private boolean isPointPossiblyOnLine(Point lineStart, Point lineEnd,
			Point point) {
		if ((lineStart.getEasting() == 339121.6368975959 && lineStart
				.getNorthing() == 566096.0221172781)
				|| (lineEnd.getEasting() == 339121.6368975959 && lineEnd
						.getNorthing() == 566096.0221172781)) {
			int i = 0;
		}
		boolean easting = true, northing = true; // lets be optimists
		if (lineStart.getEasting() > lineEnd.getEasting()) {
			easting = lineStart.getEasting() >= point.getEasting()
					&& point.getEasting() >= lineEnd.getEasting();
		} else {
			easting = lineStart.getEasting() <= point.getEasting()
					&& point.getEasting() <= lineEnd.getEasting();
		}
		if (lineStart.getNorthing() > lineEnd.getNorthing()) {
			northing = lineStart.getNorthing() >= point.getNorthing()
					&& point.getNorthing() >= lineEnd.getNorthing();
		} else {
			northing = lineStart.getNorthing() <= point.getNorthing()
					&& point.getNorthing() <= lineEnd.getNorthing();
		}
		return (easting && northing);
	}
}
