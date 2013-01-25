package uk.ac.dotrural.irp.ecosystem.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OsmWay {

	private String id;
	private List<OsmNode> nodes;
	private List<Segment> segments;
	private boolean nodesChanged;
	private Map<String, String> tags;

	private static Map<String, OsmWay> ways = new HashMap<String, OsmWay>();
	
	public static OsmWay getWay(String id){
		OsmWay way = ways.get(id);
		if(way == null){
			way = new OsmWay(id);
			ways.put(id, way);
		}
		return way;
		
	}
	
	protected OsmWay(String id) {
		super();
		setId(id);
		this.tags = new HashMap<String, String>();
		this.nodes = new ArrayList<OsmNode>();
		nodesChanged = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OsmNode getNode(int index){
		return this.nodes.get(index);
	}
	
	public List<OsmNode> getNodes() {
		return nodes;
	}

	public void addNode(OsmNode node) {
		this.addNode(node, nodes.size());
	
	}

	public int getNumberOfNodes() {
		return this.nodes.size();
	}

	public void addNode(OsmNode node, int index) {
		if (index > nodes.size()) {
			throw new IllegalArgumentException(
					"trying to add segment at index " + index
							+ " but maximum is " + nodes.size());
		}
		if (index < 0) {
			throw new IllegalArgumentException(
					"Cannot add segment with negative index");
		}
		if (index == nodes.size()) {
			this.nodes.add(node);
		} else {
			nodes.add(index, node);
		}
		this.nodesChanged = true;
	}

	public List<Segment> getSegments(){
		if (this.segments == null){
			this.segments = new ArrayList<Segment>();
			this.nodesChanged = true;
		}
		if (nodesChanged){
			this.segments.clear();
			for (int i=0,j=this.nodes.size()-1;i<j;i++){
				this.segments.add(new Segment(this.nodes.get(i), this.nodes.get(i+1)));
			}
		}
		this.nodesChanged = false;
		return this.segments;
	}
	
	public void addTag(String key, String value) {
		this.tags.put(key, value);
	}

	public Map<String, String> getTags() {
		return this.tags;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
		OsmWay other = (OsmWay) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}

	public void setNodes(List<OsmNode> newNodes) {
		this.nodes.clear();
		this.nodes.addAll(newNodes);
		nodesChanged = true;
	}

	public int getNodeIndex(OsmNode node) {
		return nodes.indexOf(node);
	}

	@Override
	public String toString() {
		return "OsmWay [id=" + id + "]";
	}

	public static void clearWays() {
		OsmWay.ways= new HashMap<String, OsmWay>();
	}

	public static Collection<OsmWay> getWays() {
		Collection<OsmWay> ways = new ArrayList<OsmWay>(OsmWay.ways.size());
		for (OsmWay w : OsmWay.ways.values()){
			ways.add(w);
		}
		return ways;
	}
	
	
	
}
