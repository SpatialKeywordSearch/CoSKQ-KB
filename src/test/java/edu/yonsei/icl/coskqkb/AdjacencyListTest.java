package edu.yonsei.icl.coskqkb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.io.output.ThresholdingOutputStream;
import org.apache.jena.vocabulary.OWL.Init;

public class AdjacencyListTest {
	public static void main(String args[]) {
		String readFileName = 
				"dataset/YagoData/yagoGraph_withSameAs.txt";
        String writeFileName = 
        		"dataset/YagoData/yagoGraph.txt";
		String folderName = "dataset/YagoParsingData/";
		/*String[] deletePredicates = 
			{"<sameAs>","<linksTo>","<redirectTo>"};*/
        
    	AdjacencyList adjacencyList = new AdjacencyList();
    	adjacencyList.addFromTxt(writeFileName);
    	
    	System.out.println("getNumberOfStartVertices: "
    			+ adjacencyList.getNumberOfStartVertices());
    	System.out.println("getNumberOfDistinctVertices: "
    			+ adjacencyList.getNumberOfDistinctVertices());
    	System.out.println("getNumberOfEdges: "
    			+ adjacencyList.getNumberOfEdges());
    	
    	//adjacencyList.writeToTxt(writeFileName);
    	
    	/*------------ Show statistics of a Graph file ------------*/
    	/*System.out.println("Number of start vertices is: "
    			+ adjacencyList.getNumberOfStartVertices());
    	
    	System.out.println("Number of distinct vertices is: "
    			+ adjacencyList.getNumberOfDistinctVertices());
    	
    	System.out.println("Number of edges is: "
    			+ adjacencyList.getNumberOfEdges());
    	
    	System.out.println("Number of non-label edges is: "
    			+ adjacencyList.getNumberOfNonlabelEdges());*/
    	
    	/*------------ Merge files in a folder ------------*/
    	/*adjacencyList.mergeTxtFiles(readFileName, 
    			folderName, writeFileName);*/
    	
		
		/*------------ Parsing turtle file to txt file ------------*/
		/*ArrayList<String> readFileArray =
        		new ArrayList<String>();
        readFileArray.add("dataset/Yago/yagoSources.ttl");
        
        ArrayList<String> writeFileArray =
        		new ArrayList<String>();
        writeFileArray.add("dataset/YagoGraph/yagoSources.txt");
        
        for(int i=0; i<readFileArray.size(); i++) {
        	System.out.println("Start processing: " + readFileArray.get(i));
        	
        	AdjacencyList adjacencyList = new AdjacencyList();
            adjacencyList.addFromTurtle(readFileArray.get(i));
            System.out.println(adjacencyList.getNumberOfVertices());
            System.out.println(adjacencyList.writeToTxt(writeFileArray.get(i)));
            
            //test readFromTxt function and justify the generated txt file
            AdjacencyList adjacencyList2 = new AdjacencyList();
            adjacencyList2.addFromTxt(writeFileArray.get(i));
            System.out.println(adjacencyList2.getNumberOfVertices());
            
            System.out.println("Finish processing: " + readFileArray.get(i));
        }*/
	}
}
