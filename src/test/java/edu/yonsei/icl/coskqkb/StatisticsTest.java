package edu.yonsei.icl.coskqkb;

public class StatisticsTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//showYagoStatistics();
		showDBpediaStatistics();
	}
	
	public static void showYagoStatistics() {
		String graphPath =
			"dataset/YagoData/yagoGraph_withSameAs.txt";
		
		int maxDepthBound = 1;
		String vertexKeywordPath =
			"dataset/YagoData/yagoVertexKeyword"
				+ maxDepthBound + ".txt";
		String subTreeKeywordPath =
			"dataset/YagoData/yagoSubTreeKeyword"
				+ maxDepthBound + ".txt";
		String subTreePath = 
			"dataset/YagoData/yagoSubTree" 
				+ maxDepthBound + ".txt";
		
		/*String graphPath =
				"dataset/DBpediaData/dbpediaGraph_withSameAs.txt";
		
		int maxDepthBound = 10;
		String vertexKeywordPath =
				"dataset/DBpediaData/dbpediaVertexKeyword"
						+ maxDepthBound + ".txt";
		String subTreeKeywordPath =
				"dataset/DBpediaData/dbpediaSubTreeKeyword"
						+ maxDepthBound + ".txt";
		String subTreePath = 
				"dataset/DBpediaData/dbpediaSubTree"
						+ maxDepthBound + ".txt";*/
		
		AdjacencyList adjacencyList = new AdjacencyList();
		adjacencyList.addFromTxt(graphPath);
		adjacencyList.createVertexHash();
		
		SubTree subTree = new SubTree();
		subTree.readVertexKeywordFromTxt(vertexKeywordPath);
		subTree.readRootKeywordFromTxt(subTreeKeywordPath);
		subTree.readSubTreeFromTxt(subTreePath);
		
		System.out.println("No. of vertices is..."
				+ adjacencyList.getNumberOfDistinctVertices());
		System.out.println("No. of edges is..."
				+ adjacencyList.getNumberOfEdges());
		System.out.println("No. of place vertices is..."
				+ adjacencyList.getNumverOfPlaceVertices());
		
		System.out.println("No. of distinct keywords is..."
				+ subTree.vertexKeywordHash.size());
		System.out.println("Average posting list length is..."
				+ subTree.getAveragePostingListLength());
		System.out.println("Max depth is..."
				+ subTree.getMaxDepth());
	}
	
	public static void showDBpediaStatistics() {
		String graphPath =
				"dataset/DBpediaData/dbpediaGraph_withSameAs.txt";
		
		int maxDepthBound = 10;
		String vertexKeywordPath =
				"dataset/DBpediaData/dbpediaVertexKeyword"
						+ maxDepthBound + ".txt";
		String subTreeKeywordPath =
				"dataset/DBpediaData/dbpediaSubTreeKeyword"
						+ maxDepthBound + ".txt";
		String subTreePath = 
				"dataset/DBpediaData/dbpediaSubTree"
						+ maxDepthBound + ".txt";
		
		AdjacencyList adjacencyList = new AdjacencyList();
		adjacencyList.addFromTxt(graphPath);
		adjacencyList.createVertexHash();
		
		SubTree subTree = new SubTree();
		subTree.readVertexKeywordFromTxt(vertexKeywordPath);
		subTree.readRootKeywordFromTxt(subTreeKeywordPath);
		subTree.readSubTreeFromTxt(subTreePath);
		
		System.out.println("No. of vertices is..."
				+ adjacencyList.getNumberOfDistinctVertices());
		System.out.println("No. of edges is..."
				+ adjacencyList.getNumberOfEdges());
		System.out.println("No. of place vertices is..."
				+ adjacencyList.getNumverOfPlaceVertices());
		
		System.out.println("No. of distinct keywords is..."
				+ subTree.vertexKeywordHash.size());
		System.out.println("Average posting list length is..."
				+ subTree.getAveragePostingListLength());
		System.out.println("Max depth is..."
				+ subTree.getMaxDepth());
	}
}
