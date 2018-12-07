package producer.consumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author zhaochen
 * @date 2018/11/20
 * @desc BlockingQueue实现生产者和消费者模式
 *
 * BlockingQueue有四种处理方式：
 *  操作           抛出异常       特殊值         阻塞           超时
 *  插入           add(e)        offer(e)      put(e)         offer(e,time,unit)
 *  移除           remove(e)     poll()        take()         put(time,unit)
 *  检查           element()     peek()        不可用          不可用
 *
 * offer:将指定元素插入队列中，成功时返回true；如果当前没有可用的空间，返回false。不会抛出异常。
 * put:将指定元素插入队列中，将等待可用的空间。也就是>maxSize的时候，阻塞，直到能够有空间插入元素。
 * take:获取并移除此队列的头元素，在元素变得可用之前一直等待。queue.length==0的时候，一直阻塞。
 *
 */
public class PCBlockingQueueTest{

    private static Integer count = 0;

    //创建一个阻塞队列
    final BlockingQueue bq = new ArrayBlockingQueue<>(10);

    public static void main(String[] args){
        PCBlockingQueueTest test3 = new PCBlockingQueueTest();
        new Thread(test3.new Producer()).start();
        new Thread(test3.new Consumer()).start();
        new Thread(test3.new Producer()).start();
        new Thread(test3.new Consumer()).start();
        new Thread(test3.new Producer()).start();
        new Thread(test3.new Consumer()).start();
        new Thread(test3.new Producer()).start();
        new Thread(test3.new Consumer()).start();

    }

    class Producer implements Runnable{
        @Override
        public void run(){
            for(int i = 0; i < 10; i++){
                try{
                    Thread.sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                try{
                    bq.put(1);
                    count++;
                    System.out.println(Thread.currentThread().getName()
                            + "生产者生产，目前总共有" + count);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    class Consumer implements Runnable{
        @Override
        public void run(){
            for(int i = 0;i < 10;i++){
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                try {
                    bq.take();
                    count--;
                    System.out.println(Thread.currentThread().getName()
                            + "消费者消费，目前总共有" + count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
