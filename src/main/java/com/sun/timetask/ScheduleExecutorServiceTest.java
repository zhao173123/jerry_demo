package com.sun.timetask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/** 
 * ScheduledExecutorService是从Java SE5的java.util.concurrent里，做为并发工具类被引进的，这是最理想的定时任务实现方式。  
 *  
 * 1>相比于Timer的单线程，它是通过线程池的方式来执行任务的  
 * 2>可以很灵活的去设定第一次执行任务delay时间 
 * 3>提供了良好的约定，以便设定执行的时间间隔 
 *  
 * 下面是实现代码，我们通过ScheduledExecutorService#scheduleAtFixedRate展示这个例子，通过代码里参数的控制，首次执行加了delay时间。 
 *  
 */ 
public class ScheduleExecutorServiceTest {

	public static void main(String[] args){
		Runnable task = new Runnable(){

			@Override
			public void run() {
				System.out.println("say, hello!");
			}
			
		};
		
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		//第二个参数(10s)为首次执行的延时时间，第三个参数(5s)为定时执行的间隔时间
		service.scheduleAtFixedRate(task, 10, 5, TimeUnit.SECONDS);
		//callable - 要执行的功能;delay - 从现在开始延迟执行的时间;unit - 延迟参数的时间单位
		//可用于提取结果或取消的 ScheduledFuture
//		ScheduledFuture<?> future = service.schedule(task, 1*1000, TimeUnit.MILLISECONDS);
//		try {
//			future.get();
//		} catch (InterruptedException | ExecutionException e) {
//			e.printStackTrace();
//		}
	}
}
