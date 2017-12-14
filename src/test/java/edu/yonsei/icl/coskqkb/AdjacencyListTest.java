package edu.yonsei.icl.coskqkb;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.io.output.ThresholdingOutputStream;
import org.apache.jena.vocabulary.OWL.Init;

public class AdjacencyListTest {
	public static void main(String args[]) {
		String knowledgeBaseName = "YAGO";
		String readFileName =
				"dataset/YagoGraph/yagoGeonamesData.txt";
		String folderName =
				"dataset/YagoGraph/";
		String writeFileName =
				"dataset/YagoData/yagoGraph.txt";
		
		AdjacencyList adjacencyList = new AdjacencyList();
		long startTime = System.currentTimeMillis();
		
		adjacencyList.mergeTxtFiles(readFileName,
				folderName, writeFileName);
		
        long finishTime = System.currentTimeMillis();
        long elapsedTime = 
        		finishTime - startTime;
		System.out.println("Graph generation time is..."
				+ elapsedTime + " ms");
    	
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
    	
	}
	
	public static void transformTurtleToTxt(String readFolderName,
			String writeFolderName, String knowledgeBaseName) {
		//get file names in the folder
    	File folder = new File(readFolderName);
    	String[] filesInFolder = folder.list();
    	
    	for(int i=0; i<filesInFolder.length; i++) {
    		
    		AdjacencyList adjacencyList = new AdjacencyList();
        	System.out.println("Start processing: " 
        			+ (i+1) + "th file..."
        			+ filesInFolder[i]);
        	String readFileName = readFolderName + filesInFolder[i];
        	String writeFileName = writeFolderName + 
        			filesInFolder[i].substring(0,
        					filesInFolder[i].length()-4) + ".txt";
        			
        	//System.out.println("read from..." + readFileName);
            adjacencyList.addFromTurtle(
            		readFileName, knowledgeBaseName);
            System.out.println("write to..." + writeFileName);
            adjacencyList.writeToTxt(writeFileName);
    	}
    	
    	System.out.println("All process finished!");
	}
}
