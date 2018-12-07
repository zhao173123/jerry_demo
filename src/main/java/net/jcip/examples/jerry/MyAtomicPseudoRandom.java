package net.jcip.examples.jerry;

import net.jcip.annotations.ThreadSafe;
import net.jcip.examples.PseudoRandom;

import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class MyAtomicPseudoRandom extends PseudoRandom{

    private AtomicInteger seed;

    MyAtomicPseudoRandom(int seed){
        this.seed = new AtomicInteger(seed);
    }

    public int nextInt(int n){
        while(true){
            int s = seed.get();
            int nextSeed = calculateNext(s);
            if(seed.compareAndSet(s, nextSeed)){
                int remainder = s % n;
                return remainder > 0 ? remainder : remainder + n;
            }
        }
    }
}
