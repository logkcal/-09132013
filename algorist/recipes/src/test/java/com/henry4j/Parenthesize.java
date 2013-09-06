package com.henry4j;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import lombok.ExtensionMethod;
import lombok.val;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

@ExtensionMethod({ Extensions.class })
public class Parenthesize {
    public static List<Iterable<Integer>> evaluation(final int begin, final int length) {
        if (length == 0) {
            return ImmutableList.of();
        } else {
            val list = ImmutableList.<Iterable<Integer>>builder();
            for (int i = 0; i < length; i++) {
                List<Iterable<Integer>> heads = evaluation(begin, i);
                List<Iterable<Integer>> tails = evaluation(begin+i+1, length-i-1);
                val pivot = ImmutableList.of(begin+i);
                if (heads.isEmpty() && tails.isEmpty()) {
                    list.add(pivot);
                } else if (heads.isEmpty()) {
                    for (Iterable<Integer> tail : tails) {
                        list.add(tail.concat(pivot));
                    }
                } else if (tails.isEmpty()) {
                    for (Iterable<Integer> head : heads) {
                        list.add(head.concat(pivot));
                    }
                } else {
                    for (Iterable<Integer> head : heads) {
                        for (Iterable<Integer> tail : tails) {
                            list.add(head.concat(tail).concat(pivot));
                        }
                    }
                }
            }
            return list.build();
        }
    }

    public static int parenthesize(int[] operands, Operator[] operators, int expected) {
        int ways = 0;
        for (Iterable<Integer> evaluation : evaluation(0, operators.length)) {
            int actual = 0;
            int[] work = Arrays.copyOf(operands, operands.length);
            for (int i : evaluation) {
                switch (operators[i]) {
                case And:
                    actual = work[i] = work[i+1] = (1 == work[i]) && (1 == work[i+1]) ? 1 : 0;
                    break;
                case Or:
                    actual = work[i] = work[i+1] = (1 == work[i]) || (1 == work[i+1]) ? 1 : 0;
                    break;
                case Xor:
                    actual = work[i] = work[i+1] = (work[i] != work[i+1]) ? 1 : 0;
                    break;
                }
            }
            if (actual == expected) {
                ways++;
            }
        }
        return ways;
    }

    public static enum Operator {
        And, Or, Xor;
    }

    @Test
    public void testParanthesyzing() {
//        List<Iterable<Integer>> l2 = evaluation(0, 2);
//        List<Iterable<Integer>> l = evaluation(0, 3);
        assertThat(parenthesize(new int[] { 1, 1, 0 }, new Operator[] { Operator.Or, Operator.Or }, 0), equalTo(0));
        assertThat(parenthesize(new int[] { 1, 1, 0 }, new Operator[] { Operator.Or, Operator.Or }, 1), equalTo(2));
    }
}
