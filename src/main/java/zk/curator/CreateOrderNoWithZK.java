package zk.curator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * ZK实现分布式锁（Curator）
 * 使用InterProcessMutex类来进行处理分布式锁，实现了一个生产唯一流水号的功能
 * 
 * PS：
 * 1.通过这种方式生成的流水号并不能支撑很大的并发量，
 * 每次操作都需要网络访问、zk的节点操作等，会花费大量的时间
 * 2.由于精确到毫秒，因此每一秒最多处理999个请求
 * 3.即使在分布式环境中下面的示例还是会出现重复现象，比如2个服务器的时间不一致
 * 
 * 综上，通过zk的分布式锁并不是一个好的选择
 * 
 * @author jerry
 *
 */
public class CreateOrderNoWithZK {

	private static final String path = "/lock_path";
	
	public static void main(String[] args){
		CuratorFramework client = getClient();
		final InterProcessMutex lock = new InterProcessMutex(client, path);
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		
		final long startTime = new Date().getTime();
		for(int i = 0; i < 10; i++){
			new Thread(() -> {
				try {
					countDownLatch.await();
					lock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss|SSS");
				System.out.println(sdf.format(new Date()));

				try{
					lock.release();
				}catch(Exception e){
					e.printStackTrace();
				}
				System.out.println("显示此线程大概花费的时间（等待+执行）：" + (new Date().getTime() - startTime) + "ms");
			}).start();
		}
		System.out.println("创建线程花费时间：" + (new Date().getTime() -startTime) + "ms");
		countDownLatch.countDown();
	}
	
	private static CuratorFramework getClient(){
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.builder()
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
