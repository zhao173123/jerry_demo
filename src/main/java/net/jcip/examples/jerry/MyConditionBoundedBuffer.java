package net.jcip.examples.jerry;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//使用显示条件变量实现的有界缓存
@ThreadSafe
public class MyConditionBoundedBuffer<T> {

    protected final Lock lock = new ReentrantLock();
    //条件谓词：notFull(count < items.length)
    private final Condition notFull = lock.newCondition();
    //条件谓词：notEmpty(count > 0)
    private final Condition notEmpty = lock.newCondition();
    private static final int BUFFER_SIZE = 100;
    @GuardedBy("lock")private final T[] items = (T[])new Object[BUFFER_SIZE];
    @GuardedBy("lock")private int tail, head, count;

    //阻塞并直到：notFull
    public void put(T x) throws InterruptedException {
        lock.lock();
        try {
            while(count == items.length){
                notFull.await();
            }
            items[tail] = x;
            if(++tail == items.length){
                tail = 0;
            }
            ++count;
        } finally {
            lock.unlock();
        }
    }

    //阻塞并直到：notEmpty
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while(count == 0){
                notEmpty.await();
            }
            T x = items[head];
            items[head] = null;
            if(++head == items.length){
                head = 0;
            }
            --count;
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}
