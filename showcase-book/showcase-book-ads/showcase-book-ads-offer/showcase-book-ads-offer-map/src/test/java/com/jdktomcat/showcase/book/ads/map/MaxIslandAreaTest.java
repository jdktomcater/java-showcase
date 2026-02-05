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

        int[][] grid3 = new int[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}};
        assertEquals(20, MaxIslandArea.maxIslandArea(grid3));

        int[][] grid4 = new int[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 0}};
        assertEquals(19, MaxIslandArea.maxIslandArea(grid4));

        int[][] grid5 = new int[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {0, 1, 1, 1, 1}};
        assertEquals(19, MaxIslandArea.maxIslandArea(grid5));

        int[][] grid6 = new int[][]{{0, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}};
        assertEquals(19, MaxIslandArea.maxIslandArea(grid6));

        int[][] grid7 = new int[][]{{1, 1, 1, 1, 0}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}};
        assertEquals(19, MaxIslandArea.maxIslandArea(grid7));

        int[][] grid8 = new int[][]{{1, 1, 1, 1, 0}};
        assertEquals(4, MaxIslandArea.maxIslandArea(grid8));

        int[][] grid9 = new int[][]{{1}, {1}, {1}, {1}, {0}};
        assertEquals(4, MaxIslandArea.maxIslandArea(grid9));

        int[][] grid10 = new int[][]{{1, 1, 1, 1, 1}};
        assertEquals(5, MaxIslandArea.maxIslandArea(grid10));

        int[][] grid11 = new int[][]{{1}, {1}, {1}, {1}, {1}};
        assertEquals(5, MaxIslandArea.maxIslandArea(grid11));

        int[][] grid12 = new int[][]{{0, 0, 0, 0, 0}};
        assertEquals(0, MaxIslandArea.maxIslandArea(grid12));

        int[][] grid13 = new int[][]{{0}, {0}, {0}, {0}, {0}};
        assertEquals(0, MaxIslandArea.maxIslandArea(grid13));
    }
}