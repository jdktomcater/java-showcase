package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class MergeIntervalsTest {

    @Test
    public void mergeIntervals() {
        int[][] intervals1 = new int[][]{{1, 3}, {2, 4}};
        int[][] mergedIntervals1 = new int[][]{{1, 4}};
        assertArrayEquals(mergedIntervals1, MergeIntervals.mergeIntervals(intervals1));

        int[][] intervals2 = new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}};
        int[][] mergedIntervals2 = new int[][]{{1, 6}, {8, 10}, {15, 18}};
        assertArrayEquals(mergedIntervals2, MergeIntervals.mergeIntervals(intervals2));

        int[][] intervals3 = new int[][]{{1, 3}};
        int[][] mergedIntervals3 = new int[][]{{1, 3}};
        assertArrayEquals(mergedIntervals3, MergeIntervals.mergeIntervals(intervals3));
    }
}