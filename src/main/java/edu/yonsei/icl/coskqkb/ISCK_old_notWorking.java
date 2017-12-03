package edu.yonsei.icl.coskqkb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.fbs.generated.Geometry_;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import rx.Observable;

public class ISCK_old_notWorking {
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
	List<Entry<String, Point>> rspQdList;
	boolean flag;
	int LSP;
	int keywordRelevance;
	HashMap< HashMap<String,Integer>,Double> validGroups;
	int numberOfVGGeneration;
	double averageRankingScoreOfkVG;
	
	public ISCK_old_notWorking() {
		this.subtreeInvertedIndex = new InvertedIndex();
		this.subTree = new SubTree();
		this.vertexInvertedIndex = new InvertedIndex();
		this.geoCoordinates = new GeoCoordinates();
		this.geoIndex = new GeoIndex();
		this.kVG = new HashMap<HashMap<String,Integer>,Double>();
		this.validGroups = 
				new HashMap< HashMap<String,Integer>,Double>();
	}
	
	public void initialize() {
		this.numberOfVGGeneration = 0;
		this.averageRankingScoreOfkVG = 0;
		this.spatialDistance = -1;
		this.rankingScore = -1;
		this.kVG = new HashMap<HashMap<String,Integer>,Double>();
		this.delta = Double.MAX_VALUE;
		this.deltaValidGroup = new HashMap<String, Integer>();
		this.nearestNeighborList = 
				new ArrayList<Entry<String, Point>>();
		this.rspQdList =
				new ArrayList<Entry<String, Point>>();
		this.flag = false;
		this.LSP = Integer.MAX_VALUE;
		this.keywordRelevance = 1;
		this.validGroups = 
				new HashMap< HashMap<String,Integer>,Double>();
		
		if (!this.subTree.subTreeHash.isEmpty()) {
			this.subTree.subTreeHash.clear();
		}
		String folderName = "dataset/YagoData/";
		String subtreeFileName = folderName +
				"yagoSubTree" + ".txt";
		//System.out.println("reading sub-tree...");
		this.subTree.readSubTreeFromTxt(subtreeFileName);
	}
	
	public void readDataFromYago() {
		//input file paths
		String folderName = "dataset/YagoData/";
		String subtreeInvertedIndexFileName = folderName +
				"yagoSubTreeInvertedIndex" + ".txt";
		/*String subtreeFileName = folderName +
				"yagoSubTree" + ".txt";*/
		String vertexInvertedIndexFileName = folderName +
				"yagoVertexInvertedIndex" + ".txt";
		String geoCoordinatesFileName = folderName +
				"yagoGeoCoordinates" + ".txt";
				
		//reading input files
		//System.out.println("reading sub-tree inverted index...");
		this.subtreeInvertedIndex.readFromTxt(
				subtreeInvertedIndexFileName);
		/*System.out.println("reading sub-tree...");
		this.subTree.readSubTreeFromTxt(subtreeFileName);*/
		//System.out.println("reading vertex inverted index...");
		this.vertexInvertedIndex.readFromTxt(
				vertexInvertedIndexFileName);
		//System.out.println("reading geo-coordinates...");
		this.geoCoordinates.readFromTxt(geoCoordinatesFileName);
		//System.out.println("creating r*-tree...");
		this.geoIndex.createRTree(this.geoCoordinates);
	}

	public HashMap<HashMap<String,Integer>,Double> 
			findTopkValidGroup(Point queryLocation,
					LinkedList<String> queryKeywords, int k) {
		initialize();
		
		long startBCKTime = System.currentTimeMillis();
		//pruning rule 1
		System.out.println("pruning unrelevant sub-trees...");
		unrelevantSubtreePruning(queryKeywords);
		
		//initialize RSP_qd
		SubTree RSP = this.subTree;
		int sizeOfRSP = RSP.subTreeHash.size();
		System.out.println("size of RSP is: "
				+ sizeOfRSP);
		
		//get NNs w.r.t the query location (to be optimized)
		System.out.println("get NNs w.r.t the query location...");
		Observable<Entry<String, Point>> NN =
				this.geoIndex.rTree.nearest(queryLocation, 
						Double.MAX_VALUE,
						Integer.MAX_VALUE);
		//prune the nearest neighbor list w.r.t. RSP_qd
		//pruning rule 2
		this.nearestNeighborList = 
				NN.filter(entry-> RSP.subTreeHash.containsKey(
						entry.value()))
				.toList().toBlocking().single();
		
		System.out.println(this.nearestNeighborList.size());
		
		//move the first two NNs to rsp_qd list
		this.rspQdList = new ArrayList<Entry<String, Point>>();
		
		this.rspQdList.add(this.nearestNeighborList.get(0));
		this.nearestNeighborList.remove(0);
		
		this.rspQdList.add(this.nearestNeighborList.get(0));
		this.nearestNeighborList.remove(0);
		
		System.out.println("Start while loop...");
		while(!this.nearestNeighborList.isEmpty()) {
			//System.out.println(this.nearestNeighborList.size());
			
			Entry<String, Point> rsp_qd =
					this.rspQdList.get(
							this.rspQdList.size()-1);
			
			//lemma 2
			double distanceQd = euclideanDistance(
					rsp_qd.geometry(), queryLocation);
				
			double upperBoundForQDB = dynamicConstraintForQDB(
					rsp_qd, queryKeywords);
			if (distanceQd>upperBoundForQDB) {
				//System.out.println(nearestNeighborList.size());
				//this.nearestNeighborList.clear();
				break;
			}
			
			List<Entry<String, Point>> RSP_qd =
					this.rspQdList;
			
			for(int i=0; i<RSP_qd.size(); i++) {
				Entry<String, Point> rsp_pi =
						RSP_qd.get(i);
				
				for(int j=i+1; j<RSP_qd.size(); j++) {
					Entry<String, Point> rsp_pj =
							RSP_qd.get(j);
					
					//lemma 3
					double distancePd = euclideanDistance(
							rsp_pi.geometry(), 
							rsp_pj.geometry());
					double upperBoundForPDB = 
							dynamicConstraintForPDB(
							distancePd,	rsp_qd, rsp_pi,
							queryKeywords, queryLocation);
					if (distancePd<distanceQd
							|| distancePd>upperBoundForPDB) {
						continue;
					}
					
					//pruning rule 3
					System.out.println("pairwise distance bound "
							+ "based pruning...");
					List<Entry<String, Point>> RSP_vg = 
							pairwiseDistanceBoundBasedPruning(
							rsp_pi, rsp_pj, RSP_qd, queryLocation);
					
					HashMap< HashMap<String,Integer>,
						Double> newValidGroups = 
						new HashMap< HashMap<String,Integer>,
						Double>();
					
					//compose valid groups
					//lemma 4
					System.out.println("compose new valid groups...");
					newValidGroups = composeValidGroupWithPhi(
							rsp_qd, rsp_pi, rsp_pj,
							RSP_vg, queryKeywords,
							queryLocation);
					numberOfVGGeneration++;
					System.out.println(numberOfVGGeneration);
					
					//calculate ranking score
					System.out.println("calculate ranking socre...");
					newValidGroups = calculateRankingScore(
							newValidGroups, queryLocation, 
							queryKeywords, rsp_qd, rsp_pi,
							rsp_pj);
					
					//maintain top-k valid groups
					System.out.println("maintain top-k vgs...");
					maintainTopkValidGroups(k, newValidGroups);
				}
			}
			
			moveToNextRspQd();
		}
		
		long finishBCKTime = System.currentTimeMillis();
	    long isckExecutionTime = 
	    		finishBCKTime - startBCKTime;
	    System.out.println("value of k is: " + k);
		System.out.println("ISCK elapsed time is: "
				+ isckExecutionTime + " ms.");
		System.out.println("size of RSP is: "
				+ sizeOfRSP);
		
		return this.kVG;
	}

	public double calculateAverageRankingScoreOfkVG() {
		
		this.kVG.forEach((vg,score)->{
			averageRankingScoreOfkVG += score;
		});
		
		return averageRankingScoreOfkVG/this.kVG.size();
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
	
	public void moveToNextRspQd() {
		this.rspQdList.add(this.nearestNeighborList.get(0));
		this.nearestNeighborList.remove(0);
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
	
	public List<Entry<String, Point>> 
			pairwiseDistanceBoundBasedPruning(
					Entry<String, Point> rsp_pi,
					Entry<String, Point> rsp_pj,
					List<Entry<String, Point>> RSP_qd,
					Point queryLocation) {
		double pairwiseDiatance = 
				euclideanDistance(rsp_pi.geometry(),
						rsp_pj.geometry());
		
		
		for(int i=0; i<RSP_qd.size(); i++) {
			Entry<String, Point> currentEntry =
					RSP_qd.get(i);
			double distanceToPi = 
					euclideanDistance(currentEntry.geometry(),
							rsp_pi.geometry());
			double distanceToPj =
					euclideanDistance(currentEntry.geometry(),
							rsp_pj.geometry());
			
			if (distanceToPi>pairwiseDiatance
					&& distanceToPj>pairwiseDiatance) {
				RSP_qd.remove(i);
				i--;
			}
		}
		
		return RSP_qd;
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
		composeValidGroupWithPhi(
					Entry<String, Point> rsp_qd,
					Entry<String, Point> rsp_pi,
					Entry<String, Point> rsp_pj,
					List<Entry<String, Point>> RSP_vg,
					LinkedList<String> queryKeywords,
					Point queryLocation) {
		
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
				&& this.subtreeInvertedIndex.termVertexHash.get(
						term).containsKey(rsp_pi.value())
				&& this.subtreeInvertedIndex.termVertexHash.get(
						term).containsKey(rsp_pj.value())) {
				uncoveredKeywords.add(term);
			}
		}
		
		
		//uncovered keyword root hash
		HashMap<String, LinkedList<String>> unKwRootHash =
				new HashMap<String, LinkedList<String>>();
		HashMap<LinkedList<String>,Integer> neededRoots = 
				new HashMap<LinkedList<String>,Integer>();
		
		//if rsp_qd,rsp_pi, and rsp_pj cover all the query keywords
		if (uncoveredKeywords.isEmpty()) {
			generatedValidGroups.put(group, Double.MAX_VALUE);
		} else {
			//fill uncovered keyword root hash
			for(int i=0; i<uncoveredKeywords.size(); i++) {
				String term = uncoveredKeywords.get(i);
				
				//add term to uncovered keyword root hash
				unKwRootHash.put(term, 
						new LinkedList<String>());
				
				RSP_vg.forEach(e->{
					//if a sub-tree covers the term,
					//add it to uncovered keyword root hash
					if (this.subtreeInvertedIndex.termVertexHash.get(
							term).containsKey(e.value())) {
						unKwRootHash.get(term).add(e.value());
					}
				});
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
			}
		}
		
		neededRoots.forEach((list,integer)->{
			HashMap<String, Integer> groupCopy = group;
			
			list.forEach(root->{
				groupCopy.put(root, 0);
			});
			
			generatedValidGroups.put(groupCopy, Double.MAX_VALUE);
		});
		
		HashMap<HashMap<String,Integer>, Double> newValidGroups =
				new HashMap<HashMap<String,Integer>, Double>();
		
		/*double upperBound = dynamicConstraintForCardinality(
				rsp_qd, rsp_pi, rsp_pj, queryKeywords, 
				queryLocation);*/
		
		generatedValidGroups.forEach((key,value)->{
			if (!this.validGroups.containsKey(key)) {
				this.validGroups.put(key, value);
				newValidGroups.put(key, value);
			}
		});
		
		return newValidGroups;
	}
	
	public int calculateLSP(String keyword,
			HashMap<String,Integer> validGroup) {
		LSP = Integer.MAX_VALUE;
		
		validGroup.forEach((root,integer)->{
			if (this.vertexInvertedIndex.termVertexHash.get(
					keyword).containsKey(root)) {
				LSP = 0;
			} else if (this.subtreeInvertedIndex.termVertexHash.
					get(keyword).containsKey(root)) {
				this.subTree.subTreeHash.get(root).forEach(
					(vertex,depth)->{
						if (this.vertexInvertedIndex.termVertexHash.
								get(keyword).containsKey(vertex)) {
							LSP = Math.min(LSP, depth);
						}	
					});
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
				
				delta = 0;
				this.deltaValidGroup = new HashMap<String,Integer>();
					
				this.kVG.forEach((key,value)->{
					if (delta<value) {
						delta = value;
						this.deltaValidGroup = key;
					}
				});
					
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
	
	public double calculateLSP(Entry<String, Point> rsp_qd,
			LinkedList<String> queryKeywords) {
		int rspQdLSP = 0;
		
		for(int i=0; i<queryKeywords.size(); i++) {
			String keyword = queryKeywords.get(i);
			
			HashMap<String, Integer> tempHash =
					new HashMap<String, Integer>();
			tempHash.put(rsp_qd.value(), 0);
			
			int tempInt = calculateLSP(keyword,tempHash);
			
			if (tempInt!=-1) {
				rspQdLSP+=tempInt;
			}
		}
		
		return rspQdLSP;
	}
	
	public double dynamicConstraintForQDB(
			Entry<String, Point> rsp_qd,
			LinkedList<String> queryKeywords) {
		
		/*if (this.rspQdLSP == -1) {
			this.rspQdLSP = calculateLSP(rsp_qd,queryKeywords);
		}*/
		double rspQdLSP = 0;
		
		return this.delta/(rspQdLSP+1);
	}
	
	public double dynamicConstraintForPDB(
			double distanceQd,
			Entry<String, Point> rsp_qd,
			Entry<String, Point> rsp_pi,
			LinkedList<String> queryKeywords,
			Point queryLocation) {

		/*if (this.rspQdLSP == -1) {
			this.rspQdLSP = calculateLSP(rsp_qd,queryKeywords);
		}*/
		double rspQdLSP = 0;
		
		/*if (this.rspPiLSP == -1) {
			this.rspPiLSP = calculateLSP(rsp_pi,queryKeywords);
		}*/
		double rspPiLSP = 0;
				
		double upperBound = 
				((delta/(rspQdLSP+rspPiLSP+1))
						-distanceQd)/2;
		
		return upperBound;
	}
	
	public double dynamicConstraintForCardinality(
			double distanceQd, double distancePd,
			Entry<String, Point> rsp_qd, 
			Entry<String, Point> rsp_pi, 
			Entry<String, Point> rsp_pj, 
			LinkedList<String> queryKeywords, 
			Point queryLocation) {
		
		double upperBound = 0;
		
		/*if (this.rspQdLSP == -1) {
			this.rspQdLSP = calculateLSP(rsp_qd, queryKeywords);
		}*/
		double rspQdLSP = 0;
		/*if (this.rspPiLSP == -1) {
			this.rspPiLSP = calculateLSP(rsp_pi, queryKeywords);
		}*/
		double rspPiLSP = 0;
		/*if (this.rspPjLSP == -1) {
			this.rspPjLSP = calculateLSP(rsp_pi, queryKeywords);
		}*/
		double rspPjLSP = 0;
		double kr = rspQdLSP + rspPiLSP 
						+ rspPjLSP + 1;
		
		upperBound = 
				(((this.delta/kr)-distanceQd)/
						distancePd)+1;
		
		return upperBound;
	}
}
