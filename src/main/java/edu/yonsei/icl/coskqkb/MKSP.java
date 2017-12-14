package edu.yonsei.icl.coskqkb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.jena.sparql.function.library.leviathan.root;
import org.apache.jena.tdb.store.Hash;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.fbs.generated.Geometry_;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import rx.Observable;

public class MKSP {
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
	int count = 0;
	int LSP;
	int keywordRelevance;
	HashMap< HashMap<String,Integer>,Double> validGroups;
	int numberOfVGGeneration;
	double averageRankingScoreOfkVG;
	
	public MKSP() {
		this.subtreeInvertedIndex = new InvertedIndex();
		this.subTree = new SubTree();
		this.vertexInvertedIndex = new InvertedIndex();
		this.geoCoordinates = new GeoCoordinates();
		this.geoIndex = new GeoIndex();
		this.kVG = new HashMap<HashMap<String,Integer>,Double>();
		this.validGroups = 
				new HashMap< HashMap<String,Integer>,Double>();
	}
	
	public void initialize(String knowledgeBaseName) {
		this.averageRankingScoreOfkVG = 0;
		this.numberOfVGGeneration = 0;
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
		
		String subtreeFileName = "";
		
		if (knowledgeBaseName.equals("YAGO")) {
			subtreeFileName = "dataset/YagoData/yagoSubTree.txt";
		}else if (knowledgeBaseName.equals("DBpedia")) {
			subtreeFileName = 
					"dataset/DBpediaData/dbpediaSubTree.txt";
		}
		
		//System.out.println("reading sub-tree...");
		this.subTree.readSubTreeFromTxt(subtreeFileName);
	}
	
	public HashMap<HashMap<String,Integer>,Double> 
			findTopkValidGroup(Point queryLocation,
					LinkedList<String> queryKeywords, 
					int k,
					String knowledBaseName) {
		
		initialize(knowledBaseName);
		
		long startBCKTime = System.currentTimeMillis();
		//pruning rule 1
		//System.out.println("pruning unrelevant sub-trees...");
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
		
		//generate k valid groups
		for (int indexOfK=0; indexOfK<k; indexOfK++) {
			LinkedList<Entry<String, Point>> rootList = 
					findOneValidGroup(queryKeywords.size()-1,
							queryKeywords);
			
			if (rootList==null) {
				break;
			}
			
			HashMap<String,Integer> validGroup =
					new HashMap<String,Integer>();
			
			for(int i=0; i<rootList.size(); i++) {
				validGroup.put(rootList.get(i).value(), 0);
			}
			
			double distanceQd = 0;
			double distancePd = 0;
			
			for(int i=0; i<rootList.size(); i++) {
				double distance = euclideanDistance(queryLocation, 
						rootList.get(i).geometry());
				if (distanceQd<distance) {
					distanceQd = distance;
				}
			}
			
			for (int i=0; i<rootList.size(); i++) {
				for (int j=i+1; j<rootList.size(); j++) {
					double distance = euclideanDistance(rootList.get(i).geometry(),
							rootList.get(j).geometry());
							
					if (distancePd<distance) {
						distancePd=distance;
					}
				}
			}
			
			double spatialDistance = 
					distanceQd + (validGroup.size()-1)*distancePd;
			
			double keywordRelevance = 
					calculateKeywordRelevance(
							validGroup, queryKeywords);
			
			double rankingScore = keywordRelevance*spatialDistance;
			
			this.kVG.put(validGroup, rankingScore);
			
			Entry<String, Point> removalEntry = rootList.get(0);
			this.nearestNeighborList.remove(removalEntry);
		}
		
		//finish processing...
		long finishBCKTime = System.currentTimeMillis();
	    long bckExecutionTime = 
	    		finishBCKTime - startBCKTime;
	    //System.out.println("value of k is: " + k);
		System.out.println("m-kSP elapsed time is: "
				+ bckExecutionTime + " ms.");
		System.out.println("size of RSP is: "
				+ sizeOfRSP);
		
		return this.kVG;
	}
	
	public LinkedList<Entry<String, Point>> findOneValidGroup(
			int numOfIter, LinkedList<String> queryKeywords) {
		LinkedList<Entry<String, Point>> result = 
				new LinkedList<Entry<String, Point>>();
		
		if(numOfIter < (queryKeywords.size()/2))
		{
			return null;
		}
		
		HashMap<Integer, LinkedList<String>>
			partOfKeywordsHash = selectSubkeywords(
					numOfIter, queryKeywords);
		HashMap<Entry<String, Point>, LinkedList<String>> 
		rootUncoverdKeywords = findSP(
				partOfKeywordsHash, queryKeywords);
		
		if(rootUncoverdKeywords.isEmpty()) {
			result = findOneValidGroup(numOfIter-1, queryKeywords);
		}
		
		else
		{
			for(java.util.Map.Entry<Entry<String, Point>,
					LinkedList<String>> entry : rootUncoverdKeywords.entrySet()) {
			    Entry<String, Point> key = entry.getKey();
			    LinkedList<String> value = entry.getValue();
			    result.add(key);
		    	
			    if(value.isEmpty())
			    {
			    	return result;
			    }
			    else {
			    	result.addAll(findOneValidGroup(
			    			value.size(), value));
			    }
			}	
			return result;
		}
		return null;
	}

	public HashMap<Entry<String, Point>, LinkedList<String>> findSP(
			HashMap<Integer, LinkedList<String>> partOfKeywordsHash, 
			LinkedList<String> querykeywords) {
		
		HashMap<Entry<String, Point>, LinkedList<String>> rootUnCoveredKeywords = new HashMap<Entry<String, Point>, LinkedList<String>>();;
		
		for (int i = 0; i < partOfKeywordsHash.size(); i++) {
			//get a keyword list
			LinkedList<String> partOfKeywordsList =
					partOfKeywordsHash.get(i);
			
			//generate term vertex hash
			HashMap<String, HashMap<String,Integer>> partOfTermVertexHash =
					new HashMap<String, HashMap<String,Integer>>();
			for(int j=0; j<partOfKeywordsList.size(); j++) {
				String keyword = partOfKeywordsList.get(j);
				if (this.subtreeInvertedIndex.termVertexHash.containsKey(
						keyword)) {
					partOfTermVertexHash.put(keyword, 
							this.subtreeInvertedIndex.termVertexHash.get(
									keyword));
				}
			}
			Entry<String, Point> entry = null;
			LinkedList<String> unCoveredKeywords = null;
			
			for(int k=0; k < this.nearestNeighborList.size(); k++) {
				entry = this.nearestNeighborList.get(k);
				String root = entry.value();
				count = 0;
				partOfTermVertexHash.forEach((term, vertexHash)->{
					if(vertexHash.containsKey(root)) {
						count++;
					}
				});
				if(count == partOfTermVertexHash.size()) {
					break;
				}
			}
			
			unCoveredKeywords = getUnCoverdKeywords(querykeywords, partOfKeywordsList); 
						
			rootUnCoveredKeywords.put(entry, unCoveredKeywords);
		}
		
		return rootUnCoveredKeywords;
	}
	
	public LinkedList<String> getUnCoverdKeywords(LinkedList<String> querykeywords, LinkedList<String> partOfKeywordsList) {
		
		LinkedList<String> unCoveredKeywords = new LinkedList<String>();
		for(int i=0; i<querykeywords.size();i++) {
			String keyword = querykeywords.get(i);
			if(!partOfKeywordsList.contains(keyword)) {
				unCoveredKeywords.add(keyword);
			}
		}
		return unCoveredKeywords;
	}
	
	public HashMap<Integer,LinkedList<String>> 
		selectSubkeywords(
				int r, LinkedList<String> querykeywords)
    {
       LinkedList<String> tempResult;
       HashMap<Integer,LinkedList<String>> result = new HashMap<Integer,LinkedList<String>>(); 
       int length = querykeywords.size();
       
       if(r>length){
          System.out.println("Invalid input, r > n");
          return null;
       }
       
       int combination[] = new int[r];
       int i = 0;
       int index = 0;
       int resultIndex = 0;         //index number
       
       while(i>=0)
       {
          if(index <= (length + (i-r)))
          {
             combination[i] = index;
             if(i == r-1)
             {
                tempResult = new LinkedList<String>();
                for(int j =0; j<combination.length; j++)
                {
                   tempResult.add(querykeywords.get(combination[j]));
                }
                result.put(resultIndex, tempResult);
                resultIndex++;
                index++;
             }
             else
             {
                index = combination[i]+1;
                i++;
             }
          }
          else
          {
             i--;
             if(i>0)
                index = combination[i]+1;
             else
                index = combination[0]+1;
          }
       }
       return result;
       
    }
	
	public double calculateAverageRankingScoreOfkVG() {
		
		this.kVG.forEach((vg,score)->{
			averageRankingScoreOfkVG += score;
		});
		
		return averageRankingScoreOfkVG/this.kVG.size();
	}

	public void readDataFromYago() {
		//input file paths
		String folderName = "dataset/YagoData/";
		String subtreeInvertedIndexFileName = folderName +
				"yagoSubTreeInvertedIndex" + ".txt";
		String subtreeFileName = folderName +
				"yagoSubTree" + ".txt";
		String vertexInvertedIndexFileName = folderName +
				"yagoVertexInvertedIndex" + ".txt";
		String geoCoordinatesFileName = folderName +
				"yagoGeoCoordinates" + ".txt";
				
		//reading input files
		System.out.println("reading sub-tree inverted index...");
		this.subtreeInvertedIndex.readFromTxt(
				subtreeInvertedIndexFileName);
		System.out.println("reading sub-tree...");
		this.subTree.readSubTreeFromTxt(subtreeFileName);
		System.out.println("reading vertex inverted index...");
		this.vertexInvertedIndex.readFromTxt(
				vertexInvertedIndexFileName);
		System.out.println("reading geo-coordinates...");
		this.geoCoordinates.readFromTxt(geoCoordinatesFileName);
		System.out.println("creating r*-tree...");
		this.geoIndex.createRTree(this.geoCoordinates);
	}
	
	public void readDataFromDBpedia() {
		//input file paths
		String folderName = "dataset/DBpediaData/";
		String subtreeInvertedIndexFileName = folderName +
				"dbpediaSubTreeInvertedIndex" + ".txt";
		String subtreeFileName = folderName +
				"dbpediaSubTree" + ".txt";
		String vertexInvertedIndexFileName = folderName +
				"dbpediaVertexInvertedIndex" + ".txt";
		String geoCoordinatesFileName = folderName +
				"dbpediaGeoCoordinates" + ".txt";
				
		//reading input files
		System.out.println("reading sub-tree inverted index...");
		this.subtreeInvertedIndex.readFromTxt(
				subtreeInvertedIndexFileName);
		System.out.println("reading sub-tree...");
		this.subTree.readSubTreeFromTxt(subtreeFileName);
		System.out.println("reading vertex inverted index...");
		this.vertexInvertedIndex.readFromTxt(
				vertexInvertedIndexFileName);
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
			composeValidGroup(
					Entry<String, Point> rsp_qd,
					Entry<String, Point> rsp_pi,
					Entry<String, Point> rsp_pj,
					List<Entry<String, Point>> RSP_vg,
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
