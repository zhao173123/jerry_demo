package zk.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * PathChildrenCache使用PathChildrenCacheListener来处理监听事件
 * 
 * PathChildrenCache不会对二级子节点进行监听，只会对子结点进行监听
 * 
 * @author jerry
 *
 */
public class CuratorPathChildrenCacheTest {

	public static void main(String[] args){
		CuratorFramework client = getClient();
		String parentPath = "/p1";
		
		PathChildrenCache pathChildrenCache = new PathChildrenCache(client, parentPath, true);
		try {
			pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
			pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener(){

				@Override
				public void childEvent(CuratorFramework framework, PathChildrenCacheEvent event) throws Exception {
					System.out.println("事件类型：" + event.getType() + ";操作节点：" + event.getData().getPath());
				}
				
			});
			
			String path = "/p1/a1";
			client.create().withMode(CreateMode.PERSISTENT).forPath(path);
			//在新建和删除子结点时必须睡眠，否则无法进行监听
			Thread.sleep(1000);
			client.delete().forPath(path);
			 
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private static CuratorFramework getClient(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(6000)
                .connectionTimeoutMs(3000)
//                .namespace("demo")
                .build();
        client.start();
        return client;
    }
}
