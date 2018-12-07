package com.sun.java8.concurrent.locks.reentrantlock;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReentrantReadWriteLock：读写锁
 *
 * ReentrantLock是一个排它锁，同一时间只允许一个线程占用；
 * 而ReentrantReadWriteLock的特点如下：
 * 1.允许多个读线程同时访问
 * 2.不允许读、写同时访问
 * 3.不允许写、写同时访问
 * 在实际情况中，对共享数据的读操作远远多于写操作，所以相对于排它锁提高了并发性。
 * ReentrantReadWriteLock内部维护了读锁和写锁，具体来说就是支持以下几点功能：
 * 1.支持公平和非公平的获取锁方式
 * 2.支持可重入。读线程在获取了读锁后还可以获取读锁，写线程获取了写锁后既可以获取写锁也可以获取读锁
 * 3.允许从写入锁降级为读锁，实现：先获取写锁，然后获取读锁，最后释放写锁。不可逆。
 * 4.读写锁都支持锁获取期间的中断
 * 5.写入锁提供condition实现，读锁不支持condition，readLock.newCondition()会抛出UnsupportedOperationException
 *
 * 背景：
 * ReentrantReadWriteLock的读写锁是通过一个32位的int类型来表示锁占用的线程数。
 * 使用的方式是高16位表示读锁数量，低16位表示写锁数量。
 *
 * 使用方式：
 * 1.获取读锁
 * ReentrantReadWriteLock.readLock().lock();
 * 和正常的lock操作一样，只是自己实现了tryAcquireShared
 *
 *  源码分析：
 *  //获取读锁
 *  protected final int tryAcquireShared(int unused) {
 *      Thread current = Thread.currentThread();
 *             int c = getState();
 *             //如果有写锁占用且占有写锁的线程不是本线程，直接返回-1；这里有第二个条件是因为写锁可以降级到读锁。
 *             if (exclusiveCount(c) != 0 &&
 *                 getExclusiveOwnerThread() != current)
 *                 return -1;
 *             int r = sharedCount(c); //获取读锁数量
 *             if (!readerShouldBlock() && //读锁不需要block(具体见下面readerShouldBlock源码分析)
 *                 r < MAX_COUNT && //当前读锁数量小于最大值2⌃16-1
 *                 compareAndSetState(c, c + SHARED_UNIT)) { //且cas获取锁成功
 *                 //firstReader不会放到readHolds中，这样在读锁只有一个的情况下，避免了查找readHolds
 *                 if (r == 0) { //当前线程为第一个获取读锁的线程
 *                     firstReader = current;
 *                     firstReaderHoldCount = 1;
 *                 } else if (firstReader == current) { //如果第一个获取读锁的对象为当前线程，firstReader重入
 *                     firstReaderHoldCount++;
 *                 } else { //将该线程持有锁的次数信息放入线程本地变量，通过ThreadLocal实现
 *                     HoldCounter rh = cachedHoldCounter;
 *                     if (rh == null || rh.tid != getThreadId(current))
 *                         cachedHoldCounter = rh = readHolds.get();
 *                     else if (rh.count == 0)
 *                         readHolds.set(rh);
 *                     rh.count++;
 *                 }
 *                 return 1;
 *             }
 *             //第一次获取锁失败有2种情况
 *             //1.当前线程占有写锁，并且有其他写锁在下一个节点等待获取写锁；除非当前线程的下一个节点被取消，否则full...也获取不到读锁
 *             //2.没有写锁被占用，但是cas获取读锁失败（有其他读锁在申请）
 *             //通过full...再次获取读锁
 *             return fullTryAcquireShared(current);
 *  }
 *
 *  readerShouldBlock的实现公平锁和非公平锁略有区别
 *  公平锁：
 *  final boolean readerShouldBlock() {
 *      return hasQueuedPredecessors();
 *  }
 *  //读锁何时需要block:判断Node等待队列中否有本节点之前的前驱节点
 *  //这里可以这么理解!hasQueuedPredecessors，h == t || ((s = h.next)!=null && s.thread == Thread.currentThread())
 *  //1.h==t 说明Node的等待队列为空
 *  //2.(s = h.next)!=null&& s.thread == Thread.currentThread():等待队列中有值且是本线程申请锁资源
 *  //满足以上2点的任何一个，!hasQueuedPredecessors返回true
 *  //再回头看!readerShouldBlock()就是!hasQueuedPredecessors()，此时可以申请读锁，继续执行下面的r<MAX_COUNT判断
 *  public final boolean hasQueuedPredecessors() {
 *      Node t = tail;
 *      Node h = head;
 *      Node s;
 *      return h != t &&
 *             ((s = h.next) == null || s.thread != Thread.currentThread());
 *  }
 *
 *  非公平锁：
 *  final boolean readerShouldBlock() {
 *     return apparentlyFirstQueuedIsExclusive();
 *  }
 *  //head非空&头结点有后续节点&后续节点是独占模式(写锁)&后续节点的thread不为null时，不满足获取读锁条件，直接执行fullTryAcquireShared
 *  final boolean apparentlyFirstQueuedIsExclusive() {
 *     Node h, s;
 *     return (h = head)!= null &&
 *          (s = h.next)!= null &&
 *           !s.isShared()&&s.thread != null;
 *  }
 *
 *  其实readerShouldBlock的本质就是在检测这次获取读锁资源的操作时，AQS的等待队列中是否已经有写锁了。
 *  如果已经有写锁，
 *  那么要判断写锁是不是本线程，是本线程可以做锁降级；不是本线程就执行fullTryAcquireShared
 *  如果没有写锁，就可以继续执行，做r<MAX_COUNT判断的分支
 *
 *  //第一次获取读锁失败，通过fullTryAcquireShared再次尝试获取读锁
 *  //其实进行到这里有2种可能
 *  //1.readerShouldBlock()返回true:等待队列中已经有写锁等待了
 *  //2.compareAndSetState(c, c + SHARED_UNIT):读锁更新失败，说明有其他线程在申请读锁
 *  //退出有3种情况：
 *  //1.如果当前线程不是写锁的持有者，直接返回-1，结束尝试获取读锁，需要排队去申请读锁
 *  //2.除了当前线程持有写锁外，还有其他线程已经排队在申请写锁
 *  //3.成功获取读锁
 *  final int fullTryAcquireShared(Thread current) {
 *      HoldCounter rh = null;
 *             for (;;) {//自旋
 *                 int c = getState();
 *                 //如果当前线程不是写锁的持有者，直接返回-1，结束尝试获取读锁，需要排队去申请读锁
 *                 if (exclusiveCount(c) != 0) {
 *                     if (getExclusiveOwnerThread() != current)
 *                         return -1;
 *                 }
 *                 //如果需要阻塞，说明除了当前线程持有写锁外，还有其他线程已经排队在申请写锁，
 *                 //故即使申请读锁的线程已经持有写锁（写锁内部再次申请读锁，俗称锁降级）还是会失败
 *                 //因为有其他线程也在申请写锁，此时，只能结束本次申请读锁的请求，转而去排队，否则，将造成死锁
 *                 else if (readerShouldBlock()) {
 *                     if (firstReader == current) { //如果当前线程是第一个获取了写锁，那其他线程无法申请写锁
 *                     } else {
 *                     //从readHolds中移除当前线程的持有数，然后返回-1，然后去排队获取读锁。
 *                         if (rh == null) {
 *                             rh = cachedHoldCounter;
 *                             if (rh == null || rh.tid != getThreadId(current)) {
 *                                 rh = readHolds.get();
 *                                 if (rh.count == 0)
 *                                     readHolds.remove();
 *                             }
 *                         }
 *                         if (rh.count == 0)
 *                             return -1;
 *                     }
 *                 }
 *                 if (sharedCount(c) == MAX_COUNT)
 *                     throw new Error("Maximum lock count exceeded");
 *                 if (compareAndSetState(c, c + SHARED_UNIT)) {//获取读锁，后续就是更新readHolds等内部变量
 *                     if (sharedCount(c) == 0) {
 *                         firstReader = current;
 *                         firstReaderHoldCount = 1;
 *                     } else if (firstReader == current) {
 *                         firstReaderHoldCount++;
 *                     } else {
 *                         if (rh == null)
 *                             rh = cachedHoldCounter;
 *                         if (rh == null || rh.tid != getThreadId(current))
 *                             rh = readHolds.get();
 *                         else if (rh.count == 0)
 *                             readHolds.set(rh);
 *                         rh.count++;
 *                         cachedHoldCounter = rh; // cache for release
 *                     }
 *                     return 1;
 *                 }
 *             }
 *  }
 *
 *
 */
public class ReentrantReadWriteLockTest {


    public static void main(String[] args) {
        final MyObject object = new MyObject();
        ExecutorService executor = Executors.newCachedThreadPool();
        for(int i = 0; i < 3; i++) {
            executor.execute(() -> {
                for(int j = 0; j < 5; j++) {
                    object.put(new Random().nextInt(1000));
                }
            });
        }

        for(int i = 0; i < 3; i++) {
            executor.execute(() -> {
                for(int j = 0; j < 5; j++) {
                    object.get();
                }
            });
        }
        executor.shutdown();
    }

    static class MyObject{
        private Object object;
        private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

        public void get(){
            rwl.readLock().lock();
            System.out.println(Thread.currentThread().getName() + "准备读数据!");
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + "读数据为：" + this.object);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                rwl.readLock().unlock();
            }
        }

        public void put(Object object){
            rwl.writeLock().lock();
            System.out.println(Thread.currentThread().getName() + "准备写数据！");
            try {
                Thread.sleep(new Random().nextInt(1000));
                this.object = object;
                System.out.println(Thread.currentThread().getName() + "写数据为：" + this.object);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                rwl.writeLock().unlock();
            }
        }
    }
}
