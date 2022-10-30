/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.bluejungle.destiny.webui.browsabledatapicker.ISearchBucket;

import junit.framework.TestCase;

/**
 * JUnit test case for {@see DefaultSearchBucketBean}
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/DefaultSearchBucketBeanTest.java#1 $
 */

public class DefaultSearchBucketBeanTest extends TestCase {

    /**
     * There's probably a more mathematical way of doing this, but rather than
     * spending too much time getting right, doing it the easy way
     */
    private static final List<Character[]> EXPECTED_SEARCH_BUCKETS = new ArrayList<Character[]>(12);
    static {
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'A', 'B' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'C', 'D' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'E', 'F' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'G', 'H' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'I', 'J' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'K', 'L' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'M', 'N' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'O', 'P', 'Q', 'R' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'S', 'T' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'U', 'V' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { 'W', 'X', 'Y', 'Z' });
        EXPECTED_SEARCH_BUCKETS.add(new Character[] { ' '});
    }

    private DefaultSearchBucketBean searchBucketBeanToTest;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DefaultSearchBucketBeanTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.searchBucketBeanToTest = new DefaultSearchBucketBean();
    }

    public void testGetSearchBuckets() {
        ISearchBucketExtended[] searchBuckets = this.searchBucketBeanToTest.getSearchBuckets(Locale.getDefault());
        assertNotNull("Ensure search buckets not null", searchBuckets);
        assertEquals("Ensure search bucket array length as expected", EXPECTED_SEARCH_BUCKETS.size(), searchBuckets.length);

        assertEquals("Ensure search bucket 0 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(0))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(0))[1], searchBuckets[0].getDisplayValue());
        assertTrue("Ensure search bucket 0 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(0), searchBuckets[0].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 1 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(1))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(1))[1], searchBuckets[1].getDisplayValue());
        assertTrue("Ensure search bucket 1 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(1), searchBuckets[1].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 2 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(2))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(2))[1], searchBuckets[2].getDisplayValue());
        assertTrue("Ensure search bucket 2 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(2), searchBuckets[2].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 3 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(3))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(3))[1], searchBuckets[3].getDisplayValue());
        assertTrue("Ensure search bucket 3 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(3), searchBuckets[3].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 4 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(4))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(4))[1], searchBuckets[4].getDisplayValue());
        assertTrue("Ensure search bucket 4 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(4), searchBuckets[4].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 5 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(5))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(5))[1], searchBuckets[5].getDisplayValue());
        assertTrue("Ensure search bucket 5 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(5), searchBuckets[5].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 6 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(6))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(6))[1], searchBuckets[6].getDisplayValue());
        assertTrue("Ensure search bucket 6 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(6), searchBuckets[6].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 7 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(7))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(7))[3], searchBuckets[7].getDisplayValue());
        assertTrue("Ensure search bucket 7 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(7), searchBuckets[7].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 8 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(8))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(8))[1], searchBuckets[8].getDisplayValue());
        assertTrue("Ensure search bucket 8 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(8), searchBuckets[8].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 9 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(9))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(9))[1], searchBuckets[9].getDisplayValue());
        assertTrue("Ensure search bucket 9 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(9), searchBuckets[9].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 10 display value as expected", ((Character[]) EXPECTED_SEARCH_BUCKETS.get(10))[0] + "-" + ((Character[]) EXPECTED_SEARCH_BUCKETS.get(10))[3], searchBuckets[10].getDisplayValue());
        assertTrue("Ensure search bucket 10 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(10), searchBuckets[10].getSeachSpec(0).getCharactersInBucket()));

        assertEquals("Ensure search bucket 11 display value as expected", "Other", searchBuckets[11].getDisplayValue());
        assertTrue("Ensure search bucket 11 search spec as expected", Arrays.equals((Character[]) EXPECTED_SEARCH_BUCKETS.get(11), searchBuckets[11].getSeachSpec(0).getCharactersInBucket()));

        /*
         * If called again, we should get the same value back. Verifies caching
         */
        ISearchBucket[] searchBucketsAgain = this.searchBucketBeanToTest.getSearchBuckets(Locale.getDefault());
        assertEquals("Ensure value is cached", searchBuckets, searchBucketsAgain);
        
        /**
         * Verify Maximum search results as expected in search spec 
         */
        int maximumNumberOfResults = 5;
        assertEquals("Ensure search spec maximum results is as expected one", maximumNumberOfResults, searchBuckets[0].getSeachSpec(maximumNumberOfResults).getMaximumResultsToReturn());
        maximumNumberOfResults = 46;
        assertEquals("Ensure search spec maximum results is as expected one", maximumNumberOfResults, searchBuckets[0].getSeachSpec(maximumNumberOfResults).getMaximumResultsToReturn());

        
        // Verify null pointer
        NullPointerException expectedException = null;
        try {
            this.searchBucketBeanToTest.getSearchBuckets(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure null pointer was thrown", expectedException);
    }
}