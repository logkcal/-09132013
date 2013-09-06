package com.henry4j;

public class Pair<U, V> { // design patterns: immutable data structure, and fluent interface.
    final private U first;
    final private V second;
    
    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }

    U first() { return first; }
    V second() { return second; }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair otherPair = (Pair)other;
            return first == otherPair.first && second == otherPair.second;
        } else {
            return false;
        }
    }
}