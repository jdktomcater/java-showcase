package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.*;

public class TopKFrequentElementsTest {

    @Test
    public void topKFrequentElements() {
        int[] nums = new int[]{1,1,1,2,2,3};
        int k = 2;
        assertArrayEquals(new int[]{1,2},TopKFrequentElements.topKFrequentElements(nums, k));
    }
}