//package com.henry4j;
//
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.junit.Assert.assertThat;
//
//import java.util.Comparator;
//import java.util.Map;
//import java.util.Stack;
//
//import lombok.ExtensionMethod;
//import lombok.val;
//
//import org.junit.Test;
//
//import com.google.common.base.CharMatcher;
//import com.google.common.base.Joiner;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableMap;
//
//@ExtensionMethod({ Extensions.class })
//public class Expression {
////    Assert.AreEqual(18, Puzzles.EvaluatePostfix(Puzzles.PostfixOf(new String[] {"1", "+", "3", "*", "4", "+", "5"})));
////    Assert.AreEqual(8, Puzzles.EvaluatePostfix(Puzzles.PostfixOf(new String[] {"1", "+", "3", "*", "4", "-", "5"})));
////    Assert.AreEqual(4, Puzzles.EvaluatePostfix(Puzzles.PostfixOf(new String[] {"1", "+", "3"})));
////    Assert.AreEqual(-25, Puzzles.EvaluatePostfix(Puzzles.PostfixOf(new String[] {"1", "-", "3", "*", "4", "*", "2", "-", "2"})));
////    Assert.AreEqual(11, Puzzles.EvaluatePostfix(Puzzles.PostfixOf(new String[] {"1", "+", "10", "/", "2", "+", "5"})));
//
//    private static CharMatcher OPERATORS = CharMatcher.anyOf("*/+-");
//    private static Map<Character, Integer> PRECEDENCES_C = ImmutableMap.of('*', 1, '/', 1, '+', 2, '-', 2);
//    private static Map<CharSequence, Integer> PRECEDENCES_S = ImmutableMap.<CharSequence, Integer>of("*", 1, "/", 1, "+", 2, "-", 2);
//    private static Comparator<Character> COMPARATOR_C = new Comparator<Character>() {
//        public int compare(Character a, Character b) {
//            return PRECEDENCES_C.get(a) - PRECEDENCES_C.get(b);
//        }
//    };
//    private static Comparator<CharSequence> COMPARATOR_S = new Comparator<CharSequence>() {
//        public int compare(CharSequence a, CharSequence b) {
//            return PRECEDENCES_S.get(a) - PRECEDENCES_S.get(b);
//        }
//    };
//
//    public static Iterable<CharSequence> postfixOf(Iterable<CharSequence> infix){
//        val stack = new Stack<CharSequence>();
//        val postfix = ImmutableList.<CharSequence>builder();
//        for (val s : infix) {
//            if (CharMatcher.DIGIT.matchesAllOf(s)) {
//                postfix.add(s);
//            } else if ("(".equals(s)) {
//                stack.push(s);
//            } else if (")".equals(s)) {
//                while (!"(".equals(stack.peek())) {
//                    postfix.add(stack.pop());
//                }
//                stack.pop();
//            } else if (OPERATORS.matches(s)) {
//                // http://interactivepython.org/courselib/static/pythonds/BasicDS/stacks.html
//                while (!stack.isEmpty() && COMPARATOR.compare(stack.peek(), c) <= 0) {
//                    postfix.add(stack.pop());
//                }
//                stack.push(c);
//            }
//        }
//        while (!stack.isEmpty()) {
//            postfix.add(stack.pop());
//        }
//        return Joiner.on(' ').join(postfix.build());
//    }
//
//    public static String postfixOf(CharSequence infix){
//        val stack = new Stack<Character>();
//        val postfix = ImmutableList.<Character>builder();
//        for (int i = 0; i < infix.length(); i++) {
//            val c = infix.charAt(i);
//            if (CharMatcher.DIGIT.matches(c)) {
//                postfix.add(c);
//            } else if ('(' == c) {
//                stack.push(c);
//            } else if (')' == c) {
//                while (stack.peek() != '(') {
//                    postfix.add(stack.pop());
//                }
//                stack.pop();
//            } else if (OPERATORS.matches(c)) {
//                // http://interactivepython.org/courselib/static/pythonds/BasicDS/stacks.html
//                while (!stack.isEmpty() && COMPARATOR_C.compare(stack.peek(), c) <= 0) {
//                    postfix.add(stack.pop());
//                }
//                stack.push(c);
//            }
//        }
//        while (!stack.isEmpty()) {
//            postfix.add(stack.pop());
//        }
//        return Joiner.on(' ').join(postfix.build());
//    }
//
//    @Test
//    public void testPostfixOf() {
//        assertThat(postfixOf("11 * 3 + 4 * 5"), equalTo("1 3 * 4 5 * +"));
//
////        assertThat(postfixOf("1", "*", "3", "+", "4", "*", "5"), equalTo(asList("1", "3", "*", "4", "5", "*", "+").iterable()));
////        assertThat(postfixOf("1", "+", "2", "*", "3", "-", "4"), equalTo(asList("1", "2", "3", "*", "+", "4", "-").iterable()));
////        assertThat(postfixOf("1", "+", "3"), equalTo(asList("1", "3", "+").iterable()));
//    }
//    
////    https://raw.github.com/0ishi/puzzles/master/src/Puzzles.cs
//}
