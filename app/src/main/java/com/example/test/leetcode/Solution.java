package com.example.test.leetcode;

/**
 * @author chengwen
 * @createTime 2024/3/30
 * 找第k个最大的数
 * https://leetcode.cn/problems/maximum-subarray/
 **/
class Solution {
    public static int findKthLargest(int[] nums, int k) {
        if(nums == null || nums.length == 0 || k < 0 || k > nums.length) return -1;
        int partition = 0, start = 0, end = nums.length - 1;
        int tartget = nums.length - k;
        while(start <= end) {
            partition = findKthLargest(nums, start, end);
            if(partition > tartget) {
                end = partition - 1;
            } else if(partition < tartget) {
                start = partition + 1;
            } else {
                return nums[partition];
            }
        }
        return -1;
    }

    private static int findKthLargest(int[] nums, int start, int end) {
        int tmp = nums[start];
        while(start < end) {
            while(start < end && nums[end] > tmp) {
                end--;
            }
            if(start < end) {
                nums[start] = nums[end];
            }
            while(start < end && nums[start] <= tmp) {
                start++;
            }
            if(start < end) {
                nums[end] = nums[start];
            }
        }
        nums[start] = tmp;
        return start;
    }
}