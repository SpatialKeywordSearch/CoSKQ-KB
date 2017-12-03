package edu.yonsei.icl.coskqkb;

public class JenaTest
{
    public static void main(String args[]) {
        String readFileName = "dataset/Yago/yagoSchema.ttl";
        String writeFileName = "dataset/YagoGraph/yagoSchema.txt";
        
        AdjacencyList adjacencyList = new AdjacencyList();
        
        Jena.readTurtle(readFileName,adjacencyList);
        //adjacencyList.printAdjacencyList();
        
        //Write file test
        //System.out.println(adjacencyList.writeToFile(writeFileName));
        
        //Read file test
        //adjacencyList = adjacencyList.readFromFile(writeFileName);
        //adjacencyList.printAdjacencyList();
    }
}