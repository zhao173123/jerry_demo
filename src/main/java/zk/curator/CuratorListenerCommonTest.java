package zk.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * CuratorListener监听
 * 针对background通知和错误通知
 * 使用此监听后，调用inBackground方法会异步获取监听
 * 而对此节点创建或修改不会触发监听事件
 * 
 * 此方法中2次触发监听，
 * 第一次触发为注册监听事件时触发
 * 第二次触发为getData异步处理返回结果时
 * 而setData()并不会触发
 * @author jerry
 *
 */
public class CuratorListenerCommonTest {

	public static void main(String[] args) {
		CuratorFramework client = getClient();
		String path = "/p1";

		try {
			CuratorListener listener = new CuratorListener() {
				@Override
				public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
					System.out.println("监听事件触发：" + event);
				}
			};
			client.getCuratorListenable().addListener(listener);
			// 异步获取节点数据
			client.getData().inBackground().forPath(path);
			// 变更节点内容
			client.setData().forPath(path, "123".getBytes());
			
			Thread.sleep(Integer.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static CuratorFramework getClient() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
				.retryPolicy(retryPolicy).sessionTimeoutMs(6000).connectionTimeoutMs(3000).build();
		client.start();
		return client;

	}
}
