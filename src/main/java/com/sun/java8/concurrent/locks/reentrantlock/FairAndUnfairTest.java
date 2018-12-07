package com.sun.java8.concurrent.locks.reentrantlock;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @auth zhaochen
 * @date 2018/8/14 下午2:23
 */
public class FairAndUnfairTest{

    private static Lock fairLock = new ReentrantLock2(true);
    private static Lock unfairLock = new ReentrantLock2(false);

    @Test
    public void fair() throws InterruptedException{
        testLock("公平锁", fairLock);
    }

    @Test
    public void unfair() throws InterruptedException{
        testLock("非公平锁", unfairLock);
    }

    private void testLock(String type,Lock lock) throws InterruptedException{
        //启动5个job
        for(int i = 0; i < 5; i++){
            Thread thread = new Thread(new Job(lock)){
                @Override
                public String toString(){
                    return getName();
                }
            };
            thread.setName("" + i);
            thread.start();
        }
        Thread.sleep(11000);
    }

    private static class Job extends Thread{
        private Lock lock;

        public Job(Lock lock){
            this.lock = lock;
        }

        @Override
        public void run(){
            for(int i = 0; i < 2; i++){
                lock.lock();
                try{
                    Thread.sleep(1000);
                    //连续2次打印当前的Thread和等待队列的Thread
                    System.out.println("获取锁的当前线程[" + Thread.currentThread().getName() + "], 同步队列中的线程"
                            + ((ReentrantLock2)lock).getQueuedThreads() + "");
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    lock.unlock();
                }
            }

        }
    }

    private static class ReentrantLock2 extends ReentrantLock{
        public ReentrantLock2(boolean fair){
            super(fair);
        }

        @Override
        public Collection<Thread> getQueuedThreads(){
            List<Thread> arrayList = Lists.newArrayList(super.getQueuedThreads());
            Collections.reverse(arrayList);
            return arrayList;
        }
    }
}


