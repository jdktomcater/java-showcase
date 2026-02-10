package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.*;

public class TopKFrequentElementsTest {

    @Test
    public void topKFrequentElements() {
        int[] nums1 = new int[]{1,1,1,2,2,3};
        int k1 = 2;
        assertArrayEquals(new int[]{1,2},TopKFrequentElements.topKFrequentElements(nums1, k1));

        int[] nums2 = new int[]{1,1,1};
        int k2 = 1;
        assertArrayEquals(new int[]{1},TopKFrequentElements.topKFrequentElements(nums2, k2));

        int[] nums3 = new int[]{1,2,3,2,2,3};
        int k3 = 2;
        assertArrayEquals(new int[]{2,3},TopKFrequentElements.topKFrequentElements(nums3, k3));

        int[] nums4 = new int[]{1,2,3,4,5,6};
        int k4 = 6;
        assertArrayEquals(new int[]{1,2,3,4,5,6},TopKFrequentElements.topKFrequentElements(nums4, k4));
    }
}