package com.henry4j;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.experimental.Accessors;

import org.junit.Test;

import com.google.common.collect.Lists;

public class FirstUniqueChar {
    public static char firstUniqueCharByHashMapAndList(Iterable<Character> stream) {
        Map<Character, Integer> countsByChar = new HashMap<Character, Integer>();
        List<Character> charset = new ArrayList<Character>(); 
        for (char c : stream) { // runs in O(n) time where n is the size of the online char stream.
            if (countsByChar.containsKey(c)) {
                countsByChar.put(c, 1 + countsByChar.get(c));
            } else {
                countsByChar.put(c, 1);
                charset.add(c);
            }
        }
        // runs in O(|charset|) time; |charset| is the size of the charset; 26 for lower-case alphabet.
        for (char c : charset) { 
            if (1 == countsByChar.get(c)) {
                return c;
            }
        }
        throw new IllegalStateException("There is no unique character in the input stream.");
    }

    public static char firstUniqueCharByLinkedHashset(Iterable<Character> stream) {
        Set<Character> charset = new HashSet<Character>();
        Set<Character> uniqueChars = new LinkedHashSet<Character>();
        for (char c : stream) { // runs in O(n) time where n is the size of the online char stream.
            if (charset.contains(c)) {
                uniqueChars.remove(c); // O(1)
            } else {
                uniqueChars.add(c); // O(1)
                charset.add(c); // O(1)
            }
        }
        return uniqueChars.iterator().next(); // O(1)
    }

    public static char firstUniqueCharByHashmap2DNode(Iterable<Character> stream) {
        Map<Character, DNode<Character>> charset = new HashMap<Character, DNode<Character>>();
        DNode<Character> head = null, tail = null;
        for (char c : stream) { // runs in O(n)
            if (null != charset.get(c)) {
                DNode<Character> node = charset.get(c);
                if (head != node) {
                    node.prev().next(node.next());
                } else {
                    (head = head.next()).prev(null);
                }
                if (tail != node) {
                    node.next().prev(node.prev());
                } else {
                    (tail = tail.prev()).next(null);
                }
                charset.put(c, null);
            } else {
                if (null == head) {
                    charset.put(c, head = tail = DNode.of(c));
                } else {
                    charset.put(c, tail = tail.next(DNode.of(c).prev(tail)).next());
                }
            }
        }
        return head.value(); // O(1)
    }

    @Data @Accessors(fluent = true)
    public static class DNode<E> {
        private DNode<E> prev;
        private DNode<E> next;
        private E value;

        public static <E> DNode<E> of(E e) {
            return new DNode<E>().value(e);
        }
    }

    @Test
    public void testFirstUniqueChar() {
        // O(n) + O(|charset|) with hashmap + array list or queue.
        assertThat(firstUniqueCharByHashMapAndList(Lists.charactersOf("interesting")), equalTo('r'));
        // O(n) + O(1) with linked hashset.
        assertThat(firstUniqueCharByLinkedHashset(Lists.charactersOf("interesting")), equalTo('r'));
        // O(n) + O(1) with linked list.
        assertThat(firstUniqueCharByHashmap2DNode(Lists.charactersOf("interesting")), equalTo('r'));
    }
}
