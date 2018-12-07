package com.sun.java8.forkjoin.eight;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 *
 *
 * 1.ForkJoinTask: 任务, 它比传统的任务更加轻量，不再对是RUNNABLE的子类,提供fork/join方法用于分割任务以及聚合结果
 *  1.0 常量解释
 *      //标识任务目前的状态，如果<0，表示任务处于结束状态；(s>>>16) != 0 唤醒其他线程，notifyAll()
 *      volatile int status;
 *  1.1 fork()
 *  1.2 join()
 * 2.ForkJoinWorkerThread : 用于执行任务的线程，区别使用非ForkJoinWorkerThread线程提交的task，
 * 启动一个该Thread就会自动注册一个WorkQueue到pool。这里规定，拥有Thread的WorkQueue只能出现在WorkQueue数组的奇数位。
 * 3.ForkJoinPool : 用于执行ForkJoinTask任务的执行池,不再是传统执行池.
 *     Worker+Queue 的组合模式,而是维护了一个队列数组WorkQueue,这样在提交任务和线程任务的时候大幅度的减少碰撞。
 *     3.0 常量解释,标识pool运行状态，使用bit位来标识不同状态
 *     private static final int  RSLOCK     = 1;
 *     private static final int  RSIGNAL    = 1 << 1; // 2
 *     private static final int  STARTED    = 1 << 2; // 4
 *     private static final int  STOP       = 1 << 29; // 536870912
 *     private static final int  TERMINATED = 1 << 30; // 1073741824
 *     private static final int  SHUTDOWN   = 1 << 31; //-2147483648
 *     //64位，每个部分都有不同的作用。0x xxxx-1 xxxx-2 xxxx-3 xxxx-4
 *     //使用16进制来标识，初始化 ：long np = (long)(-parallelism);this.ctl=((np<<AC_SHIFT)&AC_MASK)|((np<<TC_SHIFT)&TC_MASK);
 *     //每4位分别标识为1，2，3，4
 *     //编号为1的16位：AC表示现在获取的线程数，这里的初始化有个技巧，使用并行数的相反数，这样如果active的线程数还没达到设置的阈值ctl是个负数。
 *     //通过这种方式可以很简单的判断并行数是否到达了阈值。
 *     //编号为2的16位：TC表示线程总量，初始值也是并行数的相反数。TC记录了一共开启的线程数，AC记录了没有挂起的线程。
 *     //编号3和4的32位：最高位标识active|inactive，其他31位为版本标识，标识workQueue[]的index
 *     volatile long ctl;
 *     3.1 ForkJoinPool提供的提交接口很多，不管提交的是Callable、Runnable、ForkJoinTask最终都会转换成ForkJoinTask，
 *         调用externalPush(ForkJoinTask<?> task)来进行提交逻辑。
 *         提交过程如下：
 *         3.1.1 如果是第一次提交(或者是hash之后的队列还未初始化)，调用externalSubmit
 *          3.1.1.1 第一遍循环（runState不是初始状态）: 1.lock 2.创建数组WorkQueue[n]，这里的n是power of 2 3.runState设置为开始状态
 *          3.1.1.2 第二遍循环（根据ThreadLocalRandom.getProbe()hash后的数组中相应位置的WorkQueue未初始化）：
 *                      初始化WorkQueue，通过这种方式创立的WorkQueue均是SHARED_QUEUE，scanState=INACTIVE
 *          3.1.1.3 第三遍循环：找到刚刚创建的WorkQueue,lock住队列，将数据塞到array top位置。如果添加成功，就调用signalWork
 *         3.1.2 如果hash之后队列已经存在，lock队列，将数据塞到top位置。如果该队列任务很少(n<=1)，会调用signalWork
 *     3.2 signalWork ： 用于创建或者激活工作线程
 *
 *
 * 3.1 变量说明 :
 * 3.2 WorkQueue : 双向链表，用于任务的有序执行。
 *      3.2.0 常量解释
 *      //如果workQueue没有属于自己的owner，该值为inactive也就是一个负数。如果有自己的owner，该值的初始值为其在workQueue[]数组的下标，也肯定是个奇数
 *      volatile int scanState;
 *      static final int SCANNING = 1;
 *      static final int INACTIVE = 1 << 31;
 *      //记录前任的 idle worker
 *      int stackPred;
 *      //如果下标为偶数的workQueue，则其mode是共享类型；如果有自己的owner，默认是LIFO
 *      int config; //index|mode
 *      volatile int qlock;//锁标识，在多线程队列中添加数据会引起竞争，使用此锁标识
 *      volatile int base;//worker steal的偏移量，因为其他的线程都可以偷该队列的任务，所以base使用volatile
 *      int top;//owner执行任务的偏移量
 *      volatile Thread parker;//如果owner挂起，则使用该变量做记录
 *      volatile ForkJoinTask<?> currentJoin;//当前正在join等待结果的任务
 *      volatile ForkJoinTask<?> currentSteal;//当前执行的任务是steal过来的，该变量做记录
 * ******************************************
 *
 *
 * @author zhaochen
 * @date 2018/8/21
 */
//这里使用一个求整数数组所有元素之和的例子来说明，也可以参照其他同包下的实例
public class ForkJoinTest implements Calculator{

    private ForkJoinPool pool;

    public ForkJoinTest(){
        this.pool = new ForkJoinPool();
    }

    private static class SumTask extends RecursiveTask<Long>{

        private static final long serialVersionUID = 1L;
        
        private static final int THREASHOLD = 6;
        private long[] numbers;
        private int from;
        private int to;

        public SumTask(long[] numbers, int from, int to){
            this.numbers = numbers;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Long compute(){
            if(to - from < THREASHOLD){
                long total = 0;
                for(int i = from;i < to;i++){
                    total += numbers[i];
                }
                return total;
            }else{
                int middle = (from + to) / 2;
                SumTask leftTask = new SumTask(numbers, from, middle);
                SumTask rightTask = new SumTask(numbers, middle + 1, to);
                /**
                 *     public final ForkJoinTask<V> fork() {
                 *         Thread t;
                 *         //1.如果当前线程是工作线程，直接push到自己拥有的队列的top位置
                 *          if ((t = Thread.currentThread()) instanceof ForkJoinWorkerThread)
                 *             ((ForkJoinWorkerThread)t).workQueue.push(this);
                 *         else //2.非工作线程，提交到pool
                 *             ForkJoinPool.common.externalPush(this);
                 *         return this;
                 *     }
                 *
                 *
                 *
                 */
                leftTask.fork();
                rightTask.fork();
                /**
                 *
                 *     public final V join() {
                 *         int s;
                 *         //结果异常，抛出异常
                 *         if ((s = doJoin() & DONE_MASK) != NORMAL)
                 *             reportException(s);
                 *         //获取返回值
                 *         return getRawResult();
                 *     }
                 *
                 *    private int doJoin() {
                 *         int s; Thread t; ForkJoinWorkerThread wt; ForkJoinPool.WorkQueue w;
                 *         return (s = status) < 0 ? s :
                 *             ((t = Thread.currentThread()) instanceof ForkJoinWorkerThread) ?
                 *             (w = (wt = (ForkJoinWorkerThread)t).workQueue).
                 *             tryUnpush(this) && (s = doExec()) < 0 ? s :
                 *             wt.pool.awaitJoin(w, this, 0L) :
                 *             externalAwaitDone();
                 *     }
                 *
                 *  doJoin的执行过程：
                 *  1.如果需要join的任务已经完成，直接返回结果s
                 *  2.如果当前线程不是工作线程，执行externalAwaitDone()：
                 *        首先，设置任务的status为signal，这样该任务执行完成后会调用notifyAll唤醒自己；
                 *        其次，阻塞自己，直到任务执行完成唤醒自己。
                 *  ((t = Thread.currentThread()) instanceof ForkJoinWorkerThread) ?
                 *    (w = (wt = (ForkJoinWorkerThread)t).workQueue).tryUnpush(this) && (s = doExec()) < 0 ? s : wt.pool.awaitJoin(w, this, 0L)
                 *      : externalAwaitDone();
                 *  3.如果当前线程就是工作线程，且需要join的任务正好是当前线程拥有队列的top位置，意味着当前线程下一个就执行它doExec()
                 *        如果不是top位置，执行wt.pool.awaitJoin(w, this, 0L)
                 *        3.1 设置currentJoin表明自己正在等待该任务；
                 *        3.2 如果发现 w.base == w.top 或者  tryRemoveAndExec返回 true说明自己所属的队列为空，
                 *        也说明我们通过fork将本线程的任务已经被别的线程偷走，该线程也不会闲着，将会helpStealer帮助帮助自己执行任务的线程执行任务(互惠互利,你来我往)
                 *        3.3 如果tryCompensate为 true,则阻塞本线程，等待任务执行结束的唤醒
                 */
                return leftTask.join() + rightTask.join();
            }
        }
    }

    @Override
    public long sumUp(long[] numbers){
        return pool.invoke(new SumTask(numbers, 0, numbers.length - 1));
    }

    public static void main(String[] args){
        System.out.println(1 << 1); //2
        System.out.println(1 << 2); //4
        System.out.println(1 << 4); //16
        System.out.println(1 << 29); //536870912
        System.out.println(1 << 30); //1073741824
        System.out.println(1 << 31); //-2147483648

        System.out.println(Runtime.getRuntime().availableProcessors());//4核CPU

        System.out.println(0xfff);//4095
        System.out.println(0xfff<<1);//8190
        System.out.println(0xfff<<16);//268369920
        System.out.println(Integer.MAX_VALUE);//0x7fffffff -> 2147483647

        Long np = (long)-4;//64位
        //2进制数分为有符号和无符号数，第一位表示正负，1代表负数，0代表正数
        //-4->1000 .... 0000 0000 0100; -4<<1 : 1000 ... 000 0000 0100<<1 -> 1000 .... 0000 0000 1000 = -8
        System.out.println(np << 1); //-8
        System.out.println(np << 5); //-128 1000 0000 0000 0100 -> 1000 0000 1000 0000
        System.out.println(np << 12); //-16384 1000 0000 0000 0100 -> 1100 0000 0000 0000
        System.out.println(np << 13); //-32768
        System.out.println("np : " + np + ",np<<48 : " + (np << 48));//np : -4,np<<48 : -1125899906842624

        System.out.println(0xffffL + "," + (0xffffL << 48));//65535,-281474976710656

        System.out.println(0x0001L << (32 + 15)); //-2147483648

        System.out.println(1 << 31);//-2147483648

    }
}
