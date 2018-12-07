package com.sun.java8.concurrent.locks.condition;

import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionTest2 {

    /**
     * result:
     * 线程1阻塞
     * 线程0休息5秒
     * 线程2想要获取锁
     * 线程0唤醒等待线程
     * 线程2获取锁成功
     * 线程1被唤醒
     *
     * 分析:
     * 1.线程1先启动，获取锁，调用await()方法等待
     * 2.线程0后启动，获取锁，休眠5秒准备signal()
     * 3.线程2最后启动，获取锁，由于线程0未使用完毕锁，
     *  因此线程2排队，但是由于此时线程0还未signal()，
     *  因此线程1在线程0执行signal()后，在AbstractQueuedSynchronizer队列中的顺序是在线程2后面的
     *
     * @throws InterruptedException
     */
    @Test
    public void test1() throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        //线程0的作用是signal
        Runnable runnable0 = new ConditionTest2Data(lock, condition);
        Thread thread0 = new Thread(runnable0);
        thread0.setName("线程0");

        // 线程1的作用是await
        Runnable runnable1 = new ConditionTest2Data(lock, condition);
        Thread thread1 = new Thread(runnable1);
        thread1.setName("线程1");

        // 线程2的作用是lock
        Runnable runnable2 = new ConditionTest2Data(lock, condition);
        Thread thread2 = new Thread(runnable2);
        thread2.setName("线程2");

        thread1.start();
        Thread.sleep(1000);
        thread0.start();
        Thread.sleep(1000);
        thread2.start();

        thread1.join();
    }
}
