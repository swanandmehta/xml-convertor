/**
 * 
 */
package com.liberty.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author swanandm
 *
 */
public class Application {
	
	public static void main(String[] args) {
		Logger logger = LogManager.getLogger(Application.class);
		logger.trace("trace");
		logger.info("info");
		logger.debug("debug");
		logger.warn("warn");
		logger.error("error");
		logger.fatal("fatal");
	}

}
