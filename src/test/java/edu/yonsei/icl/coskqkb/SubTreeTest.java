package edu.yonsei.icl.coskqkb;

public class SubTreeTest {
	static int maxDepth;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String knowledgeBaseName = "YAGO";
		
		/*String readFileName = 
				"dataset/YagoData/yagoGraph_withSameAs.txt";
		
		int maxDepthBound = 0;
		String writeSubTreeFileName =
				"dataset/YagoData/yagoSubTree"
				+ maxDepthBound + ".txt";
		String writeVertexKeywordFileName =
				"dataset/YagoData/yagoVertexKeyword"
				+ maxDepthBound + ".txt";
		String writeRootKeywordFileName =
				"dataset/YagoData/yagoSubTreeKeyword"
				+ maxDepthBound + ".txt";*/
		
		
		/*String readFileName = 
				"dataset/DBpediaData/dbpediaGraph_withSameAs.txt";
		
		int maxDepthBound = 3;
		String writeSubTreeFileName =
				"dataset/DBpediaData/dbpediaSubTree"
				+ maxDepthBound + ".txt";
		String writeVertexKeywordFileName =
				"dataset/DBpediaData/dbpediaVertexKeyword"
				+ maxDepthBound + ".txt";
		String writeRootKeywordFileName =
				"dataset/DBpediaData/dbpediaSubTreeKeyword"
				+ maxDepthBound + ".txt";
		
		long startTime = System.currentTimeMillis();
		
		transformGraphToTxt(maxDepthBound, 
				readFileName, writeSubTreeFileName, 
				writeVertexKeywordFileName,
				writeRootKeywordFileName);
		
        long finishTime = System.currentTimeMillis();
        long elapsedTime = 
        		finishTime - startTime;
		System.out.println("Subtree generation time is..."
				+ elapsedTime + " ms");*/
		
		SubTree subTree = new SubTree();
		String target = 
			"<http://dbpedia.org/resource/Myongji_University>";
		
		//get root keyword hash
		subTree.readRootKeywordFromTxt(
				"dataset/DBpediaData/dbpediaSubTreeKeyword3.txt");
		
		System.out.println(subTree.rootKeywordHash.get(target)
				.toString());
		
		//get children vertex hash
		subTree.readSubTreeFromTxt(
				"dataset/DBpediaData/dbpediaSubTree3.txt");
		
		System.out.println(subTree.subTreeHash.get(target)
				.toString());
		
	}

	public static void transformGraphToTxt(
			int maxDepthBound,
			String readFileName, String writeSubTreeFileName,
			String writeVertexKeywordFileName,
			String writeRootKeywordFileName) {
		
		System.out.println("Start reading graph...");
		AdjacencyList adjacencyList = new AdjacencyList();
		adjacencyList.addFromTxt(readFileName);
		
		//System.out.println(adjacencyList.graph.size());
		
		System.out.println("Start creating sub-tree from graph...");
		SubTree subTree = new SubTree();
		subTree.createSubTreeFromGraph(maxDepthBound,
				adjacencyList);
		//subTree.createKeywordHash(adjacencyList);
		
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
