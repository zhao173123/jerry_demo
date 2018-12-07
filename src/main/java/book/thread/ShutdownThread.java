package book.thread;

import java.util.concurrent.TimeUnit;

/**
 * @auth zhaochen
 * @date 2018/8/8 下午4:04
 */
public class ShutdownThread{

    public static void main(String[] args) throws InterruptedException{
        Runner runner = new Runner();
        Thread countThread = new Thread(runner, "CountThread");
        countThread.start();

        TimeUnit.SECONDS.sleep(1);
        countThread.interrupt();

        Runner runner2 = new Runner();
        countThread = new Thread(runner2, "CountThread2");
        countThread.start();
        TimeUnit.SECONDS.sleep(1);
        runner2.cancel();
    }

    static class Runner implements Runnable{
        private long i;
        private volatile boolean on = true;
        @Override
        public void run(){
            while(on && !Thread.currentThread().isInterrupted()){
                i++;
            }
            System.out.println("Count i = " + i);
        }

        public void cancel(){
            on = false;
        }
    }

}
