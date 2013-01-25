package org.xmlTordf;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ParseNodes  extends DefaultHandler{

	private String storePath =   "/Users/milan/NodesConversionTest" ;
	private Model model;
	private String id;
	private String lat;
	private String lon;
	private String nodesXMLfile ="/Users/milan/OSM/scotlandNodes.xml";
	private Dataset dataset;
	
	ParseNodes () {
		//create Jena model
		dataset = TDBFactory.createDataset(storePath) ;
	model = TDBFactory.createModel(storePath);
		
	}
	
	public void  runParsing () {
		parseDocument ();
		
		
		String sparqlQueryString = "SELECT (count(*) AS ?count) { ?s ?p ?o }" ;
        // See http://www.openjena.org/ARQ/app_api.html
        
        Query query = QueryFactory.create(sparqlQueryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset) ;
        ResultSet results = qexec.execSelect() ;
        
        ResultSetFormatter.out(results) ;
        qexec.close() ;
        
        /*FileOutputStream out = new FileOutputStream("/Users/milan/DatabaseTest/test.rdf");
        model.write(out);
        out.close();*/
       
        System.out.println("Completed");
        
        TDB.sync(model) ;
        model.close();
        dataset.close();
	}
	
	
	
	private void parseDocument() {
		
		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			// parse the file and also register this class for call backs
			sp.parse(nodesXMLfile, this);
		

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
			}
		}
	
	//converts the data about the node read from the xml into the rdf and stores in jena model
	private void putInModel() throws IOException {

		// System.out.println("ID: "+ way.getID() );
		 Resource node = model.createResource("http://localhost:3030/roads/point/"+id)
		 .addProperty( RDF.type, model.getResource ("http://www.dotrural.ac.uk/irp/ontologies/infrastructure#Point"))
		 .addLiteral(model.getProperty ("http://www.w3.org/2003/01/geo/wgs84_pos#lat"), lat)
		 .addLiteral(model.getProperty ("http://www.w3.org/2003/01/geo/wgs84_pos#long"), lon);
		}

	// Event Handlers
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// reset
		 if (qName.equalsIgnoreCase("NODE") ) {
			// create a new instance of employee
			 
				 
			id = attributes.getValue("id");
			lat = attributes.getValue("lat");
			lon = attributes.getValue("lon");
			try {
				putInModel();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

	}
	
	public static void main(String[] args) throws IOException {
		ParseNodes pn = new ParseNodes ();
		pn.runParsing();
	}
	
	}
