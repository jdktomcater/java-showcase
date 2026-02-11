package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.*;

public class NumberIslandsTest {

    @Test
    public void numberIslands() {
        int[][] points = {{1, 1, 1, 1, 0}, {1, 1, 0, 1, 0}, {1, 1, 0, 0, 0}, {0, 0, 0, 0, 0}};
        assertEquals(1, NumberIslands.numberIslands(points));
        assertEquals(1, NumberIslands.numberIslandsNew(points));
    }
}