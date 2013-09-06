//package com.henry4j;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.stream.Stream;
//
//public class Trie<K, V> {
//
//    //-------------------------------------------------------------
//    // Inner interfaces
//    //-------------------------------------------------------------
//
//    public interface BiBlock<U, V> {
//        void apply(U u, V v);
//    }
//
//    public interface BiPredicate<U, V> {
//        boolean apply(U u, V v);
//    }
//
//    //-------------------------------------------------------------
//    // Private fields
//    //-------------------------------------------------------------
//
//    private V value;
//    private final Map<K, Trie<K, V>> children = new HashMap();
//
//    public V value() { return value; }
//    public Trie value(V value) { this.value = value; return this; }
//
//    //-------------------------------------------------------------
//    // Factory methods
//    //-------------------------------------------------------------
//
//    public static <K, V> Trie<K, V> of() { return new Trie(); }
//
//    //-------------------------------------------------------------
//    // Public methods: 'map', 'values', 'dfs', and 'longestCommonSubstring'
//    //-------------------------------------------------------------
//
//    public Trie<K, V> map(CharSequence chars) {
//        @SuppressWarnings("unchecked")
//        Iterable<K> keys = (Iterable<K>)Arrays.asList(chars);
//        return map(keys);
//    }
//
//    public Trie<K, V> map(Iterable<K> keys) {
//        Iterator<K> keyset = keys.iterator();
//        if (keyset.hasNext()) {
//            K key = keyset.next();
//            Trie<K, V> trie = children.get(key);
//            if (null == trie) {
//                children.put(key, trie = new Trie());
//            }
//            return trie.map(Iterables.skip(keys, 1));
//        } else {
//            return this;
//        }
//    }
//
//    public Iterable<V> values() {
//        Iterable<V> iterable = null == value ? Arrays.asList() : Arrays.asList(value);
//        for (Trie trie : children.values()) {
//            iterable = Iterables.concat(iterable, trie.values());
//        }
//        return iterable;
//    }
//
////    public void dfs(BiPredicate<Iterable<K>, Trie<K, V>> enter_if, BiBlock<Iterable<K>, Trie<K, V>> exit, Iterable<K> keys) {
////        if (null == enter_if || enter_if.apply(keys, this)) {
////            for (val e : children.entrySet()) {
////                e.getValue().dfs(enter_if, exit, Iterables.concat(keys, ImmutableList.of(e.getKey())));
////            }
////            if (null != exit) {
////                exit.apply(keys, this);
////            }
////        }
////    }
//
////    public static ImmutableList<String> longestCommonSubstring(final String... args) { // contains `k` strings.
////        val suffixTree = Trie.<Character, Integer>of();
////        for (int i = 0; i < args.length; i++) {
////            for (int j = 0; j < args[i].length(); j++) {
////                suffixTree.map(args[i].substring(j)).value(i);
////            }
////        }
////
////        val memos = new HashMap<Trie<Character, Integer>, Pair<String, BitSet>>();
////        val postorderProc = new BiBlock<Iterable<Character>, Trie<Character,Integer>>() {
////            public void apply(Iterable<Character> chars, Trie<Character, Integer> trie) {
////                val bits = new BitSet(args.length); // contains `k` bits.
////                if (null != trie.value) {
////                    bits.set(trie.value);
////                }
////                for (val child : trie.children.values()) { // does `union` of all bitsets of children.
////                    bits.or(memos.get(child).second());
////                }
////                memos.put(trie, Pair.of(Joiner.on("").join(chars), bits));
////            }
////        };
////
////        suffixTree.dfs(null /* preorderProc */, postorderProc, ImmutableList.<Character>of() /* keys */);
////
////        return memos.values() // e.g. contains ['abab', {0}], ['baba', {1}], ['aba', {0, 1}], ['bab', {0, 1}], ['ba', {0, 1}]...
////                .select(Functions.<String>allBitsSet(args.length)) // selects pairs of which bits are fully set.
////                .map(Pair.<String, BitSet>firstOf2()) // maps pairs into strings.
////                .maxima(Functions.length()); // select max substrings, e.g. substring "aba", "bab" given string "abab", "baba".
////    }
//}