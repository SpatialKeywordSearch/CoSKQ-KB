package edu.yonsei.icl.coskqkb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class SubTree {
	HashMap<String, HashMap<String,Integer>> vertexKeywordHash;
	HashMap<String, HashMap<String,Integer>> rootKeywordHash;
	HashMap<String, HashMap<String,Integer>> subTreeHash;
	static FileWriter fw;
	static BufferedWriter bw;
	static FileReader fr;
	static BufferedReader br;
	static int countingNumber;
	static int depth;
	static double totalPostingListLength;
	HashMap<String, Integer> fatherVertexList;
	HashMap<String, Integer> sonVertexList;

	public SubTree() {
		this.vertexKeywordHash = new HashMap<String, 
				HashMap<String, Integer>>();
		this.rootKeywordHash = new HashMap<String, 
				HashMap<String, Integer>>();
		this.subTreeHash = new HashMap<String, 
				HashMap<String, Integer>>();
	}
	
	public double getAveragePostingListLength() {
		totalPostingListLength = 0;
		
		rootKeywordHash.forEach((root,keywordHash)->{
			totalPostingListLength += keywordHash.size();
		});
		
		return totalPostingListLength/rootKeywordHash.size();
	}
	
	public HashMap<String, Integer> getSonVertexList(
			String startVertex, AdjacencyList adjacencyList){
		HashMap<String, Integer> sonVertexList =
				new HashMap<String, Integer>();
		HashMap<String, String> edgeEndVertexHash = 
				adjacencyList.graph.get(startVertex);
		
		if (edgeEndVertexHash==null) {
			return null;
		}
		
		edgeEndVertexHash.forEach((edge,endVertex)->{
			//add non-label, non-coordinates end vertices
			if(!edge.equals("rdfs:label")
				&& !edge.equals("<hasLongitude>")
				&& !edge.equals("<hasLatitude>")
				&& !edge.equals(
					"<http://www.w3.org/2003/01/geo/wgs84_pos#lat>")
				&& !edge.equals(
					"<http://www.w3.org/2003/01/geo/wgs84_pos#long>")
				&& !edge.equals(
					"<http://www.georss.org/georss/point>")) {
				
				if (!endVertex.startsWith("\"")
					&& !endVertex.equals("")
					&& !sonVertexList.containsKey(endVertex)) {
					sonVertexList.put(endVertex,0);
				}
			}
		});
		
		return sonVertexList;
	}
	
	public void createSubTreeFromGraph(String root,
			AdjacencyList adjacencyList) {
		this.fatherVertexList = new HashMap<String, Integer>();
		
		HashMap<String, Integer> tempHash =
				new HashMap<String, Integer>();
		this.subTreeHash.put(root, tempHash);
		
		depth = 0;
		this.fatherVertexList = getSonVertexList(root,
				adjacencyList);
		//while son list is not null
		while(this.fatherVertexList!=null) {
			depth++;
			this.sonVertexList = new HashMap<String, Integer>();
			
			this.fatherVertexList.forEach(
					(currentFather, integer)->{
				//add current father to the subtree
				this.subTreeHash.get(root).put(currentFather, 
						depth);
				
				//add sons of the current father to son list
				HashMap<String, Integer> tempInnerHash =
						new HashMap<String, Integer>();
				tempInnerHash = getSonVertexList(currentFather,
						adjacencyList);
				
				if (tempInnerHash!=null) {
					tempInnerHash.forEach(
							(currentSon,innerInteger)->{
						if(this.sonVertexList.containsKey(
								currentSon)) {
							this.sonVertexList.put(currentSon,0);
						}
					});
				}
			});
			
			//update father list for next depth loop
			if(!this.sonVertexList.isEmpty()) {
				this.fatherVertexList = this.sonVertexList;
			} else {
				this.fatherVertexList = null;
			}
		}
	}
	
	public void createVertexKeywordFromGraph(
			String startVertex, AdjacencyList adjacencyList){
		//if start vertex does not exist in vertex keyword hash
		//add start vertex with an empty linked list
		if(!this.vertexKeywordHash.containsKey(startVertex)) {
			this.vertexKeywordHash.put(startVertex, 
					new HashMap<String, Integer>());
		}
		
		//add keywords contained in start vertex itself
		String startVertexLine = startVertex.substring(1, 
				startVertex.indexOf(">"));
		
		/*if (startVertex.equals(
				"<http://dbpedia.org/resource/Hoài_Nhơn_District>")) {
			System.out.println("founded");
		}*/
		
		//delete http://.../
		int index = startVertexLine.lastIndexOf('/');
			
		if (index!=-1) {
			startVertexLine = startVertexLine.substring(index+1);
		}
			
		startVertexLine = startVertexLine.replaceAll(",", "");
		startVertexLine = startVertexLine.replaceAll("\\?", "_");
		
		String[] startVertexKeywords = startVertexLine.split("_");
		for(int i=0; i<startVertexKeywords.length; i++) {
			String term = startVertexKeywords[i];
			if(!this.vertexKeywordHash.get(
					startVertex).containsKey(term)) {
				this.vertexKeywordHash.get(startVertex).put(
						startVertexKeywords[i],0);
			}
		}
		
		if (adjacencyList.graph.containsKey(startVertex)) {
			HashMap<String, String> edgeEndvertexList= 
					adjacencyList.graph.get(startVertex);
			
			edgeEndvertexList.forEach((edge,endVertex)->{
				if(edge.equals("rdfs:label")
					|| edge.equals(
					"<http://www.w3.org/2000/01/rdf-schema#label>")) {
					//preprocessing end vertex
					if (endVertex.startsWith("\"")) {
						endVertex = endVertex.substring(1);
						
						if(endVertex.contains("\"")) {
							int indexOfDoubleQuotes = 
									endVertex.indexOf("\"");
							endVertex = endVertex.substring(
									0, indexOfDoubleQuotes);
						}
						
						endVertex = endVertex.replaceAll(",", "");
						endVertex = endVertex.replaceAll("\\?", " ");
					}
					
					String[] termArray = endVertex.split("\\s+");
					
					for(int j=0; j<termArray.length; j++) {
						String term = termArray[j];
						
						if (term.equals("")) {
							continue;
						} else if(!this.vertexKeywordHash.get(
								startVertex).containsKey(term)) {
							this.vertexKeywordHash.get(
									startVertex).put(term,0);
						}
					}
				}
			});
		}
	}
	
	public void createRootKeyword(){
		this.subTreeHash.forEach((k,v)->{
			String root = k;
			
			//if the root is a new one, add it to root keyword hash
			if(!this.rootKeywordHash.containsKey(root)) {
				this.rootKeywordHash.put(root, 
						new HashMap<String, Integer>());
			}
			
			//add root keywords
			HashMap<String, Integer> rootKeywords = 
					this.vertexKeywordHash.get(root);
			if (rootKeywords!=null) {
				//if the vertex keyword is new,
				//add it to root keyword
				rootKeywords.forEach((keyword,integer)->{
					if(!this.rootKeywordHash.get(
							root).containsKey(keyword)) {
						this.rootKeywordHash.get(root).put(
								keyword,0);
					}
				});
			}
			
			//add children vertex keywords
			v.forEach((vertex,depth)->{
				HashMap<String, Integer> vertexKeywords = 
						vertexKeywordHash.get(vertex);
				
				if (vertexKeywords!=null) {
					//if the vertex keyword is new,
					//add it to root keyword
					vertexKeywords.forEach((keyword,integer)->{
						if(!this.rootKeywordHash.get(
								root).containsKey(keyword)) {
							this.rootKeywordHash.get(
									root).put(keyword,0);
						}
					});
				}
			});
		});
		
		deleteEmptyRootKeyword();
	}
	
	public void createSubTreeFromGraph(
			AdjacencyList adjacencyList) {
		this.subTreeHash = 
				new HashMap<String, HashMap<String, Integer>>();
		
		countingNumber=0;		
		adjacencyList.graph.forEach(
				(startVertex, edgeEndVertexList)->{
			countingNumber++;
			
			if (countingNumber%100000==0) {
				System.out.println("Current processing line is..."
						+ countingNumber);
			}
			
			createSubTreeFromGraph(startVertex, adjacencyList);
		});
		
		adjacencyList.vertexHash.forEach((vertex,isPlace)->{
			if(vertex.startsWith("<")) {
				createVertexKeywordFromGraph(
						vertex, adjacencyList);
			}
		});
		
		createRootKeyword();
	}
	
	public void deleteEmptyRootKeyword() {
		HashMap<String, Integer> removalRootHash =
				new HashMap<String, Integer>();
		
		this.rootKeywordHash.forEach((root,keywordList)->{
			if(keywordList.isEmpty()) {
				removalRootHash.put(root, 1);
			}
		});
		
		removalRootHash.forEach((root, integer)->{
			this.rootKeywordHash.remove(root);
		});
	}

	public void writeSubTreeToTxt(String fileName) {
		try {
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			
			//Write root with {}, vertex with [], depth {}
			this.subTreeHash.forEach((k,v)->{
				try {
					bw.write("{" + k + "}");
					bw.newLine();
	                
	                v.forEach((vertex, depth)->{
	                	try {
							bw.write("[" + vertex + "]");
							bw.write("{" + depth + "}");
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
	
	public void writeVertexKeywordToTxt(String fileName) {
		try {
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			
			//Write vertex with {}, keyword with []
			this.vertexKeywordHash.forEach((k,v)->{
				try {
					bw.write("{" + k + "}");
					bw.newLine();
	                
					v.forEach((keyword,integer)->{
						if (!keyword.equals("")) {
	                		try {
								bw.write("[" + keyword + "]");
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
	
	public void writeRootKeywordToTxt(String fileName) {
		try {
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			
			//Write root with {}, keyword with []
			this.rootKeywordHash.forEach((k,v)->{
				try {
					bw.write("{" + k + "}");
					bw.newLine();
	                
					v.forEach((keyword,integer)->{
						if (!keyword.equals("")) {
	                		try {
								bw.write("[" + keyword + "]");
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
	
	public void readSubTreeFromTxt(String fileName) {
		this.subTreeHash = 
				new HashMap<String, HashMap<String, Integer>>();
		
    	try {
    		fr = new FileReader(fileName);
    		br = new BufferedReader(fr);
    		String currentLine;
    		String root = ""; 
    		String vertex, depthString;
    		int depth;
			
    		//Read file line by line
    		while((currentLine = br.readLine()) != null) {
    			//root line
    			if(currentLine.startsWith("{")) {
    				root = currentLine.substring(1, 
    						currentLine.length()-1);
    				
    				//add root to sub-tree hash
    				this.subTreeHash.put(root, 
    						new HashMap<String, Integer>());
    			}
    			//child vertex and depth line
    			else if (currentLine.startsWith("[")) {
    				vertex = currentLine.substring(1, 
    						currentLine.indexOf("]"));
    				depthString = currentLine.substring(
    						currentLine.indexOf("{")+1, 
    						currentLine.indexOf("}"));
    				depth = Integer.valueOf(depthString);
    				
    				//add child vertex and depth to sub-tree hash
    				this.subTreeHash.get(root).put(vertex, depth);
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
	
	public void readVertexKeywordFromTxt(String fileName) {
		this.vertexKeywordHash = 
				new HashMap<String, HashMap<String,Integer>>();
		
    	try {
    		fr = new FileReader(fileName);
    		br = new BufferedReader(fr);
    		String currentLine;
    		String vertex = "";
    		String keyword;
			
    		//Read file line by line
    		while((currentLine = br.readLine()) != null) {
    			//vertex line
    			if(currentLine.startsWith("{")) {
    				vertex = currentLine.substring(1, 
    						currentLine.length()-1);
    				
    				//add vertex to vertex keyword hash
    				this.vertexKeywordHash.put(vertex, 
    						new HashMap<String,Integer>());
    			}
    			//keyword line
    			else if (currentLine.startsWith("[")) {
    				keyword = currentLine.substring(1, 
    						currentLine.indexOf("]"));
    				
    				//add keyword to vertex keyword hash
    				this.vertexKeywordHash.get(vertex).put(
    						keyword,0);
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
	
	public void readRootKeywordFromTxt(String fileName) {
		this.rootKeywordHash = 
				new HashMap<String, HashMap<String,Integer>>();
		
    	try {
    		fr = new FileReader(fileName);
    		br = new BufferedReader(fr);
    		String currentLine;
    		String root = "";
    		String keyword;
			
    		//Read file line by line
    		while((currentLine = br.readLine()) != null) {
    			//root line
    			if(currentLine.startsWith("{")) {
    				root = currentLine.substring(1, 
    						currentLine.length()-1);
    				
    				//add root to root keyword hash
    				this.rootKeywordHash.put(root, 
    						new HashMap<String,Integer>());
    			}
    			//keyword line
    			else if (currentLine.startsWith("[")) {
    				keyword = currentLine.substring(1, 
    						currentLine.indexOf("]"));
    				
    				//add keyword to root keyword hash
    				this.rootKeywordHash.get(root).put(
    						keyword,0);
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
