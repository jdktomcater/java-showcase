package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.HashMap;
import java.util.Map;

/**
 * Longest Substring Without Repeating Characters
 */
public class LongestSubstringWithoutRepeatingCharacters {

    /**
     * Longest Substring Without Repeating Characters
     *
     * @param str target
     * @return max length
     */
    public static int lengthOfLongestSubstring(String str) {
        Map<Character, Integer> charIndexMap = new HashMap<>();
        int maxLength = 0;
        int start = 0;
        for (int end = 0; end < str.length(); end++) {
            Integer index = charIndexMap.put(str.charAt(end), end);
            if (index != null) {
                // important
                start = Math.max(start, index + 1);
            }
            maxLength = Math.max(maxLength, end - start + 1);
        }
        return maxLength;
    }
}
