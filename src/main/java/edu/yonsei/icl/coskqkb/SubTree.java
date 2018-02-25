package edu.yonsei.icl.coskqkb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

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
	static int lsp;
	static int maxDepth;
	static double totalPostingListLength;
	HashMap<String, Integer> currentLoopVertexList;
	HashMap<String, Integer> nextLoopVertexList;
	HashMap<String, Integer> sonVertexList;

	public SubTree() {
		this.vertexKeywordHash = new HashMap<String, 
				HashMap<String, Integer>>();
		this.rootKeywordHash = new HashMap<String, 
				HashMap<String, Integer>>();
		this.subTreeHash = new HashMap<String, 
				HashMap<String, Integer>>();
	}
	
	public int getMaxDepth() {
		maxDepth = 0;
		
		subTreeHash.forEach((root,vertexHash)->{
			vertexHash.forEach((vertex,vertexDepth)->{
				if (vertexDepth > maxDepth) {
					maxDepth = vertexDepth;
				}
			});
		});
		
		return maxDepth;
	}
	
	public double getAveragePostingListLength() {
		totalPostingListLength = 0;
		
		rootKeywordHash.forEach((root,keywordHash)->{
			totalPostingListLength += keywordHash.size();
		});
		
		return totalPostingListLength/rootKeywordHash.size();
	}
	
	public HashMap<String, Integer> getSonVertexList(
			String startVertex, 
			HashMap<String, HashMap<String, String>> graph){
		HashMap<String, Integer> sonVertexList =
				new HashMap<String, Integer>();
		HashMap<String, String> edgeEndVertexHash = 
				graph.get(startVertex);
		
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
	
	public void createKeywordHash(
			AdjacencyList adjacencyList) {
		adjacencyList.vertexHash.forEach((vertex,isPlace)->{
			if(vertex.startsWith("<")) {
				createVertexKeywordFromGraph(
						vertex, adjacencyList.graph);
			}
		});
		
		createRootKeyword();		
	}

	public void createSubTreeFromGraph(
			int maxDepthBound,
			AdjacencyList adjacencyList) {
		this.subTreeHash = 
				new HashMap<String, HashMap<String, Integer>>();
		
		countingNumber=0;
		
		adjacencyList.graph.forEach(
				(startVertex, edgeEndVertexList)->{
			//if the start vertex is a place vertex
			if (adjacencyList.vertexHash.get(startVertex).equals(
					Boolean.TRUE)) {
				countingNumber++;
				
				if (countingNumber%10000==0) {
					System.out.println("Current processing line is..."
							+ countingNumber);
				}
				
				createSubTreeFromGraph(startVertex, maxDepthBound,
						adjacencyList.graph);
			}
		});
		
		createKeywordHash(adjacencyList);
	}
	
	public void createSubTreeFromGraph(String root,
			int maxDepthBound,
			HashMap<String, HashMap<String, String>> graph) {
		this.currentLoopVertexList = 
				new HashMap<String, Integer>();
		this.nextLoopVertexList =
				new HashMap<String, Integer>();
		
		//add root to subtree hash with empty hash
		HashMap<String, Integer> tempHash =
				new HashMap<String, Integer>();
		this.subTreeHash.put(root, tempHash);
		
		depth = 0;
		this.nextLoopVertexList = getSonVertexList(root, graph);
		
		//while next loop vertex list is not null
		while(!this.nextLoopVertexList.isEmpty()
				&& depth < maxDepthBound) {
			depth++;
			this.sonVertexList = new HashMap<String, Integer>();
			
			this.currentLoopVertexList.putAll(nextLoopVertexList);
			/*this.nextLoopVertexList =
					new HashMap<String, Integer>();*/
			nextLoopVertexList.clear();
			
			this.currentLoopVertexList.forEach(
					(currentLoopVertex, integer)->{
				//add current father to the subtree
				this.subTreeHash.get(root).put(currentLoopVertex, 
						depth);
				
				//add sons of the current loop vertex list
				//to next loop vertex list
				HashMap<String, Integer> tempInnerHash =
						new HashMap<String, Integer>();
				tempInnerHash = getSonVertexList(currentLoopVertex,
						graph);
				
				if (tempInnerHash!=null) {
					tempInnerHash.forEach(
							(currentSon,innerInteger)->{
						if(!this.subTreeHash.get(root).containsKey(
								currentSon)) {
							this.nextLoopVertexList.put(
									currentSon,0);
						}
					});
				}
			});
			
			currentLoopVertexList.clear();
		}
	}
	
	public void createVertexKeywordFromGraph(
			String startVertex,
			HashMap<String, HashMap<String, String>> graph){
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
		
		if (graph.containsKey(startVertex)) {
			HashMap<String, String> edgeEndvertexList= 
					graph.get(startVertex);
			
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
		this.subTreeHash.forEach((root,v)->{
			
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
									root).put(keyword,depth);
						} else if (this.rootKeywordHash.get(
								root).get(keyword)>depth) {
							this.rootKeywordHash.get(
									root).replace(keyword, depth);
						}
					});
				}
			});
		});
		
		deleteEmptyRootKeyword();
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
			
			//Write root with {}, 
			//vertex with [], depth with {}
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
								bw.write("{" + 0 + "}");
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
			
			//Write root with {}
			//keyword with [], lsp with {}
			this.rootKeywordHash.forEach((k,v)->{
				try {
					bw.write("{" + k + "}");
					bw.newLine();
	                
					v.forEach((keyword,lsp)->{
						if (!keyword.equals("")) {
	                		try {
								bw.write("[" + keyword + "]");
								bw.write("{" + lsp + "}");
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
    		String lspString;
			
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
    			//keyword and lsp line
    			else if (currentLine.startsWith("[")) {
    				keyword = currentLine.substring(1, 
    						currentLine.indexOf("]"));
    				lspString = currentLine.substring(
    						currentLine.indexOf("{")+1, 
    						currentLine.indexOf("}"));
    				lsp = Integer.valueOf(lspString);
    				
    				//add keyword to root keyword hash
    				this.rootKeywordHash.get(root).put(
    						keyword,lsp);
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
