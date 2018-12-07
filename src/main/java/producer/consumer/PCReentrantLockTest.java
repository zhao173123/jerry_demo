package producer.consumer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhaochen
 * @date 2018/11/20
 */
public class PCReentrantLockTest{

    private static Integer count = 0;
    private static final Integer FULL = 10;

    private Lock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public static void main(String[] args){
        PCReentrantLockTest test2 = new PCReentrantLockTest();
        new Thread(test2.new Producer()).start();
        new Thread(test2.new Consumer()).start();
        new Thread(test2.new Producer()).start();
        new Thread(test2.new Consumer()).start();
        new Thread(test2.new Producer()).start();
        new Thread(test2.new Consumer()).start();
        new Thread(test2.new Producer()).start();
        new Thread(test2.new Consumer()).start();
    }

    class Producer implements Runnable{
        @Override
        public void run(){
            for(int i = 0; i < FULL; i++){
                try{
                    Thread.sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                lock.lock();
                try{
                    while(count == FULL){
                        try{
                            notFull.await();
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    count++;
                    System.out.println(Thread.currentThread().getName() + "生产者生产，目前总共有：" + count);
                    notEmpty.signal();
                }finally{
                    lock.unlock();
                }
            }
        }
    }

    class Consumer implements Runnable{
        @Override
        public void run(){
            for(int i = 0; i < FULL; i++){
                try{
                    Thread.sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                lock.lock();
                try{
                    while(count == 0){
                        try{
                            notEmpty.await();
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    count--;
                    System.out.println(Thread.currentThread().getName()
                            + "消费者消费，目前总共有" + count);
                    notFull.signal();
                }finally{
                    lock.unlock();
                }
            }
        }
    }

}
