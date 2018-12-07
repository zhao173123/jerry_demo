package net.jcip.examples.jerry;

import junit.framework.TestCase;

/**
 * testTakeBlocksWhenEmpty说明：
 * 如果take操作因为某种意料之外的原因失败了，那么支持限时的join方法可以确保测试最终完成。
 * 这里展示了take的多种属性，不仅能阻塞，还可以抛出InterruptedException。
 * 这种情况下，最好是对Thread进行子类化而不是使用线程池中的Runnable，
 * 即通过join方法来正确的结束测试。
 * 当主线程将一个元素放入队列后，"获取"taker线程应该接触阻塞状态。
 *
 */
public class MyBoundedBufferTest extends TestCase{

    private static final long LOCKUP_DETECT_TIMEOUT = 1000;
    private static final int CAPACITY = 10000;
    private static final int THRESHOLD = 10000;

    //新建的缓存是空的非满
    public void testIsEmptyWhenConstructed(){
        MyBoundedBuffer<Integer> bb = new MyBoundedBuffer<>(10);
        assertTrue(bb.isEmpty());
        assertFalse(bb.isFull());
    }

    //缓存满
    public void testIfFullAfterPuts() throws InterruptedException {
        MyBoundedBuffer<Integer> bb = new MyBoundedBuffer<>(10);
        for (int i = 0; i < 10; i++) {
            bb.put(i);
        }
        assertTrue(bb.isFull());
        assertFalse(bb.isEmpty());
    }

    public void testTakeBlocksWhenEmpty(){
        final MyBoundedBuffer<Integer> bb = new MyBoundedBuffer<>(10);
        Thread taker = new Thread(){
            public void run(){
                try{
                    int unused = bb.take();
                    fail();//如果执行到这里，那么表示出现了一个错误
                } catch (InterruptedException success) {

                }
            }
        };

        try{
            taker.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            taker.interrupt();
            //join的作用是让"主线程"等待"子线程"结束之后才能继续运行
            //将子线程taker加入到主线程main中，并且主线程main()会等待它的完成-此时主线程wait()
            //子线程执行完毕后唤醒主线程
            taker.join(LOCKUP_DETECT_TIMEOUT);
            assertFalse(taker.isAlive());//此时，子线程执行完毕
        } catch (InterruptedException e) {
            fail();
        }
    }
}