package com.example.test.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengwen
 * @createTime 2024/4/4
 * 56. 合并区间
 * https://leetcode.cn/problems/merge-intervals/description/
 **/
public class MerInterval {
    public static int[][] merge(int[][] intervals) {
        List<int[]> list = new ArrayList();
        list.add(intervals[0]);
        for(int i = 1; i < intervals.length; i++) {
            int[] cur = list.get(list.size() - 1);
            if(intervals[i][0] <= cur[1]) {
                cur[1] = intervals[i][1];
            } else {
                list.add(intervals[i]);
            }
        }
        return list.toArray(new int[list.size()][2]);
    }
}
