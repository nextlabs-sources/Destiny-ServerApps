/**
 * 
 */
package com.nextlabs.destiny.console.utils.logfilehandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File handler for policy evaluation
 * 
 * @author Amila Silva
 * @author kyu
 * @since 8.0.8
 *
 */
public class FileHandlerForPolicyEvaluation extends FileHandler {
    private static final Map<String, List<String>> cache = new ConcurrentHashMap<>();
	private static final String LOG_DELIMITER = "_#_";
	
    public static void main(String[] a) throws IOException {
		String matchingPolicy = "Matching policies for 13872716762397:\n"
				+ "A: ROOT_64/Allow Users in IT to View All Support Tickets\n"
				+ "X: ROOT_66/Deny access to Security Vulnerabilities if not Created By or Assigned To the User";

		String recordsInputs = "Request 13872716762397 input params\n" + "application\n	name: application"
				+ "host\n inet_address: 2130706433" + "environment\n  dest: 908"
				+ "subject\n id: Christina\n username: user\n department: it\n" + "  fromResource\n name: VIEW_TKTS ";

		FileHandlerForPolicyEvaluation e = new FileHandlerForPolicyEvaluation();

		LogRecord record = new LogRecord(Level.INFO, matchingPolicy);
		e.publish(record);

		LogRecord record2 = new LogRecord(Level.INFO, recordsInputs);
		e.publish(record2);

		System.out.println(cache.size());
		System.out.println(String.valueOf(getCache("13872716762397").get(0)));
		System.out.println(String.valueOf(getCache("13872716762397").get(1)));
		String data = String.valueOf(getCache("13872716762397").get(1));
		
	}

	/**
	 * @return the cache
	 */
	public static List<String> getCache(String logId) {

		return cache.get(logId);
	}

	private int cacheCapacity;

    private void cacheLog(String logId, String log) {
		if (cache.size() >= this.cacheCapacity) {
			return;
		}
		long now = System.currentTimeMillis();
		String newRecord = now + LOG_DELIMITER + log;
		
		List<String> logMsgs = cache.get(logId);
		if(logMsgs == null) {
			logMsgs = new ArrayList<>();
			cache.put(logId, logMsgs);
		}
		
		logMsgs.add(newRecord);
	}

	private void init() {
		final int validationLogStoreTimeInSeconds = Integer
				.parseInt(System.getProperty("console.validationlog.storetime", "60"));
		this.cacheCapacity = Integer.parseInt(System.getProperty("console.validationlog.capacity", "1000"));
		Timer cleaningTimer = new Timer();
		cleaningTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();
				
				for (Iterator<Entry<String, List<String>>> iterator = cache.entrySet().iterator(); iterator
						.hasNext();) {
					
					Entry<String, List<String>> entry = iterator.next();
					List<String> logDataList = entry.getValue();
					if(!logDataList.isEmpty()) {
					   String logContent = logDataList.get(0);
					   String timestampStr = logContent.substring(0, logContent.indexOf(LOG_DELIMITER));
					   long timestamp = Long.valueOf(timestampStr);
					   if (now - timestamp > validationLogStoreTimeInSeconds * 1000) {
							iterator.remove();
						}
					}
				}
			}
		}, validationLogStoreTimeInSeconds * 1000L, validationLogStoreTimeInSeconds * 1000L);
	}

    public FileHandlerForPolicyEvaluation() throws IOException {
		super();
		this.init();
	}

    public FileHandlerForPolicyEvaluation(String pattern, boolean append) throws IOException {
		super(pattern, append);
		this.init();
	}

	public FileHandlerForPolicyEvaluation(String pattern, int limit, int count, boolean append)
            throws IOException {
		super(pattern, limit, count, append);
		this.init();
	}

    public FileHandlerForPolicyEvaluation(String pattern, int limit, int count) throws IOException {
		super(pattern, limit, count);
		this.init();
	}

    public FileHandlerForPolicyEvaluation(String pattern) throws IOException {
		super(pattern);
		this.init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.FileHandler#publish(java.util.logging.LogRecord)
	 */
	public synchronized void publish(LogRecord record) {
		String msg = record.getMessage();
		if (record.getParameters() != null && record.getParameters().length != 0) {
			msg = String.format(msg, record.getParameters());
		}

		Matcher logIdMatcher1 = Pattern.compile("^.*Request\\D*(\\d+).*$", Pattern.MULTILINE).matcher(msg);
		Matcher logIdMatcher2 = Pattern.compile("^.*Matching policies for\\D*(\\d+).*$", Pattern.MULTILINE)
				.matcher(msg);
		String logId = null;
		if (logIdMatcher1.find()) {
			logId = logIdMatcher1.group(1);
		} else if (logIdMatcher2.find()) {
			logId = logIdMatcher2.group(1);
		}

		if (logId != null) {
            cacheLog(logId, msg);

		}
		super.publish(record);
	}

	public byte[] toBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / 8);
		buffer.putLong(x);
		return buffer.array();
	}

	public byte[] toBytes(int x) {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / 8);
		buffer.putInt(x);
		return buffer.array();
	}

	public long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / 8);
		buffer.put(bytes);
		buffer.flip();// need flip
		return buffer.getLong();
	}
}
