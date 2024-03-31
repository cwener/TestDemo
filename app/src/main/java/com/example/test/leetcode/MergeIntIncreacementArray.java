package com.example.test.leetcode;

/**
 * @author chengwen
 * @createTime 2024/3/31
 *
 * 88. 合并两个有序数组
 * https://leetcode.cn/problems/merge-sorted-array/description/
 **/
public class MergeIntIncreacementArray {
    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        if(nums1 == null || nums1.length == 0) return;
        if(nums2 == null || nums2.length == 0) return;
        int p1 = m - 1, p2 = n - 1;
        for(int i = nums1.length - 1; i >= 0 && (p1 >= 0 || p2 >= 0); i--) {
            if(p1 >= 0 && p2 >= 0) {
                nums1[i] = nums1[p1] >= nums2[p2] ? nums1[p1--] : nums2[p2--];
            } else if(p1 >= 0) {
                nums1[i] = nums1[p1--];
            } else {
                nums1[i] = nums2[p2--];
            }
        }

    }
}
