package com.henry4j.commons;

import static org.junit.Assert.assertNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.henry4j.commons.base.Functors;

public class FunctorTest {
    @Test
    public void testReceiveString() {
        String r = Functors.<String>receiveQuietly().apply(new BaseFuture<String>() {
            @Override
            public String get() throws InterruptedException, ExecutionException {
                throw new IllegalStateException();
            }
        });
        assertNull(r);
    }

//    @Test
//    public void testReceiveInts() {
//        int[] r = Functors.<int[]>receiveQuietly().apply(new BaseFuture<int[]>() {
//            @Override
//            public int[] get() throws InterruptedException, ExecutionException {
//                throw new IllegalStateException();
//            }
//        });
//        assertThat(r.length, equalTo(0));
//    }

    public static class BaseFuture<E> implements Future<E> {
        @Override
        public boolean cancel(boolean arg0) {
            return false;
        }

        @Override
        public E get() throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public E get(long arg0, TimeUnit arg1) throws InterruptedException,
                ExecutionException, TimeoutException {
            return null;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }
    }
}
