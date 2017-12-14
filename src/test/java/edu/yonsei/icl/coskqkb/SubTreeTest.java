package edu.yonsei.icl.coskqkb;

public class SubTreeTest {
	static int maxDepth;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String knowledgeBaseName = "YAGO";
		String readFileName = 
				"dataset/YagoData/yagoGraph.txt";
		String writeSubTreeFileName =
				"dataset/YagoData/yagoSubTree.txt";
		String writeVertexKeywordFileName =
				"dataset/YagoData/yagoVertexKeyword.txt";
		String writeRootKeywordFileName =
				"dataset/YagoData/yagoSubTreeKeyword.txt";
		
		long startTime = System.currentTimeMillis();
		
		transformGraphToTxt(readFileName,
				writeSubTreeFileName, writeVertexKeywordFileName,
				writeRootKeywordFileName);
		
        long finishTime = System.currentTimeMillis();
        long elapsedTime = 
        		finishTime - startTime;
		System.out.println("Subtree generation time is..."
				+ elapsedTime + " ms");
		
		
		
		
		/*maxDepth = 0;
		subTree1.subTreeHash.forEach((root,vertexHash)->{
			vertexHash.forEach((vertex,depth)->{
				if(maxDepth<depth) {
					maxDepth = depth;
				}
			});
		});
		
		System.out.println("max depth is..." + maxDepth);*/
		
		/*System.out.println("Start creating sub-trees...");
		long startTime = System.currentTimeMillis();
		subTree1.createSubTreeFromGraph(adjacencyList);
        long finishTime = System.currentTimeMillis();
        long executionTime = finishTime - startTime;
        System.out.println("Finish creating sub-trees..."
        		+ "elapsed time: " + executionTime
        		+ " milliseconds");*/
        
		/*subTree1.readSubTreeFromTxt(writeSubTreeFileName);
		subTree1.readVertexKeywordFromTxt(writeVertexKeywordFileName);
		subTree1.readRootKeywordFromTxt(writeRootKeywordFileName);*/
		
        /*System.out.println("Statistics for subTree1 is..."
        		+ " \nNo. of sub-trees: "
        		+ subTree1.subTreeHash.size()
        		+ " \nNo. of vertices: "
        		+ subTree1.vertexKeywordHash.size()
        		+ " \nNo. of roots: "
        		+ subTree1.rootKeywordHash.size());*/
        
        /*System.out.println(subTree1.vertexKeywordHash.get(
        		"<Village_Inn_Golf__Conference_Ct>").toString());*/
        
		/*-- Read from txt files --*/
        /*SubTree subTree2 = new SubTree();
        subTree2.readSubTreeFromTxt(writeSubTreeFileName);
        subTree2.readVertexKeywordFromTxt(writeVertexKeywordFileName);
        subTree2.readRootKeywordFromTxt(writeRootKeywordFileName);
        
        System.out.println("Statistics for subTree2 is..."
        		+ " \nNo. of sub-trees: "
        		+ subTree2.subTreeHash.size()
        		+ " \nNo. of vertices: "
        		+ subTree2.vertexKeywordHash.size()
        		+ " \nNo. of roots: "
        		+ subTree2.rootKeywordHash.size());*/
	}

	public static void transformGraphToTxt(
			String readFileName, String writeSubTreeFileName,
			String writeVertexKeywordFileName,
			String writeRootKeywordFileName) {
		
		System.out.println("Start reading graph...");
		AdjacencyList adjacencyList = new AdjacencyList();
		adjacencyList.addFromTxt(readFileName);
		
		//System.out.println(adjacencyList.graph.size());
		
		System.out.println("Start creating sub-tree from graph...");
		SubTree subTree = new SubTree();
		subTree.createSubTreeFromGraph(
				adjacencyList);
		
		/*-- Write to txt files --*/
        System.out.println("Start writing sub-tree to..."
        		+ writeSubTreeFileName);
		subTree.writeSubTreeToTxt(writeSubTreeFileName);
		
		System.out.println("Start writing vertex keyword to..."
				+ writeVertexKeywordFileName);
		subTree.writeVertexKeywordToTxt(writeVertexKeywordFileName);
		
		System.out.println("Start writing root keyword to..."
				+ writeRootKeywordFileName);
		subTree.writeRootKeywordToTxt(writeRootKeywordFileName);
	}
}
