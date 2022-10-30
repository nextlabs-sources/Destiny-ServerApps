/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

/**
 * @author nnallagatla
 *
 */
public interface IMonitorExecutor {
	/**
	 * Evaluate monitors
	 */
	public void runMonitors();
	
	/**
	 * shutdown the executor
	 */
	public void shutdown();
}
