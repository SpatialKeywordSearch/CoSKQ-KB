package edu.yonsei.icl.coskqkb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class GeoCoordinates {
	HashMap<String, HashMap<String, String>> vertexCoordinatesHash;
	static FileWriter fw;
	static BufferedWriter bw;
	static FileReader fr;
	static BufferedReader br;
	
	public GeoCoordinates() {
		this.vertexCoordinatesHash = 
				new HashMap<String, HashMap<String, String>>();
	}
	
	public int getNumberOfPlaceVertices() {
		return this.vertexCoordinatesHash.size();
	}
	
	public void createGeoCoordinates(AdjacencyList adjacencyList) {
		adjacencyList.graph.forEach((k,v)->{
			String startVertex = k;
			
			//add to vertex coordinates hash
			HashMap<String, String> tempHash =
					new HashMap<String, String>();
			this.vertexCoordinatesHash.put(startVertex, tempHash);
			
			v.forEach((edge,endVertex)->{
				if (!this.vertexCoordinatesHash.get(
						startVertex).containsKey("<hasLongitude>")
					|| !this.vertexCoordinatesHash.get(
						startVertex).containsKey("<hasLatitude>")) {
					
					//for YAGO
					if(edge.equals("<hasLongitude>")
							|| edge.equals("<hasLatitude>")) {
						//process end vertex to get float value
						if(endVertex.startsWith("\"")) {
							endVertex = endVertex.substring(1);
							endVertex = endVertex.substring(
									0, endVertex.indexOf("\""));
						}
						
						//add one of the coordinates w.r.t. a vertex
						this.vertexCoordinatesHash.get(
								startVertex).put(edge, endVertex);
					}
					
					//for DBpedia
					if (edge.equals(
							"<http://www.georss.org/georss/point>")) {
						if(endVertex.startsWith("\"")) {
							endVertex = endVertex.substring(1);
							endVertex = endVertex.substring(
									0, endVertex.indexOf("\""));
						}
						
						String[] latLong = endVertex.split("\\s+");
						this.vertexCoordinatesHash.get(startVertex).put(
								"<hasLatitude>", latLong[0]);
						this.vertexCoordinatesHash.get(startVertex).put(
								"<hasLongitude>", latLong[1]);
					}
				}
			});
		});
	}
	
	public void writeToTxt(String filename) {
		try {
			fw = new FileWriter(filename);
			bw = new BufferedWriter(fw);
			
			this.vertexCoordinatesHash.forEach((k,v)->{
				try {
					bw.write("vertex:" + k);
					bw.newLine();
	                
	                String longitude = v.get("<hasLongitude>");
	                bw.write("longitude:" + longitude);
					bw.newLine();
	                
	                String latitude = v.get("<hasLatitude>");
	                bw.write("latitude:" + latitude);
					bw.newLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	   		});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void readFromTxt(String filename) {
		this.vertexCoordinatesHash = 
				new HashMap<String, HashMap<String, String>>();
    	try {
    		fr = new FileReader(filename);
    		br = new BufferedReader(fr);
    		String currentLine;
    		String vertex = ""; 
    		String longitude, latitude;
			
    		//Read file line by line
    		while((currentLine = br.readLine()) != null) {
    			//vertex line
    			if(currentLine.startsWith("vertex")) {
    				vertex = currentLine.substring(7);
    				
    				//add to vertex coordinates hash
    				this.vertexCoordinatesHash.put(vertex,
    						new HashMap<String, String>());
    			}
    			//longitude and latitude line
    			else if (currentLine.startsWith("longitude")) {
    				longitude = currentLine.substring(10);
    				
    				//add to adjacency array list
    				this.vertexCoordinatesHash.get(vertex).put(
    						"<hasLongitude>", longitude);
				}else if(currentLine.startsWith("latitude")) {
					latitude = currentLine.substring(9);
    				
    				//add to adjacency array list
					this.vertexCoordinatesHash.get(vertex).put(
    						"<hasLatitude>", latitude);
				}
    		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
}
