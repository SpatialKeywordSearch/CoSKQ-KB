package edu.yonsei.icl.coskqkb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class GeoIndex {
	RTree<String, Point> rTree;
	static FileWriter fw;
	static BufferedWriter bw;
	static FileReader fr;
	static BufferedReader br;
	
	public GeoIndex() {
		//R*-tree
		this.rTree = RTree.star().maxChildren(6).create();
	}
	
	public void createRTree(GeoCoordinates geoCoordinates) {
		geoCoordinates.vertexCoordinatesHash.forEach((k,v)->{
			String vertex = k;
			Double longitude = 
					Double.valueOf(v.get("<hasLongitude>"));
			Double latitude = 
					Double.valueOf(v.get("<hasLatitude>"));
			
			//add to R*-tree
			this.rTree = this.rTree.add(vertex, 
					Geometries.point(longitude, latitude));
		});
	}
	
	public void writeToTxt(String fileName) {
		try {
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			
			bw.write(this.rTree.asString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
