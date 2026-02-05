package com.jdktomcat.showcase.book.ads.map;

import org.junit.Test;

import static org.junit.Assert.*;

public class MaxIslandAreaTest {

    @Test
    public void maxIslandArea() {
        int[][] grid = new int[][]{{1, 1, 0, 0, 1}, {1, 0, 0, 1, 0}, {1, 1, 0, 1, 0}, {0, 0, 1, 0, 0}};
        assertEquals(5, MaxIslandArea.maxIslandArea(grid));
    }
}