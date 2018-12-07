package zk;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class TestGetChildrenMethod implements Watcher{

	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	
	private static ZooKeeper zooKeeper;
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException{
		zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new TestGetChildrenMethod());
		countDownLatch.await();
		
		//创建父节点
		zooKeeper.create("/zk-create-demo-getChildrenMethod", "123".getBytes(), 
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		//在父节点下创建新建a1节点
		zooKeeper.create("/zk-create-demo-getChildrenMethod/a1", "456".getBytes(), 
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		//同步获取结果
		List<String> childrenList = zooKeeper.getChildren("/zk-create-demo-getChildrenMethod", true);
		System.out.println("同步获取结果:" + childrenList);//同步获取结果:[a1]
		
		//异步获得结果
		zooKeeper.getChildren("/zk-create-demo-getChildrenMethod", true, new MyChildren2Callback(), null);
		
		// 在父节点下面创建a2节点
		zooKeeper.create("/zk-create-demo-getChildrenMethod/a2", "456".getBytes(), 
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		Thread.sleep(1000);
	}
	
	@Override
	public void process(WatchedEvent event) {
		if(Event.KeeperState.SyncConnected == event.getState()){
			// 连接时的监听事件
			if(Event.EventType.None == event.getType() && null == event.getPath()){
				countDownLatch.countDown();
				
			}else if(Event.EventType.NodeChildrenChanged == event.getType()){// 子节点变更时的监听
				try {
					//重新获取Children并注册监听：[a1, a2]
					System.out.println("重新获取Children并注册监听：" + zooKeeper.getChildren(event.getPath(), true));
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

class MyChildren2Callback implements AsyncCallback.Children2Callback{

	//异步获取getChildren的结果，rc = 0;path = /zk-create-demo-getChildrenMethod;ctx = null;
	//children = [a1];stat = 2949526,2949526,1510191702896,1510191702896,0,1,0,0,3,1,2949527
	@Override
	public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
		System.out.println("异步获取getChildren的结果，rc = " + rc + 
				";path = " + path + ";ctx = " + ctx + ";children = " + children + ";stat = "+ stat);
	}
	
}