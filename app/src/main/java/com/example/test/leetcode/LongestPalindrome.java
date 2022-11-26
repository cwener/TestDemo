package com.example.test.leetcode;

/**
 * @author: chengwen
 * @date: 2022/11/20
 * 最长回文子串
 */
public class LongestPalindrome {
    public static String longestPalindrome(String s) {
        if(s == null) return null;
        int max = 0, start = 0, end = 0;
        for(int i = 0; i < s.length(); i++) {
            int sub1 = longestPalindrome(s, i, i);
            int sub2 = longestPalindrome(s, i, i + 1);
            int tmp = Math.max(sub1, sub2);
            if(max < tmp) {
                if(sub1 > sub2) {
                    start = i - sub1 / 2;
                    end = i + sub1 / 2;
                } else {
                    start = i - sub2 / 2 + 1;
                    end = i + sub2 / 2 + 1;
                }
                max = tmp;
            }
        }
        return s.substring(start, end + 1);
    }

    private static int longestPalindrome(String s, int left, int right) {
        int sub = 0;
        while(left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            sub = right - left + 1;
            left--;
            right++;
        }
        return sub;
    }
}
