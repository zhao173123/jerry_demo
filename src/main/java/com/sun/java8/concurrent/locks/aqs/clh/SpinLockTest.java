package com.sun.java8.concurrent.locks.aqs.clh;

public class SpinLockTest implements Runnable {

    static int sum;
    private SpinLock lock;

    public SpinLockTest(SpinLock lock) {
        this.lock = lock;
    }

    public static void main(String[] args) throws InterruptedException {
        SpinLock lock = new SpinLock();
        for(int i = 0; i < 100; i++) {
            SpinLockTest test = new SpinLockTest(lock);
            Thread t = new Thread(test);
            t.start();
        }
        Thread.currentThread().sleep(1000);
        System.out.println(sum);
    }

    @Override
    public void run() {
        this.lock.lock();
        sum++;
        this.lock.unlock();
    }
}
