package com.henry4j;

import static lombok.Yield.yield;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import lombok.Actions.Action1;
import lombok.Functions.Function1;
import lombok.Predicates.Predicate1;
import lombok.val;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;

public class Extensions {
    public static <U, V> com.google.common.base.Function<U, V> guava(final Function1<U, V> f1) {
        return new com.google.common.base.Function<U, V>() {
            public V apply(U u) {
                return f1.apply(u);
            }
        };
    }

    public static <E> Iterable<E> iterable(final Collection<E> c) {
        return (Iterable<E>)c;
    }

    public static <V, K extends Comparable<K>> ImmutableList<V> maxima(Iterable<V> values, Function1<V, K> by) {
        val indexed = Multimaps.index(values, guava(by));
        val indices = indexed.keySet();
        return indexed.get(Ordering.natural().max(indices));
    }

    public static <E> Iterable<E> concat(final Iterable<E> a, final Iterable<E> b) {
        return Iterables.concat(a, b);
    }

    public static <U, V> Iterable<V> map(final Iterable<U> from, final Function1<U, V> function) {
        for (U u : from) {
            yield(function.apply(u));
        }
    }

    public static <E> Iterable<E> select(final Iterable<E> from, final Predicate1<E> predicate) {
        for (E e : from) {
            if (predicate.apply(e)) {
                yield(e);
            }
        }
    }

    public static <E> Iterable<E> reject(final Iterable<E> from, final Predicate1<E> predicate) {
        for (E e : from) {
            if (!predicate.apply(e)) {
                yield(e);
            }
        }
    }

    public static <E> void each(final Iterable<E> from, final Action1<E> process) {
        for (E e : from) {
            process.apply(e);
        }
    }

    public static <E> Iterable<E> times(final E e, final int n) {
        for (int i = 0; i < n; i++) {
            yield(e);
        }
    }

    public static <T, V> Iterable<V> map(Iterable<T> from, Function<T, V> map) {
        return Iterables.transform(from, map);
    }

    public static <E> Iterable<E> select(Iterable<E> from, Predicate<E> predicate) {
        return Iterables.filter(from, predicate);
    }

    public static <K> Map<K, Integer> decrement(Map<K, Integer> m, K key) {
        if (m.get(key) > 1) {
            m.put(key, m.get(key) - 1);
        } else {
            m.remove(key);
        }
        return m;
    }

    public static <K> Map<K, Integer> increment(Map<K, Integer> m, K key) {
        if (!m.containsKey(key)) {
            m.put(key, 1);
        } else {
            m.put(key, 1 + m.get(key));
        }
        return m;
    }

    public static Map<Character, List<Integer>> indicesByChar(CharSequence s) {
        // O(n)
        Map<Character, List<Integer>> indicesByChar = new HashMap<Character, List<Integer>>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!indicesByChar.containsKey(c)) {
                indicesByChar.put(c, new ArrayList<Integer>());
            }
            indicesByChar.get(c).add(i);
        }
        return indicesByChar;
    }

    public static SortedMap<Character, Integer> countsByChar(CharSequence s) {
        // O(n)
        Map<Character, Integer> countsByChar = new HashMap<Character, Integer>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            increment(countsByChar, c);
        }
        return new TreeMap<Character, Integer>(countsByChar); // O(k logk)
    }

    public static <T> int bsearchFirstOf(List<T> list, int fromIndex, int toIndex, Comparable<T> comparable) {
        if (fromIndex < toIndex) {
            int mid = (fromIndex + toIndex) / 2;
            int comp = comparable.compareTo(list.get(mid));
            if (comp < 0) {
                return bsearchFirstOf(list, fromIndex, mid - 1, comparable);
            } else if (comp > 0) {
                return bsearchFirstOf(list, mid + 1, toIndex, comparable);
            } else {
                return bsearchFirstOf(list, fromIndex, mid, comparable);
            }
        } else {
            return 0 == comparable.compareTo(list.get(fromIndex)) ? fromIndex : -1;
        }
    }

    public static <T> int bsearchLastOf(List<T> list, int fromIndex, int toIndex, Comparable<T> comparable) {
        if (fromIndex < toIndex) {
            int mid = (1 + fromIndex + toIndex) / 2;
            int comp = comparable.compareTo(list.get(mid));
            if (comp < 0) {
                return bsearchLastOf(list, fromIndex, mid - 1, comparable);
            } else if (comp > 0) {
                return bsearchLastOf(list, mid + 1, toIndex, comparable);
            } else {
                return bsearchLastOf(list, mid, toIndex, comparable);
            }
        } else {
            return 0 == comparable.compareTo(list.get(fromIndex)) ? fromIndex : -1;
        }
    }

    public static char[] selectLowerCases(char... chars) {
        BitSet bits = new BitSet(26);
        for (char c : chars) { // O(k)
            bits.set(c - 'a');
        }
        char[] sorted = new char[bits.cardinality()];
        for (int i = 0, j = 0; i < bits.length(); i++) { // O(max(26, k))
            if (bits.get(i)) {
                sorted[j++] = (char)(i + 'a');
            }
        }
        return sorted;
    }

    public static String subsequence(CharSequence s, BitSet bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (bits.get(i)) {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    public static <T> Comparable<T> lessThan(final Comparable<T> comparable) {
        return new Comparable<T>() {
            public int compareTo(T other) {
                return comparable.compareTo(other) > 0 ? 0 : -1;
            }
        };
    }

    public static <T> Comparable<T> greaterThan(final Comparable<T> comparable) {
        return new Comparable<T>() {
            public int compareTo(T other) {
                return comparable.compareTo(other) < 0 ? 0 : 1;
            }
        };
    }
}
