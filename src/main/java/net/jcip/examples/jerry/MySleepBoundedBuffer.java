package net.jcip.examples.jerry;

/**
 * 尝试通过put和take实现一种简单的"轮询或休眠"重试机制
 * 从而使调用者无须在每次调用时都实现重试逻辑。
 *
 * @param <V>
 */
public class MySleepBoundedBuffer<V> extends MyBaseBoundedBuffer<V>{

    int SLEEP_GRANULARITY = 60;

    protected MySleepBoundedBuffer(int capacity) {
        super(capacity);
    }

    public void put(V v) throws InterruptedException {
        while(true){
            synchronized (this){
                if(!isFull()){
                    doPut(v);
                    return;
                }
            }
            Thread.sleep(SLEEP_GRANULARITY);
        }
    }

    public V take() throws InterruptedException {
        while(true){
            synchronized (this){
                if(!isEmpty()){
                    return doTake();
                }
            }
            Thread.sleep(SLEEP_GRANULARITY);
        }
    }
}
