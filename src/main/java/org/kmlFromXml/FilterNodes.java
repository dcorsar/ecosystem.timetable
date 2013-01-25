package org.kmlFromXml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FilterNodes extends DefaultHandler {
	 ArrayList <String> list = new ArrayList ();
	 BufferedWriter bufferWritter;
	 ArrayList ways = new ArrayList ();
	 ArrayList nodeList = new ArrayList ();
	 ArrayList coordinates = new ArrayList ();
	 boolean endOfList = false;
	


	public  FilterNodes (String listOfwayIDs, String xmlWays,
			String xmlNodes, String startNode,String endNode, String kmlPath) throws IOException {
		
		
				
				
				// Gets the list of way Ids from the csv file
				CSVreader x=new CSVreader(listOfwayIDs);
				x.ReadFile();
				//x.displayArrayList();
				//generates a list of way Ids from the csv file
				ways = x.getArrayList();
		
				
				// takes the first way from the list and starts recording nodes from the starting node that is passed as an argument
			
				ArrayList nextWayList = new ArrayList();
				ArrayList currentWayList = new ArrayList();
				SaxParser parser;
				
				
				
				// pass the list of ways from csv file and xml file in the parser (use Scotland xml file first)
				
				parser = new SaxParser (xmlWays,ways);
				
				// get all the nodes for a particular way in a hash map
				HashMap <String,ArrayList> listWaysandNodes = parser.getHasMapWithWaysAndNodes();
				
				
				
				System.out.println ("try to find node "+listWaysandNodes.get("4785033"));
				
				
				// get all the nodes and their coordinates in a hash map 
				//System.out.println ("this is List " + parser.getNodesList());
				
				CoordinateParser parseNodes = new CoordinateParser (xmlNodes,parser.getNodesList());
				HashMap nodesAndCoordinates = parseNodes.getHashMapNodesAndCoordinates ();
				//System.out.println("This is list coordinates : " + nodesAndCoordinates);
				

			//	System.out.println ("HELLLO -- "+listWaysandNodes );
				
				int count =0;
				// find the path start with first way id from the list 
				for (int numb=0;numb<ways.size();numb++) {
					
					//this can be slightly improved if the lists are switching next way list to current way list in next loop - saves looking up in the hash map
					
					currentWayList = listWaysandNodes.get(ways.get(numb));
					
					
					//if cant find way by id assign empty list
					
					if (currentWayList==null) {
						currentWayList = new ArrayList();
					}
					
					int temp = numb+1;
				
					if (temp <ways.size()) {
					
					nextWayList = listWaysandNodes.get(ways.get(temp));
					
					
					}
					
					else {
						endOfList = true;
					}
					
					if (nextWayList==null || nextWayList.size()==0) {
						nextWayList = new ArrayList();
						System.out.println ("Way not found " + ways.get(temp));
						
						System.out.println ("Count" + count++);
						
					}
					
					ArrayList possibleWay1 = new ArrayList ();
					ArrayList possibleWay2 = new ArrayList ();
					boolean startNodeFound = false;
					
					if (!endOfList) {
						
					
					/*	for (int i = 0; i<currentWayList.size();i++) {
						
							nodeList.add(currentWayList.get(i));
							
							if (!nextWayList.isEmpty())
								startNode = (String) nextWayList.get(0);
								}
							}*/
						
						for (int i = 0; i<currentWayList.size();i++) {
							boolean dontAdd = false;
							if (currentWayList.get(i).equals(startNode)&&(!startNodeFound) ) {
								
								
							
								System.out.println ("found start node");
								startNodeFound = true;
								possibleWay1.add(currentWayList.get(i));
								possibleWay2.add(currentWayList.get(i));
								dontAdd =true;
							}
							
							
							
							if ((startNodeFound)&&(!dontAdd)) {
									possibleWay1.add(currentWayList.get(i));
									
							}
							
							
								
							if ((!startNodeFound)&&(!dontAdd)) {
								possibleWay2.add(currentWayList.get(i));
								
							}
								
						}
					
					
							startNode = findSame (currentWayList,nextWayList);
					
							/*int temp2 = temp+1;
							ArrayList nextNextWayList = new ArrayList ();
							if (temp2 <ways.size()) {
								nextNextWayList = listWaysandNodes.get(ways.get(temp2));
							}
							
							if (nextNextWayList==null)
								nextNextWayList = new ArrayList ();
							
							*/
							//String nodeOfnextTurn = findSame (currentWayList,nextNextWayList);
					
							if (possibleWay1.contains(startNode)) {
								for (int z=0;z<possibleWay1.size();z++) {
									
							//		if (!(nodeList.contains(possibleWay1.get(z))))
								nodeList.add(possibleWay1.get(z));
								
									
								if ((possibleWay1.get(z)).equals(startNode)) {
									//break if the next turn also part of this road
									break;
									
								}
								}
							}
							
							else {
								if (possibleWay2.contains(startNode)) {
									
									for (int y=possibleWay2.size()-1;y>0;y--) {
								//		if (!(nodeList.contains(possibleWay2.get(y))))
									nodeList.add(possibleWay2.get(y));
										if ((possibleWay2.get(y)).equals(startNode)) {
											//break if the next turn also part of this road
											break;
										}
												
									}
								}
								else {
									
									//if cant find connection start next element from the first node of the next way
									System.out.println("Satrting element from first node!!!!");
									
									System.out.println ("-------------------------------");
									System.out.println (currentWayList);
									System.out.println (startNode);
									System.out.println ("Array 1");
									System.out.println (possibleWay1);
									System.out.println ("Array 2");
									System.out.println (possibleWay2);
									System.out.println ("-------------------------------");

									if (!nextWayList.isEmpty())
									startNode = (String) nextWayList.get(0);
									}
							}
					
							if (ways.get(numb).equals("4785033")) {
								System.out.println ("----------TEST---------------------");
								System.out.println (currentWayList);
								System.out.println (nextWayList);
								System.out.println (startNode);
								
					//			System.out.println (nextNextWayList);
								System.out.println ("Array 1");
								System.out.println (possibleWay1);
								System.out.println ("Array 2");
								System.out.println (possibleWay2);
								System.out.println ("-------------------------------");
							}
							
					
					
					
					
					
				}
					
					
					else {
						//last way encountered
						
						if (possibleWay1.contains(startNode)) {
							for (int z=0;z<possibleWay1.size();z++)
								
								if (!(nodeList.contains(possibleWay1.get(z))))
							nodeList.add(possibleWay1.get(z));
						}
						
						else {
							if (possibleWay2.contains(startNode)) {
								
								for (int y=0;y<possibleWay2.size();y++)
									if (!(nodeList.contains(possibleWay2.get(y))))
								nodeList.add(possibleWay2.get(y));
							}
							else {
								
								//if cant find connection start next element from the first node of the next way
								System.out.println("problem finding connecting node!!!!");
								
								System.out.println ("-------------------------------");
								System.out.println (startNode);
								System.out.println ("Array 1");
								System.out.println (possibleWay1);
								System.out.println ("Array 2");
								System.out.println (possibleWay2);
								System.out.println ("-------------------------------");

								if (!nextWayList.isEmpty())
								startNode = (String) nextWayList.get(0);
								}
						}
						
						
						
					}
					
					
					
				}
				
				
				
				/*
				
				for (int numb=0;numb<ways.size();numb++) {
				
				
					
				if (nextWayList.size() > 0 ) {
				 parser = new SaxParser (scotlandWays,(String) ways.get(numb));
				//get list of nodes of current road
				 currentWayList = parser.getList();
				}
				
				else {
					currentWayList = nextWayList;
				}
				
				
				
				
				
				if (numb < ways.size()+2) {
					parser = new SaxParser (scotlandWays,(String) ways.get(numb+1));
					//get list of nodes of next road
					nextWayList = parser.getList();
					
					//break if can't find way by an ID Note it needs to jump in second file... 
					
					if (nextWayList.size() == 0 ) {
						System.out.println ("Can't find way by Id");
					//	break;
					}
				}
				
				
				ArrayList possibleWay1 = new ArrayList ();
				ArrayList possibleWay2 = new ArrayList ();
				boolean startNodeFound = false;
				
				
				
				for (int i = 0; i<currentWayList.size();i++) {
					
					if (currentWayList.get(i).equals(startNode) ) {
					
						System.out.println ("found start node");
						startNodeFound = true;
						possibleWay1.add(currentWayList.get(i));
						possibleWay2.add(currentWayList.get(i));
					}
					
					else {
					
					if (startNodeFound) {
							possibleWay1.add(currentWayList.get(i));
					}
					
					else { 
						
					if (!startNodeFound)
						possibleWay2.add(currentWayList.get(i));
					}
					}
				
				
				
				}
				
				System.out.println ("list found");
				System.out.println (possibleWay1);
				System.out.println ("list found");
				System.out.println (possibleWay2);
				
				
				//find out on which node they connect
				startNode = findSame (currentWayList,nextWayList);
				
				if (possibleWay1.contains(startNode)) {
					for (int z=0;z<possibleWay1.size();z++)
					nodeList.add(possibleWay1.get(z));
				}
				
				else {
					if (possibleWay2.contains(startNode)) {
						
						for (int y=0;y<possibleWay2.size();y++)
						nodeList.add(possibleWay2.get(y));
					}
					else {
						
						//if cant find connection start next element from the first node
						System.out.println("problem finding connecting node!!!!");

						//startNode = (String) nextWayList.get(0);
						
												
					}
				}
				
				
				} */
				
				
				System.out.println ("Node List ----------------------");
			//	System.out.println (nodeList);
				
			
				
				for (int i=0; i < nodeList.size() ; i++) {
					System.out.println(nodeList.get(i));
				}
				
				
				for (int i=0; i < nodeList.size() ; i++) {
				//	if (!(coordinates.contains(nodesAndCoordinates.get(nodeList.get(i))))) {
					coordinates.add(nodesAndCoordinates.get(nodeList.get(i)));
					//}
				//	else {
					//	System.out.println("HAAAAAAAAAAAAAAAAA");
				//	}
				}
				
				
				
				//nodeList.add("961716");
				//nodeList.add("283038461");
				//nodeList.add("637628");
				
				
				
				
				
				/*
				for (int n=0; n<nodeList.size();n++) {
				CoordinateParser coordinateParser = new CoordinateParser (scotlandNodes,(String) nodeList.get(n));
				//get list of nodes of current road
				
				
				
				coordinates.add(coordinateParser.getList().get(0));
				
				}*/
				
				System.out.println ("Coordinates ----------------------");
				System.out.println (coordinates.size());
				
				//System.out.println (coordinates.get(0));
				//CoordinateParser coordinateParser = new CoordinateParser (scotlandNodes,(String) nodeList.get(n));
				
				
				
				KmlWriter writer = new KmlWriter (coordinates,kmlPath);
	}
	
	
	
	private String findSame (ArrayList list1, ArrayList list2) {
		String result = "hey";
		
		for (int i = 0; i<list1.size();i++) {
			
			for (int j = 0; j<list2.size();j++) {
				if (list1.get(i).equals(list2.get(j))) {
					return (String) list1.get(i);
				}
			}
			
			}
		
		if (result.equals("hey")) {
			
		}
		return result;
	}

	public void runExample() throws IOException {
		
		

	}
	
	
	

	
	


	public ArrayList<String> getNodesList() {
		// TODO Auto-generated method stub
		return list;
	}
}
