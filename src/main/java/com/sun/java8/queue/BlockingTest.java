package com.sun.java8.queue;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zhaochen
 * @date 2018/8/15
 */
public class BlockingTest{

    /**
     * 1.add会抛出异常，offer没有，put一直阻塞
     * 2.poll返回值，take阻塞，remove异常
     *
     * @throws InterruptedException
     */
    @Test
    public void testArrayBlockingQueue() throws InterruptedException{
        ArrayBlockingQueue<Integer> abq = new ArrayBlockingQueue(5);
        abq.add(1);
        abq.add(2);
        abq.add(5);
        abq.add(3);
        assert 1 == abq.poll();
        assert 2 == abq.poll();
        assert 5 == abq.poll();
        assert 3 == abq.poll();
        abq.offer(6);
        assert 6 == abq.take();
        abq.put(7);
        abq.remove();
        assert 0 == abq.size();
    }

    @Test
    public void testLinkedBlockingQueue() throws InterruptedException{
        LinkedBlockingQueue<Integer> lbq = new LinkedBlockingQueue();
        lbq.add(1);
        lbq.add(2);
        assert 1 == lbq.poll();
        assert 2 == lbq.take();
    }
}
