package com.company.utils;

import java.util.Arrays;

/**
 * Created by pss on 11.06.17.
 *
 * Calculate median and standard deviation for an array.
 */
public class Statistics {
    private long[] anArray;
    private int size;

    public double getStdDev() {

        //Calculate Mean
        double sum = 0.0;
        for (double a : anArray)
            sum += a;
        double mean = sum / size;

        //Calculate Variance
        double temp = 0;
        for (double a : anArray)
            temp += (a - mean) * (a - mean);
        double variance = temp / size;

        //Calculate Std Dev
        return Math.sqrt(variance);
    }

    public double getMedian() {
        if (anArray.length % 2 == 0) {
            return (double) (anArray[(anArray.length / 2) - 1] + anArray[anArray.length / 2]) / 2.0;
        }
        return (double) anArray[anArray.length / 2];
    }


    public Statistics(long[] data) {
        this.anArray = data;
        Arrays.sort(data);
        size = data.length;
    }


}
