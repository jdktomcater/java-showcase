package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * LRU Cache — Problem Description
 * Design a data structure that implements a Least Recently Used (LRU) Cache.
 * The cache should support the following operations in O(1) time:
 * get(key):
 * Return the value of the key if it exists in the cache; otherwise return -1.
 * put(key, value):
 * Insert or update the value of the key.
 * If the cache exceeds its capacity, it should evict the least recently used key.
 * What “Least Recently Used” Means
 * Every time a key is accessed (via get or put), it becomes the most recently used
 * When the cache is full, the key that has not been accessed for the longest time is removed
 */
public class LRUCache {
    Map<Integer, Node> cache = new HashMap<>();
    Node head = new Node(-1, -1);
    Node tail = new Node(-1, -1);
    int capacity;

    static class Node {
        int key;
        int value;
        Node next;
        Node prev;
        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }
    public int get(int key) {
        if (!cache.containsKey(key)) {
            return -1;
        }
        Node node = cache.get(key);
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (!cache.containsKey(key)) {
            Node node = new Node(key, value);
            cache.put(key, node);
            insertHead(node);
            if (cache.size() > capacity) {
                Node last = tail.prev;
                remove(last);
                cache.remove(last.key);
            }
            return;
        }
        Node node = cache.get(key);
        moveToHead(node);
        node.value = value;
    }

    private void moveToHead(Node node) {
        remove(node);
        insertHead(node);
    }
    private void remove(Node node) {
        Node prev = node.prev;
        Node next = node.next;
        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }
    }
    private void insertHead(Node node) {
        Node first = head.next;
        head.next = node;
        node.next = first;
        node.prev = head;
        first.prev = node;
    }
}
