package edu.yonsei.icl.coskqkb;

import java.util.HashMap;
import java.util.List;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import rx.Observable;

public class NearestNeighborSearchTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RTree<String, Point> tree = 
				RTree.star().maxChildren(6).create();
		
		tree = tree.add("DAVE", Geometries.point(10, 20))
		           .add("FRED", Geometries.point(12, 25))
		           .add("MARY", Geometries.point(97, 125));
		
		BCK bck = new BCK();
		bck.geoIndex.rTree = tree;
		
		SubTree RSP_qd = new SubTree();
		RSP_qd.subTreeHash.put("MARY", new HashMap<String,Integer>());
		
		Observable<Entry<String, Point>> NN =
				bck.geoIndex.rTree.nearest(
						Geometries.point(10, 18), 
						Double.MAX_VALUE, 3);
		
		bck.nearestNeighborList = 
				NN.toList().toBlocking().single();
		
		
		
		//Entry<String, Point> rsp_qd =
		//		bck.nearestNeighborSearch(RSP_qd);
		
		//System.out.println("The result is..."
		//		+ rsp_qd);
	}

}
