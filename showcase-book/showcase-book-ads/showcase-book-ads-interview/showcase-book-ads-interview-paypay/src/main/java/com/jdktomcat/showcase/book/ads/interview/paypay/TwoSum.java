package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.HashMap;

/**
 * Two Sum is a classic coding problem (LeetCode #1) where you’re given:
 * An array of integers nums
 * An integer target
 * You need to find the indices of two different numbers in the array that add up to the target and return those indices.
 * You can assume there’s exactly one valid answer and you can’t use the same element twice.
 * Problem
 * Input:
 * nums = [2, 7, 11, 15], target = 9
 * Output:
 * [0, 1]
 * Explanation: Because nums[0] + nums[1] = 2 + 7 = 9.
 */
public class TwoSum {

    /**
     * find the indices of two different numbers in the array that add up to the target and return those indices
     *
     * @param nums   array of integers nums
     * @param target integer target
     * @return indices
     */
    public static int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> numIndexMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (numIndexMap.containsKey(target - nums[i])) {
                return new int[]{numIndexMap.get(target - nums[i]), i};
            }
            numIndexMap.putIfAbsent(nums[i], i);
        }
        throw new IllegalArgumentException("No two sum solution");
    }
}
