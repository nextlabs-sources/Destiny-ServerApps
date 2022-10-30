package com.nextlabs.destiny.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditLogger {
	
    private static final Logger log = LoggerFactory.getLogger(AuditLogger.class);

    private AuditLogger() {
    	super();
	}

	public static void log(String message) {
		log.info(message);
	}
	
	public static void log(String message, Object... arguments) {
		log.info(message, arguments);
	}
}
