package com.company.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by pss on 11.06.17.
 *
 * Base tests on Median and StdDev.
 */
public class StatisticsTest {

    @Test
    public void testMedianEven() {
        long[] anArray = {1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1};
        Statistics st = new Statistics(anArray);
        assertEquals("Median failed for Even array median", 3.5d, st.getMedian(), 0);
    }

    @Test
    public void testMedianOdd() {
        long[] anArray = {1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2};
        Statistics st = new Statistics(anArray);
        assertEquals("Median failed for Odd array median", 4.0d, st.getMedian(), 0);
    }

    @Test
    public void testStdDev() {
        long[] anArray = {2, 4, 4, 4, 5, 5, 7, 9};
        Statistics st = new Statistics(anArray);
        assertEquals("Standard deviation failed", 2.0d, st.getStdDev(), 0);
    }
}
