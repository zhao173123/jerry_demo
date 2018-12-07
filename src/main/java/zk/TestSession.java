package zk;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class TestSession implements Watcher{

	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	
	@Override
	public void process(WatchedEvent event) {
		System.out.println("Receive watcher event: " + event);
		if(Event.KeeperState.SyncConnected == event.getState()){
			countDownLatch.countDown();
		}
	}
	
	public static void main(String[] args) throws IOException{
		Long startTime = new Date().getTime();
		//zk客户端和服务器创建回话都是异步过程，因此使用CountDownLatch来阻塞现成，等待服务器创建完成，并发送事件通知
		//public Zookeeper(String connectString, int sessionTimeout, Watcher watcher)
		/**
		 * 	connectString:表示连接的zk服务器地址列表，支持多个地址拼接，中间用逗号隔开
		 *  sessionTimeout:超时时间
		 * 	watcher:监听节点的状态变化；如果不需要监听，设为null  
		 **/
		ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new TestSession());
		try{
			countDownLatch.await();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		System.out.println("创建连接话费的时间：" + (new Date().getTime() - startTime) + "ms");
		System.out.println("连接状态"  + zooKeeper.getState());
	}
}
