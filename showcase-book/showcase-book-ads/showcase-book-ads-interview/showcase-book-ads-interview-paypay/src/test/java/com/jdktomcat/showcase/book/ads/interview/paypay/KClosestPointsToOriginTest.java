package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.*;

public class KClosestPointsToOriginTest {

    @Test
    public void kClosestPointsToOrigin() {
        int[][] points = {{3, 3}, {5, -1}, {-2, 4}};
        int k = 2;
        int[][] result = {{3, 3}, {-2, 4}};
        assertArrayEquals(result, KClosestPointsToOrigin.kClosestPointsToOrigin(points, k));
    }
}