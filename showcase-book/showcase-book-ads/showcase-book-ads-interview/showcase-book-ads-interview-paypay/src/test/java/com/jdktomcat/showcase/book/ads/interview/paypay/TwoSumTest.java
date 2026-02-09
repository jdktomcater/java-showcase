package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.*;

public class TwoSumTest {

    @Test
    public void twoSum() {
        int[] nums1 = {3, 3};
        int target1 = 6;
        assertArrayEquals(new int[]{0, 1}, TwoSum.twoSum(nums1, target1));

        int[] nums2 = {3, 3, 10};
        int target2 = 13;
        assertArrayEquals(new int[]{0, 2}, TwoSum.twoSum(nums2, target2));

        int[] nums3 = {1,1,1,1,1,1,1,1,1,1,1,1};
        int target3 = 2;
        assertArrayEquals(new int[]{0, 1}, TwoSum.twoSum(nums3, target3));

        int[] nums4 = {1,1,1,1,1,1,1,1,2,1,1,1};
        int target4 = 3;
        assertArrayEquals(new int[]{0, 8}, TwoSum.twoSum(nums4, target4));
    }
}