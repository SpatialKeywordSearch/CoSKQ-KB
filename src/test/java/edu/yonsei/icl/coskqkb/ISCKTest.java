package edu.yonsei.icl.coskqkb;

import java.util.LinkedList;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class ISCKTest {
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
		
		int maxDepthBound = 10;
		
		/*----   Start initializing query keywords   ----*/
		/*queryKeywords20 = new LinkedList<String>();
		queryKeywords20 = getQueryKeywords(
				knowledgeBaseName, maxDepthBound);*/
		qualitativeKeywords = new LinkedList<String>();
		qualitativeKeywords = getQualitativeKeywords();
		
		//initializeQueryKeywords();
		
		ISCK isck = new ISCK();
		
		long startLoadingDataTime = System.currentTimeMillis();
		
		if (knowledgeBaseName.equals("YAGO")) {
			System.out.println("Start reading data from yago...");
			isck.readDataFromYago(maxDepthBound);
		} else if (knowledgeBaseName.equals("DBpedia")) {
			System.out.println("Start reading data from DBpedia...");
			isck.readDataFromDBpedia(maxDepthBound);
		}
        
		long finishLoadingDataTime = System.currentTimeMillis();
        long dataLoadingTime = 
        		finishLoadingDataTime - startLoadingDataTime;
		System.out.println("Finish reading data,\n"
				+ "Data loading elapsed time is: "
				+ dataLoadingTime + " ms.");
		
		//qualitative test
		executeQuery(isck, queryLocation, 
				qualitativeKeywords, 10, knowledgeBaseName,
				maxDepthBound);
		
		//varying k
		/*executeQuery(isck, queryLocation, 
				queryKeywords10, 1, knowledgeBaseName, maxDepthBound);
		executeQuery(isck, queryLocation, 
				queryKeywords10, 3, knowledgeBaseName, maxDepthBound);
		executeQuery(isck, queryLocation, 
				queryKeywords10, 5, knowledgeBaseName, maxDepthBound);
		executeQuery(isck, queryLocation, 
				queryKeywords10, 8, knowledgeBaseName, maxDepthBound);
		executeQuery(isck, queryLocation, 
				queryKeywords10, 10, knowledgeBaseName, maxDepthBound);
		executeQuery(isck, queryLocation, 
				queryKeywords10, 15, knowledgeBaseName, maxDepthBound);
		executeQuery(isck, queryLocation, 
				queryKeywords10, 20, knowledgeBaseName, maxDepthBound);*/
		
		//varying #qk
		/*executeQuery(isck, queryLocation, 
				queryKeywords5, 10, knowledgeBaseName, maxDepthBound);
		executeQuery(isck, queryLocation, 
				queryKeywords8, 10, knowledgeBaseName, maxDepthBound);
		executeQuery(isck, queryLocation, 
				queryKeywords10, 10, knowledgeBaseName, maxDepthBound);
		executeQuery(isck, queryLocation, 
				queryKeywords15, 10, knowledgeBaseName, maxDepthBound);
		executeQuery(isck, queryLocation, 
				queryKeywords20, 10, knowledgeBaseName, maxDepthBound);*/
	}
	
	public static LinkedList<String> getQualitativeKeywords(){
		qualitativeKeywords = new LinkedList<String>();
		
		qualitativeKeywords.add("Palace");
		qualitativeKeywords.add("Hospital");
		qualitativeKeywords.add("University");
		qualitativeKeywords.add("Arts");
		qualitativeKeywords.add("Market");
		qualitativeKeywords.add("Church");
		qualitativeKeywords.add("Jewellery");
		qualitativeKeywords.add("Baseball");
		
		return qualitativeKeywords;
	}

	public static LinkedList<String> getQueryKeywords(
			String knowledgeBaseName,
			int maxDepthBound){
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
			
			/*if (maxDepthBound==1) {
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
			} else if (maxDepthBound==0) {
				queryKeywords20.add("Easy");
				queryKeywords20.add("Shear");
				queryKeywords20.add("England");
				queryKeywords20.add("Raw");
				queryKeywords20.add("Honorable");
				queryKeywords20.add("Ray");
				queryKeywords20.add("Western");
				queryKeywords20.add("Ministries");
				queryKeywords20.add("Mile");
				queryKeywords20.add("Jacks");
				queryKeywords20.add("Cabin");
				queryKeywords20.add("Kirk");
				queryKeywords20.add("Sanatorium");
				queryKeywords20.add("Sweeneys");
				queryKeywords20.add("Morrisville");
				queryKeywords20.add("Bryan");
				queryKeywords20.add("Chamouni");
				queryKeywords20.add("Dooley");
				queryKeywords20.add("Laurel");
				queryKeywords20.add("Tomasza");
			}*/
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
			
			/*if (maxDepthBound==10) {
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
			} else if (maxDepthBound==8) {
				queryKeywords20.add("Rex");
				queryKeywords20.add("Rama");
				queryKeywords20.add("Saltlick");
				queryKeywords20.add("Seward");
				queryKeywords20.add("Elmo");
				queryKeywords20.add("Louise");
				queryKeywords20.add("Hurt");
				queryKeywords20.add("wreck");
				queryKeywords20.add("Rams");
				queryKeywords20.add("Ostermundigen");
				queryKeywords20.add("Grindstone-Rowes");
				queryKeywords20.add("BCC");
				queryKeywords20.add("BCL");
				queryKeywords20.add("Steele");
				queryKeywords20.add("Madden");
				queryKeywords20.add("Taylorstown");
				queryKeywords20.add("Chrysostom");
				queryKeywords20.add("Minerals");
				queryKeywords20.add("Amtrak");
				queryKeywords20.add("Sewall");
			} else if (maxDepthBound==5) {
				queryKeywords20.add("Rex");
				queryKeywords20.add("Rama");
				queryKeywords20.add("Saltlick");
				queryKeywords20.add("Elmo");
				queryKeywords20.add("Louise");
				queryKeywords20.add("wreck");
				queryKeywords20.add("Rams");
				queryKeywords20.add("Ostermundigen");
				queryKeywords20.add("Grindstone-Rowes");
				queryKeywords20.add("Foley");
				queryKeywords20.add("BCC");
				queryKeywords20.add("Rapp");
				queryKeywords20.add("BCL");
				queryKeywords20.add("Steele");
				queryKeywords20.add("Moonstone");
				queryKeywords20.add("Budke");
				queryKeywords20.add("SIU");
				queryKeywords20.add("Taylorstown");
				queryKeywords20.add("Chrysostom");
				queryKeywords20.add("Minerals");
			} else if (maxDepthBound==3) {
				queryKeywords20.add("Rama");
				queryKeywords20.add("Coshocton");
				queryKeywords20.add("Saltlick");
				queryKeywords20.add("Louise");
				queryKeywords20.add("wreck");
				queryKeywords20.add("Jacks");
				queryKeywords20.add("Pappert");
				queryKeywords20.add("BCC");
				queryKeywords20.add("WXKX");
				queryKeywords20.add("Nashville");
				queryKeywords20.add("BCL");
				queryKeywords20.add("Steele");
				queryKeywords20.add("Madden");
				queryKeywords20.add("Yough");
				queryKeywords20.add("Southview");
				queryKeywords20.add("Taylorstown");
				queryKeywords20.add("Chrysostom");
				queryKeywords20.add("Minerals");
				queryKeywords20.add("KDKA");
				queryKeywords20.add("ShopNBC");
			} else if (maxDepthBound==1) {
				queryKeywords20.add("Saltlick");
				queryKeywords20.add("Louise");
				queryKeywords20.add("wreck");
				queryKeywords20.add("Jacks");
				queryKeywords20.add("Pappert");
				queryKeywords20.add("WFGM-FM");
				queryKeywords20.add("BCC");
				queryKeywords20.add("WXKX");
				queryKeywords20.add("Nashville");
				queryKeywords20.add("Penguins");
				queryKeywords20.add("Game");
				queryKeywords20.add("Yough");
				queryKeywords20.add("Southview");
				queryKeywords20.add("Taylorstown");
				queryKeywords20.add("Chrysostom");
				queryKeywords20.add("Minerals");
				queryKeywords20.add("KDKA");
				queryKeywords20.add("ShopNBC");
				queryKeywords20.add("Amtrak");
				queryKeywords20.add("Sewall");
			} else if (maxDepthBound==0) {
				queryKeywords20.add("Saltlick");
				queryKeywords20.add("wreck");
				queryKeywords20.add("Boiler");
				queryKeywords20.add("Trib");
				queryKeywords20.add("Jacks");
				queryKeywords20.add("Logstown");
				queryKeywords20.add("Morrisville");
				queryKeywords20.add("WPTS-FM");
				queryKeywords20.add("Omni");
				queryKeywords20.add("Amphitheatre");
				queryKeywords20.add("Pappert");
				queryKeywords20.add("Cardinal");
				queryKeywords20.add("Highway");
				queryKeywords20.add("Hopewell");
				queryKeywords20.add("Burnett");
				queryKeywords20.add("Cumberland");
				queryKeywords20.add("Wickerham");
				queryKeywords20.add("Cultural");
				queryKeywords20.add("PONY");
				queryKeywords20.add("Middlesex");
			}*/
		}
		
		return queryKeywords20;
	}
	
	public static void executeQuery(
			ISCK isck, Point queryLocation,
			LinkedList<String> queryKeywords, int k,
			String knowledgeBaseName, int treeDepthBound) {
		System.out.println("# of query keywords="
				+ queryKeywords.size() + ", and k=" + k);
		isck.kVG = isck.findTopkValidGroup(
				queryLocation, queryKeywords, k,
				knowledgeBaseName, treeDepthBound);
		System.out.println("average ranking score of kVG is: "
				+ isck.calculateAverageRankingScoreOfkVG());
		System.out.println("# of VG generation is: "
				+ isck.numberOfVGGeneration);
		System.out.println("# of VGRS calculation is: "
				+ isck.numberOfVGRSCalculation);
		System.out.println("Number of result vg is: "
				+ isck.kVG.size());
		System.out.println(isck.kVG.toString());
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
