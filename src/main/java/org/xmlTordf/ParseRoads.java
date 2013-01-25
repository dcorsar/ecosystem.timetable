package org.xmlTordf;

import java.io.IOException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.parser.Way;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ParseRoads extends DefaultHandler{

	private String storePath =   "/Users/milan/RoadsConversionTest" ;
	private Model model;
	private String id;
	private String nodeID;
	private String roadsXMLfile ="/Users/milan/OSM/scotlandRoads.xml";
//	private Dataset dataset;
	private Way tempWay;
	private boolean isRoad = false;
	List ways;
	int count =0;
	int roadId = 0;
	
	private String tempVal;
	
	ParseRoads () {
		//create Jena model
//		dataset = TDBFactory.createDataset(storePath) ;
	model = TDBFactory.createModel(storePath);
		
	}
	
	public void  runParsing () {
		parseDocument ();
		
		
		countTriples();
        
        /*FileOutputStream out = new FileOutputStream("/Users/milan/DatabaseTest/test.rdf");
        model.write(out);
        out.close();*/
       
        System.out.println("Completed");
        
        TDB.sync(model) ;
        model.close();
//        dataset.close();
	}

	private void countTriples() {
		String sparqlQueryString = "SELECT (count(*) AS ?count) { ?s ?p ?o }" ;
        // See http://www.openjena.org/ARQ/app_api.html
        
        Query query = QueryFactory.create(sparqlQueryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        ResultSet results = qexec.execSelect() ;
        
        ResultSetFormatter.out(results) ;
        qexec.close() ;
	}
	
	
	
	private void parseDocument() {
		
		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			// parse the file and also register this class for call backs
			sp.parse(roadsXMLfile, this);
		

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
			}
		}
	
	//converts the data about the node read from the xml into the rdf and stores in jena model
	private void putInModel(Way way) throws IOException {
		
		ArrayList listOfNodes = way.getNodes();
		ArrayList listofTags = way.getTags();
		id = way.getID();
		
		String name = "";
		
		for (int i = 0 ; i < listofTags.size();i++) {
			if (((String) ((String [])listofTags.get(i))[0]).equalsIgnoreCase("NAME")||((String) ((String [])listofTags.get(i))[0]).equalsIgnoreCase("REF")) {
				name = (String) ((String [])listofTags.get(i))[1];
			}
				
		}

		// System.out.println("ID: "+ way.getID() );
		 Resource node = model.createResource("http://localhost:3030/roads/road/"+id)
		 .addProperty( RDF.type, model.getResource ("http://transport.data.gov.uk/def/traffic/Road"))
		 .addProperty( RDFS.label, name);
		 
		 int pointNumber = 0;
		 
		 for (int i = 0 ; i < listOfNodes.size();i++) {
			 roadId++;
			 pointNumber++;
			
				
		model.getResource ("http://localhost:3030/roads/road/"+id)
		 .addProperty( model.getProperty ("http://www.dotrural.ac.uk/irp/ontologies/infrastructure#hasRoadPoint"), model.createResource ("http://localhost:3030/roads/roadPoint/"+ roadId)
		 .addProperty(RDF.type, model.getResource("http://www.dotrural.ac.uk/irp/ontologies/infrastructure#RoadPoint"))
		 .addProperty(model.getProperty ("http://www.dotrural.ac.uk/irp/ontologies/infrastructure#hasPoint"), model.getResource ("http://localhost:3030/roads/point/"+(String)listOfNodes.get(i))));
		
		 
		model.getResource ("http://localhost:3030/roads/roadPoint/"+roadId)
		 .addLiteral(model.getProperty ("http://www.dotrural.ac.uk/irp/ontologies/infrastructure#pointNumber"),pointNumber);
		 
			 
			 
		 }
		 
		}

	// Event Handlers
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// reset
		tempVal = "";
		if(qName.equalsIgnoreCase("WAY")) {
			//create a new instance of employee
			tempWay = new Way();
			tempWay.setID(attributes.getValue("id"));
		}
		else if(qName.equalsIgnoreCase("TAG")&&(tempWay!=null)) {
			//create a new instance of employee
			if ((!attributes.getValue("k").equals("note"))&&(!attributes.getValue("k").equals("width"))&&(!attributes.getValue("k").equals("incline"))) {
			String [] touple = new String [2];
			touple [0] = attributes.getValue("k");
			touple [1] = attributes.getValue("v");
			
			if(touple [0]==null) {
				touple [0]="";
			}
			
			if(touple [1]==null) {
				touple [1]="";
			}
			tempWay.addTag(touple);
			}
		}
		else if(qName.equalsIgnoreCase("ND")&&(tempWay!=null)) {
			
			tempWay.addNodes(attributes.getValue("ref"));
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tempVal = new String(ch,start,length);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equalsIgnoreCase("WAY")) {
			//add it to the list
			ArrayList tempList = tempWay.getTags();
			
				//ways.add(tempWay);
				try {
					putInModel(tempWay);
					count++;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			
		}
	}
	
	public static void main(String[] args) throws IOException {
		ParseRoads pn = new ParseRoads ();
		pn.runParsing();
	//	pn.countTriples();
	}
	

}
