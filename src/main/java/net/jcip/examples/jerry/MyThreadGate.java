package net.jcip.examples.jerry;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

//开始阀门闭锁
//仅仅支持等待打开阀门，因此只在open中执行通知
@ThreadSafe
public class MyThreadGate {
    //条件谓词:open-since(n) (isOpen || generation)
    @GuardedBy("this") private boolean isOpen;
    @GuardedBy("this") private int generation;

    public synchronized void close(){
        isOpen = false;
    }

    public synchronized void open(){
        ++generation;
        isOpen = true;
        notifyAll();
    }

    /**
     * 条件谓词：opened-since(generation on entry)
     *
     * 当阀门打开时有N个线程正在等待，那么这些线程都应该被执行；
     * 然而，阀门在打开后又迅速的关闭了，并且await()只检查isOpen，
     * 那么所有线程都可能无法释放：当所有线程收到通知时，将重新请求锁并退出wait，
     * 而此时的阀门可能已经再次关闭了。
     * 因此，在这里使用一个更复杂的条件谓词：每次当阀门关闭时，递增一个"Generation"计数器，
     * 如果阀门现在时打开的，或者阀门自从该线程到达后就一直时打开的，那么线程就可以通过await
     * @throws InterruptedException
     */
    public synchronized void await() throws InterruptedException {
        int arrivalGeneration = generation;
        while(!isOpen && arrivalGeneration == generation){
            wait();
        }
    }

}
