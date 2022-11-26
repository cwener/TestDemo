package com.example.test.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: chengwen
 * @date: 2022/11/26
 * 三数之和
 */
public class TreeSum {
    public static List<List<Integer>> threeSum(int[] nums) {
        if(nums == null || nums.length < 3) return null;
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        for(int i = 0; i < nums.length - 2; i++) {
            // 当前开始的第一个如果与nums[i]前一个值相同，则应略过。
            if(i != 0 && nums[i] == nums[i - 1]) continue;
            int left = i + 1, right = nums.length - 1;
            int target = -nums[i];
            while(left < right) {
                // 跟前一次结果比较，相同则重复，应略过。但是left = i时
                if (left > i + 1 && nums[left] == nums[left - 1]) {
                    left++;
                    continue;
                }
                int tmp = nums[left] + nums[right];
                if(tmp == target) {
                    List<Integer> list = new ArrayList<>();
                    list.add(nums[i]);
                    list.add(nums[left]);
                    list.add(nums[right]);
                    res.add(list);
                    left++;
                } else if(tmp < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return res;
    }
}
