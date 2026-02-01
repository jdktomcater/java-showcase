package com.jdktomcat.showcase.book.ads.integer;

import java.util.Arrays;

/**
 * input integer array only one just occur once and other numbers occurs three times
 * find out which one
 */
public class SingleNumber {

    public static int singleNumber(int[] arrays) {
        return Arrays.stream(arrays).sum();
    }

}
