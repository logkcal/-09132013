package com.henry4j;

import static com.henry4j.Beans.DP.*;
import static com.henry4j.Beans.Geometry.*;
import static com.henry4j.Beans.Strings.*;
import static java.util.stream.Collectors.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import com.henry4j.Beans.BNode;
import com.henry4j.Beans.Edge;
import com.henry4j.Beans.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparators;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Spliterator.OfInt;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Streams;
import org.junit.Test;

public class BeansTest {
    @Test
    public void test2() {
        int[] a = {9, 8, 7, 8, 7, 6, 5 };
        int[] b = Arrays.stream(a).peek(e -> { System.out.println(e); }).toArray();
        IntStream is = Arrays.stream(a);
        int[] j = new int[1];
        OfInt sp = is.spliterator();
        sp.tryAdvance((int i) -> { j[0] = i; });
        is = Streams.intStream(sp);
        assert null != is;
        Streams.intStream(sp, characteristics)
    }

    @Test
    public void testLruCache() {
        Beans.LruCache<Integer, Character> c = new Beans.LruCache<>(3)
                .put(1, 'a').put(2, 'b').put(3, 'c');
        assertThat(c.get(1), equalTo('a'));
        assertThat(c.toArray(), equalTo(new Pair[]{new Pair(2, 'b'), new Pair(3, 'c'), new Pair(1, 'a')}));
        assertThat(c.get(2), equalTo('b'));
        assertThat(c.toArray(), equalTo(new Pair[]{new Pair(3, 'c'), new Pair(1, 'a'), new Pair(2, 'b')}));
        assertThat(c.put(4, 'd').toArray(), equalTo(new Pair[]{new Pair(1, 'a'), new Pair(2, 'b'), new Pair(4, 'd')}));
        assertNull(c.get(3));
        assertThat(c.get(1), equalTo('a'));
        assertThat(c.toArray(), equalTo(new Pair[]{new Pair(2, 'b'), new Pair(4, 'd'), new Pair(1, 'a')}));
    }

    @Test
    public void testPriorityHeap() {
        PriorityHeap<String, Integer> ph = new PriorityHeap(Comparators.reverseOrder());
        ph.offer("d", 10).offer("e", 30).offer("h", 50)
            .offer("f", 20).offer("b", 40).offer("c", 60)
            .offer("a", 80).offer("i", 90).offer("g", 70);
        ph.offer("a", 92).offer("b", 98).offer("h", 120);
        ph.offer("i",  45).offer("c", 25);
        assertThat(ph.peek(), equalTo(new Pair("h", 120)));
        assertThat(ph.poll(), equalTo(new Pair("h", 120)));
        assertThat(ph.poll(), equalTo(new Pair("b", 98)));
        assertThat(ph.poll(), equalTo(new Pair("a", 92)));
        assertThat(ph.poll(), equalTo(new Pair("g", 70)));
        assertThat(ph.poll(), equalTo(new Pair("i", 45)));
        assertThat(ph.poll(), equalTo(new Pair("e", 30)));
        assertThat(ph.poll(), equalTo(new Pair("c", 25)));
        assertThat(ph.poll(), equalTo(new Pair("f", 20)));
        assertThat(ph.poll(), equalTo(new Pair("d", 10)));
    }

    @Test
    public void testRabinKarp() {
        assertThat(rabinKarp("aabab", "ab"), equalTo(1));
        assertThat(rabinKarp("aaabcc", "abc"), equalTo(2));
        assertThat(rabinKarp("aaabc", "abc"), equalTo(2));
        assertThat(rabinKarp("abcc", "abc"), equalTo(0));
        assertThat(rabinKarp("abc", "abc"), equalTo(0));
        assertThat(rabinKarp("abc", "xyz"), equalTo(-1));
        assertThat(rabinKarp("abcc", "xyz"), equalTo(-1));
    }

    @Test
    public void testEditDistance() {
        assertThat(editDistance("kitten", "sitting", true), equalTo(3));
    }

    @Test
    public void testTwoColorable() {
        // graph: B1 ― A0
        //        |    |
        //        C2 ― D3
        List<Edge[]> edges = new ArrayList<>();
        edges.add(new Edge[] {new Edge(1), new Edge(3)}); // A0 - B1, A0 - D3
        edges.add(new Edge[] {new Edge(0), new Edge(2)}); // B1 - A0, B1 - C2
        edges.add(new Edge[] {new Edge(1), new Edge(3)}); // C2 - B1, C2 - D3
        edges.add(new Edge[] {new Edge(0), new Edge(2)}); // D3 - A0, D3 - C2
        assertTrue(Graph.isTwoColorable(edges.toArray(new Edge[edges.size()][])));

        // graph: B1 ― A0
        //        |  X
        //        C2 ― D3
        edges = new ArrayList<>();
        edges.add(new Edge[] {new Edge(1), new Edge(2)}); // A0 - B1, A0 - D2
        edges.add(new Edge[] {new Edge(0), new Edge(2), new Edge(3)}); // B1 - A0, B1 - C2, B1 - D3
        edges.add(new Edge[] {new Edge(0), new Edge(1), new Edge(3)}); // C2 - A0, C2 - B1, C2 - D3
        edges.add(new Edge[] {new Edge(1), new Edge(2)}); // D3 - B1, D3 - C2
        assertFalse(Graph.isTwoColorable(edges.toArray(new Edge[edges.size()][])));
    }

    @Test
    public void testFastMajorityVote() {
        // http://www.cs.utexas.edu/~moore/best-ideas/mjrty/index.html
        assertThat(majority("AAACCBBCCCBCC"), equalTo('C'));
        assertThat(majority("AAAACBBCCCBCC"), equalTo('\0'));
        assertThat(majority("AB"), equalTo('\0'));
        assertThat(majority("ABC"), equalTo('\0'));
    }

    public static char majority(String s) {
        BiFunction<int[], Integer, int[]> reducer = (m, e) ->
            (m[1] == 0) ? new int[] {e, 1}
                        : (m[0] == e) ? new int[] {e, m[1] + 1} 
                                      : new int[] {e, m[1] - 1};
        int[] chosen = s.chars().boxed().reduce(new int[]{0, 0}, reducer, (a, b) -> a);
        if (chosen[1] != 0 
            && s.chars()
                .map(e -> e == chosen[0] ? 1 : 0)
                .reduce(0, Integer::sum) > (s.length() / 2)) {
            return (char)chosen[0];    
        } else {
            return 0;
        }
    }

    @Test
    public void testFindBinaryTreeStringDepth() {
        assertThat(findBinaryTreeStringDepth("(00)"), equalTo(0));
        assertThat(findBinaryTreeStringDepth("((00)0)"), equalTo(1));
        assertThat(findBinaryTreeStringDepth("((00)(00))"), equalTo(1));
        assertThat(findBinaryTreeStringDepth("((00)(0(00)))"), equalTo(2));
        assertThat(findBinaryTreeStringDepth("((00)(0(0(00))))"), equalTo(3));
        assertThat(findBinaryTreeStringDepth("x"), equalTo(-1));
        assertThat(findBinaryTreeStringDepth("0"), equalTo(-1));
        assertThat(findBinaryTreeStringDepth("()"), equalTo(-1));
        assertThat(findBinaryTreeStringDepth("(0)"), equalTo(-1));
        assertThat(findBinaryTreeStringDepth("(00)x"), equalTo(-1));
        assertThat(findBinaryTreeStringDepth("(0p)"), equalTo(-1));
    }

    public static int findBinaryTreeStringDepth(String s) {
        Stack<Integer> childrens = new Stack<>();
        int children = 0;
        int maxDepth = 0;
        for (char c : s.toCharArray()) {
            if ('(' == c) {
                maxDepth = Math.max(maxDepth, childrens.size());
                childrens.push(children + 1);
                children = 0;
            } else if (childrens.isEmpty()) {
                return -1;
            } else if (')' == c) {
                if (2 != children) {
                    return -1;
                }
                children = childrens.pop();
            } else if ('0' == c) {
                children++;
            } else {
                return -1;
            }
        }
        return childrens.isEmpty() ? maxDepth : - 1;
    }

    @Test
    public void testHasCycleInDirectedNUndirectedGraphs() {
        // graph: B1 ← C2 → A0
        //         ↓  ↗
        //        D3 ← E4
        List<Edge[]> edges = new ArrayList<>();
        edges.add(new Edge[0]);
        edges.add(new Edge[] { new Edge(3) });
        edges.add(new Edge[] { new Edge(0), new Edge(1) });
        edges.add(new Edge[] { new Edge(2) });
        edges.add(new Edge[] { new Edge(3) });
        assertTrue(Graph.hasCycle(edges.toArray(new Edge[edges.size()][]), true));

        // graph: B1 ← C2 → A0
        //         ↓    ↓
        //        D3 ← E4
        edges.clear();
        edges.add(new Edge[0]);
        edges.add(new Edge[] { new Edge(3) });
        edges.add(new Edge[] { new Edge(0), new Edge(1), new Edge(4) });
        edges.add(new Edge[0]);
        edges.add(new Edge[] { new Edge(3) });
        assertFalse(Graph.hasCycle(edges.toArray(new Edge[edges.size()][]), true));

        // undirected graph: A0 - B1 - C2
        edges.clear();
        edges.add(new Edge[] { new Edge(1) }); // A0 → B1
        edges.add(new Edge[] { new Edge(0), new Edge(2) }); // B1 → A0, B1 → C2
        edges.add(new Edge[] { new Edge(1) }); // C2 → B1
        assertFalse(Graph.hasCycle(edges.toArray(new Edge[edges.size()][]), false));

        // undirected graph: A0 - B1 - C2 - A0
        edges.clear();
        edges.add(new Edge[] { new Edge(1), new Edge(2) }); // A0 → B1, A0 → C2
        edges.add(new Edge[] { new Edge(0), new Edge(2) }); // B1 → A0, B1 → C2
        edges.add(new Edge[] { new Edge(1), new Edge(0) }); // C2 → B1, C2 → A0
        assertTrue(Graph.hasCycle(edges.toArray(new Edge[edges.size()][]), false));
    }

    @Test
    public void testToDoublyLinkedList() {
        // http://www.youtube.com/watch?v=WJZtqZJpSlQ
        // http://codesam.blogspot.com/2011/04/convert-binary-tree-to-double-linked.html
        // tree:   1
        //       2    3
        //      4 5  6 7
        BNode<Integer> tree = BNode.<Integer>of(4, 2, 5, 1, 6, 3, 7);
        BNode<Integer> read = tree.toDoublyLinkedList();
        List<Integer> values = new ArrayList<>();
        while (null != read) {
            values.add(read.value());
            read = read.right();
        }
        assertThat(values.toArray(new Integer[values.size()]), equalTo(new Integer[] {1, 2, 3, 4, 5, 6, 7}));
    }

    @Test
    public void testMaxsumSubtree() {
        // tree:  -2
        //          1
        //        3  -2
        //     -1
        BNode e = new BNode(-1, null, null);
        BNode c = new BNode(3, e, null);
        BNode d = new BNode(-2, null, null);
        BNode b = new BNode(1, c, d);
        BNode a = new BNode(-2, b, null);
        assertThat(BNode.maxsumSubtree(a), equalTo(c));
    }

    @Test
    public void testIsOrdered() {
        assertTrue(BNode.of(1, 2, 3, 4, 5, 6, 7).isOrdered());
        assertFalse(BNode.of(1, 2, 3, 4, 8, 6, 7).isOrdered());
    }

    @Test
    public void testSkyline() { 
        int[][] buildings = {{1,11,5}, {2,6,7}, {3,13,9}, {12,7,16}, {14,3,25}, {19,18,22}, {23,13,29}, {24,4,28}};
        int[][] skyline = {{1, 11}, {3, 13}, {9, 0}, {12, 7}, {16, 3}, {19, 18}, {22, 3}, {23, 13}, {29, 0}};
        assertThat(skyline(Arrays.stream(buildings).collect(toCollection(LinkedList::new))), equalTo(skyline));
    }

    @Test
    public void testLongestIncreasingSubsequence() {
        assertThat(longestIncreasingSubsequence(1, 3, 3, 2), equalTo(new int[] {1, 3, 3}));
        assertThat(longestIncreasingSubsequence(7, 8, 1, 5, 6, 2, 3), equalTo(new int[] {1, 2, 3}));

        int[][] circus = {{65, 100}, {90, 150}, {50, 120}, {56, 90}, {75, 190}, {60, 95}, {68, 110}, {80, 92}};
        Function<int[][], Stream<int[]>> byElement0 = e -> Arrays.stream(e).sorted((a, b) -> a[0] - b[0]);
        int[] weightsInHeightOrder = byElement0.apply(circus).map(e -> e[1]).toArray();
        assertThat(weightsInHeightOrder, equalTo(new int[] {120, 90, 95, 100, 110, 190, 92, 150}));
        int[] weightsInLongestIS = longestIncreasingSubsequence(weightsInHeightOrder);
        assertThat(weightsInLongestIS, equalTo(new int[] {90, 95, 100, 110, 150}));
        Set<Integer> weights = Arrays.stream(weightsInLongestIS).boxed().collect(toSet());
        List<int[]>  tower = byElement0.apply(circus).filter(e -> weights.contains(e[1])).collect(toList());
        int[][] expected = {{56, 90}, {60, 95}, {65, 100}, {68, 110}, {90, 150}};
        assertThat(tower.toArray(new int[tower.size()][]), equalTo(expected));
    }

    @Test
    public void testMinimalCoins() {
        assertThat(Beans.DP.minimalCoins(10, new int[] {1, 5, 7}), equalTo(new int[] { 5, 5 }));
        assertThat(Beans.DP.minimalCoins(13, new int[] {1, 5, 7}), equalTo(new int[] { 7, 5, 1 }));
        assertThat(Beans.DP.minimalCoins(14, new int[] {1, 5, 7}), equalTo(new int[] { 7, 7 }));
    }
}
