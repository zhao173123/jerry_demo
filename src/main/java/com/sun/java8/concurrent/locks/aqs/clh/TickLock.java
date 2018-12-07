package com.sun.java8.concurrent.locks.aqs.clh;


import java.util.concurrent.atomic.AtomicInteger;

public class TickLock {

    private AtomicInteger serviceNum = new AtomicInteger();//服务号
    private AtomicInteger ticketNum = new AtomicInteger();//排队号

    public int lock() {
        //原子获得一个排队号
        int myTicketNum = ticketNum.getAndIncrement();
        //只要当前服务号不是自己的就不断轮询
        while (serviceNum.get() != myTicketNum) {

        }
        return myTicketNum;
    }

    public void unlock(int myTicket) {
        //只有当前线程拥有者才能释放锁
        int next = myTicket + 1;
        serviceNum.compareAndSet(myTicket, next);
    }

}
