package com.sun.timetask;

import java.util.Timer;
import java.util.TimerTask;

public class TimeTaskTest {
	
	public static void main(String[] args){
		TimerTask task = new TimerTask(){

			@Override
			public void run() {
				//task run here
				System.out.println("say,hello!");
			}
			
		};
		Timer timer = new Timer();
		long delay = 0;
		long intervalPeriod = 1 * 5000;
		// schedules the task to be run in an interval 
//		timer.scheduleAtFixedRate(task, delay, intervalPeriod);
		timer.schedule(task, delay, intervalPeriod);
	}
}
