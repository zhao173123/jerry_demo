package book.thread;

import java.util.concurrent.TimeUnit;

/**
 * @auth zhaochen@mgzf.com
 * @date 2018/8/8 上午11:42
 */
public class ThreadState{

    public static void main(String[] args){
        new Thread(new TimeWaiting(), "TimeWaitingThread").start();
        new Thread(new Waiting(), "WaitingThread").start();

        //使用2个Blocked线程，一个获取锁成功 TIMED_WAITING，一个被阻塞 BLOCKED
        new Thread(new Blocked(), "BlockedThread-1").start();
        new Thread(new Blocked(), "BlockedThread-2").start();
    }

    // 不断的进行睡眠 TIMED_WAITING
    static class TimeWaiting implements Runnable{
        @Override
        public void run(){
            while(true){
                SleepUtils.second(100);
            }
        }
    }

    // 在Waiting.class实例上等待 WAITING
    static class Waiting implements Runnable{
        @Override
        public void run(){
            while(true){
                synchronized(Waiting.class){
                    try{
                        Waiting.class.wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 在Blocked.class实例上加锁后，不会释放该锁
    static class Blocked implements Runnable{
        @Override
        public void run(){
            synchronized(Blocked.class){
                while(true){
                    SleepUtils.second(100);
                }
            }
        }
    }


}

class SleepUtils{
    public static final void second(long seconds){
        try{
            TimeUnit.SECONDS.sleep(seconds);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}