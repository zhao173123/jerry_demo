package zk;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class TestGetData implements Watcher {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);

	private static ZooKeeper zooKeeper;
	private static Stat stat = new Stat();
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException{
		zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new TestGetData());
		countDownLatch.await();
		
		String path = "/test-get-data";
		zooKeeper.create(path, "123".getBytes(), 
				ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		//同步读取节点内容：123
		System.out.println("同步读取节点内容：" + new String(zooKeeper.getData(path, true, stat)));
		//同步读取stat：czxid = 2949541;mzxid = 2949541;version = 0
		System.out.println("同步读取stat：czxid = " + stat.getCzxid() + 
				";mzxid = " + stat.getMzxid() + ";version = " + stat.getVersion());
		
		//异步读取节点内容
		zooKeeper.getData(path, true, new MyDataCallback(), null);
		
		zooKeeper.setData(path, "123".getBytes(), -1);
		Thread.sleep(10000);
	}

	@Override
	public void process(WatchedEvent event) {
		if (Event.KeeperState.SyncConnected == event.getState()) {
			if (Event.EventType.None == event.getType() && null == event.getPath()) {// 连接时的监听事件
				countDownLatch.countDown();
			} else if (Event.EventType.NodeDataChanged == event.getType()) {// 子结点内容变更时的监听
				try {
					//监听获取通知内容：data = 123
					System.out.println("监听获取通知内容：data = " + new String(zooKeeper.getData(event.getPath(), true, stat)));
					//监听获取通知stat：czxid = 2949541;mzxid = 2949542;version = 1
					System.out.println("监听获取通知stat：czxid = " + stat.getCzxid() + ";mzxid = " + stat.getMzxid() + 
							";version = " + stat.getVersion());
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

class MyDataCallback implements DataCallback{

	//异步结果：rc = 0;path = /test-get-data;data = 123;mzxid = 2949545;czxid = 2949545;version = 0
	@Override
	public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
		System.out.println("异步结果：rc = " + rc + ";path = " + path + ";data = " + 
						new String(data) + ";mzxid = " + stat.getMzxid() + ";czxid = " + stat.getCzxid() + 
						";version = " + stat.getVersion());
	}
	
}