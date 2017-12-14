package edu.yonsei.icl.coskqkb;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class QueryGeneratorTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String knowledgeBaseName = "DBpedia";
		
		QueryGenerator queryGenerator = new QueryGenerator();
		
		//set parameters
		Point location = Geometries.point(-80, 40);
		double maxDiatance = 30;
		int numberOfNN = 1500;
		int numberOfKeywords = 50;
		int valueOfK = 3;
		
		queryGenerator.generateQuery(
				location, maxDiatance, numberOfNN, 
				numberOfKeywords, valueOfK,
				knowledgeBaseName);
		
		System.out.println("Query keywords are..."
				+ queryGenerator.queryKeywords.toString());
	}

}
