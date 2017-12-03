package edu.yonsei.icl.coskqkb;

public class GeoIndexTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String readFileName =
				"dataset/YagoData/yagoGeoCoordinates.txt";
		String writeFileName =
				"dataset/YagoData/yagoGeoIndex.txt";
		String visualizationFileName =
				"dataset/YagoData/yagoRTreeVisualization.png";
		
		GeoCoordinates geoCoordinates = new GeoCoordinates();
		System.out.println("Start reading geocoordinates..."
				+ readFileName);
		geoCoordinates.readFromTxt(readFileName);
		System.out.println("Finish reading geocoordinates..."
				+ readFileName);
		
		GeoIndex geoIndex = new GeoIndex();
		System.out.println("Start creating R*-tree");
		
		long startTime = System.currentTimeMillis();
		geoIndex.createRTree(geoCoordinates);
        long finishTime = System.currentTimeMillis();
        long executionTime = finishTime - startTime;
		
		System.out.println("Finish creating R*-tree..."
				+ executionTime + " milliseconds");
		
		geoIndex.writeToTxt(writeFileName);
		
		/*geoIndex.rTree.visualize(1000, 1000).save(
				visualizationFileName);*/
		
		/*System.out.println("Start writing R*-tree to txt..."
				+ writeFileName);
		geoIndex.writeToTxt(writeFileName);
		System.out.println("Finish writing R*-tree to txt..."
				+ writeFileName);*/
	}
}
