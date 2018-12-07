package com.sun.java8.concurrent.locks.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ConditionTest2Data implements Runnable{

    private Lock lock;
    private Condition condition;

    public ConditionTest2Data(Lock lock,Condition condition){
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run() {
        if("线程0".equals(Thread.currentThread().getName())){
            thread0Process();
        }else if("线程1".equals(Thread.currentThread().getName())){
            thread1Process();
        }else if("线程2".equals(Thread.currentThread().getName())){
            thread2Process();
        }
    }

    private void thread0Process(){
        try {
            lock.lock();
            System.out.println("线程0休息5秒");
            Thread.sleep(5000);
            condition.signal();
            System.out.println("线程0唤醒等待线程");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    private void thread1Process(){
        try {
            lock.lock();
            System.out.println("线程1阻塞");
            condition.await();
            System.out.println("线程1被唤醒");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    private void thread2Process(){
        try {
            System.out.println("线程2想要获取锁");
            lock.lock();
            System.out.println("线程2获取锁成功");
        } finally {
            lock.unlock();
        }
    }

}
