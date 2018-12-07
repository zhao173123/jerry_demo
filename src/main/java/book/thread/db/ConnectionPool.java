package book.thread.db;

import com.google.common.collect.Lists;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * @auth zhaochen
 * @date 2018/8/9 下午4:31
 */
public class ConnectionPool{

    private LinkedList<Connection> pool = Lists.newLinkedList();

    public ConnectionPool(int initialSize){
        if(initialSize > 0){
            for(int i = 0 ; i < initialSize; i++){
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    //释放连接需要通知，使其他消费者感知
    public void releaseConnection(Connection connection){
        if(connection != null){
            synchronized(pool){
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    //在mills内无法获取到连接,返回null
    public Connection fetchConnection(long mills) throws InterruptedException{
        synchronized(pool){
            if(mills <= 0){
                while(pool.isEmpty()){
                    pool.wait();
                }
                return pool.removeFirst();
            }else{
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while(pool.isEmpty() && remaining > 0){
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                if(!pool.isEmpty()){
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }

}