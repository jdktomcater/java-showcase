package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.*;

/**
 * Top K Frequent Elements — Problem Description
 * Given an integer array nums and an integer k, return the k most frequent elements in the array.
 * The answer can be returned in any order.
 * Input
 * nums: an array of integers
 * k: an integer representing how many top frequent elements to return
 * Output
 * An array of k integers that appear most frequently in nums
 * Constraints
 * 1 ≤ nums.length ≤ 10⁵
 * k is in the range [1, number of unique elements in nums]
 * It is guaranteed that the answer is unique
 */
public class TopKFrequentElements {

    /**
     * Top K Frequent Elements
     *
     * @param nums target nums
     * @param k    threshold
     * @return result nums
     */
    public static int[] topKFrequentElements(int[] nums, int k) {
        Map<Integer, Integer> frequency = new HashMap<>();
        for (int num : nums) {
            frequency.put(num, frequency.getOrDefault(num, 0) + 1);
        }
        PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(Map.Entry.comparingByValue());
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            result[i] = minHeap.poll().getKey();
        }
        return result;
    }
}
