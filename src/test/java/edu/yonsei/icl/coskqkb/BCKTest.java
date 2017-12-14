package edu.yonsei.icl.coskqkb;

import java.util.LinkedList;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class BCKTest {
	static LinkedList<String> queryKeywords20;
	static LinkedList<String> queryKeywords15;
	static LinkedList<String> queryKeywords10;
	static LinkedList<String> queryKeywords8;
	static LinkedList<String> queryKeywords5;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String knowledgeBaseName = "DBpedia";
		Point queryLocation = Geometries.point(-80, 40);
		
		/*----   Start initializing query keywords   ----*/
		queryKeywords20 = new LinkedList<String>();
		queryKeywords20 = getQueryKeywords(knowledgeBaseName);
		
		initializeQueryKeywords();
		
		BCK bck = new BCK();
		
		long startLoadingDataTime = System.currentTimeMillis();
		
		if (knowledgeBaseName.equals("YAGO")) {
			System.out.println("Start reading data from yago...");
			bck.readDataFromYago();
		} else if (knowledgeBaseName.equals("DBpedia")) {
			System.out.println("Start reading data from DBpedia...");
			bck.readDataFromDBpedia();
		}
        
		long finishLoadingDataTime = System.currentTimeMillis();
        long dataLoadingTime = 
        		finishLoadingDataTime - startLoadingDataTime;
		System.out.println("Finish reading data from yago,\n"
				+ "Data loading elapsed time is: "
				+ dataLoadingTime + " ms.");
		
		executeQuery(bck, queryLocation, 
				queryKeywords10, 1, knowledgeBaseName);
		/*executeQuery(bck, queryLocation, 
				queryKeywords10, 3, knowledgeBaseName);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 5, knowledgeBaseName);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 8, knowledgeBaseName);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 10, knowledgeBaseName);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 15, knowledgeBaseName);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 20, knowledgeBaseName);*/
	}

	public static LinkedList<String> getQueryKeywords(
			String knowledgeBaseName){
		LinkedList<String> queryKeywords20 = 
				new LinkedList<String>();
		
		if (knowledgeBaseName.equals("YAGO")) {
			queryKeywords20.add("Easy");
			queryKeywords20.add("Shear");
			queryKeywords20.add("England");
			queryKeywords20.add("Raw");
			queryKeywords20.add("Honorable");
			queryKeywords20.add("Ray");
			queryKeywords20.add("House");
			queryKeywords20.add("Western");
			queryKeywords20.add("Ministries");
			queryKeywords20.add("Mile");
			queryKeywords20.add("Kirk");
			queryKeywords20.add("Jacks");
			queryKeywords20.add("Cabin");
			queryKeywords20.add("Sanatorium");
			queryKeywords20.add("Sweeneys");
			queryKeywords20.add("Morrisville");
			queryKeywords20.add("Bryan");
			queryKeywords20.add("Chamouni");
			queryKeywords20.add("Dooley");
			queryKeywords20.add("Laurel");
		} else if (knowledgeBaseName.equals("DBpedia")) {
			queryKeywords20.add("Saltlick");
			queryKeywords20.add("Louise");
			queryKeywords20.add("wreck");
			queryKeywords20.add("Jacks");
			queryKeywords20.add("Pappert");
			queryKeywords20.add("WFGM-FM");
			queryKeywords20.add("BCC");
			queryKeywords20.add("WXKX");
			queryKeywords20.add("Nashville");
			queryKeywords20.add("BCL");
			queryKeywords20.add("Steele");
			queryKeywords20.add("Yough");
			queryKeywords20.add("Southview");
			queryKeywords20.add("Taylorstown");
			queryKeywords20.add("Chrysostom");
			queryKeywords20.add("Minerals");
			queryKeywords20.add("KDKA");
			queryKeywords20.add("ShopNBC");
			queryKeywords20.add("Amtrak");
			queryKeywords20.add("Sewall");
		}
		
		return queryKeywords20;
	}
	
	public static void executeQuery(
			BCK bck, Point queryLocation,
			LinkedList<String> queryKeywords, int k,
			String knowledgeBaseName) {
		System.out.println("# of query keywords="
				+ queryKeywords.size() + ", and k=" + k);
		bck.kVG = bck.findTopkValidGroup(
				queryLocation, queryKeywords, k,
				knowledgeBaseName);
		System.out.println("# of VG generation is: "
				+ bck.numberOfVGGeneration);
		System.out.println("average ranking score of kVG is: "
				+ bck.calculateAverageRankingScoreOfkVG());
		System.out.println("Number of result vg is: "
				+ bck.kVG.size());
		System.out.println(bck.kVG.toString());
		System.out.println();
	}
	
	public static void initializeQueryKeywords() {
		queryKeywords15 = new LinkedList<String>();
		for(int i=0; i<15; i++) {
			queryKeywords15.add(queryKeywords20.get(i));
		}
		
		queryKeywords10 = new LinkedList<String>();
		for(int i=0; i<10; i++) {
			queryKeywords10.add(queryKeywords20.get(i));
		}
		
		queryKeywords8 = new LinkedList<String>();
		for(int i=0; i<8; i++) {
			queryKeywords8.add(queryKeywords20.get(i));
		}
				
		queryKeywords5 = new LinkedList<String>();
		for(int i=0; i<5; i++) {
			queryKeywords5.add(queryKeywords20.get(i));
		}
	}
}
