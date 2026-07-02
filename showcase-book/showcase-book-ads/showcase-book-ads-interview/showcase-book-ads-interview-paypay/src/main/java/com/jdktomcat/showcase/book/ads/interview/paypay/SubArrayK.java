package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.HashMap;
import java.util.Map;

public class SubArrayK {

    private static  int subarraySum(int[] nums, int k){
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        int sum = 0;
        int res = 0;
        for (int num : nums) {
            sum += num;
            res += map.getOrDefault(sum - k, 0);
            map.merge(sum, 1, Integer::sum);
        }
        return res;
    }
}
