package com.henry4j;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.henry4j.Recipes.BoundedBlockingQ;
import com.henry4j.Recipes.BoundedHashSet;

public class RecipesTest {
    @Test
    public void testBoundedBlockingQ() throws InterruptedException {
        final BoundedBlockingQ<String> q = new BoundedBlockingQ<String>(3);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(10);
                    q.put("v");
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
        assertThat(q.take(), equalTo("v"));
        q.put("x");
        q.put("y");
        q.put("z");
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(10);
                    assertThat(q.take(), equalTo("x"));
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
        q.put("w");
        assertThat(q.take(), equalTo("y"));
        assertThat(q.take(), equalTo("z"));
        assertThat(q.take(), equalTo("w"));
    }

    @Test
    public void testBoundedHashSet() throws InterruptedException {
        final BoundedHashSet<String> set = new BoundedHashSet<String>(3);
        set.remove("k");
        set.add("v");
        set.add("w");
        set.add("x");
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(10);
                    assertTrue(set.remove("w"));
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
        set.add("y");
    }
}
