package edu.yonsei.icl.coskqkb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import rx.Observable;

public class QueryGenerator {
	InvertedIndex subtreeInvertedIndex;
	GeoCoordinates geoCoordinates;
	GeoIndex geoIndex;
	HashMap<String, Integer> nearestNeighborHash;
	HashMap<String, Integer> queryKeywordHash;
	LinkedList<String> queryKeywords;
	Point queryLocation;
	int k;
	int threshold;
	String thresholdKeyword;
	
	public QueryGenerator() {
		subtreeInvertedIndex = new InvertedIndex();
		geoCoordinates = new GeoCoordinates();
		geoIndex = new GeoIndex();
		nearestNeighborHash = new HashMap<String, Integer>();
		queryKeywordHash = new HashMap<String, Integer>();
		queryKeywords = new LinkedList<String>();
		queryLocation = Geometries.point(0, 0);
		k = 0;
		threshold = 0;
		thresholdKeyword = "";
	}
	
	public void readDataFromYago() {
		//input file paths
		String folderName = "dataset/YagoData/";
		/*String graphFileName = folderName +
				"yagoGraph" + ".txt";*/
		String subtreeInvertedIndexFileName = folderName +
				"yagoSubTreeInvertedIndex" + ".txt";
		/*String subtreeFileName = folderName +
				"yagoSubTree" + ".txt";*/
		/*String vertexInvertedIndexFileName = folderName +
				"yagoVertexInvertedIndex" + ".txt";*/
		String geoCoordinatesFileName = folderName +
				"yagoGeoCoordinates" + ".txt";
						
		//reading input files
		//this.adjacencyList.addFromTxt(graphFileName);
		this.subtreeInvertedIndex.readFromTxt(
				subtreeInvertedIndexFileName);
		//this.subTree.readSubTreeFromTxt(subtreeFileName);
		//this.vertexInvertedIndex.readFromTxt(vertexInvertedIndexFileName);
		this.geoCoordinates.readFromTxt(geoCoordinatesFileName);
		this.geoIndex.createRTree(geoCoordinates);
	}
	
	public void readDataFromDBpedia() {
		//input file paths
		String folderName = "dataset/DBpediaData/";
		/*String graphFileName = folderName +
				"yagoGraph" + ".txt";*/
		String subtreeInvertedIndexFileName = folderName +
				"dbpediaSubTreeInvertedIndex" + ".txt";
		/*String subtreeFileName = folderName +
				"yagoSubTree" + ".txt";*/
		/*String vertexInvertedIndexFileName = folderName +
				"yagoVertexInvertedIndex" + ".txt";*/
		String geoCoordinatesFileName = folderName +
				"dbpediaGeoCoordinates" + ".txt";
						
		//reading input files
		//this.adjacencyList.addFromTxt(graphFileName);
		this.subtreeInvertedIndex.readFromTxt(
				subtreeInvertedIndexFileName);
		//this.subTree.readSubTreeFromTxt(subtreeFileName);
		//this.vertexInvertedIndex.readFromTxt(vertexInvertedIndexFileName);
		this.geoCoordinates.readFromTxt(geoCoordinatesFileName);
		this.geoIndex.createRTree(geoCoordinates);
	}
	
	public void nearestNeighborSearch(Point location,
			double maxDiatance, int numberOfNN) {
		Observable<Entry<String, Point>> NN =
				this.geoIndex.rTree.nearest(
						location, maxDiatance, numberOfNN);
		
		List<Entry<String, Point>> nearestNeighborList = 
				NN.toList().toBlocking().single();
		
		for(int i=0; i<nearestNeighborList.size(); i++) {
			this.nearestNeighborHash.put(
					nearestNeighborList.get(i).value(), 0);
		}
	}
	
	public void generateQueryKeywords(int numberOfKeywords) {
		HashMap<String, Integer> termCountHash =
				new HashMap<String, Integer>();
		
		System.out.println("size of NN hash is..."
				+ this.nearestNeighborHash.size());
		
		//generate term count hash
		this.subtreeInvertedIndex.termVertexHash.forEach(
			(term,rootHash)->{
				this.nearestNeighborHash.forEach((root,integer)->{
					if (rootHash.containsKey(root)) {
						if (!termCountHash.containsKey(term)) {
							termCountHash.put(term, 1);
						}else {
							int count = termCountHash.get(term);
							count++;
							termCountHash.put(term, count);
						}
					}
				});
		});
		
		/*System.out.println("size of term count hash is..."
				+ termCountHash.size());*/
		
		//generate top-k keywords w.r.t count
		termCountHash.forEach((term,count)->{
			if (this.queryKeywordHash.size()<numberOfKeywords) {
				this.queryKeywordHash.put(term, count);
				
				//if queryKeywordHash is full,
				//calculate threshold and thresholdKeyword
				if (this.queryKeywordHash.size()==numberOfKeywords) {
					threshold = 0;
					thresholdKeyword = "";
					
					this.queryKeywordHash.forEach((key,value)->{
						if (threshold<value) {
							threshold = value;
							thresholdKeyword = key;
						}
					});
				}
			} else if (threshold>count) {
				this.queryKeywordHash.remove(thresholdKeyword);
				this.queryKeywordHash.put(term, count);
				
				//update threshold and thresholdKeyword
				threshold = 0;
				thresholdKeyword = "";
				
				this.queryKeywordHash.forEach((key,value)->{
					if (threshold<value) {
						threshold = value;
						thresholdKeyword = key;
					}
				});
			}
		});
		
		/*System.out.println("size of query keyword hash is..."
				+ this.queryKeywordHash.size());*/
		
		this.queryKeywordHash.forEach((keyword,count)->{
			this.queryKeywords.add(keyword);
		});
	}
		
	public void generateQuery(Point location,
			double maxDiatance, int numberOfNN, 
			int numberOfKeywords, int valueOfK,
			String knowledgeBaseName) {
		//read Yago data
		if (knowledgeBaseName.equals("YAGO")) {
			System.out.println("Start reading data from yago...");
			readDataFromYago();
		}
		
		//read Yago data
		if (knowledgeBaseName.equals("DBpedia")) {
			System.out.println("Start reading data from DBpedia...");
			readDataFromDBpedia();
		}		
		
		//get nearest neighbor hash
		System.out.println("Start searching for NNs...");
		nearestNeighborSearch(location, maxDiatance, numberOfNN);
		
		//generate query keywords
		System.out.println("Start generating query keywords...");
		generateQueryKeywords(numberOfKeywords);
		
		this.queryLocation = location;
		this.k = valueOfK;
	}
}
