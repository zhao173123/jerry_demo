package zk.curator;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.curator.utils.CloseableUtils;

/**
 * 本列启动了10个client，随机选中一个作为leader
 * 通过注册监听的方式来判断自己是否成为leader
 * 调用close()释放当前领导权
 * 
 * @author jerry
 *
 */
public class LeaderLatchTest {

	private static final String PATH = "/demo/leader";

	public static void main(String[] args) {

		List<LeaderLatch> latchList = Lists.newArrayList();
		List<CuratorFramework> clients = Lists.newArrayList();
		try {
			for (int i = 0; i < 10; i++) {
				CuratorFramework client = getClient();
				clients.add(client);

				final LeaderLatch leaderLatch = new LeaderLatch(client, PATH, "client#" + i);
				leaderLatch.addListener(new LeaderLatchListener() {

					@Override
					public void isLeader() {
						System.out.println(leaderLatch.getId() + ":I am leader, I am doing jobs!");
					}

					@Override
					public void notLeader() {
						System.out.println(leaderLatch.getId() + ":I am not leader, I will do nothing!");
					}

				});
				latchList.add(leaderLatch);

				leaderLatch.start();
			}
			Thread.sleep(Integer.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			for(CuratorFramework client : clients){
				CloseableUtils.closeQuietly(client);
			}
			
			for(LeaderLatch leaderLatch : latchList){
				CloseableUtils.closeQuietly(leaderLatch);
			}
		}

	}

	private static CuratorFramework getClient() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory
									.builder()
									.connectString("127.0.0.1:2181")
									.retryPolicy(retryPolicy)
									.sessionTimeoutMs(6000)
									.connectionTimeoutMs(3000)
									.namespace("demo")
									.build();
		client.start();
		return client;
	}
}
