package com.jdktomcat.showcase.book.ads.map;

/**
 * max island area
 */
public class MaxIslandArea {

    /**
     * max island land area
     * input array, 0 present water 1 present land
     *
     * @param grid map
     * @return max
     */
    public static int maxIslandArea(int[][] grid) {
        if (grid == null) {
            return 0;
        }
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int maxArea = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1 && !visited[i][j]) {
                    maxArea = Math.max(maxArea, getArea(i, j, grid, visited));
                }
            }
        }
        return maxArea;
    }

    /**
     * check the island
     *
     * @param i       target col
     * @param j       target row
     * @param grid    target map
     * @param visited visited
     * @return area
     */
    private static int getArea(int i, int j, int[][] grid, boolean[][] visited) {
        int area = 1;
        visited[i][j] = true;
        if (i < grid.length - 1 && grid[i + 1][j] == 1 && !visited[i + 1][j]) {
            area += getArea(i + 1, j, grid, visited);
        }
        if (i > 0 && grid[i - 1][j] == 1 && !visited[i - 1][j]) {
            area += getArea(i - 1, j, grid, visited);
        }
        if (j < grid[0].length - 1 && grid[i][j + 1] == 1 && !visited[i][j + 1]) {
            area += getArea(i, j + 1, grid, visited);
        }
        if (j > 0 && grid[i][j - 1] == 1 && !visited[i][j - 1]) {
            area += getArea(i, j - 1, grid, visited);
        }
        return area;
    }
}
