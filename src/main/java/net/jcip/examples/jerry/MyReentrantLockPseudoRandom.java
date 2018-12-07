package net.jcip.examples.jerry;

import net.jcip.annotations.ThreadSafe;
import net.jcip.examples.PseudoRandom;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class MyReentrantLockPseudoRandom extends PseudoRandom {
    private final Lock lock = new ReentrantLock(false);

    private int seed;

    MyReentrantLockPseudoRandom(int seed){
        this.seed = seed;
    }

    public int nextInt(int n){
        lock.lock();
        try {
            int s = seed;
            seed = calculateNext(s);
            int remainder = s % n;
            return remainder > 0 ? remainder : remainder + n;
        } finally {
            lock.unlock();
        }
    }
}
