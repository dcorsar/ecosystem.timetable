package org.parser;

import java.util.ArrayList;

public class Way {
	
	private String id ;
	private ArrayList nodes = new ArrayList ();
	private ArrayList tag= new ArrayList (); 
	
	
	
	public Way () {}
	
	
	public void  addNodes (String str) {
		nodes.add(str);
		
	}
	
	public void setID (String string ) {
		id= string;
	}
	
	public void addTag (String [] str) {
		tag.add(str);
	}

	public ArrayList  getNodes () {
		return nodes;
		
	}
	
	public String getID ( ) {
		return id;
	}
	
	public ArrayList  getTags () {
		return tag;
	}

}
