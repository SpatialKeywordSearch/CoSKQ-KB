package edu.yonsei.icl.coskqkb;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.jena.graph.Triple ;
import org.apache.jena.riot.RDFDataMgr ;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

public class Jena
{
	//Read RDF triples from a .ttl file and add to an adjacency list
    public static void readTurtle(String turtleFile, AdjacencyList adjacencyList) {
    	final String filename = turtleFile;
    	
        // Create a PipedRDFStream to accept input and a PipedRDFIterator to
        // consume it
        PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
        final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

        // PipedRDFStream and PipedRDFIterator need to be on different threads
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Create a runnable for our parser thread
        Runnable parser = new Runnable() {

            public void run() {
                // Call the parsing process.
                RDFDataMgr.parse(inputStream, filename);
            }
        };

        // Start the parser on another thread
        executor.submit(parser);

        // We will consume the input on the main thread here

        // We can now iterate over data as it is parsed, parsing only runs as
        // far ahead of our consumption as the buffer size allows
        while (iter.hasNext()) {
            Triple next = iter.next();
            
            adjacencyList.addTriple(next.getSubject().toString(),
            		next.getPredicate().toString(), next.getObject().toString());
            
            //Print triple
            //System.out.println("Subject:  "+next.getSubject());
            //System.out.println("Predicate:  "+next.getPredicate());
            //System.out.println("Object:  "+next.getObject());
            //System.out.println("\n");
        }
        
        //System.out.println("Read file done!");
            
        executor.shutdown();
    }
}
