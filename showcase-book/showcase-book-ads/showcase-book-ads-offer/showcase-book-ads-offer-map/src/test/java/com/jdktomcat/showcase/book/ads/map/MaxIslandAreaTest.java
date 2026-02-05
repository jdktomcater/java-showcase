package com.jdktomcat.showcase.book.ads.map;

import org.junit.Test;

import static org.junit.Assert.*;

public class MaxIslandAreaTest {

    @Test
    public void maxIslandArea() {
        int[][] grid1 = new int[][]{{1, 1, 0, 0, 1}, {1, 0, 0, 1, 0}, {1, 1, 0, 1, 0}, {0, 0, 1, 0, 0}};
        assertEquals(5, MaxIslandArea.maxIslandArea(grid1));

        int[][] grid2 = new int[][]{{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
        assertEquals(0, MaxIslandArea.maxIslandArea(grid2));
    }
}