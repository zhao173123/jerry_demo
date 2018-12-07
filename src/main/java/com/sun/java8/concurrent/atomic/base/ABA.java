package com.sun.java8.concurrent.atomic.base;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 分别用AtomicInteger和AtomicStampedReference来对初始值为100的原子整型变量进行更新
 * <p>
 * AtomicInteger会成功执行CAS操作 加上版本戳的AtomicStampedReference对于ABA问题会执行CAS失败
 *
 * @author jerry
 */
public class ABA {

    private static AtomicInteger atomicInt = new AtomicInteger(100);
    private static AtomicStampedReference <Integer> atomicStampedRef = new AtomicStampedReference <>(100, 0);

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            atomicInt.compareAndSet(100, 101);
            atomicInt.compareAndSet(101, 100);
        });

        Thread t2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean c3 = atomicInt.compareAndSet(100, 101);
            System.out.println(c3);// true
        });

        t1.start();
        t2.start();
        t1.join();// 等待t1结束
        t2.join();// 等待t2结束

        Thread refT1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicStampedRef.compareAndSet(100, 101, atomicStampedRef.getStamp(), atomicStampedRef.getStamp() + 1);
            atomicStampedRef.compareAndSet(101, 100, atomicStampedRef.getStamp(), atomicStampedRef.getStamp() + 1);
        });

        Thread refT2 = new Thread(() -> {
            int stamp = atomicStampedRef.getStamp();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean c3 = atomicStampedRef.compareAndSet(100, 101, stamp, stamp + 1);
            System.out.println(c3);// false
        });

        refT1.start();
        refT2.start();
    }
}
