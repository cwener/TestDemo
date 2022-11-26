package com.example.test.leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: chengwen
 * @date: 2022/11/17
 * 最长无重复子串
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

    public static int lengthOfLongestSubstring2(String s) {
        if(s == null || s.length() == 0) return 0;
        Set<Character> set = new HashSet<>();
        int left = 0, right = 0, max = 0;
        while(right < s.length()) {
            if(set.contains(s.charAt(right))) {
                set.remove(s.charAt(left));
                left++;
            } else {
                set.add(s.charAt(right));
                max = Math.max(set.size(), max);
                right++;
            }
        }
        return max;
    }
}
