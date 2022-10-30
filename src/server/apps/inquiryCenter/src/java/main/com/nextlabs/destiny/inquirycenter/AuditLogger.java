package com.nextlabs.destiny.inquirycenter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuditLogger {
	
	private static final Log log = LogFactory.getLog(AuditLogger.class); 
	
	public static void log(Object message) {
		log.info(message);
	}
}
