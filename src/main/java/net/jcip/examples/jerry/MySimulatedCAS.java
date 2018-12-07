package net.jcip.examples.jerry;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class MySimulatedCAS {

    @GuardedBy("this")
    private int value;

    public synchronized int get() {
        return value;
    }

    public synchronized int compareAndSwap(int exceptedValue, int newValue) {
        int oldValue = value;
        if (oldValue == exceptedValue)
            value = newValue;
        return oldValue;
    }

    public synchronized boolean compareAndSet(int expectedValue,
                                              int newValue) {
        return expectedValue == compareAndSwap(expectedValue, newValue);
    }
}
