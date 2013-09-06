package com.henry4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparators;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Streams;
import static java.util.stream.Streams.*;

public class Beans {
    public static class Search {
//          def self.backtrack(candidate, expand_out, reduce_off)
//    unless reduce_off.call(candidate)
//      expand_out.call(candidate).each do |e|
//        candidate.push e
//        backtrack(candidate, expand_out, reduce_off)
//        candidate.pop
//      end
//    end
//  end
//        public static <E> Object combination(E[] ary, int n) {
//            List<List<E>> combinations = new ArrayList<>();
//            Function<Deque<E>, Iterable<E>> expendOut = c -> {
//                return Arrays.asList(ary[c.size()], null);
//            };
//            Predicate<Deque<E>> reduceOff = c -> {
//                if (ary.length == c.size()) {
//                    Streams.
//                    combinations.add(Arrays.stream(c.to()).filter(e -> null != e).toArray());
//                    return true;
//                } else {
//                    return false;
//                }
//            };
//            return null;
//        }

        public static <E> void backtrack(Deque<E> candidate, Function<Deque<E>, Iterable<E>> expendOut, Predicate<Deque<E>> reduceOff) {
            if (!reduceOff.test(candidate)) {
                expendOut.apply(candidate).forEach(e -> {
                    candidate.push(e);
                    backtrack(candidate, expendOut, reduceOff);
                    candidate.pop();
                });
            }
        }
    }
    
    public static class LruCache<K, V> {
        final Map<K, DNode<Pair<K, V>>> map = new HashMap<>();
        final int capacity;
        DNode<Pair<K, V>> head, tail;

        public LruCache(int capacity) {
            this.capacity = capacity;
        }

        public LruCache put(K k, V v) {
            if (map.containsKey(k)) {
                removeNode(map.get(k));
            }
            offerNode(new DNode<>(new Pair<>(k, v)));
            map.put(k, tail);
            while (map.size() > capacity) {
                map.remove(pollNode().value.first());
            }
            return this;
        }

        public V get(K k) {
            if (map.containsKey(k)) {
                removeNode(map.get(k));
                offerNode(map.get(k));
                return tail.value.second();
            } else {
                return null;
            }
        }

        public int size() {
            return map.size();
        }

        private void removeNode(DNode<Pair<K, V>> node) {
            if (head != node) {
                node.prev.next = node.next;
            } else {
                (head = head.next).prev = null;
            }
            if (tail != node) {
                node.next.prev = node.prev;
            } else {
                (tail = tail.prev).next = null;
            }
        }

        private void offerNode(DNode<Pair<K, V>> node) {
            node.next = null;
            node.prev = tail;
            if (null != tail) {
                tail.next = node;
                tail = tail.next;
            } else {
                head = tail = node;
            }
        }

        private DNode<Pair<K, V>> pollNode() {
            if (null != head) {
                DNode<Pair<K, V>> poll = head;
                if (null != head.next) {
                    (head = head.next).prev = null;
                } else {
                    head = tail = null;
                }
                return poll;
            } else {
                return null;
            }
        }

        public Pair<K, V>[] toArray() {
            Pair<K, V>[] array = new Pair[map.size()];
            DNode<Pair<K, V>> peek = head;
            for (int i = 0; i < map.size(); i++) {
                array[i] = peek.value;
                peek = peek.next;
            }
            return array;
        }

        public static class DNode<E> {
            DNode<E> prev;
            DNode<E> next;
            final E value;

            public DNode(E value) {
                this.value = value;
            }
        }
    }

    public static class Graph {
        public static boolean hasCycle(Edge[][] g, boolean directed) {
            return Arrays.indices(g).anyMatch(v -> {
                BitSet entered = new BitSet(g.length);
                BitSet exited = new BitSet(g.length);
                int[] treeEdges = generateInt(() -> -1).limit(g.length).toArray(); // parent map
                int[] backEdges = generateInt(() -> -1).limit(g.length).toArray();
                Enter enter = x -> {
                    boolean b;
                    if (b = !entered.get(x)) {
                        entered.set(x);
                    }
                    return b;
                };
                Exit exit = x -> exited.set(x);
                Cross cross = (x, e) -> {
                    if (!entered.get(e.y)) {
                        treeEdges[e.y] = x; // x becomes the parent of y in DFS.
                    } else if ((!directed && treeEdges[x] != e.y)
                                || (directed && !exited.get(e.y))) {
                        backEdges[e.y] = x;
                    }
                };
                dfs(v, g, enter, exit, cross);
                return Arrays.stream(backEdges).anyMatch(e -> -1 != e);
            });
        }

        public static boolean isTwoColorable(Edge[][] g) {
            int[] colors = new int[g.length];
            boolean[] bipartite = {true};
            BitSet entered = new BitSet(g.length);
            Enter enter = x -> {
                boolean b;
                if (b = !entered.get(x)) {
                    entered.set(x);
                }
                return b;
            };
            Cross cross = (x, e) -> {
                bipartite[0] = bipartite[0] && colors[x] != colors[e.y];
                colors[e.y] = colors[x] == 1 ? 2 : 1;
            };
            Arrays.indices(g).forEach(x -> {
                if (!entered.get(x)) {
                    colors[x] = 1;
                    dfs(x, g, enter, null, cross);
                    Arrays.fill(colors, 0);
                }
            });
            return bipartite[0];
        }

        public static void dfs(int v, Edge[][] g, Enter enter, Exit exit, Cross cross) {
            if (null == enter || enter.test(v)) {
                Arrays.stream(g[v]).forEach(e -> {
                    if (null != cross) {
                        cross.accept(v, e);
                    }
                    dfs(e.y, g, enter, exit, cross);
                });
                if (null != exit) {
                    exit.accept(v);
                }
            }
        }

        public interface Exit extends IntConsumer {}
        public interface Enter extends IntPredicate {}
        public interface Cross {
            public void accept(int v, Edge e);
        }
    }

    public static class Edge {
        int y;
        int weight;

        public Edge(int y) {
            this(y, 1);
        }

        public Edge(int y, int weight) {
            this.y = y;
            this.weight = weight;
        }

        int y() {
            return y;
        }

        int weight() {
            return weight;
        }
    }

    public static class BNode<E extends Comparable<? super E>> {
        BNode<E> left;
        BNode<E> right;
        E value;

        public BNode(E value, BNode<E> left, BNode<E> right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public static <E extends Comparable<? super E>> BNode of(E... values) {
                return of(values, 0, values.length - 1);
        }

        private static <E extends Comparable<? super E>> BNode of(E[] values, int lbound, int ubound) {
            if (lbound <= ubound) {
                int pivot = (lbound + ubound) / 2;
                return new BNode(values[pivot], of(values, lbound, pivot-1), of(values, pivot+1, ubound));
            } else {
                return null;
            }
        }

        public E value() {
            return value;
        }

        public BNode left() {
            return left;
        }

        public BNode right() {
            return right;
        }

        public List<BNode<E>> children() {
            return Arrays.asList(left, right).stream().filter(c -> null != c).collect(Collectors.toList());
        }

        public boolean isOrdered() {
            final boolean[] ordered = {true};
            final BNode[] prev = {null};
            Consumer<BNode> process = e -> {
                ordered[0] = ordered[0]
                             && (null == prev[0]
                                 || prev[0].value.compareTo(e.value) < 0);
                prev[0] = e;
            };
            order(process, e -> ordered[0], null);
            return ordered[0];
        }

        public static BNode<Integer> maxsumSubtree(BNode<Integer> root) {
            BNode[] max = new BNode[1];
            Map<BNode, Integer> sums = new HashMap<>();
            Consumer<BNode<Integer>> exit = e -> {
                sums.put(e, e.children().stream().map(c -> sums.get(c)).reduce(e.value, Integer::sum));
                if (null == max[0] || sums.get(e) > sums.get(max[0])) {
                    max[0] = e;
                }
            };
            root.dfs(null, exit);
            return max[0];
        }

        public void order(Consumer<BNode> process, Function<BNode, Boolean> enterIff, Consumer<BNode> exit) {
            if (null == enterIff || enterIff.apply(this)) {
                if (null != left) {
                    left.order(process, enterIff, exit);
                }
                if (null != process) {
                    process.accept(this);
                }
                if (null != right) {
                    right.order(process, enterIff, exit);
                }
                if (null != exit) {
                    exit.accept(this);
                }
            }
        }

        public void dfs(Function<BNode<E>, Boolean> enterIff, Consumer<BNode<E>> exit) {
            if (null == enterIff || enterIff.apply(this)) {
                if (null != left) {
                    left.dfs(enterIff, exit);
                }
                if (null != right) {
                    right.dfs(enterIff, exit);
                }
                if (null != exit) {
                    exit.accept(this);
                }
            }
        }

        public void bfs(Function<BNode<E>, Boolean> enterIff, Consumer<BNode<E>> exit) {
            Queue<BNode> q = new LinkedList();
            q.offer(this);
            while (!q.isEmpty()) {
                BNode<E> v = q.poll();
                if (null == enterIff || enterIff.apply(v)) {
                    v.children().forEach(e -> q.offer(e));
                    if (null != exit) {
                        exit.accept(v);
                    }
                }
            }
        }

        public BNode toDoublyLinkedList() {
            BNode[] head = new BNode[1], pred = new BNode[1];
            Consumer<BNode<E>> exit = e -> {
                if (null != pred[0]) {
                    pred[0].right = e;
                } else {
                    head[0] = e;
                }
                e.left = pred[0];
                e.right = null;
                pred[0] = e;
            };
            this.bfs(null, exit);
            return head[0];
        }
    }

    public static class Geometry {
        public static int[][] skyline(Queue<int[]> buildingsByL) {
            SortedMap<Integer, Set<int[]>> buildingsByH = new TreeMap<>();
            PriorityQueue<int[]> buildingsByR = new PriorityQueue<>(1, (a, b) -> a[2] - b[2]);
            List<int[]> skyline = new ArrayList<>();
            int height = 0;
            while (!buildingsByL.isEmpty() || !buildingsByR.isEmpty()) {
                if (buildingsByR.isEmpty() 
                    || (!buildingsByL.isEmpty() 
                        && buildingsByL.peek()[0] < buildingsByR.peek()[2])) {
                    int[] b = buildingsByL.poll();
                    buildingsByR.offer(b);
                    Set<int[]> values = buildingsByH.get(b[1]);
                    if (null == values) {
                        buildingsByH.put(b[1], values = new HashSet<>());
                    }
                    values.add(b);
                    if (height != buildingsByH.lastKey()) {
                        height = buildingsByH.lastKey();
                        skyline.add(new int[] {b[0], height});
                    }
                } else {
                    int[] b = buildingsByR.poll();
                    buildingsByH.get(b[1]).remove(b);
                    if (buildingsByH.get(b[1]).isEmpty()) {
                        buildingsByH.remove(b[1]);
                    }
                    if (buildingsByH.isEmpty() || height != buildingsByH.lastKey()) {
                        height = buildingsByH.isEmpty() ? 0 : buildingsByH.lastKey();
                        skyline.add(new int[] {b[2], height});
                    }
                }
            }
            return skyline.toArray(new int[skyline.size()][]);
        }
    }

    public static class DP {
        // comparable to http://en.wikipedia.org/wiki/Levenshtein_distance
        public static int editDistance(String s, String t, boolean whole) {
            Integer[][] memos = new Integer[s.length() + 1][];
            for (int i = 0; i < memos.length; i++) {
                memos[i] = new Integer[t.length() + 1];
            }
            BiFunction<Integer, Integer, Integer>[] maps = new BiFunction[1];
            maps[0] = (i, j) -> {
                if (null == memos[i][j]) {
                    if (0 == i) {
                        memos[i][j] = whole ? j : 0;
                    } else if (0 == j) {
                        memos[i][j] = i;
                    } else {
                        int diff = (s.charAt(i-1) == t.charAt(j-1) ? 0 : 1);
                        memos[i][j] = Arrays.asList(maps[0].apply(i-1, j) + 1,
                                        maps[0].apply(i, j-1) + 1,
                                        maps[0].apply(i-1, j-1) + diff)
                                .stream()
                                .min(Comparators.naturalOrder())
                                .orElse(0);
                    }
                }
                return memos[i][j];
            };
            return maps[0].apply(s.length(), t.length());
        }

        // http://www.algorithmist.com/index.php/Longest_Increasing_Subsequence
        // http://en.wikipedia.org/wiki/Longest_increasing_subsequence
        // http://stackoverflow.com/questions/4938833/find-longest-increasing-sequence/4974062#4974062
        // http://wordaligned.org/articles/patience-sort
        // http://architects.dzone.com/articles/algorithm-week-longest
        public static int[] longestIncreasingSubsequence(int... ary) {
            List<int[]> memos = new ArrayList<>();
            for (int i = 0; i < ary.length; i++) { // ary[i]
                int j = memos.size() - 1;
                for ( ; j >= 0 && memos.get(j)[memos.get(j).length - 1] > ary[i]; j--) {
                }
                if (j == memos.size() - 1) { // needs to append to the last memo.
                    memos.add(null);
                }
                if (j == -1) {
                    memos.set(0, new int[] { ary[i] });
                } else {
                    memos.set(j + 1, concat(memos.get(j), ary[i]));
                }
            }
            return memos.get(memos.size() - 1);
        }

        public static int[] minimalCoins(int amount, int... denominations) {
            Map<Integer, int[]> memos = new HashMap<>();
            @SuppressWarnings("unchecked")
            IntFunction<int[]>[] maps = new IntFunction[1];
            maps[0] = k -> {
                int[] ia = memos.get(k);
                if (null == ia) {
                    memos.put(k, ia = Arrays.stream(denominations)
                            .filter(d -> d <= k)
                            .map(d -> concat(maps[0].apply(k - d), d))
                            .min(Comparators.comparing((int[] e) -> e.length))
                            .orElse(new int[0]));
                }
                return ia;
            };
            return maps[0].apply(amount);
        }

        public static int[] concat(int[] a, int... b) {
            int[] c = Arrays.copyOf(a, a.length + b.length);
            System.arraycopy(b, 0, c, a.length, b.length);
            return c;
        }
    }

    // http://pages.ebay.com/help/sell/fees.html
    // the basic cost of selling an item is the insertion fee plus the final value fee.
    // * $0.5 for buy it now or fixed price format listings
    // * 7% for initial $50 (max: $3.5), 5% for next $50 - $1000 (max: $47.5), and 2% for the remaining.
    // http://docs.oracle.com/javase/6/docs/api/java/util/TreeMap.html
    // formulas = new TreeMap() {{ put($0, Pair.of($0, 7%)); put($50, Pair.of($3.5, 5%)); put($1000, Pair.of($51, 2%)) }}
    // sale = $1100; formula = formulas.floorEntry(sale);
    // fees = 0.5 /* insertion */ + formula.value().first() /* final base */ + formula.value().second() * (sale - formula.key()) /* final addition */
    public static class Trees {
        static double ebaySalesFee() {
            TreeMap<Double, Pair<Double, Double>> formulas = new TreeMap() {{
                put(0, new Pair(0.0, 0.07));
                put(50, new Pair(3.5, 0.05));
                put(1000, new Pair(51.0, 0.02));
            }};
            double sales = 1100;
            Map.Entry<Double, Pair<Double, Double>> formula = formulas.floorEntry(sales);
            double insertion = 0.5;
            double finalBase = formula.getValue().first();
            double finalAddition = (sales - formula.getKey()) * formula.getValue().second();
            return insertion + finalBase + finalAddition;
        }
    }

    public static class Strings {
        public static int rabinKarp(String text, String pattern) {
            char[] t = text.toCharArray();
            char[] p = pattern.toCharArray();
            int n = t.length;
            int m = p.length;
            int hashP = hash(p, 0, m);
            int hashT = hash(t, 0, m);
            if (hashP == hashT) {
                return 0;
            }
            int a_to_m = 1; // 31 ^ m
            for (int i = 0; i < m; i++) {
                a_to_m = (a_to_m << 5) - a_to_m;
            }
            for (int i = 0; i < n - m; i++) {
                if (hashP == (hashT = hash(t, i, m, a_to_m, hashT))) {
                    return 1 + i;
                }
            }
            return -1;
        }

        public static int hash(char[] chars, int offset, int length, int a_to_m, int hash) {
            hash = (hash << 5) - hash;
            return hash - a_to_m * chars[offset] + chars[offset + length];
        }

        public static int hash(char[] chars, int offset, int length) {
            int hash = 0;
            for (int i = 0; i < length; i++) {
                hash = (hash << 5) - hash + chars[offset + i];
            }
            return hash;
        }
    }
}
