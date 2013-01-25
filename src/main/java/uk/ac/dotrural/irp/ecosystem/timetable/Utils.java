package uk.ac.dotrural.irp.ecosystem.timetable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import uk.ac.dotrural.irp.ecosystem.timetable.model.GeographicFeature;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmNode;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteNodeMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmRouteWayMap;
import uk.ac.dotrural.irp.ecosystem.timetable.model.OsmWay;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateRequest;

public class Utils {
	public static double calculateDistanceBetween(GeographicFeature from,
			GeographicFeature to) {
		return Utils.calculateDistanceBetween(from.getPoint(), to.getPoint());
	}

	public static double calculateDistanceBetween(Point from, Point to) {
		return Math.sqrt(Math.pow(from.getEasting() - to.getEasting(), 2)
				+ Math.pow(from.getNorthing() - to.getNorthing(), 2));
	}

	/*
	 * Determines the time in seconds from midnight
	 * 
	 * @param atcoTime
	 * 
	 * @return
	 */
	public static long parseAtcoTime(String atcoTime) {
		// first two characters are number of hours
		// second two are minutes passed the hour
		return Integer.parseInt(atcoTime.substring(0, 2)) * 3600
				+ Integer.parseInt(atcoTime.substring(2)) * 60;
	}
	
	/**
	 * Returns the time difference between atcoT1 and atcoT2 by subtracting T2 from T1 
	 * but also takes into account if T2 is on next dat
	 * @param atcoT1
	 * @param atcoT2
	 * @return
	 */
	public static long difference(long atcoT1, long atcoT2){
		long diff = atcoT1 - atcoT2;
		if (diff<0){
			 diff = ((atcoT1+86400)-atcoT2);
		}
		return diff;
	}

	/**
	 * 
	 * @param atcoTime
	 *            Time in seconds from midnight
	 * @return
	 */
	public static String atcoTimeToString(long atcoTime) {
		if (atcoTime == Long.MIN_VALUE) {
			return "";
		}
		String t = "";
		// add hours
		int hours = (int) (atcoTime / 3600);
		if (hours < 10) {
			t += "0";
		}
		t += Integer.toString(hours);
		t += ":";
		// add minutess
		int minutes = (int) ((atcoTime % 3600));
		if (minutes == 0) {
			t += "00";
		} else {
			int mins = minutes / 60;
			if (mins < 10) {
				t += "0";
			}
			t += Integer.toString(mins);
		}
		t += ":";
		// add seconds
		if (minutes == 00) {
			t += "00";
		} else {
			int seconds = minutes % 60;
			if (seconds < 10) {
				t += "0";
			}
			t += Integer.toString(seconds);
		}

		return t;
	}

	public static void update(String tdbMOdelLocatl, Collection<String> updates) {
//		Dataset dataset = TDBFactory.createDataset(tdbMOdelLocatl);
		Model model = TDBFactory.createModel(tdbMOdelLocatl);//dataset.getDefaultModel();
		UpdateRequest ur = new UpdateRequest();
		for (String s : updates) {
//			 System.out.println(s);
			ur.add(s);
		}
		Model m = ModelFactory.createOntologyModel();
		System.out.println("...");
		UpdateAction.execute(ur, m);
		System.out.println("...");
		model.add(m);
		model.close();
	}
	
	public static OsmRouteWayMap createOsmWayMapFor(OsmRouteNodeMap map){
		OsmRouteWayMap wayMap = new OsmRouteWayMap();
		wayMap.setDirection(map.getDirection());
		wayMap.setServiceUri(map.getServiceUri());
		wayMap.setEndNode(map.getEndNode());
		wayMap.setStartNode(map.getStartNode());
		OsmWay way = OsmWay.getWay(UUID.randomUUID().toString());
		for (OsmNode node : map.getNodes()){
			way.addNode(node);
		}
		wayMap.addWay(way);
		
		return wayMap;
	}


	public static long addToTime(long time, long mins) {
		long newTime = time + mins;
		if (newTime >= 86400){
			newTime -= 86400;
		}
		return newTime;
	}
}
