package edu.yonsei.icl.coskqkb;

public class InvertedIndexTest {

	public static void main(String[] args) {
		String readFileName = 
				"dataset/YagoData/yagoSubTreeKeyword.txt";
		String writeFileName =
				"dataset/YagoData/yagoSubTreeInvertedIndex.txt";
		
		long startTime = System.currentTimeMillis();
		
		transformToInvertedIndex(readFileName, writeFileName);
		
		long finishTime = System.currentTimeMillis();
        long elapsedTime = 
        		finishTime - startTime;
		System.out.println("Inverted index generation time is..."
				+ elapsedTime + " ms");
		
		/*AdjacencyList adjacencyList = new AdjacencyList();
		adjacencyList.addFromTxt(readFileName);*/
		
		/*System.out.println("Strat creating invertex index from..."
				+ readFileName);
		invertedIndex.createFromTxt(readFileName);
		
		System.out.println("Start writing invertex index to..."
				+ writeFileName);
		invertedIndex.writeToTxt(writeFileName);
		
		System.out.println(invertedIndex.getNumberOfTerms());*/

		
    	/*-- Read adjacency list from a Graph file --*/
		/*System.out.println("Start reading file..."
				+ readFileName);
		AdjacencyList adjacencyList = new AdjacencyList();
		adjacencyList.addFromTxt(readFileName);
		System.out.println("Finish reading file..."
				+ readFileName);*/
		
		/*-- Create inverted index w.r.t. adjacency list --*/
		/*-- and write to a txt file --*/
		/*InvertedIndex invertedIndex = new InvertedIndex();
		System.out.println("Start creating inverted index...");
		
	    long startTime = System.currentTimeMillis();
		invertedIndex.createFromGraph(adjacencyList);
        long finishTime = System.currentTimeMillis();
        long executionTime = finishTime - startTime;

		System.out.println("Finish creating inverted index...");
		System.out.println("Inverted index creating time is: "
				+ executionTime + " milliseconds");
		
		System.out.println("Start writing to txt file..."
				+ writeFileName);
		invertedIndex.writeToTxt(writeFileName);
		System.out.println("Finish writing to txt file..."
				+ writeFileName);*/
	}

	public static void transformToInvertedIndex(
			String readFileName, String writeFileName) {
		InvertedIndex invertedIndex = new InvertedIndex();
		invertedIndex.createFromTxt(readFileName);
		invertedIndex.writeToTxt(writeFileName);
	}
}
