package com.henry4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Comparators;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriorityHeap<K, V extends Comparable<V>> {
    private final List<Pair<K, V>> a = new ArrayList<>();
    private final Map<K, Integer> h = new HashMap<>();
    private final Comparator<V> ordering;

    public PriorityHeap() {
        ordering = Comparators.naturalOrder();
    }

    public PriorityHeap(Comparator<V> ordering) {
        this.ordering = ordering;
    }

    public PriorityHeap<K, V> offer(K element, V priority) {
        Pair prioritized = new Pair(element, priority);
        if (h.containsKey(element)) {
            int n = h.get(element);
            a.set(n, prioritized);
            if (n == bubbleUp(n)) {
                bubbleDown(n);
            }
        } else {
            a.add(prioritized);
            bubbleUp(a.size() - 1);
        }
        return this;
    }

    public Pair<K, V> peek() {
        return !a.isEmpty() ? a.get(0) : null;
    }

    public Pair<K, V> poll() {
        if (!a.isEmpty()) {
            Pair poll = a.get(0);
            a.set(0, a.get(a.size() - 1));
            a.remove(a.size() - 1);
            if (!a.isEmpty()) {
                bubbleDown(0);
            }
            return poll;
        } else {
            return null;
        }
    }
    
    public int bubbleUp(int n) {
        int p = (n - 1)/2;
        if (n > 0 && ordering.compare(a.get(p).second(), a.get(n).second()) > 0) {
            Pair e = a.get(p); a.set(p, a.get(n)); a.set(n, e);
            h.put(a.get(n).first(), n);
            return bubbleUp(p);
        } else {
            h.put(a.get(n).first(), n);
            return n;
        }
    }

    public int bubbleDown(int n) {
        int c = n;
        if (2*n + 1 < a.size() && ordering.compare(a.get(c).second(), a.get(2*n + 1).second()) > 0) {
            c = 2*n + 1;
        }
        if (2*n + 2 < a.size() && ordering.compare(a.get(c).second(), a.get(2*n + 2).second()) > 0) {
            c = 2*n + 2;
        }
        if (c != n) {
            Pair e = a.get(c); a.set(c, a.get(n)); a.set(n, e);
            h.put(a.get(n).first(), n);
            return bubbleDown(c);
        } else {
            h.put(a.get(n).first(), n);
            return n;
        }
    }
}
