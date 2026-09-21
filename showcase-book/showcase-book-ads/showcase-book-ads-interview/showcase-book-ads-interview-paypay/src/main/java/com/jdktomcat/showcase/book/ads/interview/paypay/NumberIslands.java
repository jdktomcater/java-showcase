package com.jdktomcat.showcase.book.ads.interview.paypay;

/**
 * Given a 2D grid of characters where:
 * '1' represents land
 * '0' represents water
 * Return the number of islands in the grid.
 * An island is formed by connecting adjacent lands horizontally or vertically (not diagonally).
 * You may assume all four edges of the grid are surrounded by water.
 */
public class NumberIslands {

    /**
     * number islands
     *
     * @param points target points
     * @return number islands
     */
    public static int numberIslands(int[][] points) {
        if (points == null || points.length == 0) {
            return 0;
        }
        int count = 0;
        int row = points.length, col = points[0].length;
        boolean[][] visited = new boolean[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (points[i][j] == 1 && !visited[i][j]) {
                    travelAndMark(points, visited, i, j);
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * number islands
     *
     * @param points target points
     * @return number islands
     */
    public static int numberIslandsNew(int[][] points) {
        if (points == null || points.length == 0) {
            return 0;
        }
        int count = 0;
        int row = points.length, col = points[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (points[i][j] == 1) {
                    travelAndMarkNew(points, i, j);
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * travel and mark
     *
     * @param points target point
     * @param i      target c index
     * @param j      target r index
     */
    private static void travelAndMarkNew(int[][] points, int i, int j) {
        if (i < 0 || j < 0 || i >= points.length || j >= points[0].length || points[i][j] == 0) {
            return;
        }
        points[i][j] = 0;
        travelAndMarkNew(points, i + 1, j);
        travelAndMarkNew(points, i - 1, j);
        travelAndMarkNew(points, i, j + 1);
        travelAndMarkNew(points, i, j - 1);
    }

    /**
     * travel and mark
     *
     * @param points  target point
     * @param visited point visited
     * @param i       target c index
     * @param j       target r index
     */
    private static void travelAndMark(int[][] points, boolean[][] visited, int i, int j) {
        visited[i][j] = true;
        if (i < points.length - 1 && points[i + 1][j] == 1 && !visited[i + 1][j]) {
            travelAndMark(points, visited, i + 1, j);
        }
        if (i > 0 && points[i - 1][j] == 1 && !visited[i - 1][j]) { 
            travelAndMark(points, visited, i - 1, j);
        }
        if (j < points[0].length - 1 && points[i][j + 1] == 1 && !visited[i][j + 1]) {
            travelAndMark(points, visited, i, j + 1);
        }
        if (j > 0 && points[i][j - 1] == 1 && !visited[i][j - 1]) {
            travelAndMark(points, visited, i, j - 1);
        }
    }
}
