package com.example.test.leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author chengwen
 * @createTime 2024/3/31
 * 全排列
 * https://leetcode.cn/problems/permutations/
 * 思路：遍历到当前位置时，依次将后面的数与当前位置交换，临时数组带上这个交换后的固定位置数，再递归去下一个加1位置重复此操作
 **/
public class Permute {
    public List<List<Integer>> permute(int[] nums) {
        if(nums == null || nums.length == 0) return null;
        List<List<Integer>> res = new ArrayList();
        dfs(nums, 0, new LinkedList<>(), res);
        return res;
    }

    private void dfs(int[] nums, int start, LinkedList<Integer> curList, List<List<Integer>> res) {
        if(start == nums.length) {
            res.add(new ArrayList(curList));
            return;
        }
        for(int i = start; i < nums.length; i++) {
            swap(nums, start, i);
            curList.addLast(nums[start]);
            dfs(nums, start + 1, curList, res);
            curList.removeLast();
            swap(nums, start, i);
        }
    }

    private void swap(int[] nums, int start, int end) {
        if(end > nums.length - 1 || start < 0) return;
        int tmp = nums[start];
        nums[start] = nums[end];
        nums[end] = tmp;
    }
}
