package net.jcip.examples.jerry;

import net.jcip.annotations.GuardedBy;

import java.util.concurrent.Semaphore;

/**
 * 固定长度的队列
 * availableItems：从缓存中删除的元素个数，初始值为0
 * availbaleSpaces：可以插入到缓存中的元素个数，初始值等于缓存大小
 *
 * @param <E>
 */
public class MyBoundedBuffer<E> {
    private final Semaphore availableItems, availableSpaces;
    @GuardedBy("this")
    private final E[] items;
    @GuardedBy("this")
    private int putPosition = 0, takePosition = 0;

    public MyBoundedBuffer(int capacity) {
        availableItems = new Semaphore(0);
        availableSpaces = new Semaphore(capacity);
        items = (E[]) new Object[capacity];
    }

    public boolean isEmpty() {
        return availableItems.availablePermits() == 0;
    }

    public boolean isFull() {
        return availableSpaces.availablePermits() == 0;
    }

    /**
     * 请求从availableSpaces中获得一个许可，
     * 如果缓冲区不满那么请求会立即执行，否则请求会一直阻塞到缓存不满。
     * 在获得一个许可之后，插入一个数据都缓存中，并返回一个许可到availableItems信号量。
     *
     * @param x
     * @throws InterruptedException
     */
    public void put(E x) throws InterruptedException {
        availableSpaces.acquire();
        doInsert(x);
        availableItems.release();
    }

    private synchronized void doInsert(E x) {
        int i = putPosition;
        items[i] = x;
        putPosition = (++i == items.length) ? 0 : i;
    }

    /**
     * 请求从availableItems中获得一个许可permit,
     * 如果缓存不为空那么请求会立即成功，否则请求会一直阻塞到缓存不为空。
     * 在获得了一个许可之后，将删除缓存中的下一个元素，并返回一个许可到availableSpaces信号量。
     *
     * @return
     * @throws InterruptedException
     */
    public E take() throws InterruptedException {
        availableItems.acquire();
        E item = doExtract();
        availableSpaces.release();
        return item;
    }

    private synchronized E doExtract() {
        int i = takePosition;
        E x = items[i];
        items[i] = null;
        takePosition = (++i == items.length) ? 0 : i;
        return x;
    }

}
