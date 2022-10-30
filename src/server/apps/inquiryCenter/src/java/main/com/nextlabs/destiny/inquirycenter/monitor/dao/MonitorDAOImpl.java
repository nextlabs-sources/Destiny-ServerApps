/*
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.monitor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.expression.Expression;

import org.richfaces.json.JSONException;

import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.MonitorDO;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.DBUtil;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.nextlabs.destiny.inquirycenter.monitor.service.MonitoringService;

/**
 * @author nnallagatla
 * 
 */
public class MonitorDAOImpl implements MonitorDAO {

	public MonitorDAOImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextlabs.destiny.inquirycenter.monitor.MonitorDAO#create(com.nextlabs
	 * .destiny.inquirycenter.monitor.MonitorDO)
	 */
	@Override
	public void create(MonitorDO monitor) throws HibernateException {
		LOG.debug("Entering");
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		// The session should not be closed
		Session s = null;
		Transaction t = null;
		try {
			s = dataSource.getSession();
			t = s.beginTransaction();
			s.save(monitor);
			t.commit();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		LOG.debug("Exiting");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextlabs.destiny.inquirycenter.monitor.MonitorDAO#update(com.nextlabs
	 * .destiny.inquirycenter.monitor.MonitorDO)
	 */
	@Override
	public void update(MonitorDO monitor) throws HibernateException {
		LOG.debug("Entering");
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		// The session should not be closed
		Session s = null;
		Transaction t = null;
		try {
			s = dataSource.getSession();
			t = s.beginTransaction();

			Timestamp ts = new Timestamp(System.currentTimeMillis());
			
			MonitorDO dbMonitor = lookup(monitor.getId());
			
			//set this version as archived
			dbMonitor.setArchived(true);
			dbMonitor.setLastUpdatedAt(ts);
			s.update(dbMonitor);
			
			copyUpdates(monitor, dbMonitor);
			
			//set id as null and insert again
			monitor.setId(null);
			s.save(monitor);
			
			t.commit();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		LOG.debug("Exiting");
	}
	
	/**
	 * copy the attributes that can be changed to the object fetched from database
	 * @param monitor
	 * @param dbMonitor
	 */
	private void copyUpdates(MonitorDO monitor, MonitorDO dbMonitor) {
		if (monitor == null || dbMonitor == null) {
			return;
		}
		
		monitor.setCreatedAt(dbMonitor.getCreatedAt());
		monitor.setLastUpdatedAt(dbMonitor.getLastUpdatedAt());
		monitor.setMonitorUID(dbMonitor.getMonitorUID());
		monitor.setActive(dbMonitor.isActive());		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nextlabs.destiny.inquirycenter.monitor.MonitorDAO#getAll()
	 */
	@Override
	public List<MonitorDO> getAll() throws HibernateException {
		LOG.debug("Entering");
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		List<MonitorDO> monitors = null;
		// The session should not be closed
		Session s = null;
		try {
			s = dataSource.getSession();
			Criteria criteria = s.createCriteria(MonitorDO.class);
			criteria.add(Expression.and(Expression.eq("deleted", false), Expression.eq("archived", false)));
			monitors = criteria.list();
			
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		LOG.debug("Exiting");
		return monitors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextlabs.destiny.inquirycenter.monitor.MonitorDAO#getActiveMonitors()
	 */
	@Override
	public List<MonitorDO> getActiveMonitors() throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextlabs.destiny.inquirycenter.monitor.MonitorDAO#delete(com.nextlabs
	 * .destiny.inquirycenter.monitor.MonitorDO)
	 */
	@Override
	public void delete(MonitorDO monitor) throws HibernateException {
		LOG.debug("Entering");
		if (monitor == null) {
			return;
		}
		monitor.setDeleted(true);
		
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		// The session should not be closed
		Session s = null;
		Transaction t = null;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			s = dataSource.getSession();
			t = s.beginTransaction();
			s.update(monitor);
			
			con = s.connection ();
			stmt = con.prepareStatement ("UPDATE ALERT SET is_deleted = ? WHERE monitor_uid = ?");
			stmt.setBoolean(1, true);
			stmt.setString(2, monitor.getMonitorUID());
			stmt.executeUpdate (); 
			stmt.close();
			t.commit();
			
		} catch (SQLException e) {
			LOG.error(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException err) {
					// ignore
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException err) {
					// ignore
				}
			}
			HibernateUtils.closeSession(s, LOG);
		}
		LOG.debug("Exiting");
	}

	@Override
	public MonitorDO lookup(Long monitorId) throws HibernateException {
		LOG.debug("Entering");
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		MonitorDO monitor = null;
		// The session should not be closed
		Session s = null;
		try {
			s = dataSource.getSession();
			monitor = (MonitorDO) s.get(MonitorDO.class, monitorId);
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		LOG.debug("Exiting");
		return monitor;
	}

	@Override
	public List<MonitorDO> lookupByMonitorName(String name)
			throws HibernateException {
		LOG.debug("Entering");
		List<MonitorDO> monitors = new ArrayList<MonitorDO>();
		Session s = null;
		try {
			IHibernateRepository dataSource = 
					DBUtil.getActivityDataSource();
			s = dataSource.getSession();
			Criteria criteria = s.createCriteria(MonitorDO.class);			
			criteria.add(Expression.and(Expression.and(Expression.eq("deleted", false), Expression.eq("archived", false)),Expression.eq("name", name)));						
			monitors = criteria.list();
			
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		LOG.debug("Exiting");
		return monitors;
	}

	
	
	@Override
	public void directUpdate(MonitorDO monitor) throws HibernateException {
		LOG.debug("Entering");
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		// The session should not be closed
		Session s = null;
		Transaction t = null;
		try {
			s = dataSource.getSession();
			t = s.beginTransaction();

			s.update(monitor);			
			
			t.commit();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		LOG.debug("Exiting");
	}

	@Override
	public List<MonitorDO> getRunnableMonitors() throws HibernateException {
		LOG.debug("Entering");
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		List<MonitorDO> monitors = null;
		// The session should not be closed
		Session s = null;
		try {
			s = dataSource.getSession();
			Criteria criteria = s.createCriteria(MonitorDO.class);
			/*
			 * Select * from monitors where active = true and deleted = false and archived = false;
			 */
			criteria.add(Expression.and(Expression.eq("active", true),
					Expression.and(Expression.eq("deleted", false), Expression.eq("archived", false))));
			monitors = criteria.list();
			
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		LOG.debug("Exiting");
		return monitors;		
	}
	
	@Override
	public List<MonitorDO> getNonArchivedMonitors() throws HibernateException {
		LOG.debug("Entering");
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		List<MonitorDO> monitors = null;
		// The session should not be closed
		Session s = null;
		try {
			s = dataSource.getSession();
			Criteria criteria = s.createCriteria(MonitorDO.class);
			/*
			 * Select * from monitors where deleted = false and archived = false;
			 */
			criteria.add(Expression.and(Expression.eq("deleted", false), Expression.eq("archived", false)));
			monitors = criteria.list();
			
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		LOG.debug("Exiting");
		return monitors;		
	}

}
