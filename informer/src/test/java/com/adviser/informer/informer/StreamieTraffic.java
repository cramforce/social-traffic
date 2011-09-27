package com.adviser.informer.informer;

import java.util.LinkedList;

import com.adviser.informer.model.streamie.Traffic;
import com.adviser.informer.model.streamie.Tuple;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class StreamieTraffic 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public StreamieTraffic( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( StreamieTraffic.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testTrafficAdd()
    {
       final Traffic traffic = new Traffic();
       traffic.add(10, 1, 1);
       final LinkedList<Tuple> tuples = traffic.getTraffic();
       assertEquals(tuples.size(), 1);
       Tuple tuple = tuples.getFirst(); 
       assertEquals(tuple.timestamp, 10);
       assertEquals(tuple.inAmount, 1);
       assertEquals(tuple.outAmount, 1);

       traffic.add(10, 2, 4);
       assertEquals(tuples.size(), 1);
       assertEquals(tuple.timestamp, 10);
       assertEquals(tuple.inAmount, 3);
       assertEquals(tuple.outAmount, 5);
       
       traffic.add(5, 34, 56);
       assertEquals(tuples.size(), 2);
       tuple = tuples.getFirst();
       assertEquals(tuple.timestamp, 5);
       assertEquals(tuple.inAmount, 34);
       assertEquals(tuple.outAmount, 56);
       tuple = tuples.getLast();
       assertEquals(tuple.timestamp, 10);
       assertEquals(tuple.inAmount, 3);
       assertEquals(tuple.outAmount, 5);
       

       traffic.add(15, 134, 156);
       assertEquals(tuples.size(), 3);
       tuple = tuples.getFirst();
       assertEquals(tuple.timestamp, 5);
       assertEquals(tuple.inAmount, 34);
       assertEquals(tuple.outAmount, 56);
       tuple = tuples.get(1);
       assertEquals(tuple.timestamp, 10);
       assertEquals(tuple.inAmount, 3);
       assertEquals(tuple.outAmount, 5);
       tuple = tuples.getLast();
       assertEquals(tuple.timestamp, 15);
       assertEquals(tuple.inAmount, 134);
       assertEquals(tuple.outAmount, 156);

       traffic.add(12, 234, 256);
       assertEquals(tuples.size(), 4);
       tuple = tuples.getFirst();
       assertEquals(tuple.timestamp, 5);
       assertEquals(tuple.inAmount, 34);
       assertEquals(tuple.outAmount, 56);
       tuple = tuples.get(1);
       assertEquals(tuple.timestamp, 10);
       assertEquals(tuple.inAmount, 3);
       assertEquals(tuple.outAmount, 5);
       tuple = tuples.get(2);
       assertEquals(tuple.timestamp, 12);
       assertEquals(tuple.inAmount, 234);
       assertEquals(tuple.outAmount, 256);
       tuple = tuples.getLast();
       assertEquals(tuple.timestamp, 15);
       assertEquals(tuple.inAmount, 134);
       assertEquals(tuple.outAmount, 156);
     
       
   }
}
