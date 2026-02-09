package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Merge Intervals:
 * Given an array of intervals where intervals[i] = [start_i, end_i],
 * merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.
 * Two intervals overlap if:
 * interval A starts before or at interval B ends
 * AND
 * interval A ends after or at interval B starts
 * input:
 * [[1,3],[2,6],[8,10],[15,18]]
 * output:
 * [[1,6],[8,10],[15,18]]
 */
public class MergeIntervals {

    /**
     * merge intervals
     *
     * @param intervals target intervals
     * @return merged intervals
     */
    public static int[][] mergeIntervals(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return intervals;
        }
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        List<int[]> result = new ArrayList<>();
        int[] interval = intervals[0];
        result.add(interval);
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= interval[1]) {
                interval[1] = Math.max(intervals[i][1], interval[1]);
            } else {
                interval = intervals[i];
                result.add(interval);
            }
        }
        return result.toArray(new int[result.size()][]);
    }

}
