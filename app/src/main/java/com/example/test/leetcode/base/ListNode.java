package com.example.test.leetcode.base;

/**
 * @author: chengwen
 * @date: 2022/11/26
 */
public class ListNode {
    int val;
    public ListNode next;

    public ListNode() {
    }

    public ListNode(int val) {
        this.val = val;
    }

    public ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}
