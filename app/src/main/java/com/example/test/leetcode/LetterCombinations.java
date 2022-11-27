package com.example.test.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: chengwen
 * @date: 2022/11/27
 * 电话号码组合
 */
public class LetterCombinations {
    Map<Character, String> phoneMap = new HashMap<Character, String>() {{
        put('2', "abc");
        put('3', "def");
        put('4', "ghi");
        put('5', "jkl");
        put('6', "mno");
        put('7', "pqrs");
        put('8', "tuv");
        put('9', "wxyz");
    }};
    public List<String> letterCombinations(String digits) {
        if(digits == null)
            return null;
        List<String> res = new ArrayList<>();
        if(digits.length() == 0) {
            return res;
        }
        combinations(res, 0, digits, new StringBuilder());
        return res;
    }

    private void combinations(List<String> res, int index, String digits, StringBuilder sb) {
        if(index == digits.length()) {
            res.add(sb.toString());
            return;
        }
        char tmpChar = digits.charAt(index);
        String tmp = phoneMap.get(tmpChar);
        for(int i = 0; i < tmp.length(); i++) {
            sb.append(tmp.charAt(i));
            combinations(res, index + 1, digits, sb);
            sb.deleteCharAt(index);
        }
    }
}
