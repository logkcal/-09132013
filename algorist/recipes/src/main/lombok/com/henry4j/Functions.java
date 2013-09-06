package com.henry4j;

import java.util.BitSet;

import lombok.Function;

import com.google.common.base.Predicate;

public class Functions {
    @Function
    public static int length(String s) {
        return s.length();
    }

    public static final <T> Predicate<Pair<T, BitSet>> allBitsSet(final int length) {
        return new Predicate<Pair<T, BitSet>>() {
            public boolean apply(Pair<T, BitSet> pair) {
                return pair.second().cardinality() == length;
            }
        };
    }
}
