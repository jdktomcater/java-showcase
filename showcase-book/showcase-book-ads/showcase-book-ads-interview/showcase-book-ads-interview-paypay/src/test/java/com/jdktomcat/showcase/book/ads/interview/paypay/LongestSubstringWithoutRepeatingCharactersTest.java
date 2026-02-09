package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class LongestSubstringWithoutRepeatingCharactersTest {

    @Test
    public void lengthOfLongestSubstring() {
        Map<String, Integer> dataMap = Map.of(
                "abba", 2,
                "tmmzuxt", 5,
                "dvdf", 3,
                "", 0,
                "bbbb", 1,
                "abcabcbb", 3
        );
        dataMap.forEach((key, value) -> assertEquals((int) value, LongestSubstringWithoutRepeatingCharacters.lengthOfLongestSubstring(key)));
    }
}