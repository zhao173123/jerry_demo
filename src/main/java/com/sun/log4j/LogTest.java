package com.sun.log4j;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.Test;

public class LogTest {

	private Logger logger;
	
	@Before
	public void initialize(){
		logger = Logger.getLogger("logger");
		logger.getAllAppenders();
		Logger.getRootLogger().getAllAppenders();
	}
	
	@Test
	public void basicLogger(){
		BasicConfigurator.configure();
		logger.info("basicLogger");
	}
	
	@Test
	public void addAppenderWithStream(){
//		logger.getAppender(new ConsoleAppender(
//				new PatternLayout("%p %t %m%n"), ConsoleAppender.SYSTEM_OUT));
		logger.info("addApenderWithStream");//INFO main addApenderWithStream
	}
	
	@Test
	public void addAppenderWithoutStream(){
//		logger.getAppender(new ConsoleAppender(
//				new PatternLayout("%p %t %m%n")));
		logger.info("addAppenderWithoutStream");//INFO main addApenderWithStream
	}
}
