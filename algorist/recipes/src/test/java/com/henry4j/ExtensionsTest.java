package com.henry4j;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import lombok.val;
import lombok.ExtensionMethod;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

@ExtensionMethod({ Extensions.class })
public class ExtensionsTest {
    @Test
    public void testBinarySearch() {
        val list = ImmutableList.of(1, 1, 2, 3, 3, 3, 4, 4, 4, 4);
        assertThat(list.bsearchLastOf(0, list.size() - 1, 3), equalTo(5));
        assertThat(list.bsearchLastOf(0, list.size() - 1, 5), equalTo(-1));
        assertThat(list.bsearchLastOf(0, list.size() - 1, 4), equalTo(9));
        assertThat(list.bsearchLastOf(0, list.size() - 1, 2), equalTo(2));
        assertThat(list.bsearchLastOf(0, list.size() - 1, 1), equalTo(1));
        assertThat(list.bsearchLastOf(0, list.size() - 1, 0), equalTo(-1));
        assertThat(list.bsearchFirstOf(0, list.size() - 1, 3), equalTo(3));
        assertThat(list.bsearchFirstOf(0, list.size() - 1, 5), equalTo(-1));
        assertThat(list.bsearchFirstOf(0, list.size() - 1, 4), equalTo(6));
        assertThat(list.bsearchFirstOf(0, list.size() - 1, 2), equalTo(2));
        assertThat(list.bsearchFirstOf(0, list.size() - 1, 1), equalTo(0));
        assertThat(list.bsearchFirstOf(0, list.size() - 1, 0), equalTo(-1));
        assertThat(list.bsearchFirstOf(0, list.size() - 1, Integer.valueOf(3).greaterThan()), equalTo(6)); // first greater than 3
        assertThat(list.bsearchLastOf(0, list.size() - 1, Integer.valueOf(3).lessThan()), equalTo(2)); // last smaller than 3
    }
}
