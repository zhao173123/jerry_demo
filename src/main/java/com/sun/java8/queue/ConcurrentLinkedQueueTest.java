package com.sun.java8.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * 核心为Node（通过E item和指向下一个节点的Node<E> next构造的一个无界线程安全队列）
 *
 * @author jerry
 */
public class ConcurrentLinkedQueueTest<E>{

    // 初始化，tail=head=new Node(null);
    public static void main(String[] args){
        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
        queue.add(1);
        System.out.println(queue.poll());
    }

    // 加入队列（队尾）
    public boolean add(E e){
        return offer(e);
    }

    /**
     * 入队说明：
     * 1.将入队的节点设置成当前队列尾节点的下一个节点
     * 2.更新tail节点，如果tail节点的next节点不为空，则将入队节点设置成tail节点；
     *   如果tail节点的next节点为空，则将入队节点设置成tail的next节点
     * PS：所以tail节点不总是尾节点,延迟设置tail和head
     * 0.初始化 head|tail -> null
     * 1.offer("aaa") 执行q==null分支，p.casNext() 此时head->null|tail->null|tail.next -> aaa，
     *   p==t,返回true，跳出for循环
     * 2.offer("bbb") 执行else分支，p=q;因为for循环，接着执行q==null分支，p.casNext() 此时head->null|newNode|
     *   p!=t,casTail(t,newNode) 设置tail，tail->newNode
     * 3.t != (t = tail) => t != tail, t = tail
     * @param e
     * @return
     */
    public boolean offer(E e){
        Node<E> newNode = new Node(e);
        for(Node<E> t = tail, p = t;;){
            Node<E> q = p.next;
            // p是尾节点
            if(q == null){
                if(p.casNext(null, newNode)){// 1. 设置p节点的next节点为newNode，因为之前next为null，所以是casNext(null, newNode)
                    if(p != t)
                        casTail(t, newNode);// 2. 设置tail节点为newNode
                    return true;
                }
            }
            else if(p == q){ //表示当前节点已经不再链表中(已被移除),有其他线程执行poll；需要将p重新指向head或tail
                p = (t != (t = tail)) ? t : head;
            }
            // q!=null && p!=q;
            else
                p = (p != t && t != (t = tail)) ? t : q;//t!=tal,t=tail
        }
    }

    // 出列，p为出列元素；q为新的head节点
    public E poll(){
        restartFromHead:
        for(;;){//自循环
            for(Node<E> h = head, p = h, q;;){
                E item = p.item;
                //p.item就是需要出列的元素；将p节点引用的元素置为null
                if(item != null && p.casItem(item, null)){
                    // Successful CAS is the linearization point
                    // for item to be removed from this queue.
                    //将p节点的下一个节点设置成head节点
                    if(p != h) // hop two nodes at a time
                        updateHead(h, ((q = p.next) != null) ? q : p);
                    return item;//对头item出列
                }else if((q = p.next) == null){//队列已经为空
                    updateHead(h, p);
                    return null;
                }else if(p == q)
                    continue restartFromHead;
                else
                    p = q;
            }
        }
    }

    //size()

    final void updateHead(Node<E> h, Node<E> p){
        if(h != p && casHead(h, p))
            h.lazySetNext(h);
    }

    private boolean casHead(Node<E> cmp, Node<E> val){
        return UNSAFE.compareAndSwapObject(this, headOffset, cmp, val);
    }

    private transient volatile Node<E> head;
    private transient volatile Node<E> tail;

    private boolean casTail(Node<E> cmp, Node<E> val){
        return UNSAFE.compareAndSwapObject(this, tailOffset, cmp, val);
    }

    private static class Node<E>{
        volatile E item;
        volatile Node<E> next;

        /**
         * Constructs a new node. Uses relaxed write because item can only be
         * seen after publication via casNext.
         */
        Node(E item){
            UNSAFE.putObject(this, itemOffset, item);
        }

        boolean casItem(E cmp, E val){
            return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
        }

        void lazySetNext(Node<E> val){
            UNSAFE.putOrderedObject(this, nextOffset, val);
        }

        boolean casNext(Node<E> cmp, Node<E> val){
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        // Unsafe mechanics

        private static final sun.misc.Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;

        static{
            try{
                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Class<?> k = Node.class;
                itemOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("next"));
            }catch(Exception e){
                throw new Error(e);
            }
        }
    }

    private static final sun.misc.Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;

    static{
        try{
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> k = ConcurrentLinkedQueue.class;
            headOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("tail"));
        }catch(Exception e){
            throw new Error(e);
        }
    }

}
