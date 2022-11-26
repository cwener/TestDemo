package com.example.test.leetcode;

import com.example.test.leetcode.base.ListNode;

/**
 * @author: chengwen
 * @date: 2022/11/26
 * 删除链表的倒数第 N 个结点
 */
public class RemoveNthFromEnd {
    public static ListNode removeNthFromEnd(ListNode head, int n) {
        if(head == null) return null;
        ListNode emptyHead = new ListNode();
        emptyHead.next = head;
        ListNode left = emptyHead, right = head;
        int index = 1;
        while(index < n) {
            if(right != null) {
                right = right.next;
                index++;
            } else {
                return null;
            }
        }
        while(right.next != null) {
            left = left.next;
            right = right.next;
        }
        left.next = left.next.next;
        return emptyHead.next;
    }
}
