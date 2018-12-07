package book.thread;

import java.util.concurrent.TimeUnit;

/**
 * @auth zhaochen
 * @date 2018/8/8 下午3:46
 */
public class InterruptedThread{

    public static void main(String[] args) throws InterruptedException{
        Thread sleepThread = new Thread(new SleepRunner(), "SleepThread");
        sleepThread.setDaemon(true);
        Thread busyThread = new Thread(new BusyRunner(), "BusyThread");
        busyThread.setDaemon(true);
        sleepThread.start();
        busyThread.start();

        TimeUnit.SECONDS.sleep(5);
        sleepThread.interrupt();
        busyThread.interrupt();
        System.out.println("SleepThread interrupted is " + sleepThread.isInterrupted());
        System.out.println("BusyThread interrupted is " + busyThread.isInterrupted());
        SleepUtils.second(2);
    }

    static class SleepRunner implements Runnable{
        @Override
        public void run(){
            while(true){
                SleepUtils.second(10);
            }
        }
    }

    static class BusyRunner implements Runnable{
        @Override
        public void run(){
            while(true){
            }
        }
    }
}
