package com.sun.java8.concurrent.locks.aqs.clh;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class CLHLock {

    public static class CLHNode {
        private boolean isLocked = true;//默认在等待锁
    }

    private volatile CLHNode tail;
    private static final AtomicReferenceFieldUpdater<CLHLock, CLHLock.CLHNode> UPDATOR = AtomicReferenceFieldUpdater.newUpdater(CLHLock.class, CLHNode.class, "tail");

    public void lock(CLHNode currentThreadCLHNode) {
        //this.tail设置成currentThreadCLHNode
        CLHNode preNode = UPDATOR.getAndSet(this, currentThreadCLHNode);
        if (preNode != null) {//已经有线程占用了锁，进入自旋
            while (preNode.isLocked) {

            }
        }
    }

    public void unlock(CLHNode currrentThreadCLHNode) {
        //如果队列里只有当前线程，则释放对当前线程的引用
        if (UPDATOR.compareAndSet(this, currrentThreadCLHNode, null)) {
            //还有后续线程
            currrentThreadCLHNode.isLocked = false;//改变状态，让后续线程结束自旋
        }
    }

}
