package edu.yonsei.icl.coskqkb;

public class GeoIndexTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*String readFileName =
				"dataset/DBpediaData/dbpediaGeoCoordinates.txt";
		String writeFileName =
				"dataset/DBpediaData/dbpediaGeoIndex.txt";
		String visualizationFileName =
				"dataset/DBpediaData/dbpediaRTreeVisualization.png";*/
		
		String readFileName =
				"dataset/YagoData/yagoGeoCoordinates.txt";
		String writeFileName =
				"dataset/YagoData/yagoGeoIndex.txt";
		String visualizationFileName =
				"dataset/YagoData/yagoRTreeVisualization.png";
		
		long startTime = System.currentTimeMillis();
		
		GeoCoordinates geoCoordinates = new GeoCoordinates();
		System.out.println("Start reading geocoordinates..."
				+ readFileName);
		geoCoordinates.readFromTxt(readFileName);
		
		GeoIndex geoIndex = new GeoIndex();
		System.out.println("Start creating R*-tree");
		geoIndex.createRTree(geoCoordinates);
		
		long finishTime = System.currentTimeMillis();
        long elapsedTime = 
        		finishTime - startTime;
		System.out.println("GeoIndex generation time is..."
				+ elapsedTime + " ms");
		
		System.out.println("Start write R*-tree to..."
				+ writeFileName);
		geoIndex.writeToTxt(writeFileName);
		
		geoIndex.rTree.visualize(1000, 1000).save(
				visualizationFileName);
		
		/*System.out.println("Start writing R*-tree to txt..."
				+ writeFileName);
		geoIndex.writeToTxt(writeFileName);
		System.out.println("Finish writing R*-tree to txt..."
				+ writeFileName);*/
	}
}
