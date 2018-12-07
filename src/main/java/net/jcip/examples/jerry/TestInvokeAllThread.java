package net.jcip.examples.jerry;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.curator.shaded.com.google.common.collect.Lists;

public class TestInvokeAllThread {

	static ExecutorService mExecutor = Executors.newFixedThreadPool(5);
	
	/**
	 * 计算价格的任务(实现Callable)
	 * 
	 * @author jerry
	 *
	 */
	private class QuoteTask implements Callable<BigDecimal>{

		private final double price;
		private final int num;
		
		public QuoteTask(double price, int num){
			this.price = price;
			this.num = num;
		}
		
		@Override
		public BigDecimal call() throws Exception {
			Random r = new Random();
			long time = (r.nextInt(10) + 1) * 1000;
			Thread.sleep(time);
			BigDecimal d = BigDecimal.valueOf(price * num).setScale(2);
			System.out.println("耗时" + time/1000 + "s,单价是:" + price + ",人数是:"
					+ num + ",总额是:" + d);
			return d;
		}
	}
	
	/**
	 * 在预定时间内请求获得旅游报价信息
	 * @throws InterruptedException 
	 */
	public void getRankedTravelQuotes() throws InterruptedException{
		List<QuoteTask>  tasks = Lists.newArrayList();
		//模拟10个计算旅游报价的任务
		for(int i = 1; i <= 10; i++){
			tasks.add(new QuoteTask(200, i));
		}
		
		/**
		 * 使用invokeAll批量提交限时任务
		 * 预期15s所有任务都执行完毕，
		 * 没有完成的任务都会自动取消
		 * 
		 */
		List<Future<BigDecimal>> futures = mExecutor.invokeAll(tasks, 15, TimeUnit.SECONDS);
		//报价合计集合
		List<BigDecimal> totalPriceList = Lists.newArrayList();
		Iterator<QuoteTask> taskIter = tasks.iterator();
		for(Future<BigDecimal> future : futures){
			QuoteTask task = taskIter.next();
			try{
				totalPriceList.add(future.get());
			}catch(ExecutionException e){
				//返回计算失败的原因
				totalPriceList.add(BigDecimal.valueOf(-1));
				System.out.println("任务执行异常，单价是:" + task.price + ",人数是:" + task.num);
			}catch(CancellationException e){
				totalPriceList.add(BigDecimal.ZERO);
				System.out.println("任务超时，取消计算，单价是:" + task.price + ",人数是:" + task.num);
			}
		}
		for(BigDecimal bigDecimal : totalPriceList){
			System.out.println(bigDecimal);
		}
		mExecutor.shutdown();
	}
	
	public static void main(String[] args){
		TestInvokeAllThread it = new TestInvokeAllThread();
		try {
			it.getRankedTravelQuotes();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
