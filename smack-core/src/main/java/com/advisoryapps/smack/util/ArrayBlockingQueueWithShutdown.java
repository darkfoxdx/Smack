/**
 *
 * Copyright 2014-2019 Florian Schmaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.advisoryapps.smack.util;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Like ArrayBlockingQueue but with additional {@link #shutdown()} and {@link #start} methods. Will
 * throw {@link InterruptedException} if Queue has been shutdown on {@link #take()} and
 * {@link #poll(long, TimeUnit)}.
 * <p>
 * Based on ArrayBlockingQueue of OpenJDK by Doug Lea (who released ArrayBlockingQueue as public
 * domain).
 *
 * @param <E> the type of elements held in this collection
 */
public class ArrayBlockingQueueWithShutdown<E> extends AbstractQueue<E> implements BlockingQueue<E> {

    private final E[] items;

    private int takeIndex;

    private int putIndex;

    private int count;

    private final ReentrantLock lock;

    private final Condition notEmpty;

    private final Condition notFull;

    private volatile boolean isShutdown = false;

    private int inc(int i) {
        return (++i == items.length) ? 0 : i;
    }

    private void insert(E e) {
        insert(e, true);
    }

    private void insert(E e, boolean signalNotEmpty) {
        items[putIndex] = e;
        putIndex = inc(putIndex);
        count++;
        if (signalNotEmpty) {
            notEmpty.signal();
        }
    }

    private E extract() {
        E e = items[takeIndex];
        items[takeIndex] = null;
        takeIndex = inc(takeIndex);
        count--;
        notFull.signal();
        return e;
    }

    private void removeAt(int i) {
        if (i == takeIndex) {
            items[takeIndex] = null;
            takeIndex = inc(takeIndex);
        }
        else {
            while (true) {
                int nexti = inc(i);
                if (nexti != putIndex) {
                    items[i] = items[nexti];
                    i = nexti;
                }
                else {
                    items[i] = null;
                    putIndex = i;
                    break;
                }
            }
        }
        count--;
        notFull.signal();
    }

    private static void checkNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    private void checkNotShutdown() throws InterruptedException {
        if (isShutdown) {
            throw new InterruptedException("Queue was already shut down");
        }
    }

    private boolean hasNoElements() {
        return count == 0;
    }

    private boolean hasElements() {
        return !hasNoElements();
    }

    private boolean isFull() {
        return count == items.length;
    }

    private boolean isNotFull() {
        return !isFull();
    }

    public ArrayBlockingQueueWithShutdown(int capacity) {
        this(capacity, false);
    }

    @SuppressWarnings("unchecked")
    public ArrayBlockingQueueWithShutdown(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        items = (E[]) new Object[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    /**
     * Shutdown the Queue. Will method currently waiting for a not full/empty condition will unblock
     * (and usually throw a InterruptedException).
     */
    public void shutdown() {
        lock.lock();
        try {
            isShutdown = true;
            notEmpty.signalAll();
            notFull.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Start the queue. Newly created instances will be started automatically, thus this only needs
     * to be called after {@link #shutdown()}.
     */
    public void start() {
        lock.lock();
        try {
            isShutdown = false;
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Returns true if the queue is currently shut down.
     *
     * @return true if the queue is shut down.
     */
    public boolean isShutdown() {
        lock.lock();
        try {
            return isShutdown;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E poll() {
        lock.lock();
        try {
            if (hasNoElements()) {
                return null;
            }
            E e = extract();
            return e;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public E peek() {
        lock.lock();
        try {
            return hasNoElements() ? null : items[takeIndex];
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(E e) {
        checkNotNull(e);
        lock.lock();
        try {
            if (isFull() || isShutdown) {
                return false;
            }
            else {
                insert(e);
                return true;
            }
        }
        finally {
            lock.unlock();
        }
    }

    public boolean offerAndShutdown(E e) {
        checkNotNull(e);
        boolean res;
        lock.lock();
        try {
            res = offer(e);
            shutdown();
        } finally {
            lock.unlock();
        }
        return res;
    }

    private void putInternal(E e, boolean signalNotEmpty) throws InterruptedException {
        assert lock.isHeldByCurrentThread();

        while (isFull()) {
            try {
                notFull.await();
                checkNotShutdown();
            }
            catch (InterruptedException ie) {
                notFull.signal();
                throw ie;
            }
        }
        insert(e, signalNotEmpty);
    }

    /**
     * Inserts the specified element into this queue, waiting if necessary
     * for space to become available.
     * <p>
     * This may throw an {@link InterruptedException} in two cases
     * <ol>
     *  <li>If the queue was shut down.</li>
     *  <li>If the thread was was interrupted.</li>
     * </ol>
     * So you have to check which is the case, e.g. by calling {@link #isShutdown()}.
     *
     * @param e the element to add.
     * @throws InterruptedException if interrupted while waiting or if the queue was shut down.
     */
    @Override
    public void put(E e) throws InterruptedException {
        checkNotNull(e);
        lock.lockInterruptibly();

        try {
            putInternal(e, true);
        }
        finally {
            lock.unlock();
        }
    }

    public void putAll(Collection<? extends E> elements) throws InterruptedException {
        checkNotNull(elements);
        lock.lockInterruptibly();

        try {
            for (E element : elements) {
                putInternal(element, false);
            }
        } finally {
            notEmpty.signalAll();
            lock.unlock();
        }
    }

    public enum TryPutResult {
        /**
         * The method was unable to acquire the queue lock.
         */
        couldNotLock,

        /**
         * The queue was shut down.
         */
        queueWasShutDown,

        /**
         * The method was unable to put another element into the queue because the queue was full.
         */
        queueWasFull,

        /**
         * The element was successfully placed into the queue.
         */
        putSuccessful,
    }

    public TryPutResult tryPut(E e) {
        checkNotNull(e);

        boolean locked = lock.tryLock();
        if (!locked) {
            return TryPutResult.couldNotLock;
        }
        try {
            if (isShutdown) {
                return TryPutResult.queueWasShutDown;
            }
            if (isFull()) {
                return TryPutResult.queueWasFull;
            }

            insert(e);
            return TryPutResult.putSuccessful;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        checkNotNull(e);
        long nanos = unit.toNanos(timeout);
        lock.lockInterruptibly();
        try {
            while (true) {
                if (isNotFull()) {
                    insert(e);
                    return true;
                }
                if (nanos <= 0) {
                    return false;
                }
                try {
                    nanos = notFull.awaitNanos(nanos);
                    checkNotShutdown();
                }
                catch (InterruptedException ie) {
                    notFull.signal();
                    throw ie;
                }
            }
        }
        finally {
            lock.unlock();
        }

    }

    @Override
    public E take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            checkNotShutdown();
            try {
                while (hasNoElements()) {
                    notEmpty.await();
                    checkNotShutdown();
                }
            }
            catch (InterruptedException ie) {
                notEmpty.signal();
                throw ie;
            }
            E e = extract();
            return e;
        }
        finally {
            lock.unlock();
        }
    }

    public enum TryTakeResultCode {
        /**
         * The method was unable to acquire the queue lock.
         */
        couldNotLock,

        /**
         * The queue was shut down.
         */
        queueWasShutDown,

        /**
         * The queue was empty.
         */
        queueWasEmpty,

        /**
         * An element was successfully removed from the queue.
         */
        takeSuccessful,
    }

    public static final class TryTakeResult<E> {
        private final E element;
        private final TryTakeResultCode resultCode;

        private TryTakeResult(TryTakeResultCode resultCode) {
            assert resultCode != null;
            this.resultCode = resultCode;
            this.element = null;
        }

        private TryTakeResult(E element) {
            assert element != null;
            this.resultCode = TryTakeResultCode.takeSuccessful;
            this.element = element;
        }

        public TryTakeResultCode getResultCode() {
            return resultCode;
        }

        public E getElement() {
            return element;
        }
    }

    public TryTakeResult<E> tryTake() {
        boolean locked = lock.tryLock();
        if (!locked) {
            return new TryTakeResult<E>(TryTakeResultCode.couldNotLock);
        }
        try {
            if (isShutdown) {
                return new TryTakeResult<E>(TryTakeResultCode.queueWasShutDown);
            }
            if (hasNoElements()) {
                return new TryTakeResult<E>(TryTakeResultCode.queueWasEmpty);
            }
            E element = extract();
            return new TryTakeResult<E>(element);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        lock.lockInterruptibly();
        try {
            checkNotShutdown();
            while (true) {
                if (hasElements()) {
                    E e = extract();
                    return e;
                }
                if (nanos <= 0) {
                    return null;
                }
                try {
                    nanos = notEmpty.awaitNanos(nanos);
                    checkNotShutdown();
                }
                catch (InterruptedException ie) {
                    notEmpty.signal();
                    throw ie;
                }
            }
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public int remainingCapacity() {
        lock.lock();
        try {
            return items.length - count;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        checkNotNull(c);
        if (c == this) {
            throw new IllegalArgumentException();
        }
        lock.lock();
        try {
            int i = takeIndex;
            int n = 0;
            for (; n < count; n++) {
                c.add(items[i]);
                items[i] = null;
                i = inc(i);
            }
            if (n > 0) {
                count = 0;
                putIndex = 0;
                takeIndex = 0;
                notFull.signalAll();
            }
            return n;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        checkNotNull(c);
        if (c == this) {
            throw new IllegalArgumentException();
        }
        if (maxElements <= 0) {
            return 0;
        }
        lock.lock();
        try {
            int i = takeIndex;
            int n = 0;
            int max = (maxElements < count) ? maxElements : count;
            for (; n < max; n++) {
                c.add(items[i]);
                items[i] = null;
                i = inc(i);
            }
            if (n > 0) {
                count -= n;
                takeIndex = i;
                notFull.signalAll();
            }
            return n;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return count;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        lock.lock();
        try {
            return new Itr();
        }
        finally {
            lock.unlock();
        }
    }

    private class Itr implements Iterator<E> {
        private int nextIndex;
        private E nextItem;
        private int lastRet;

        Itr() {
            lastRet = -1;
            if (count == 0) {
                nextIndex = -1;
            }
            else {
                nextIndex = takeIndex;
                nextItem = items[takeIndex];
            }
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        private void checkNext() {
            if (nextIndex == putIndex) {
                nextIndex = -1;
                nextItem = null;
            }
            else {
                nextItem = items[nextIndex];
                if (nextItem == null) {
                    nextIndex = -1;
                }
            }
        }

        @Override
        public E next() {
            lock.lock();
            try {
                if (nextIndex < 0) {
                    throw new NoSuchElementException();
                }
                lastRet = nextIndex;
                E e = nextItem;
                nextIndex = inc(nextIndex);
                checkNext();
                return e;
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public void remove() {
            lock.lock();
            try {
                int i = lastRet;
                if (i < 0) {
                    throw new IllegalStateException();
                }
                lastRet = -1;
                int ti = takeIndex;
                removeAt(i);
                nextIndex = (i == ti) ? takeIndex : i;
                checkNext();
            }
            finally {
                lock.unlock();
            }
        }
    }

}
