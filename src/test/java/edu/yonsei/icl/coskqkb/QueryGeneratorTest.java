package edu.yonsei.icl.coskqkb;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class QueryGeneratorTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String knowledgeBaseName = "DBpedia";
		int maxDepthBound = 3;
		
		QueryGenerator queryGenerator = new QueryGenerator();
		
		//set parameters
		Point location = Geometries.point(126.93694444444445,
				37.555277777777775);
		double maxDiatance = 0.035;
		int numberOfNN = 5000;
		int numberOfKeywords = 1000;
		int valueOfK = 10;
		
		queryGenerator.generateQuery(
				location, maxDiatance, numberOfNN, 
				numberOfKeywords, valueOfK,
				knowledgeBaseName, maxDepthBound);
		
		System.out.println("Query keywords are...");
		
		for(int i=0; i<queryGenerator.queryKeywords.size(); i++) {
			System.out.print(queryGenerator.queryKeywords.get(i));
			if(i%5==4) {
				System.out.println(";");
			} else {
				System.out.print(", ");
			}
		}
	}

}
