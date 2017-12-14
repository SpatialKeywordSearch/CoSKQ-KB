package edu.yonsei.icl.coskqkb;

public class StatisticsTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		showYagoStatistics();
		//showDBpediaStatistics();
	}
	
	public static void showYagoStatistics() {
		String graphPath =
			"dataset/YagoData/yagoGraph.txt";
		String vertexKeywordPath =
			"dataset/YagoData/yagoVertexKeyword.txt";
		String subTreeKeywordPath =
			"dataset/YagoData/yagoSubTreeKeyword.txt";
		
		AdjacencyList adjacencyList = new AdjacencyList();
		adjacencyList.addFromTxt(graphPath);
		adjacencyList.createVertexHash();
		
		SubTree subTree = new SubTree();
		subTree.readVertexKeywordFromTxt(vertexKeywordPath);
		subTree.readRootKeywordFromTxt(subTreeKeywordPath);
		
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
	}
	
	public static void showDBpediaStatistics() {
		String graphPath =
			"dataset/DBpediaData/dbpediaGraph.txt";
		String vertexKeywordPath =
			"dataset/DBpediaData/dbpediaVertexKeyword.txt";
		String subTreeKeywordPath =
			"dataset/DBpediaData/dbpediaSubTreeKeyword.txt";
		
		AdjacencyList adjacencyList = new AdjacencyList();
		adjacencyList.addFromTxt(graphPath);
		adjacencyList.createVertexHash();
		
		SubTree subTree = new SubTree();
		subTree.readVertexKeywordFromTxt(vertexKeywordPath);
		subTree.readRootKeywordFromTxt(subTreeKeywordPath);
		
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
	}
}
