package edu.yonsei.icl.coskqkb;

public class GeoCoordinatesTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String readFileName = 
				"dataset/YagoData/yagoGraph.txt";
		String writeFileName = 
				"dataset/YagoData/yagoGeoCoordinates.txt";
		
		long startTime = System.currentTimeMillis();

		transformGraphToGeoCoordinates(readFileName, writeFileName);
		
		long finishTime = System.currentTimeMillis();
        long elapsedTime = 
        		finishTime - startTime;
		System.out.println("Geocoordinates generation time is..."
				+ elapsedTime + " ms");
		
		/*AdjacencyList adjacencyList = new AdjacencyList();
		adjacencyList.addFromTxt(readFileName);
		System.out.println("No. of vertices in the graph..."
				+ adjacencyList.getNumberOfStartVertices());
		
		GeoCoordinates geoCoordinates = new GeoCoordinates();
		geoCoordinates.createGeoCoordinates(adjacencyList);
		System.out.println("No. of vertices in geocoordinates..."
				+ geoCoordinates.getNumberOfPlaceVertices());*/
	}
	
	public static void transformGraphToGeoCoordinates(
			String readFileName, String writeFileName) {
		AdjacencyList adjacencyList = new AdjacencyList();
		adjacencyList.addFromTxt(readFileName);
		
		GeoCoordinates geoCoordinates = new GeoCoordinates();
		geoCoordinates.createGeoCoordinates(adjacencyList);
		geoCoordinates.writeToTxt(writeFileName);
	}
}
