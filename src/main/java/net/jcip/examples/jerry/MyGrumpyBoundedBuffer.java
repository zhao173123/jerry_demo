package net.jcip.examples.jerry;

/**
 * 继承自MyBaseBoundedBuffer实现的一个简单的有界缓存
 * put和take都进行了同步以确保实现对缓存状态的独占访问
 *
 * @param <V>
 */
public class MyGrumpyBoundedBuffer<V> extends MyBaseBoundedBuffer<V>{

    protected MyGrumpyBoundedBuffer(int capacity) {
        super(capacity);
    }

    public synchronized void put(V v){
        if(isFull()){
            throw new BufferFullException();
        }
        doPut(v);
    }

    public synchronized V take(){
        if(isEmpty()){
            throw new BufferEmptyException();
        }
        return doTake();
    }


    
}

class BufferFullException extends RuntimeException {

}

class BufferEmptyException extends RuntimeException {
}