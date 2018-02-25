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
	static LinkedList<String> qualitativeKeywords;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String knowledgeBaseName = "DBpedia";
		//Point queryLocation = Geometries.point(-80, 40);
		Point queryLocation = Geometries.point(
				126.93694444444445, 37.555277777777775);
		int maxDepthBound = 3;
		
		/*----   Start initializing query keywords   ----*/
		/*queryKeywords20 = new LinkedList<String>();
		queryKeywords20 = getQueryKeywords(knowledgeBaseName);
		initializeQueryKeywords();*/
		
		qualitativeKeywords = new LinkedList<String>();
		qualitativeKeywords = getQualitativeKeywords();
		
		BCK bck = new BCK();
		
		long startLoadingDataTime = System.currentTimeMillis();
		
		if (knowledgeBaseName.equals("YAGO")) {
			System.out.println("Start reading data from yago...");
			bck.readDataFromYago(maxDepthBound);
		} else if (knowledgeBaseName.equals("DBpedia")) {
			System.out.println("Start reading data from DBpedia...");
			bck.readDataFromDBpedia(maxDepthBound);
		}
        
		long finishLoadingDataTime = System.currentTimeMillis();
        long dataLoadingTime = 
        		finishLoadingDataTime - startLoadingDataTime;
		System.out.println("Finish reading data,\n"
				+ "Data loading elapsed time is: "
				+ dataLoadingTime + " ms.");
		
		//qualitative test
		executeQuery(bck, queryLocation, 
				qualitativeKeywords, 5, 
				knowledgeBaseName, maxDepthBound);
		
		/*executeQuery(bck, queryLocation, 
				queryKeywords10, 1, 
				knowledgeBaseName, maxDepthBound);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 3, 
				knowledgeBaseName, maxDepthBound);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 5, 
				knowledgeBaseName, maxDepthBound);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 8, 
				knowledgeBaseName, maxDepthBound);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 10, 
				knowledgeBaseName, maxDepthBound);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 15, 
				knowledgeBaseName, maxDepthBound);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 20, 
				knowledgeBaseName, maxDepthBound);
		
		executeQuery(bck, queryLocation, 
				queryKeywords5, 10, 
				knowledgeBaseName, maxDepthBound);
		executeQuery(bck, queryLocation, 
				queryKeywords8, 10, 
				knowledgeBaseName, maxDepthBound);
		executeQuery(bck, queryLocation, 
				queryKeywords10, 10, 
				knowledgeBaseName, maxDepthBound);
		executeQuery(bck, queryLocation, 
				queryKeywords15, 10, 
				knowledgeBaseName, maxDepthBound);
		executeQuery(bck, queryLocation, 
				queryKeywords20, 10, 
				knowledgeBaseName, maxDepthBound);*/
	}
	
	public static LinkedList<String> getQualitativeKeywords(){
		qualitativeKeywords = new LinkedList<String>();
		
		qualitativeKeywords.add("Park");
		//qualitativeKeywords.add("Museum");
		//qualitativeKeywords.add("Church");
		//qualitativeKeywords.add("Catholic");
		//qualitativeKeywords.add("art");
		qualitativeKeywords.add("palace");
		qualitativeKeywords.add("University");
		qualitativeKeywords.add("Hospital");
		qualitativeKeywords.add("History");
		qualitativeKeywords.add("Christianity");
		qualitativeKeywords.add("Seaworld");
		
		return qualitativeKeywords;
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
			queryKeywords20.add("Tomasza");
		} else if (knowledgeBaseName.equals("DBpedia")) {
			queryKeywords20.add("Rama");
			queryKeywords20.add("Saltlick");
			queryKeywords20.add("wreck");
			queryKeywords20.add("Chico");
			queryKeywords20.add("Rams");
			queryKeywords20.add("BCC");
			queryKeywords20.add("BCL");
			queryKeywords20.add("Steele");
			queryKeywords20.add("Madden");
			queryKeywords20.add("Taylorstown");
			queryKeywords20.add("Chrysostom");
			queryKeywords20.add("Minerals");
			queryKeywords20.add("Amtrak");
			queryKeywords20.add("Sewall");
			queryKeywords20.add("Pontotoc");
			queryKeywords20.add("Gladesville");
			queryKeywords20.add("Fond");
			queryKeywords20.add("Ada");
			queryKeywords20.add("www.fresno.gov");
			queryKeywords20.add("pittsburghmills.com");
		}
		
		return queryKeywords20;
	}
	
	public static void executeQuery(
			BCK bck, Point queryLocation,
			LinkedList<String> queryKeywords, int k,
			String knowledgeBaseName,
			int maxDepthBound) {
		System.out.println("# of query keywords="
				+ queryKeywords.size() + ", and k=" + k);
		bck.kVG = bck.findTopkValidGroup(
				queryLocation, queryKeywords, k,
				knowledgeBaseName, maxDepthBound);
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
