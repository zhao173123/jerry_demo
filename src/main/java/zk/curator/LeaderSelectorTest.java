package zk.curator;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.curator.utils.CloseableUtils;

/**
 * 本列创建10个LeaderSelector并对其添加监听，
 * 当被选为leader后，调用takeLeadership()进行业务逻辑处理，处理完成释放领导权；
 * 其中autoRequeue()方法确保此实例在释放领导权后还能获得领导权；
 * 
 * @author jerry
 *
 */
public class LeaderSelectorTest {

	private static String PATH = "/demo/leader";

	public static void main(String[] args) throws InterruptedException {
		List<LeaderSelector> selectors = Lists.newArrayList();
		List<CuratorFramework> clients = Lists.newArrayList();

		try {
			for (int i = 0; i < 10; i++) {
				CuratorFramework client = getClient();
				clients.add(client);

				final String name = "client#" + i;
				LeaderSelector leaderSelector = new LeaderSelector(client, PATH, new LeaderSelectorListener() {

					@Override
					public void stateChanged(CuratorFramework client, ConnectionState newState) {
						System.out.println(name + ": I am leader!");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void takeLeadership(CuratorFramework client) throws Exception {

					}

				});

				leaderSelector.autoRequeue();
				leaderSelector.start();
				selectors.add(leaderSelector);
			}
			Thread.sleep(Integer.MAX_VALUE);
		} finally {
			for (CuratorFramework client : clients) {
				CloseableUtils.closeQuietly(client);
			}

			for (LeaderSelector selector : selectors) {
				CloseableUtils.closeQuietly(selector);
			}
		}
	}

	private static CuratorFramework getClient() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
				.retryPolicy(retryPolicy).sessionTimeoutMs(6000).connectionTimeoutMs(3000).namespace("demo").build();
		client.start();
		return client;
	}

}
