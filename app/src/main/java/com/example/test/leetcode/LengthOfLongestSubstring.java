package com.example.test.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: chengwen
 * @date: 2022/11/17
 */
public class LengthOfLongestSubstring {
    public int lengthOfLongestSubstring(String s) {
        if(s == null || s.length() == 0) return 0;
        Map<Character, Integer> map = new HashMap<>();
        int maxSub = 0;
        int left = 0, right = 0;
        while(right < s.length()) {
            if(map.containsKey(s.charAt(right))) {
                left = Math.max(map.get(s.charAt(right)) + 1, left);
            }
            maxSub = Math.max(maxSub, right - left + 1);

            map.put(s.charAt(right), right);
            right++;
        }
        return maxSub;
    }
}
