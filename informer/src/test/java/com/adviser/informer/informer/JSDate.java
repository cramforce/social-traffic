package com.adviser.informer.informer;

import java.util.Date;

import com.adviser.informer.model.DateDeserializer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class JSDate 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public JSDate( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( JSDate.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testParseJSDate()
    {
       //final String str = "Thu Apr 28 2011 17:38:56 GMT+0200 (CEST)";
      final String str = "Mon May 30 2011 10:17:36 GMT+0200 (CEST)";
       //final String str = "Thu Apr 28 2011 17:38:56 CEST";
       final Date date = DateDeserializer.str2Date(str);
       assertNotNull("date should not be null", date);
   //    assertEqual(5, date.getDay());
   //    assertEqual(28, date.)
       
   }
}
