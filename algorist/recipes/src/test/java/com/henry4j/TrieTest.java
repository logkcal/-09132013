package com.henry4j;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import lombok.val;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TrieTest {
    @Test
    public void testBasicCRUD() {
        // a suffix tree of "bananas"
        val s = "bananas";
        val suffixTree = Trie.<Character, Integer>of();
        for (int i = 0; i < s.length(); i++) {
            suffixTree.map(s.substring(i)).value(i);
        }
        assertThat(suffixTree.values(), hasItems(0, 1, 2, 3, 4, 5, 6));

        // all indices of query strings
        val q = ImmutableList.of("b", "ba", "n", "na", "nas", "s", "bas");
        val a = ImmutableList.of(
            new Integer[] { 0 }, new Integer[] { 0 },
            new Integer[] { 4, 2 }, new Integer[] { 4, 2 },
            new Integer[] { 4 }, new Integer[] { 6 });
        for (int i = 0; i < a.size(); i++) {
            assertThat(suffixTree.map(q.get(i)).values(), hasItems(a.get(i)));
        }
        assertThat(suffixTree.map(q.get(6)).value(), nullValue());
    }

    @Test
    public void testAutoComplete() {
        // auto-complete a prefix
        val d = ImmutableList.of("the", "day", "they", "their", "they're", "them");
        Trie<String, Integer> trie = Trie.of();
        for (int i = 0; i < d.size(); i++) {
            trie.map(d.get(i)).value(i);
        }

        assertThat(trie.map("the").values(), hasItems(0, 5, 2, 4, 3));
        assertThat(trie.map("they").value(), equalTo(2));
    }

    @Test
    public void testLongestCommonSubstring() {
        // longest common substring, or palindrome
        assertThat(Trie.longestCommonSubstring("abab", "baba"), hasItems("aba", "bab"));
        assertThat(Trie.longestCommonSubstring("bananas", new StringBuilder("bananas").reverse().toString()), hasItems("anana"));
        assertThat(Trie.longestCommonSubstring("abab", "baba", "aabb"), hasItems("ab"));
    }
}
