package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Here’s a clear, interview-style English description of the problem.
 * <p>
 * K Closest Points to Origin — Problem Description
 * <p>
 * Given an array of points on a 2D plane, where each point is represented as [x, y], and an integer k, return the k points that are closest to the origin (0, 0).
 * <p>
 * The distance between two points on a plane is defined using the Euclidean distance:
 * <p>
 * 𝑥
 * 2
 * +
 * 𝑦
 * 2
 * x
 * 2
 * +y
 * 2
 * ​
 * Since the square root function is monotonic, you may compare distances using
 * 𝑥
 * 2
 * +
 * 𝑦
 * 2
 * x
 * 2
 * +y
 * 2
 * instead.
 * The answer can be returned in any order.
 * <p>
 * Input
 * <p>
 * points: a 2D integer array where points[i] = [x, y]
 * <p>
 * k: the number of closest points to return
 * <p>
 * Output
 * <p>
 * A 2D array containing k points closest to the origin
 */
public class KClosestPointsToOrigin {

    /**
     * K Closest Points to Origin
     *
     * @param points original points
     * @param k      target
     * @return result
     */
    public static int[][] kClosestPointsToOrigin(int[][] points, int k) {
        PriorityQueue<int[]> miniHeap = new PriorityQueue<>((o1, o2) -> o2[0] * o2[0] + o2[1] * o2[1] - o1[0] * o1[0] + o1[1] * o1[1]);
        for (int[] point : points) {
            miniHeap.offer(point);
            if (miniHeap.size() > k) {
                miniHeap.poll();
            }
        }
        int[][] result = new int[miniHeap.size()][2];
        miniHeap.toArray(result);
        return result;
    }
}
