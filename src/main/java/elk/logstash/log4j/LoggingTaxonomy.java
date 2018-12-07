package elk.logstash.log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LoggingTaxonomy {

	private static final Logger log = LoggerFactory.getLogger(LoggingTaxonomy.class);

    static public void main(String[] args) {
        MDC.put("environment", System.getenv("APP_ENV"));
        // run the app
        log.debug("the app is running!");
    }
}
