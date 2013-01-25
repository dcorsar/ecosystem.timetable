package uk.ac.dotrural.irp.ecosystem.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

public class OsmNode extends Point {
	private double lon = Double.NaN, lat = Double.NaN;
	private String id;
	private Collection<OsmWay> ways;
	
	private LatLng latlng;
	private OSRef osref;

	private static Map<String, OsmNode> nodes = new HashMap<String, OsmNode>();
	
	public static OsmNode getNode(String id){
		OsmNode node = nodes.get(id);
		if(node == null){
			node = new OsmNode(id);
			nodes.put(id, node);
		}
		return node;
		
	}
	
	protected OsmNode(String id) {
		super();
		setId(id);
		this.ways = new ArrayList<OsmWay>();
	}
	
	public void addOsmWay(OsmWay way){
		this.ways.add(way);
	}
	
	public boolean removeOsmWay(OsmWay way){
		return this.ways.remove(way);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private void updateLocation() {
		if (lat != Double.NaN && lon != Double.NaN) {
			latlng = new LatLng(lat, lon);
			osref = latlng.toOSRef();
			setEasting(osref.getEasting());
			setNorthing(osref.getNorthing());
		}
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
		updateLocation();
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
		updateLocation();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((latlng == null) ? 0 : latlng.hashCode());
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((osref == null) ? 0 : osref.hashCode());
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
		OsmNode other = (OsmNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
//		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
//			return false;
//		if (latlng == null) {
//			if (other.latlng != null)
//				return false;
//		} else if (!latlng.equals(other.latlng))
//			return false;
//		if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
//			return false;
//		if (osref == null) {
//			if (other.osref != null)
//				return false;
//		} else if (!osref.equals(other.osref))
//			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OsmNode [id=" + id + "]";
	}

	public static void clearNodes() {
		OsmNode.nodes = new HashMap<String, OsmNode>();
	}

	public static Collection<OsmNode> getNodes() {
		Collection<OsmNode> nodes = new ArrayList<OsmNode>(OsmNode.nodes.size());
		for (OsmNode n : OsmNode.nodes.values()){
			nodes.add(n);
		}
		return nodes;
	}
	
	
	
	
}
