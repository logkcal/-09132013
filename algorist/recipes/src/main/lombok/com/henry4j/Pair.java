package com.henry4j;

import lombok.EqualsAndHashCode;
import lombok.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(staticName="of")
@Getter @Accessors(fluent = true)
@ToString
@EqualsAndHashCode
public class Pair<U, V> { // design patterns: immutable data structure, and fluent interface.
    final private U first;
    final private V second;

    @Function
    public static <U, V> U firstOf2(Pair<U, V> pair) {
        return pair.first();
    }
}