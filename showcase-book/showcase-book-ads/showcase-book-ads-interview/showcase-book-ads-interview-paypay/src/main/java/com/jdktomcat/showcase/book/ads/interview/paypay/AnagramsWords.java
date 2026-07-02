package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.*;

public class AnagramsWords {

    public List<List<String>> groupAnagrams(String[] strs){
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            String sortedKey = convert(str);
            map.computeIfAbsent(sortedKey, k -> new ArrayList<>()).add(str);
        }
        return new ArrayList<>(map.values());
    }

    private String convert(String str){
        if(str == null || str.isEmpty()) return "";
        char[] chars = str.toCharArray();
        Arrays.sort(chars);

        return new String(chars);
    }
}
