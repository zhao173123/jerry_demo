package zk;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class TestCreateNode implements Watcher {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);

	@Override
	public void process(WatchedEvent event) {
		if (Event.KeeperState.SyncConnected == event.getState()) {
			countDownLatch.countDown();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new TestCreateNode());
		countDownLatch.await();

		/**
		 * 参数说明：
		 * path:创建节点的路径，比如/zk-create-demo..
		 * data[]:字节数组，创建节点初始化内容。使用者需自己进行序列化和反序列化；
		 * 		  复杂对象可以使用Hessian或Kryo进行序列／反序列化
		 * acl:节点的acl策略
		 * createMode:节点类型，类型定义在CreateMode中
		 * 			（1）PERSISTENT,持久化
		 * 			（2）PERSISTENT_SEQUENTIAL，持久顺序
		 * 			（3）EPHEMERAL，临时
		 * 			（4）EPHEMERAL_SEQUENTIAL，临时顺序
		 * cb:异步创建方法参数，注册的回调函数，需要实现StringCallback接口
		 * 
		 */
		
		// 同步创建临时节点
		String ephemeralPath = zooKeeper.create("/zk-create-demo-ephemeral-", "123".getBytes(),
				ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.out.println("同步创建临时节点成功：" + ephemeralPath);

		// 同步创建临时顺序节点
		String sequentialPath = zooKeeper.create("/zk-create-demo-sequential-", "456".getBytes(),
				ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("同步创建临时顺序节点成功：" + sequentialPath);

		// 异步创建临时节点
		zooKeeper.create("/zk-create-demo-async-ephemeral-", "abc".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL, new MyStringCallBack(), "我是传递内容！");

		// 异步创建临时顺序节点
		zooKeeper.create("/zl-create-demo-async-sequential-", "def".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL_SEQUENTIAL, new MyStringCallBack(), "我是传递内容！");

		Thread.sleep(1000);
	}

}

class MyStringCallBack implements StringCallback {

	//ctx:用户传递信息
	@Override
	public void processResult(int rc, String path, Object ctx, String name) {
		System.out.println("异步创建回调过程：状态" + rc + "，创建路径:" + path + ";传递消息：" + ctx + ";实际节点名称" + name);
	}

}