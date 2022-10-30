/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.monitor.dao;

import java.util.List;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.expression.Expression;

import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.AlertDO;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;

/**
 * @author nnallagatla
 *
 */
public class AlertDAOImpl implements AlertDAO {

	@Override
	public void create(AlertDO alert) throws HibernateException {
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
			s.save(alert);
			t.commit();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
	}

	@Override
	public List<AlertDO> getAll() throws HibernateException {
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		List<AlertDO> alerts = null;
		// The session should not be closed
		Session s = null;
		try {
			s = dataSource.getSession();
			s = dataSource.getSession();
			Criteria criteria = s.createCriteria(AlertDO.class);
			criteria.add(Expression.not(Expression.eq("deleted", true)));
			alerts = criteria.list();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		return alerts;
	}

	@Override
	public void update(AlertDO alert) throws HibernateException {
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
			s.update(alert);
			t.commit();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
	}
	
	@Override
	public AlertDO lookup(Long alertId) throws HibernateException {
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		AlertDO alert = null;
		// The session should not be closed
		Session s = null;
		try {
			s = dataSource.getSession();
			alert = (AlertDO) s.get(AlertDO.class, alertId);
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		return alert;
	}

	@Override
	public List<AlertDO> getActiveAlerts() throws HibernateException {
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository dataSource = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

		List<AlertDO> alerts = null;
		Session s = null;
		try {
			s = dataSource.getSession();
			Criteria criteria = s.createCriteria(AlertDO.class);
			criteria.add(Expression.not(Expression.or(Expression.eq("hidden", true) , Expression.eq("deleted", true))));
			LOG.info(criteria);
			alerts = criteria.list();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		return alerts;
	}

}
