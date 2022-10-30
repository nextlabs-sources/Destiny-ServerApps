/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.monitor.dao;

import java.util.List;

import net.sf.hibernate.HibernateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.AlertDO;

/**
 * @author nnallagatla
 *
 */
public interface AlertDAO {
	
	public static final Log LOG = LogFactory.getLog(AlertDAO.class.getName());
	
	/**
	 * create alert
	 * @param alert
	 * @throws HibernateException 
	 */
	public void create(AlertDO alert) throws HibernateException;
	
	/**
	 * dismiss alert
	 * @param alert
	 * @throws HibernateException 
	 */
	public void update(AlertDO alert) throws HibernateException;
	
	/**
	 * fetch all alerts
	 * @return 
	 * @throws HibernateException 
	 */
	public List<AlertDO> getAll() throws HibernateException;

	/**
	 * fetch alerts that are not dismissed
	 * @return 
	 * @throws HibernateException 
	 */
	public List<AlertDO> getActiveAlerts() throws HibernateException;
	
	/**
	 * 
	 * @param alertId
	 * @return
	 * @throws HibernateException 
	 */
	public AlertDO lookup(Long alertId) throws HibernateException;
}
