package net.jcip.examples.jerry;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

@ThreadSafe
public class MyOneShowLatch {

    private final Sync sync = new Sync();

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }

    /**
     *  释放锁
     *  1.首先调用AQS的tryReleaseShared -> Sync.tryReleaseShared()
     *  2.接着调用AQS的doReleaseShared()
     *
     */
    public void signal(){
        sync.releaseShared(0);
    }

    private class Sync extends AbstractQueuedSynchronizer {

        //如果闭锁是开的（state==1），那么这个操作将成功，否则失败
        @Override
        protected int tryAcquireShared(int arg) {
            return getState() == 1 ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            setState(1); // 打开闭锁
            return true; // 现在其他线程都可以获取该锁
        }
    }

}
