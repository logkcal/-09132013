package com.henry4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Fiber {
    public static class ConditionBoundedBuffer<E> // comparable to Java Concurrency in Practice 14.11
            extends ConditionBoundedBufferBase<E> {
        private final Lock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        public ConditionBoundedBuffer(int capacity) {
            super(capacity);
        }

        @Override
        public boolean offer(E e) throws InterruptedException {
            lock.lock();
            try {
                while (isFull())
                    notFull.await();
                items[tail] = e;
                tail = (++tail == items.length) ? 0 : tail;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
            return true;
        }

        @Override
        public E poll() throws InterruptedException {
            lock.lock();
            try {
                while (isEmpty())
                    notFull.await();
                E e = items[head];
                head = (++head == items.length) ? 0 : head;
                notEmpty.await();
                return e;
            } finally {
                lock.unlock();
            }
        }
    }

    public static class ConditionBoundedBufferBase<E> { // comparable to Java Concurrency in Practice 14.6
        protected final E[] items;
        protected int head = 0;
        protected int tail = 0;

        public ConditionBoundedBufferBase(int capacity) {
            items = (E[])new Object[capacity + 1];
        }

        public synchronized boolean offer(E e) throws InterruptedException {
            while (isFull())
                wait();
            boolean wasEmpty = isEmpty();
            items[tail] = e;
            tail = (++tail == items.length) ? 0 : tail;
            if (wasEmpty)
                notifyAll();
            return true;
        }

        public synchronized E poll() throws InterruptedException {
            while (isEmpty()) {
                wait();
            }
            boolean wasFull = isFull();
            E e = items[head];
            head = (++head == items.length) ? 0 : head;
            if (wasFull)
                notifyAll();
            return e;
        }

        public boolean isEmpty() {
            return head == tail;
        }

        public boolean isFull() {
            return (head == tail + 1) || (head == 0 && tail == items.length);
            // return head == (tail + 1) % q.length;
        }
    }

    public static class BoundedArrayBuffer<E> { // comparable to Java Concurrency in Practice 12.1
        private final Semaphore empty;
        private final Semaphore full = new Semaphore(0);
        private final E[] items;
        private int head = 0;
        private int tail = 0;

        public BoundedArrayBuffer(int capacity) {
            empty = new Semaphore(capacity);
            items = (E[])new Object[capacity];
        }

        public boolean offer(E e) throws InterruptedException {
            empty.acquire();
            synchronized (this) {
                items[tail] = e;
                tail = (++tail == items.length) ? 0 : tail;
            }
            full.release();
            return true;
        }

        public E poll() throws InterruptedException {
            full.acquire();
            E e;
            synchronized (this) {
                e = items[head];
                head = (++head == items.length) ? 0 : head;
            }
            empty.release();
            return e;
        }

        public boolean isEmpty() {
            return full.availablePermits() == 0;
        }

        public boolean isFull() {
            return empty.availablePermits() == 0;
        }
    }

    public static class BoundedLinkedList<E> {
        private final Queue<E> q = new LinkedList<>();
        private final Semaphore use = new Semaphore(1);
        private final Semaphore empty;
        private final Semaphore full = new Semaphore(0);

        public BoundedLinkedList(int capacity) {
            empty = new Semaphore(capacity);
        }

        public boolean offer(E e) throws InterruptedException {
            boolean added = false;
            empty.acquire();
            try {
                use.acquire();
                try {
                    if (added = q.add(e)) {
                        full.release();
                    }
                } finally {
                    use.release();
                }
            } finally {
                if (!added) {
                    empty.release();
                }
            }
            return added;
        }

        public E poll() throws InterruptedException {
            boolean removed = false;
            full.acquire();
            try {
                use.acquire();
                try {
                    E e = q.remove(); // instead of poll();
                    if (null != e) {
                        removed = true;
                        empty.release();
                    }
                    return e;
                } finally {
                    use.release();
                }
            } finally {
                if (!removed) {
                    full.release();
                }
            }
        }
    }

    public static class BoundedHashSet<E> { // comparable to Java Concurrency in Practice 5.14
        private final Set<E> set = Collections.synchronizedSet(new HashSet<E>());
        private final Semaphore empty;

        public BoundedHashSet(int capacity) {
            empty = new Semaphore(capacity);
        }

        public boolean add(E e) throws InterruptedException {
            boolean added = false;
            empty.acquire();
            try {
                return (added = set.add(e));
            } finally {
                if (!added) {
                    empty.release();
                }
            }
        }

        public boolean remove(E e) {
            boolean removed;
            if (removed = set.remove(e)) {
                empty.release();
            }
            return removed;
        }
    }

    public interface Computable<A, V> {
        V compute(A arg) throws InterruptedException;
    }

    public class Memoizer<A, V> implements Computable<A, V> {
        private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();
        private final Computable<A, V> c;

        public Memoizer(Computable<A, V> c) { this.c = c; }

        @Override
        public V compute(final A arg) throws InterruptedException {
            while (true) {
                Future<V> f = cache.get(arg);
                if (f == null) {
                    FutureTask<V> ft = new FutureTask<>(() -> c.compute(arg));
                    if (null == (f = cache.putIfAbsent(arg, ft))) {
                        f = ft;
                        ft.run();
                    }
                }
                try {
                    return f.get();
                } catch (CancellationException e) {
                    cache.remove(arg, f);
                } catch (ExecutionException e) {
                    throw launderThrowable(e.getCause());
                }
            }
        }
    }

    public static class SemaphoreOnLock {
        private final Lock lock = new ReentrantLock();
        // CONDITION PREDICATE: permitsAvailable (permits > 0)
        private final Condition permitsAvailable = lock.newCondition();
        private int permits;

        public SemaphoreOnLock(int initialPermits) {
            permits = initialPermits;
        }

        public void acquire() throws InterruptedException {
            lock.lock();
            try {
                while (permits <= 0)
                    permitsAvailable.await();
                --permits;
            } finally {
                lock.unlock();
            }
        }

        public void release() {
            lock.lock();
            try {
                ++permits;
                permitsAvailable.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    public static class StripedMap {
        // Synchronization policy: buckets[n] guarded by locks[n%N_LOCKS]
        private static final int N_LOCKS = 16;
        private final Node[] buckets;
        private final Object[] locks;

        private static class Node {
            Object key;
            Object value;
            Node next;
        }

        public StripedMap(int buckets) {
            this.buckets = new Node[buckets];
            this.locks = new Object[N_LOCKS];
            for (int i = 0; i < N_LOCKS; i++) {
                this.locks[i] = new Object();
            }
        }

        public Object get(Object key) {
            int hash = hash(key);
            synchronized (locks[hash % N_LOCKS]) {
                for (Node m = buckets[hash]; m != null; m = m.next) {
                    if (m.key.equals(key)) {
                        return m.value;
                    }
                }
            }
            return null;
        }

        public void clear() {
            for (int i = 0; i < buckets.length; i++) {
                synchronized (locks[i % N_LOCKS]) {
                    buckets[i] = null;
                }
            }
        }

        private int hash(Object key) {
            return Math.abs(key.hashCode() % buckets.length);
        }
    }

    public static class SimpleLatchOnAQS { // Java Concurrency in Practice 14.14
        private final Sync sync = new Sync();

        public void await() throws InterruptedException {
            sync.acquireSharedInterruptibly(0);
        }

        public void signal() {
            sync.releaseShared(0);
        }
 
        private class Sync extends AbstractQueuedSynchronizer {
            @Override
            protected int tryAcquireShared(int ignored) {
                // acquired(1) if the latch is open, or not acquired (-1)
                return getState() == 1 ? 1 : -1;
            }

            @Override
            protected boolean tryReleaseShared(int ignored) {
                setState(1); // the latch is open
                return true; // other threads may now to able to acquire
            }
        }
    }

    class Sync4ReentrantLockOnAQS extends AbstractQueuedSynchronizer {
        Thread owner;

        @Override
        protected boolean tryAcquire(int ignored) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (0 == c) {
                if (compareAndSetState(0, 1)) {
                    owner = current;
                    return true;
                }
            } else if (current == owner) {
                setState(c+1);
                return true;
            }
            return false;
        }
    }

    class Sync4SemaphoreOrCountDownLatchOnAQS extends AbstractQueuedSynchronizer {
        @Override
        protected int tryAcquireShared(int acquires) {
            while (true) {
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0
                    || compareAndSetState(available, remaining)) {
                    return remaining;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            while (true) {
                int p = getState();
                if (compareAndSetState(p, p + releases)) {
                    return true;
                }
            }
        }
    }

    // In the AQS-based implementation of ReentrantReadWriteLock, a single AQS subclass 
    // manages both read & write locking. ReentrantRead-WriteLock uses 16 bits of the state 
    // for the write-lock count, and the other 16 bits for the read-lock count.
    // Operations on the read lock use the shared acquire and release methods; 
    // operations on the write lock use the exclusive acquire and release methods.
    // Internally, AQS maintains a queue of waiting threads, keeping track of whether
    // a thread has requested exclusive or shared access. In ReentrantRead-WriteLock, 
    // when the lock becomes available, if the thread at the head of the queue was looking 
    // for write access it will get it, and if the thread at the head of the queue was
    // looking for read access, all queued threads up to the first writer will get it.
    public static class ReadWriteLockOnAQS {
    }

    public static class ReadWriteMap<K, V> {
        private final Map<K, V> map = new HashMap<>();
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock r = lock.readLock();
        private final Lock w = lock.writeLock();

        public V put(K key, V value) {
            w.lock();
            try {
                return map.put(key, value);
            } finally {
                w.unlock();
            }
        }
    }

    public static class LockFreeStack<E> {
        AtomicReference<Node<E>> top = new AtomicReference<>();

        public void push(E item) {
            Node<E> newHead = new Node<>(item);
            Node<E> oldHead;
            do {
                oldHead = top.get();
                newHead.next = oldHead;
            } while(!top.compareAndSet(oldHead, newHead));
        }

        public E pop() {
            Node<E> oldHead;
            Node<E> newHead;
            do {
                oldHead = top.get();
                if (null == oldHead) {
                    return null;
                }
                newHead = oldHead.next;
            } while (!top.compareAndSet(oldHead, newHead));
            return oldHead.item;
        }

        private static class Node<E> {
            public final E item;
            public Node<E> next;

            public Node(E item) {
                this.item = item;
            }
        }
    }

    public static class LockFreeQueue<E> {
        private final Node<E> nu11 = new Node<>(null, null);
        private final AtomicReference<Node<E>> head = new AtomicReference<>(nu11);
        private final AtomicReference<Node<E>> tail = new AtomicReference<>(nu11);

        public boolean put(E item) {
            Node<E> newNode = new Node<>(item, null);
            while (true) {
                Node<E> oldTail = tail.get();
                Node<E> newTail = oldTail.next.get();
                if (oldTail == tail.get()) {
                    if (newTail != null) {
                        // Queue in intermediate state, advance tail
                        tail.compareAndSet(oldTail, newTail);
                    } else {
                        // In quiescent state, try inserting new node
                        if (oldTail.next.compareAndSet(null, newNode)) {
                            // Insertion succeeded, try advancing tail
                            tail.compareAndSet(oldTail, newNode);
                            return true;
                        }
                    }
                }
            }
        }

        private static class Node<E> {
            public final E item;
            public AtomicReference<Node<E>> next;

            public Node(E item, Node<E> next) {
                this.item = item;
                this.next = new AtomicReference<>(next);
            }
        }
    }

    public static class SemaphoreLock implements Lock {
        private final Semaphore semaphore;

        public SemaphoreLock(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        public void unlock() {
            this.semaphore.release();
        }

        public void lock() {
            this.semaphore.acquireUninterruptibly();
        }

        public boolean tryLock() {
            return this.semaphore.tryAcquire();
        }

        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return this.semaphore.tryAcquire(time, unit);
        }

        public void lockInterruptibly() throws InterruptedException {
            this.semaphore.acquire();
        }

        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }

    public class SemaphoreReadWriteLock implements ReadWriteLock {
        private final Lock readLock;
        private final Lock writeLock;

        public SemaphoreReadWriteLock(Semaphore semaphore) {
            this.readLock = new SemaphoreLock(semaphore);
            this.writeLock = new SemaphoreWriteLock(semaphore);
        }

        public Lock readLock() {
            return this.readLock;
        }

        public Lock writeLock() {
            return this.writeLock;
        }

        private class SemaphoreWriteLock implements Lock {
            private final Semaphore semaphore;
            private final int permits;

            SemaphoreWriteLock(Semaphore semaphore) {
                this.semaphore = semaphore;
                this.permits = semaphore.availablePermits();
            }

            public void unlock() {
                this.semaphore.release(this.permits);
            }

            public void lock() {
                int drained = this.drainPermits();
                if (drained < this.permits) {
                    this.semaphore.acquireUninterruptibly(this.permits - drained);
                }
            }

            public boolean tryLock() {
                return this.semaphore.tryAcquire(this.permits);
            }

            public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
                int drained = this.drainPermits();
                if (drained == this.permits) {
                    return true;
                }
                boolean acquired = false;
                try {
                    acquired = this.semaphore.tryAcquire(this.permits - drained, timeout, unit);
                } finally {
                    if (!acquired && (drained > 0)) {
                        this.semaphore.release(drained);
                    }
                }
                return acquired;
            }

            public void lockInterruptibly() throws InterruptedException {
                int drained = this.drainPermits();
                if (drained < this.permits) {
                    try {
                        this.semaphore.acquire(this.permits - drained);
                    } catch (InterruptedException e) {
                        if (drained > 0) {
                            this.semaphore.release(drained);
                        }
                        throw e;
                    }
                }
            }

            public Condition newCondition() {
                throw new UnsupportedOperationException();
            }

            private int drainPermits() {
                return this.semaphore.isFair() ? 0 : this.semaphore.drainPermits();
            }
        }
    }

    private static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException)
            return (RuntimeException)t;
        else if (t instanceof Error)
            throw (Error)t;
        else
            throw new IllegalStateException("UNCHECKED: this bug should go unhandled.", t);
    }
}
