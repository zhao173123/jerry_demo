package producer.consumer;

/**
 * @author zhaochen
 * @date 2018/11/19
 * @desc wait\notify实现
 */
public class PCWaitNotifyTest{

    private static Integer count = 0;
    private static final Integer FUL = 10;
    private static String LOCK = "lock";

    public static void main(String[] args){
        PCWaitNotifyTest test = new PCWaitNotifyTest();
        new Thread(test.new Producer()).start();
        new Thread(test.new Consumer()).start();
        new Thread(test.new Producer()).start();
        new Thread(test.new Consumer()).start();
        new Thread(test.new Producer()).start();
        new Thread(test.new Consumer()).start();
        new Thread(test.new Producer()).start();
        new Thread(test.new Consumer()).start();
    }

    /**
     * 生产者
     */
    class Producer implements Runnable{
        @Override
        public void run(){
            for(int i = 0; i < FUL; i++){
                try{
                    Thread.sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                synchronized(LOCK){
                    while(count == FUL){
                        try{
                            LOCK.wait();
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    count++;
                    System.out.println(Thread.currentThread().getName() + "生产者生产，目前共有：" + count);
                    LOCK.notifyAll();
                }
            }
        }
    }

    class Consumer implements Runnable{
        @Override
        public void run(){
            for(int i = 0; i < FUL; i++){
                try{
                    Thread.sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                synchronized(LOCK){
                    while(count == 0){
                        try{
                            LOCK.wait();
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    count--;
                    System.out.println(Thread.currentThread().getName() + "消费者消费，目前消费者数量：" + count);
                    LOCK.notifyAll();
                }
            }
        }
    }

}
