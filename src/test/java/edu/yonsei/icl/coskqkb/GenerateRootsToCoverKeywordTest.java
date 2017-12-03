package edu.yonsei.icl.coskqkb;

import java.util.HashMap;
import java.util.LinkedList;

public class GenerateRootsToCoverKeywordTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LinkedList<String> uncoveredKeywords =
				new LinkedList<String>();
		uncoveredKeywords.add("k1");
		uncoveredKeywords.add("k2");
		uncoveredKeywords.add("k3");
		
		HashMap<String, LinkedList<String>> unKwRootHash = 
				new HashMap<String, LinkedList<String>>();
		unKwRootHash.put("k1", new LinkedList<String>());
		unKwRootHash.get("k1").add("r11");
		unKwRootHash.get("k1").add("r12");
		unKwRootHash.get("k1").add("r13");
		
		unKwRootHash.put("k2", new LinkedList<String>());
		unKwRootHash.get("k2").add("r21");
		unKwRootHash.get("k2").add("r22");
		unKwRootHash.put("k3", new LinkedList<String>());
		unKwRootHash.get("k3").add("r31");
		unKwRootHash.get("k3").add("r32");
		unKwRootHash.get("k3").add("r33");
		
		HashMap<LinkedList<String>,Integer> finalResult =
				new HashMap<LinkedList<String>,Integer>();
		HashMap<LinkedList<String>,Integer> result =
				new HashMap<LinkedList<String>,Integer>();
		LinkedList<String> inputList = new LinkedList<String>();
		
		finalResult = recurFunc(0, inputList, result,
				uncoveredKeywords, unKwRootHash);
		
		System.out.println(finalResult.size());
		System.out.println(finalResult.toString());
	}
	
	public static HashMap<LinkedList<String>,Integer> recurFunc(
			int index, LinkedList<String> inputList,
			HashMap<LinkedList<String>,Integer> result, 
			LinkedList<String> uncoveredKeywords, 
			HashMap<String, LinkedList<String>> unKwRootHash) {
		
		HashMap<LinkedList<String>,Integer> temp = result;
		
		if(index<uncoveredKeywords.size())
		{
			for(int i=0; i<unKwRootHash.get(uncoveredKeywords.get(index)).size(); i++)
			{
				LinkedList<String> tempList = new LinkedList<String>(); 
				tempList.addAll(inputList);
				tempList.add(unKwRootHash.get(uncoveredKeywords.get(index)).get(i));
				temp = recurFunc(index+1, tempList, temp, uncoveredKeywords, unKwRootHash);
				
				if(tempList.size()==uncoveredKeywords.size())
				{
					temp.put(tempList, 0);
				}
			}
		}
		
		return temp;
	}
}