package com.sun.java8.forkjoin;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author zhaochen
 * @date 2018/8/20
 * https://www.baeldung.com/java-fork-join
 */
public class CustomRecursiveTask extends RecursiveTask<Integer>{

    private int[] arr;
    private static final int THRESHOLD = 20;

    public CustomRecursiveTask(int[] arr){
        this.arr = arr;
    }

    @Override
    protected Integer compute(){
        if(arr.length > THRESHOLD){
            return ForkJoinTask.invokeAll(createSubTasks())
                    .stream()
                    .mapToInt(ForkJoinTask::join)
                    .sum();
        }else{
            return processing(arr);
        }
    }

    private Collection<CustomRecursiveTask> createSubTasks(){
        List<CustomRecursiveTask> divideTasks = Lists.newArrayList();
        divideTasks.add(new CustomRecursiveTask(Arrays.copyOfRange(arr, 0, arr.length / 2)));
        divideTasks.add(new CustomRecursiveTask(Arrays.copyOfRange(arr, arr.length / 2, arr.length)));
        return divideTasks;
    }

    private Integer processing(int[] arr){
        return Arrays.stream(arr)
                .filter(a -> a > 10 && a < 27)
                .map(a -> a * 10)
                .sum();
    }

    public static void main(String[] args){
        int [] arr = new int[]{12,12,13,14,15,16,17,18,19,20,10,19,18,17,16,15,41,31,12,11,10,11,12,13,14,15,16,17,12};
        CustomRecursiveTask customRecursiveTask = new CustomRecursiveTask(arr);
        ForkJoinPool pool = new ForkJoinPool();
        long startTime1 = System.currentTimeMillis();
        pool.execute(customRecursiveTask);
        int result = customRecursiveTask.join();
        long endTime1 = System.currentTimeMillis();
        System.out.println(result + ",time : " + (endTime1 - startTime1) + " ms"); //167

        long startTime2 = System.currentTimeMillis();
        result = pool.invoke(customRecursiveTask);
        long endTime2 = System.currentTimeMillis();
        System.out.println(result + ",time : " + (endTime2 - startTime2) + " ms"); // 0
    }

}
