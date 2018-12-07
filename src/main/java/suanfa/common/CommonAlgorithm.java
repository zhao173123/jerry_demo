package suanfa.common;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @author zhaochen
 * @date 2018/8/16
 */
public class CommonAlgorithm{

    public static final int[] nums = new int[]{1, 3, 15, 12, 10, 9};

    /**
     * 1.冒泡排序
     * 相邻的两个数比较，将较小的或较大的数据放在后面
     * 总的对比次数为N(N-1)，时间复杂度为O(n^2)
     */
    @Test
    public void bubbleSort(){
        int length = nums.length;
        int end = length - 1;
        for(int i = 0; i < end; i++){
            for(int j = 0; j < end; j++){
                if(nums[j] > nums[j+1]){
                    int temp = nums[j];
                    nums[j] = nums[j+1];
                    nums[j+1] = temp;
                }
            }
            end--;
        }

        for(int i = 0; i < nums.length; i++){
            System.out.print(nums[i] + " ");
        }
    }

    /**
     * 2.选择排序
     * 每次从待排队列中选出一个最小的元素，存放在序列的起始位置
     */
    public void choiceSort(){
        // for()
    }

    //3.插入排序

    //4.归并排序

    /**
     * 5.快速排序:
     * 1.取出第一个元素，将所有数据分为2份，左边的比该元素小，右边的比该元素大
     * 2.对左右2个子数组分别排序
     * 使用Fork/Join框架实现
     */
    static class QuickSortTask extends RecursiveAction{

        private final long[] arr;
        private final int lo, hi;
        private static final int THRESHOLD = 0;

        public QuickSortTask(long[] arr){
            this.arr = arr;
            this.lo = 0;
            this.hi = arr.length - 1;
        }

        public QuickSortTask(long[] arr, int lo, int hi){
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected void compute(){
            if(hi - lo > THRESHOLD){
                Arrays.sort(arr, lo, hi + 1);
            }else{
                long x = arr[hi];
                int i = lo - 1;
                for(int j = lo;j < hi;j++){
                    if(arr[j] <= x){
                        i++;
                        swap(arr, i, j);
                    }
                }
                swap(arr, i+1, hi);
                int pivot = i + 1;
                invokeAll(new QuickSortTask(arr, lo, pivot - 1), new QuickSortTask(arr, pivot + 1, hi));
            }
        }

        private void swap(long[] arr, int i, int j){
            if(i != j){
                long temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
    }

    @Test
    public void testQuickStartSort(){
        long[] arr = new long[]{1,2,3,4,5,6,7,19,21,3,12,54,32,67,86};
        QuickSortTask qst = new QuickSortTask(arr);
        ForkJoinPool fjPool = new ForkJoinPool();
        fjPool.submit(qst);
        fjPool.shutdown();

        for(long l : arr){
            System.out.print(l + " ");
        }
    }



    //6.二分查找法
}
