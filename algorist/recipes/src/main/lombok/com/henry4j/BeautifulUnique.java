package com.henry4j;

import java.util.BitSet;
import java.util.HashMap;
import java.util.SortedMap;

import lombok.ExtensionMethod;
import lombok.val;

@ExtensionMethod({ Extensions.class })
public class BeautifulUnique {
    public static String mostBeautifulUniqueStringOf_v2(String s) { // O(n * k)
        SortedMap<Character, Integer> countsByChar = s.countsByChar(); // O(n)

        // O(n * k); greedily goes to set a new bit, and clear some old bits.
        val bits = new BitSet(s.length());
        val bitIndicesByChar = new HashMap<Character, Integer>(countsByChar.size());
        for (int i = 0; i < s.length(); i++) { // O(n * k)
            char c = s.charAt(i);
            if (!bitIndicesByChar.containsKey(c)) {
                for (val minor : countsByChar.headMap(c).keySet()) { // O(k)
                    if (bitIndicesByChar.containsKey(minor)) { // O(1)
                        bits.clear(bitIndicesByChar.get(minor));
                        bitIndicesByChar.remove(minor);
                    }
                }
                countsByChar.decrement(c);
                bits.set(i);
                bitIndicesByChar.put(c, i);
            }
        }

        return s.subsequence(bits); // O(n); reduces to subsequence from a bit set.
    }
}

/*
Most Beautiful Unique String

String s is called unique if all the characters of s are different.
String s2 is producible from string s1, if we can remove some characters of s1 to obtain s2.
String s1 is more beautiful than string s2 if length of s1 is more than length of s2 or they have equal length and s1 is lexicographically greater than s2.
Given a string s you have to find the most beautiful unique string that is producible from s.

Input:
First line of input comes a string s having no more than 1,000,000(10^6) characters. all the characters of s are lowercase english letters.

Output:
Print the most beautiful unique string that is producable from s

Sample Input:
babab

Sample Output:
ba

Explanation
In the above test case all unique strings that are producible from s are "ab" and "ba" and "ba" is more beautiful than "ab".
*/
