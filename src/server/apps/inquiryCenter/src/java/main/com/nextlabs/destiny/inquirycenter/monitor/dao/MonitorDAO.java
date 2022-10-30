/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.monitor.dao;

import java.util.List;

import net.sf.hibernate.HibernateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.MonitorDO;

/**
 * @author nnallagatla
 *
 */
public interface MonitorDAO {
	
	public static final Log LOG = LogFactory.getLog(MonitorDAO.class.getName());
	
	/**
	 * 
	 * @param monitor
	 */
	public void create(MonitorDO monitor) throws HibernateException;
	
	/**
	 * 
	 * @param monitor
	 * @return
	 */
	public void update(MonitorDO monitor) throws HibernateException;

	/**
	 * 
	 * @param monitor
	 * @return
	 */
	public void directUpdate(MonitorDO monitor) throws HibernateException;
	
	
	/**
	 * 
	 * @return
	 */
	public List<MonitorDO> getAll() throws HibernateException;
	
	/**
	 * 
	 * @return
	 * @throws HibernateException
	 */
	public List<MonitorDO> getRunnableMonitors() throws HibernateException;
	
	/**
	 * 
	 * @return
	 */
	public List<MonitorDO> getActiveMonitors() throws HibernateException;
	
	/**
	 * @return
	 */
	List<MonitorDO> getNonArchivedMonitors() throws HibernateException;
	
	/**
	 * 
	 * @param monitor
	 */
	public void delete(MonitorDO monitor) throws HibernateException;
	
	/**
	 * lookup by monitorId
	 * @param monitorId
	 * @return
	 */
	public MonitorDO lookup(Long monitorId) throws HibernateException;

	/**
	 * accepts * as wildcard
	 * @param name
	 * @return
	 * @throws HibernateException
	 */
	List<MonitorDO> lookupByMonitorName(String name) throws HibernateException;
}
