package edu.yonsei.icl.coskqkb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator.OfDouble;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.fbs.generated.Geometry_;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import rx.Observable;

public class BCK {
	InvertedIndex subtreeInvertedIndex;
	SubTree subTree;
	InvertedIndex vertexInvertedIndex;
	GeoCoordinates geoCoordinates;
	GeoIndex geoIndex;
	double spatialDistance;
	double rankingScore;
	HashMap<HashMap<String,Integer>,Double> kVG;
	double delta;
	HashMap<String, Integer> deltaValidGroup;
	List<Entry<String, Point>> nearestNeighborList;
	LinkedList<Entry<String, Point>> rspQdList;
	boolean flag;
	int LSP;
	int keywordRelevance;
	HashMap< HashMap<String,Integer>,Double> validGroups;
	int numberOfVGGeneration;
	int numberOfVGRSCalculation;
	double averageRankingScoreOfkVG;
	
	public BCK() {
		this.subtreeInvertedIndex = new InvertedIndex();
		this.subTree = new SubTree();
		this.vertexInvertedIndex = new InvertedIndex();
		this.geoCoordinates = new GeoCoordinates();
		this.geoIndex = new GeoIndex();
		this.kVG = new HashMap<HashMap<String,Integer>,Double>();
		this.validGroups = 
				new HashMap< HashMap<String,Integer>,Double>();
	}
	
	public void initialize(String knowledgeBaseName,
			int maxDepthBound) {
		this.averageRankingScoreOfkVG = 0;
		this.numberOfVGGeneration = 0;
		this.spatialDistance = -1;
		this.rankingScore = -1;
		this.kVG = new HashMap<HashMap<String,Integer>,Double>();
		this.delta = 0;
		this.deltaValidGroup = new HashMap<String, Integer>();
		this.nearestNeighborList = 
				new ArrayList<Entry<String, Point>>();
		this.rspQdList =
				new LinkedList<Entry<String, Point>>();
		this.flag = false;
		this.LSP = Integer.MAX_VALUE;
		this.keywordRelevance = 1;
		this.validGroups = 
				new HashMap< HashMap<String,Integer>,Double>();
		
		if (!this.subTree.subTreeHash.isEmpty()) {
			this.subTree.subTreeHash.clear();
		}
		
		String subtreeFileName = "";
		
		if (knowledgeBaseName.equals("YAGO")) {
			subtreeFileName = "dataset/YagoData/yagoSubTree"
					+ maxDepthBound + ".txt";
		}else if (knowledgeBaseName.equals("DBpedia")) {
			subtreeFileName = 
					"dataset/DBpediaData/dbpediaSubTree"
					+ maxDepthBound + ".txt";
		}
		
		//System.out.println("reading sub-tree...");
		this.subTree.readSubTreeFromTxt(subtreeFileName);
	}
	
	public HashMap<HashMap<String,Integer>,Double> 
			findTopkValidGroup(
					Point queryLocation,
					LinkedList<String> queryKeywords, 
					int k,
					String knowledBaseName,
					int maxDepthBound) {
		
//		long startTime = System.currentTimeMillis();

		initialize(knowledBaseName, maxDepthBound);
		
//		long finishTime = System.currentTimeMillis();
//        long elapsedTime = 
//        		finishTime - startTime;
//		System.out.println("Initialization time is..."
//				+ elapsedTime + " ms");
		
		long startBCKTime = System.currentTimeMillis();
		//pruning rule 1
		//System.out.println("pruning irrelevant sub-trees...");
		unrelevantSubtreePruning(queryKeywords);
		
		//initialize RSP_qd
		SubTree RSP = this.subTree;
		int sizeOfRSP = RSP.subTreeHash.size();
		/*System.out.println("size of RSP is: "
				+ sizeOfRSP);*/
		
		//get NNs w.r.t the query location (to be optimized)
		//System.out.println("get NNs w.r.t the query location...");
		Observable<Entry<String, Point>> NN =
				this.geoIndex.rTree.nearest(queryLocation, 
						Double.MAX_VALUE,
						Integer.MAX_VALUE);
		
		//pruning rule 2
		this.nearestNeighborList = 
				NN.filter(entry-> RSP.subTreeHash.containsKey(
						entry.value()))
				.toList().toBlocking().single();
		
		/*System.out.println("size Of RSP is ..."
				+ this.nearestNeighborList.size());*/
		
		this.rspQdList.addLast(this.nearestNeighborList.get(0));
		this.nearestNeighborList.remove(0);
		
		this.rspQdList.addLast(this.nearestNeighborList.get(0));
		this.nearestNeighborList.remove(0);
		
		this.rspQdList.addLast(this.nearestNeighborList.get(0));
		this.nearestNeighborList.remove(0);
		
		//System.out.println("Start while loop...");
		while(!nearestNeighborList.isEmpty()
				&& this.rspQdList.size()<200) {
			/*if (this.rspQdList.size() % 1000 == 0) {
				System.out.println("Currently processing line is ..."
						+ this.rspQdList.size());
			}*/
			
			/*System.out.println("Currently processing line is ..."
					+ this.rspQdList.size());*/
			
			Entry<String, Point> rsp_qd =
					this.rspQdList.getLast();
			
			HashMap<Entry<String, Point>,Integer> RSP_pi =
					new HashMap<Entry<String, Point>,Integer>();
			
			//initialize RSP_qd using rspQdList (without rsp_qd) 
			for (int i=0; i<this.rspQdList.size()-1; i++) {
				RSP_pi.put(this.rspQdList.get(i), 0);
			}
			
			HashMap<Entry<String, Point>,Integer> RSP_pj =
					new HashMap<Entry<String, Point>,Integer>();
			//inner loop
			RSP_pj.putAll(RSP_pi);
			
			for (Entry<String, Point> rsp_pi : RSP_pi.keySet()) {
				RSP_pj.remove(rsp_pi);
				
				for (Entry<String, Point> rsp_pj : RSP_pj.keySet()) {
					HashMap<Entry<String, Point>,Integer> RSP_vg =
							new HashMap<Entry<String, Point>,Integer>();
					
					//initialize RSP_vg
					RSP_vg.putAll(RSP_pj);
					RSP_vg.remove(rsp_pj);
					
					//pruning rule 3
					/*System.out.println("pairwise distance bound "
							+ "based pruning...");*/
					RSP_vg = pairwiseDistanceBoundBasedPruning(
							rsp_pi, rsp_pj, RSP_vg);
					
					HashMap< HashMap<String,Integer>,
						Double> newValidGroups = 
						new HashMap< HashMap<String,Integer>,
						Double>();
					
					//compose valid groups
					//System.out.println("compose new valid groups...");
					newValidGroups = composeValidGroup(
							rsp_qd, rsp_pi, rsp_pj,
							RSP_vg, queryKeywords);
					numberOfVGGeneration++;
					
					//calculate ranking score
					//System.out.println("calculate ranking socre...");
					if (!newValidGroups.isEmpty()) {
						newValidGroups = calculateRankingScore(
								newValidGroups, queryLocation, 
								queryKeywords, rsp_qd, rsp_pi, rsp_pj);
					}
					
					//maintain top-k valid groups
					if (!newValidGroups.isEmpty()) {
						//System.out.println("maintain top-k vgs...");
						maintainTopkValidGroups(k, 
								newValidGroups);
					}
				}
			}
			
			/*if (this.kVG.size() == k) {
				break;
			}*/
			
			moveToNextRspQd();
			//this.rspQdList.addLast(this.nearestNeighborList.get(0));
			//this.nearestNeighborList.remove(0);
		}
		
		long finishBCKTime = System.currentTimeMillis();
	    long bckExecutionTime = 
	    		finishBCKTime - startBCKTime;
	    System.out.println("value of k is: " + k);
		System.out.println("BCK elapsed time is: "
				+ bckExecutionTime + " ms.");
		System.out.println("size of RSP is: "
				+ sizeOfRSP);
		
		return this.kVG;
	}
	
	public void moveToNextRspQd() {
		this.rspQdList.addLast(this.nearestNeighborList.get(0));

		this.nearestNeighborList.remove(0);
	}

	public double calculateAverageRankingScoreOfkVG() {
		
		this.kVG.forEach((vg,score)->{
			averageRankingScoreOfkVG += score;
		});
		
		return averageRankingScoreOfkVG/this.kVG.size();
	}

	public void readDataFromYago(
			int maxDepthBound) {
		//input file paths
		String folderName = "dataset/YagoData/";
		String subtreeFileName = folderName +
				"yagoSubTree" + maxDepthBound + ".txt";
		String subtreeInvertedIndexFileName = folderName +
				"yagoSubTreeInvertedIndex" 
				+ maxDepthBound + ".txt";
		String vertexInvertedIndexFileName = folderName +
				"yagoVertexInvertedIndex" 
				+ maxDepthBound + ".txt";
		String geoCoordinatesFileName = folderName +
				"yagoGeoCoordinates" + ".txt";
				
		//reading input files
		System.out.println("reading sub-tree...");
		this.subTree.readSubTreeFromTxt(subtreeFileName);
		
		System.out.println("reading sub-tree inverted index...");
		this.subtreeInvertedIndex.readFromTxt(
				subtreeInvertedIndexFileName);
		
		System.out.println("reading vertex inverted index...");
		this.vertexInvertedIndex.readFromTxt(
				vertexInvertedIndexFileName);
		
		System.out.println("reading geo-coordinates...");
		this.geoCoordinates.readFromTxt(geoCoordinatesFileName);
		
		System.out.println("creating r*-tree...");
		this.geoIndex.createRTree(this.geoCoordinates);
	}
	
	public void readDataFromDBpedia(
			int maxDepthBound) {
		//input file paths
		String folderName = "dataset/DBpediaData/";
		String subtreeFileName = folderName +
				"dbpediaSubTree" + maxDepthBound + ".txt";
		String subtreeInvertedIndexFileName = folderName +
				"dbpediaSubTreeInvertedIndex"
				+ maxDepthBound + ".txt";
		/*String vertexInvertedIndexFileName = folderName +
				"dbpediaVertexInvertedIndex" 
				+ maxDepthBound + ".txt";*/
		String geoCoordinatesFileName = folderName +
				"dbpediaGeoCoordinates" + ".txt";
				
		//reading input files
		System.out.println("reading sub-tree...");
		this.subTree.readSubTreeFromTxt(subtreeFileName);
		
		System.out.println("reading sub-tree inverted index...");
		this.subtreeInvertedIndex.readFromTxt(
				subtreeInvertedIndexFileName);
		
		/*System.out.println("reading vertex inverted index...");
		this.vertexInvertedIndex.readFromTxt(
				vertexInvertedIndexFileName);*/
		
		System.out.println("reading geo-coordinates...");
		this.geoCoordinates.readFromTxt(geoCoordinatesFileName);
		
		System.out.println("creating r*-tree...");
		this.geoIndex.createRTree(this.geoCoordinates);
	}
	
	public void unrelevantSubtreePruning(
			LinkedList<String> queryKeywords) {
		HashMap<String, Integer> removalHash =
				new HashMap<String, Integer>();
		
		//prune sub-trees based on pruning rule 1
		this.subTree.subTreeHash.forEach((root,vertexHash)->{
			
			//if the sub-tree does not cover any query keywords
			flag=false;
				
			for(int i=0; i<queryKeywords.size(); i++) {
				String term = queryKeywords.get(i);
					
				if (this.subtreeInvertedIndex.termVertexHash.get(
						term).containsKey(root)) {
					flag=true;
					break;
				}
			}
				
			if (flag==false) {
				removalHash.put(root, 0);
			}
		});
		
		removalHash.forEach((root,integer)->{
			this.subTree.subTreeHash.remove(root);
		});
	}
	
	public void nearestNeighborListPruning(SubTree RSP) {
		
		for(int i=0; i<this.nearestNeighborList.size(); i++) {
			if (!RSP.subTreeHash.containsKey(
					this.nearestNeighborList.get(i).value())) {
				this.nearestNeighborList.remove(i);
				i--;
			}
		}
	}
	
	public double euclideanDistance(
			Point pi, Point pj) {
		double distance = 
			Math.sqrt(
				Math.pow(pi.x() - pj.x(), 2)
				+ Math.pow(pi.y() - pj.y(), 2)
			);
		
		return distance;
	}
	
	public HashMap<Entry<String, Point>,Integer> 
			pairwiseDistanceBoundBasedPruning(
					Entry<String, Point> rsp_pi,
					Entry<String, Point> rsp_pj,
					HashMap<Entry<String, Point>,Integer> RSP_vg) {
		
		HashMap<Entry<String, Point>,Integer> result = 
				new HashMap<Entry<String, Point>,Integer>();
		
		double pairwiseDiatance = 
				euclideanDistance(rsp_pi.geometry(),
						rsp_pj.geometry());
		
		for(Entry<String, Point> currentEntry : RSP_vg.keySet()) {
			double distanceToPi = 
					euclideanDistance(currentEntry.geometry(),
							rsp_pi.geometry());
			double distanceToPj =
					euclideanDistance(currentEntry.geometry(),
							rsp_pj.geometry());
			
			if (distanceToPi>pairwiseDiatance
					|| distanceToPj>pairwiseDiatance) {
				RSP_vg.replace(currentEntry, 1);
			}
		}
		
		for (Map.Entry<Entry<String, Point>, Integer> currentEntry :
				RSP_vg.entrySet()) {
			if (currentEntry.getValue() == 0) {
				result.put(currentEntry.getKey(), 0);
			}
		}
		
		return result;
	}
	
	public HashMap<LinkedList<String>,Integer> 
		generateRootsToCoverKeyword(
			int index, LinkedList<String> inputList,
			HashMap<LinkedList<String>,Integer> result, 
			LinkedList<String> uncoveredKeywords, 
			HashMap<String, LinkedList<String>> unKwRootHash) {
		
		HashMap<LinkedList<String>,Integer> temp = result;
		
		if(index<uncoveredKeywords.size())
		{
			for(int i=0; i<unKwRootHash.get(
					uncoveredKeywords.get(index)).size(); i++)
			{
				LinkedList<String> tempList = 
						new LinkedList<String>(); 
				tempList.addAll(inputList);
				tempList.add(unKwRootHash.get(
						uncoveredKeywords.get(index)).get(i));
				temp = generateRootsToCoverKeyword(
						index+1, tempList, temp, 
						uncoveredKeywords, unKwRootHash);
				
				if(tempList.size()==uncoveredKeywords.size())
				{
					temp.put(tempList, 0);
				}
			}
		}
		
		return temp;
	}
	
	public HashMap<HashMap<String,Integer>, Double> 
			composeValidGroup(
					Entry<String, Point> rsp_qd,
					Entry<String, Point> rsp_pi,
					Entry<String, Point> rsp_pj,
					HashMap<Entry<String, Point>, Integer> RSP_vg,
					LinkedList<String> queryKeywords) {
		
		HashMap<HashMap<String,Integer>, Double> generatedValidGroups =
				new HashMap<HashMap<String,Integer>, Double>();
		
		HashMap<String, Integer> group =
				new HashMap<String, Integer>();
		//add rsp_qd,rsp_pi, and rsp_pj
		group.put(rsp_qd.value(), 0);
		group.put(rsp_pi.value(), 0);
		group.put(rsp_pj.value(), 0);
		
		LinkedList<String> uncoveredKeywords = 
				new LinkedList<String>();
		
		//add uncovered keywords
		for(int i=0; i<queryKeywords.size(); i++) {
			String term = queryKeywords.get(i);
			if (!this.subtreeInvertedIndex.termVertexHash.get(
					term).containsKey(rsp_qd.value())
				&& !this.subtreeInvertedIndex.termVertexHash.get(
						term).containsKey(rsp_pi.value())
				&& !this.subtreeInvertedIndex.termVertexHash.get(
						term).containsKey(rsp_pj.value())) {
				
				uncoveredKeywords.add(term);
			}
		}
		
		//uncovered keyword root hash
		HashMap<String, LinkedList<String>> unKwRootHash =
				new HashMap<String, LinkedList<String>>();
		
		HashMap<LinkedList<String>,Integer> neededRoots = 
				new HashMap<LinkedList<String>,Integer>();
		
		HashMap<HashMap<String,Integer>, Double> newValidGroups =
				new HashMap<HashMap<String,Integer>, Double>();
		
		//if rsp_qd,rsp_pi, and rsp_pj cover all the query keywords
		if (uncoveredKeywords.isEmpty()) {
			generatedValidGroups.put(group, Double.MAX_VALUE);
			
			generatedValidGroups.forEach((key,value)->{
				if (!this.validGroups.containsKey(key)) {
					this.validGroups.put(key, value);
					newValidGroups.put(key, value);
				}
			});
		} else {
			//fill uncovered keyword root hash
			if (RSP_vg.isEmpty()) {
				return newValidGroups;
			}
			
			for(int i=0; i<uncoveredKeywords.size(); i++) {
				String term = uncoveredKeywords.get(i);
				
				//add term to uncovered keyword root hash
				unKwRootHash.put(term, 
						new LinkedList<String>());
				
				for (Entry<String, Point> rsp_vg : RSP_vg.keySet()) {
					if (this.subtreeInvertedIndex.termVertexHash.get(
							term).containsKey(rsp_vg.value())) {
						unKwRootHash.get(term).add(rsp_vg.value());
					}
				}
			}
			
			//check whether there exist a valid group
			this.flag = true;
			unKwRootHash.forEach((term,rootList)->{
				if(rootList.isEmpty()) {
					this.flag = false;
				}
			});
			
			//if there exists valid groups
			//generate valid groups with unKwRootHash
			if (this.flag) {
				LinkedList<String> inputList =
					new LinkedList<String>();
				HashMap<LinkedList<String>,Integer> result =
					new HashMap<LinkedList<String>,Integer>();
				
				neededRoots = generateRootsToCoverKeyword(
						0, inputList, result,
						uncoveredKeywords, unKwRootHash);
				
				neededRoots.forEach((list,integer)->{
					HashMap<String, Integer> groupCopy = group;
					
					list.forEach(root->{
						groupCopy.put(root, 0);
					});
					
					generatedValidGroups.put(groupCopy, Double.MAX_VALUE);
				});
				
				generatedValidGroups.forEach((key,value)->{
					if (!this.validGroups.containsKey(key)) {
						this.validGroups.put(key, value);
						newValidGroups.put(key, value);
					}
				});
			}
		}
		
		return newValidGroups;
	}
	
	public int calculateLSP(String keyword,
			HashMap<String,Integer> validGroup) {
		LSP = Integer.MAX_VALUE;
		
		validGroup.forEach((root,integer)->{
			if (this.subtreeInvertedIndex.termVertexHash.
					get(keyword).containsKey(root)) {
				int temp = this.subtreeInvertedIndex.termVertexHash.
						get(keyword).get(root);
				
				if (temp < LSP) {
					LSP = temp;
				}
			}
		});
		
		if (LSP==Integer.MAX_VALUE) {
			LSP=-1;
		}
		
		return LSP;
	}
	
	public int calculateKeywordRelevance(
			HashMap<String,Integer> validGroup,
			LinkedList<String> queryKeywords) {
		int keywordRelevance = 1;
		
		for(int i=0; i<queryKeywords.size(); i++) {
			String keyword = queryKeywords.get(i);
			
			int currentLSP = calculateLSP(keyword, validGroup);
			if (currentLSP != -1) {
				keywordRelevance += currentLSP;
			}
		}
		
		return keywordRelevance;
	}
	
	public double calculateSpatialDistance(
			HashMap<String,Integer> validGroup,
			Point queryLocation, Entry<String, Point> rsp_qd,
			Entry<String, Point> rsp_pi,
			Entry<String, Point> rsp_pj) {
		double spatialDiatance=0;
		
		double queryDistance = euclideanDistance(
				queryLocation, rsp_qd.geometry());
		
		double pairwiseDistance = euclideanDistance(
				rsp_pi.geometry(), rsp_pj.geometry());
		
		int cardinality = validGroup.size()-1;
		
		spatialDiatance = queryDistance + 
				(cardinality*pairwiseDistance);
		
		return spatialDiatance;
	}
	
	public HashMap<HashMap<String,Integer>,Double> 
			calculateRankingScore(
					HashMap<HashMap<String,Integer>,Double> validGroups,
					Point queryLocation, LinkedList<String> queryKeywords,
					Entry<String, Point> rsp_qd,
					Entry<String, Point> rsp_pi,
					Entry<String, Point> rsp_pj) {
		
		validGroups.forEach((validGroup,score)->{
			spatialDistance = calculateSpatialDistance(
					validGroup, queryLocation, rsp_qd, rsp_pi, rsp_pj);
			keywordRelevance = calculateKeywordRelevance(
					validGroup, queryKeywords);
			rankingScore = keywordRelevance*spatialDistance;
			validGroups.put(validGroup, rankingScore);
		});
		
		return validGroups;
	}
	
	public void maintainTopkValidGroups(int k,
			HashMap<HashMap<String,Integer>,Double> validGroups) {
		validGroups.forEach((validGroup,score)->{
			if (this.kVG.size()<k) {
				this.kVG.put(validGroup, score);
				
				//if kVG is full, calculate delta and deltaValidGroup
				if (this.kVG.size()==k) {
					delta = 0;
					this.deltaValidGroup = new HashMap<String,Integer>();
					
					this.kVG.forEach((key,value)->{
						if (delta<value) {
							delta = value;
							this.deltaValidGroup = key;
						}
					});
				}
			} else if (delta>score) {
				this.kVG.remove(this.deltaValidGroup);
				this.kVG.put(validGroup, score);
				
				//update delta and deltaValidGroup
				delta = 0;
				this.deltaValidGroup = new HashMap<String,Integer>();
				
				this.kVG.forEach((key,value)->{
					if (delta<value) {
						delta = value;
						this.deltaValidGroup = key;
					}
				});
			}
		});
	}
}
