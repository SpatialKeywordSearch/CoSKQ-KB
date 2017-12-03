package edu.yonsei.icl.coskqkb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class InvertedIndex {
	HashMap<String, HashMap<String,Integer>> termVertexHash;
	static FileWriter fw;
	static BufferedWriter bw;
	static FileReader fr;
	static BufferedReader br;
	
	public InvertedIndex() {
		this.termVertexHash = 
				new HashMap<String, HashMap<String,Integer>>();
	}
	
	public int getNumberOfTerms() {
		return this.termVertexHash.size();
	}
	
	public void createFromGraph(AdjacencyList adjacencyList) {
		adjacencyList.graph.forEach((startVertex,v)->{
			v.forEach((edge,endVertex)->{
				if(edge.equals("rdfs:label")) {
					
					//preprocessing end vertex
					if (endVertex.startsWith("\"")) {
						endVertex = endVertex.substring(1);
						
						if(endVertex.contains("\"")) {
							int indexOfDoubleQuotes = 
									endVertex.indexOf("\"");
							endVertex = endVertex.substring(
									0, indexOfDoubleQuotes);
						}
					}
					
					String[] termArray = endVertex.split("\\s+");
					
					for(int j=0; j<termArray.length; j++) {
						String term = termArray[j];
						
						
						if (term.equals("")) {
							continue;
						}
						//if the term is a new one
						else if(!this.termVertexHash.containsKey(
								term)) {
							HashMap<String,Integer> tempHash =
									new HashMap<String,Integer>();
							tempHash.put(startVertex, 0);
							this.termVertexHash.put(term, tempHash);
						} else {
							if(!this.termVertexHash.get(
									term).containsKey(
									startVertex)) {
								this.termVertexHash.get(term).put(
										startVertex,1);
							}
						}
					}
				}
			});	
		});
	}
	
	public void writeToTxt(String filename) {
		try {
			fw = new FileWriter(filename);
			bw = new BufferedWriter(fw);
			
			//Write term with {}, start vertex with []
			this.termVertexHash.forEach((k,v)->{
				try {
					bw.write("{" + k + "}");
					bw.newLine();
					
					v.forEach((vertex, integer)->{
						if(!vertex.equals("")) {
							try {
								bw.write("[" + vertex + "]");
				                bw.newLine();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
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
		//initialize term vertex hash
		this.termVertexHash = 
				new HashMap<String, HashMap<String,Integer>>();
		
    	try {
    		fr = new FileReader(filename);
    		br = new BufferedReader(fr);
    		String currentLine;
    		String term = ""; 
    		String vertex;
			
    		//Read file line by line
    		while((currentLine = br.readLine()) != null) {
    			//term line
    			if(currentLine.startsWith("{")) {
    				term = currentLine.substring(1, 
    						currentLine.length()-1);
    				
    				this.termVertexHash.put(term,
    						new HashMap<String,Integer>());
    			}
    			//vertex line
    			else if (currentLine.startsWith("[")) {
    				vertex = currentLine.substring(1, 
    						currentLine.length()-1);
    				
    				//add to vertex linked list w.r.t. term
    				this.termVertexHash.get(term).put(vertex,0);
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
	
	//create vertex inverted index from 
	//vertex or sub-tree keyword txt file
	public void createFromTxt(String filename) {
		//initialize term vertex hash
		this.termVertexHash = 
				new HashMap<String, HashMap<String,Integer>>();
		
    	try {
    		fr = new FileReader(filename);
    		br = new BufferedReader(fr);
    		String currentLine;
    		String term = ""; 
    		String vertex = "";
			
    		//Read file line by line
    		while((currentLine = br.readLine()) != null) {
    			//term line
    			if(currentLine.startsWith("{")) {
    				vertex = currentLine.substring(1, 
    						currentLine.length()-1);
    			}
    			//vertex line
    			else if (currentLine.startsWith("[")) {
    				term = currentLine.substring(1, 
    						currentLine.length()-1);
    				
    				//add term
    				if(!this.termVertexHash.containsKey(term)
    						&& term!=null) {
    					this.termVertexHash.put(term, 
    							new HashMap<String,Integer>());
    				}
    				
    				//add to vertex linked list w.r.t. term
    				this.termVertexHash.get(term).put(vertex,0);
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
