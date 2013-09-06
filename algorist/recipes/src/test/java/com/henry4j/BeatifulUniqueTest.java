package com.henry4j;

import static com.henry4j.BeautifulUnique.mostBeautifulUniqueStringOf_v2;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import lombok.val;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.compress.Lz4Codec;
import org.apache.hadoop.io.compress.lz4.Lz4Compressor;
import org.junit.Test;

import com.google.common.io.ByteStreams;

public class BeatifulUniqueTest {
    @Test
    public void testFindMostBeautifulUniqueString() {
        assertThat(mostBeautifulUniqueStringOf_v2("acbdba"), equalTo("cdba"));
        assertThat(mostBeautifulUniqueStringOf_v2("acdcab"), equalTo("dcab"));
        assertThat(mostBeautifulUniqueStringOf_v2("bcdaca"), equalTo("bdca"));
        assertThat(iterative("acbdba"), equalTo("cdba"));
        assertThat(iterative("acdcab"), equalTo("dcab"));
        assertThat(iterative("bcdaca"), equalTo("bdca"));
    }

    public static String iterative(String s) {
        List<Character> list = new LinkedList<Character>();
        for (int i = s.length() - 1; i >= 0; i--) {
            char current = s.charAt(i);
            if (!list.contains(current)) {
                list.add(0, current);
            } else {
                if (current > list.get(0)) {
                    list.remove(current);
                    list.add(0, current);
                }
            }
        }
        String answer = "";
        for (int i = 0; i < list.size(); i++) {
            answer += list.get(i);
        }
        return answer;
    }
}
