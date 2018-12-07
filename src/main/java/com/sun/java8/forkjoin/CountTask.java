package com.sun.java8.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author zhaochen
 * @date 2018/8/17
 * @desc fork/join框架
 * <p>
 * 1.使用Fork/Join框架，必须首先创建一个ForkJoin任务。它提供了在任务中执行fork()和join()操作的机制。
 * 通常不需要直接使用ForkJoin，只需要继承子类RecursiveAction（用于没有返回结果的任务）或者RescursiveTask（用于有返回值的任务）即可。
 * 2.ForkJoinPool：ForkJoinTask需要通过ForkJoinPool来执行。
 * </p>
 */
public class CountTask extends RecursiveTask<Integer>{

    private static final int THRESHOLD = 2;
    private int start;
    private int end;

    public CountTask(int start, int end){
        this.start = start;
        this.end = end;
    }

    /**
     * compute 4~5 = 9
     * compute 6~7 = 13
     * compute 8~9 = 17
     * compute 1~3 = 6
     * sum = 13 + 17 ==> 30
     * sum = 6 + 9 ==> 15
     * sum = 15 + 30 ==> 45
     *
     * @return
     */
    @Override
    protected Integer compute(){
        int sum = 0;
        boolean canCompute = (end - start) <= THRESHOLD;
        if(canCompute){
            for(int i = start;i <= end;i++){
                sum += i;
            }
            System.out.println(String.format("compute %d~%d = %d", start, end, sum));
            return sum;
        }
        int middle = (start + end) / 2;
        CountTask leftTask = new CountTask(start, middle);
        CountTask rightTask = new CountTask(middle + 1, end);
        /**
         * 这里方腾飞的方式
         * letfTask.fork();
         * rightTask.fork();
         * 是错误的，这样分配了2个线程，导致本身线程并没有处理任务.
         * 应该使用invokeAll的方式
         */
        invokeAll(leftTask, rightTask);
        int leftResult = leftTask.join();
        int rightResult = rightTask.join();
        sum = leftResult + rightResult;
        System.out.println("sum = " + leftResult + " + " + rightResult + " ==> " + sum);
        return sum;
    }

    public static void main(String[] args){
        //JDK7
        // ForkJoinPool forkJoinPool = new ForkJoinPool(2);
        //JDK8
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        CountTask task = new CountTask(1, 10);
        // Future<Integer> result = forkJoinPool.submit(task);
        int result = forkJoinPool.invoke(task);//提交一个Fork/Join的任务并发执行
        System.out.println(result);
    }

}
