package elk.logstash.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

/**
 * log4j-logstash
 * 
 * @author jerry
 *
 */
public class Log4jTest {
		
//	public static Logger logger = Logger.getLogger(Log4jTest.class);
	
	public static Logger logger = Logger.getLogger("logstash");
	
	@Before
	public void setUp(){
//		PropertyConfigurator.configure(Log4jTest.class.getClassLoader().getResourceAsStream("log4j.properties"));
	}
	
	@Test
	public void testLog(){
		logger.debug("hello logstash, this is a message from log4j.");
	}
	
	@Test
	public void testException(){
		logger.error("error", new Exception("sorry, error!"));
	}
	
	public static void main(String[] args){
		logger.debug("This is a debug message");
		logger.info("This is a info message");
		logger.warn("This is a warn message");
		logger.error("This is a error message");
		
		try{
			System.out.println(5/0);
		}catch(Exception e){
			logger.error(e);
		}
	}
}
