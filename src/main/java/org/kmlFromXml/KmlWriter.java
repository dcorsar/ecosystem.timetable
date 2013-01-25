package org.kmlFromXml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class KmlWriter {

	BufferedWriter bufferWritter;
	
public 	KmlWriter (ArrayList coordinates, String filename) throws IOException {
       	
	FileWriter fileWritter = new FileWriter(filename);
	
    bufferWritter = new BufferedWriter(fileWritter);
    String data= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    bufferWritter.write(new String(data.getBytes(),"UTF-8"));
    
  data= "<kml xmlns=\"http://www.opengis.net/kml/2.2\">";
    bufferWritter.write(new String(data.getBytes(),"UTF-8"));
    
    data="<Document>\n";
   	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
   	 data="<name>Paths</name>\n";
   	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
   	data="<description>X95 route.</description>\n";
  	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
 	data= "<Style id=\"yellowLineGreenPoly\">\n";
 	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
 	data="<LineStyle>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data="<color>7f0000ff</color>\n";
   	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
   	 data="<width>4</width>\n";
   	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
   	 data=" </LineStyle>\n";
   	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
   	 data="  <PolyStyle>\n";
   	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
   	data="<color>7f0000ff</color>\n";
  	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
  	data="</PolyStyle>\n";
 	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
 	data="</Style>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data="<Placemark>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data="<name>Absolute Extruded</name>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data="<description>X95 route</description>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data="<styleUrl>#yellowLineGreenPoly</styleUrl>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data=" <LineString>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data=" <extrude>1</extrude>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data=" <tessellate>1</tessellate>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data=" <altitudeMode>absolute</altitudeMode>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data="  <coordinates>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
  	 
  	 // code here
	 for (int i=0;i<coordinates.size();i++) {
		 String [] values;
		 values = (String[]) coordinates.get(i);
		 
		 data=values[1]+","+values[0]+"\n";
		 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 }
	 
	 data="</coordinates>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data="</LineString>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data="</Placemark>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data="</Document>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 data="</kml>\n";
	 bufferWritter.write(new String(data.getBytes(),"UTF-8"));
	 
   	 
	
//	printData();
	 
	 bufferWritter.close();
	 
	 
	 
	 
	
	
}
	
	
	
	
	
	
}
