package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Given an array of meeting time intervals where
 * intervals[i] = [start_i, end_i],
 * return the minimum number of conference rooms required so that all meetings can be held without overlap.
 * A meeting room can only host one meeting at a time.
 * <p>
 * input:
 * [[0,30],[5,10],[15,20]]
 * output:
 * 2
 */
public class MeetingRoom {

    /**
     * get the minimum meeting room according schedules
     *
     * @param schedules schedules
     * @return minimum meeting room
     */
    public static int meetingRoom(int[][] schedules) {
        if (schedules == null || schedules.length == 0) {
            return 0;
        }
        Arrays.sort(schedules, Comparator.comparingInt(o -> o[0]));
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        for(int[] schedule : schedules) {
            int[] minSchedule = minHeap.peek();
            if(minSchedule == null) {
                minHeap.offer(schedule);
            }else{
            }
        }
        return 0;

    }
}
