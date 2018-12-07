package zk.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * 1.首先会对节点/p1注册一个Watcher监听事件，同时返回当前节点的信息1⃣️
 * 2.随后改变节点的内容“new content”，此时触发监听事件，并打印出监听事件信息2⃣️
 * 3.但是节点信息再改变时，监听器失效，无法再次获取节点变动事件
 * 
 * PS：与zk的原生api类似，只能监听一次
 * 
 * @author jerry
 *
 */
public class CuratorListenerOnceTest {

	public static void main(String[] args){
		CuratorFramework client = getClient();
		String path = "/p1";
		
		try {
			byte[] content = client.getData().usingWatcher(new Watcher(){
				//2⃣️.监听器watchedEvent：WatchedEvent state:SyncConnected type:NodeDataChanged path:/p1
				//只有节点信息内容变更时才触发
				@Override
				public void process(WatchedEvent event) {
					System.out.println("监听器watchedEvent：" + event);
				}
			}).forPath(path);
			//1⃣️.监听节点内容：curator-demo
			System.out.println("监听节点内容：" + new String(content));
			
			//第一次变更节点数据
			client.setData().forPath(path, "new content".getBytes());
			//第二次变更节点数据
			client.setData().forPath(path, "second content".getBytes());
			
			Thread.sleep(Integer.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static CuratorFramework getClient(){
		RetryPolicy retryPolice = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.builder()
									.connectString("127.0.0.1:2181")
									.retryPolicy(retryPolice)
									.sessionTimeoutMs(6000)
									.connectionTimeoutMs(3000)
//									.namespace("demo")
									.build();
		client.start();
		return client;
									
	}
}
