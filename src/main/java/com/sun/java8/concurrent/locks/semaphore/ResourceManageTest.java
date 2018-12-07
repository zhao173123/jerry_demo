package com.sun.java8.concurrent.locks.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 1.synchronized关键字和Lock锁实现了资源的并发访问控制， 在同一时间只允许唯一了线程进入临界区访问资源(读锁除外)，
 * 这样子控制的主要目的是为了解决多个线程并发同一资源造成的数据不一致的问题。
 * 2.另外一种场景下，一个资源有多个副本可供同时使用，比如打印机房有多个打印机、厕所有多个坑可供同时使用
 * Java提供了另外的并发访问控制--资源的多副本的并发访问控制Semaphore
 * Semaphore是用来保护一个或者多个共享资源的访问，Semaphore内部维护了一个计数器， 其值为可以访问的共享资源的个数。
 * 一个线程要访问共享资源，先获得信号量，如果信号量的计数器值大于1， 意味着有共享资源可以访问，则使其计数器值减去1，再访问共享资源。
 * 如果计数器值为0,线程进入休眠。当某个线程使用完共享资源后， 释放信号量，并将信号量内部的计数器加1， 之前进入休眠的线程将被唤醒并再次试图获得信号量。
 * <p>
 * *****模拟控制商场厕所的并发使用******
 * 10个厕所，100人等待使用
 *
 * @author jerry
 */
public class ResourceManageTest {

    public static final int MAX_RESOURCE = 10;
    private final Semaphore semaphore;
    private boolean resourceArray[];
    private final ReentrantLock lock;

    public ResourceManageTest() {
        this.resourceArray = new boolean[MAX_RESOURCE];// 存放厕所状态
        // 控制10个共享资源的使用，使用先进先出(FIFO)的公平模式进行共享;公平模式的信号量，先来的先获得信号量
        this.semaphore = new Semaphore(MAX_RESOURCE, true);
        this.lock = new ReentrantLock(true);// 公平模式的锁，先来的先选
        for (int i = 0; i < MAX_RESOURCE; i++) {// 初始化为资源可用的情况
            resourceArray[i] = true;
        }
    }

    public void useResource(int userId) {
        try {
            semaphore.acquire();
            int id = getResourceId();
            System.out.print("userId:" + userId + "正在使用资源，资源id:" + id + "\n");
            Thread.sleep(100);
        } catch (InterruptedException e) {

        } finally {
            semaphore.release();
        }
    }

    private int getResourceId() {
        int id = -1;
        lock.lock();
        try {
            for (int i = 0; i < 10; i++) {
                if (resourceArray[i]) {
                    resourceArray[i] = false;
                    id = i;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return id;
    }


}
