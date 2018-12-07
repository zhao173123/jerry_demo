package com.sun.java8.concurrent.locks.aqs.clh;

import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.*;

//AtomicReference引入是为了使用它的cas操作，解决多线程并发操作引起的数据不一致
public class SpinLock {

    private AtomicReference<Thread> owner = new AtomicReference<>();

    public void lock(){
        Thread currentThread = Thread.currentThread();
        //如果锁未被占用，则设置当前线程为锁的拥有者
        while(!owner.compareAndSet( null, currentThread)){

        }
    }

    public void unlock(){
        Thread currentThread = Thread.currentThread();
        //只有锁的拥有者才能释放锁
        owner.compareAndSet(currentThread, null);
    }

}
