package com.henry4j;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import lombok.Functions.Function3;
import lombok.val;

import org.junit.Test;

/*
 * Q9.11  Given a boolean expression consisting of the symbols 0, 1, &, |, and ^, and a 
 * desired boolean result value result, implement a function to count the number of ways
 * of parenthesizing the expression such that it evaluates to result.
 */
public class Q9_11 {
    @SuppressWarnings("serial")
    static final Map<Character, Function3<String, String, Integer, Integer>> handlers = 
            new HashMap<Character, Function3<String, String, Integer, Integer>>() {{
        put('|', new Function3<String, String, Integer, Integer>() {
            public Integer apply(String left, String right, Integer result) {
                int cnt = 0;
                if (result == 0) {
                    cnt = parenthesize(left, 0) * parenthesize(right, 0);
                } else {
                    cnt = parenthesize(left, 0) * parenthesize(right, 1)
                            + parenthesize(left, 1) * parenthesize(right, 0)
                            + parenthesize(left, 1) * parenthesize(right, 1);
                }
                return cnt;
            }
        });
        put('&', new Function3<String, String, Integer, Integer>() {
            public Integer apply(String left, String right, Integer result) {
                int cnt = 0;
                if (result == 0) {
                    cnt = parenthesize(left, 0) * parenthesize(right, 0)
                            + parenthesize(left, 0) * parenthesize(right, 1)
                            + parenthesize(left, 1) * parenthesize(right, 0);
                } else {
                    cnt = parenthesize(left, 1) * parenthesize(right, 1);
                }
                return cnt;
            }
        });
        put('^', new Function3<String, String, Integer, Integer>() {
            public Integer apply(String left, String right, Integer result) {
                int cnt = 0;
                if (result == 0) {
                    cnt = parenthesize(left, 0) * parenthesize(right, 0)
                            + parenthesize(left, 1) * parenthesize(right, 1);
                } else {
                    cnt = parenthesize(left, 0) * parenthesize(right, 1)
                            + parenthesize(left, 1) * parenthesize(right, 0);
                }
                return cnt;
            }
        });
    }};

    static Map<String, Integer> memos = new HashMap<String, Integer>();

    static int parenthesize(String expression, int expected) {
        if (memos.containsKey(expression))
            return memos.get(expression);
        else if (1 == expression.length())
            return expected == Integer.parseInt(expression) ? 1 : 0;
        int cnt = 0;
        for (int i = 0; i < expression.length(); i++) {
            char cursor = expression.charAt(i);
            if (cursor == '|' || cursor == '&' || cursor == '^') {
                val left = expression.substring(0, i);
                val right = expression.substring(i + 1, expression.length());
                cnt += handlers.get(cursor).apply(left, right, expected);
            }
        }
        return cnt;
    }

    @Test
    public void testParanthesyzing() {
        assertThat(parenthesize("0|0|0", 0), equalTo(2));
        assertThat(parenthesize("1|1|1", 1), equalTo(2));
        assertThat(parenthesize("1&1&1&1", 0), equalTo(0));
        assertThat(parenthesize("0&0&0&0", 0), equalTo(5));
        assertThat(parenthesize("0&0&0|1", 0), equalTo(3));
        assertThat(parenthesize("0&0&0|1", 1), equalTo(2));
        assertThat(parenthesize("0&1&0|1", 0), equalTo(3));
        assertThat(parenthesize("0&1&0|1", 1), equalTo(2));
        assertThat(parenthesize("1^0&1|0&0", 0), equalTo(6));
        assertThat(parenthesize("1^0&1|0&0", 1), equalTo(8));
    }
}