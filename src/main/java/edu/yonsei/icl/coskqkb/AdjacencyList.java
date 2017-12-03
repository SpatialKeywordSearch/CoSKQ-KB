package edu.yonsei.icl.coskqkb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.text.SimpleDateFormat;
  
public class AdjacencyList {
	//used to store adjacency list
    HashMap<String, HashMap<String,String> > graph;
    //used to store vertices and whether it is a place vertex
    HashMap<String, Boolean> vertexHash;
    static FileWriter fw;
    static BufferedWriter bw;
    static FileReader fr;
    static BufferedReader br;
    int numberOfEdges;
    
    //Constructor
    public AdjacencyList() {
    	this.graph = 
    			new HashMap<String, HashMap<String,String> >();
    	this.vertexHash = new HashMap<String, Boolean>();
    }
    
    //Returns the number of vertices
    public int getNumberOfStartVertices() {
        return this.graph.size();
    }
    
    public int getNumberOfDistinctVertices() {
    	this.createVertexHash();
    	return vertexHash.size();
    }
    
    public int getNumberOfEdges() {
    	numberOfEdges = 0;
    	
    	this.graph.forEach((k,v)->{
    		numberOfEdges += v.size();
   		});
    	
    	return numberOfEdges;
    }
    
    public int getNumberOfNonlabelEdges() {
    	numberOfEdges = 0;
    	
    	this.graph.forEach((k,v)->{
    		v.forEach((edge,endVertex)->{
    			if(!edge.equals("rdfs:label")) {
    				numberOfEdges++;
    			}
    		});
   		});
    	
    	return numberOfEdges;
    }
      
    //Returns the number of outward edges from a vertex
    public int getNumberOfEdgesFromVertex(String startVertex) {
    	return this.graph.get(startVertex).size();
    }
      
    //Returns a copy of the Linked List of outward edges from a vertex
    public HashMap<String,String> getEdgesFromVertex(String startVertex) {
        return this.graph.get(startVertex);
    }
    
    //Prints the adjacency list
    public void printAdjacencyList() {
    	this.graph.forEach((k,v)->{
            System.out.print("adjacencyList[" + k + "] -> ");
            
            v.forEach((edge,endVertex)->{
            	System.out.print(edge + "(" + 
            			endVertex + ")");
            });
            System.out.println();
        });
    }
    
    public void createVertexHash() {
		this.graph.forEach((k,v)->{
			if(!this.graph.containsKey(k)) {
				vertexHash.put(k, Boolean.FALSE);
			}
			
			v.forEach((edge,endVertex)->{
				//if start vertex has geo-coordinate
				//set it as place vertex
				if(edge.equals("<hasLatitude>")
					|| edge.equals("<hasLongitude>")) {
					vertexHash.put(k, Boolean.TRUE);
				}
				
				//if edge is not rdfs:label
				if(!edge.equals("rdfs:label")) {
					vertexHash.put(endVertex, Boolean.FALSE);
				}
			});
		});
	}

	//Add a triple to vertexArrayList and adjacencyList
    public void addTriple(String subject, String predicate, 
    		String object) {
    	//if the subject is a new one
    	if(!this.graph.containsKey(subject)) {
    		this.graph.put(subject,
    				new HashMap<String,String>());
    	}
    	
    	this.graph.get(subject).put(predicate, object);
    }
    
    //Add the adjacency list from a txt file
    public void addFromTxt(String filename) {
    	try {
    		fr = new FileReader(filename);
    		br = new BufferedReader(fr);
    		String currentLine;
    		String startVertex = ""; 
    		String edge, endVertex;
			
    		//Read file line by line
    		while((currentLine = br.readLine()) != null) {
    			//start vertex line
    			if(currentLine.startsWith("{")) {
    				startVertex = currentLine.substring(1, currentLine.length()-1);
    			}
    			//edge and endVertex line
    			else if (currentLine.startsWith("[")) {
    				edge = currentLine.substring(1, currentLine.indexOf("]"));
    				endVertex = currentLine.substring(
    						currentLine.indexOf("{")+1, currentLine.indexOf("}"));
    				
    				//add to adjacency array list
    				this.addTriple(startVertex, edge, endVertex);
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
    	
    	createVertexHash();
    }
    
    //Add the edge and end vertex for 
    //existing start vertices from a txt file
    public void addOnlyEdgeEndVertexFromTxt(String filename) {
    	try {
    		fr = new FileReader(filename);
    		br = new BufferedReader(fr);
    		String currentLine;
    		String startVertex = ""; 
    		String edge, endVertex;
			
    		//Read file line by line
    		while((currentLine = br.readLine()) != null) {
    			//start vertex line
    			if(currentLine.startsWith("{")) {
    				startVertex = currentLine.substring(1, 
    						currentLine.length()-1);
    			}
    			//edge and endVertex line
    			//only add when there already exists key "startVertex"
    			else if (this.vertexHash.containsKey(
    					startVertex)) {    				
    				edge = currentLine.substring(1, 
    						currentLine.indexOf("]"));
    				endVertex = currentLine.substring(
    						currentLine.indexOf("{")+1, 
    						currentLine.indexOf("}"));
    				
    				this.addTriple(startVertex, edge, endVertex);
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
    
    //Add the adjacency list from a ttl file
    public void addFromTurtle(String filename) {
    	ArrayList<String> document = new ArrayList<String>();
		String[] triple;
		String startVertex, edge, endVertex;
		
		Calendar cal = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-HH:mm:ss");
    	
    	try {
    		fr = new FileReader(filename);
    		br = new BufferedReader(fr);
    		
    		String currentLine;
    		
    		cal = Calendar.getInstance();
	        System.out.println("Start reading " + filename + 
	        		"..." + sdf.format(cal.getTime()));
	        
    		while((currentLine = br.readLine()) != null) {
    			//if the line starts with # or @, skip the line
    			if(currentLine.startsWith("#") 
    					|| currentLine.startsWith("@") 
    					|| currentLine.isEmpty()) {
    				continue;
    			}
    			
    			document.add(currentLine);
    		}
    		
    		cal = Calendar.getInstance();
	        System.out.println("End reading " + filename +
	        		"..." + sdf.format(cal.getTime()));
    		System.out.println("Number of lines: " + document.size() +
    				"," + filename);
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
    	
    	//add document to adjacency list
		for(int numberOfLine=0; numberOfLine<document.size();
				numberOfLine++) {
			if(numberOfLine%100000 == 0 && numberOfLine!=0) {
				cal = Calendar.getInstance();
				System.out.println(
						"Counting line for " + filename + ": " + numberOfLine
						+ "," + sdf.format(cal.getTime()));
			}
			
			triple = document.get(numberOfLine).split("\t");
			startVertex = triple[0];
			edge = triple[1];
			endVertex = triple[2];
			
			this.addTriple(startVertex, edge, endVertex);
		}
    }
    
    //Write the adjacency list into a txt file
	public void writeToTxt(String filename) {
		try {
			fw = new FileWriter(filename);
			bw = new BufferedWriter(fw);
			
			//Write subject with {}, predicate with [], object with ()
			this.graph.forEach((k,v)->{
				try {
					bw.write("{" + k + "}");
					bw.newLine();
	                
					v.forEach((edge,endVertex)->{
						try {
							bw.write("[" + edge + "]" + 
									"{" + endVertex + "}");
		                    bw.newLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	   		});
		} catch (Exception e) {
			// TODO: handle exception
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

	//Merge adjacency lists stored in txt files,
    //and write into one merged txt file
    public void mergeTxtFiles(String readFileName, 
    		String folderName, String writeFileName) {
    	Calendar cal = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-HH:mm:ss");
        
        //read the base file
        this.addFromTxt(readFileName);
        
        //create vertex hash based on the first file
        this.createVertexHash();
    	
    	//get file names in the folder
    	File folder = new File(folderName);
    	String[] filesInFolder = folder.list();
    	
    	for(int i=0; i<filesInFolder.length; i++) {
    		if(readFileName.equals(folderName + filesInFolder[i])) {
    			continue;
    		}
    		
    		cal = Calendar.getInstance();
    		System.out.println("Start processing..." + filesInFolder[i]
    				+ "..." + sdf.format(cal.getTime()));
    		
    		this.addOnlyEdgeEndVertexFromTxt(folderName + filesInFolder[i]);
    		
    		cal = Calendar.getInstance();
    		System.out.println("End processing..." + filesInFolder[i]
    				+ "..." + sdf.format(cal.getTime()));
    		/*System.out.println("Current number of start vertices is: "
    				+ this.getNumberOfStartVertices());
    		System.out.println("Current number of edges is: "
    				+ this.getNumberOfEdges());
    		System.out.println();*/
    	}
    	
    	//update the vertex hash
    	this.createVertexHash();
    	
    	this.writeToTxt(writeFileName);
    }
    
    public void deleteEdge (String[] deletePredicates) {
    	HashMap<String, Integer> removalStartVertex =
    			new HashMap<String, Integer>();
    	
    	this.graph.forEach((startVertex,edgeEndVertex)->{
    		edgeEndVertex.forEach((edge,endVertex)->{
    			for(int j=0; j<deletePredicates.length; j++) {
    				if(edge.equals(deletePredicates[j])) {
    					this.graph.get(startVertex).remove(
    							edge);
    					
    					if(this.graph.get(startVertex)==null) {
    						removalStartVertex.put(
    								startVertex, 1);
    					}
    					
    					break;
    				}
    			}
    		});
    	});
    	
    	//remove empty start vertex
    	removalStartVertex.forEach((startVertex,integer)->{
    		this.graph.remove(startVertex);
    	});
    }
}
