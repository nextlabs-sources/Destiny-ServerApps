/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.savedreport.dao;

import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.expression.Expression;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.DBUtil;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.SavedReportDO;

/**
 * <p>
 *  Implementation of the SavedReportDAO
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class SavedReportDAOImpl implements SavedReportDAO {

	/* (non-Javadoc)
	 * @see com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAO#create(com.nextlabs.destiny.inquirycenter.savedreport.dto.SavedReportDO)
	 */
	@Override
	public void create(SavedReportDO savedReport) throws HibernateException {
		Session s = null;
		Transaction t = null;
		try {
			IHibernateRepository dataSource = 
					DBUtil.getActivityDataSource();
			s = dataSource.getSession();
			t = s.beginTransaction();
			s.save(savedReport);
			t.commit();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
	}

	/* (non-Javadoc)
	 * @see com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAO#update(com.nextlabs.destiny.inquirycenter.savedreport.dto.SavedReportDO)
	 */
	@Override
	public void update(SavedReportDO savedReport) throws HibernateException {
		Session s = null;
		Transaction t = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			s = dataSource.getSession();
			t = s.beginTransaction();
			s.update(savedReport);
			t.commit();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
	}

	/* (non-Javadoc)
	 * @see com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAO#delete(com.nextlabs.destiny.inquirycenter.savedreport.dto.SavedReportDO)
	 */
	@Override
	public void delete(SavedReportDO savedReport) throws HibernateException {
		Session s = null;
		Transaction t = null;
		
		if (savedReport == null || savedReport.getId() == null) {
			return;
		}

		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			s = dataSource.getSession();
			t = s.beginTransaction();
			s.delete(savedReport);
			t.commit();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
	}

	/* (non-Javadoc)
	 * @see com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAO#lookup(java.lang.Long)
	 */
	@Override
	public SavedReportDO lookup(Long savedReportId) throws HibernateException {
		SavedReportDO report = null;
		Session s = null;
		try {
			IHibernateRepository dataSource = 
					DBUtil.getActivityDataSource();
			s = dataSource.getSession();
			report = (SavedReportDO) s.get(SavedReportDO.class, savedReportId);
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		return report;
	}

	@Override
	public List<SavedReportDO> lookupByReportName(String savedReportName)
			throws HibernateException {
		List<SavedReportDO> report = new ArrayList<SavedReportDO>();
		Session s = null;
		try {
			IHibernateRepository dataSource = 
					DBUtil.getActivityDataSource();
			s = dataSource.getSession();
			Criteria criteria = s.createCriteria(SavedReportDO.class);
			
			if(savedReportName.contains("*")) {
				criteria.add(Expression.ilike("title", savedReportName.replace("*", "") + "%"));
			} else {
				criteria.add(Expression.eq("title", savedReportName));
			}

			report = criteria.list();
			
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		return report;
	}

	/* (non-Javadoc)
	 * @see com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAO#getAll()
	 */
	@Override
	public List<SavedReportDO> getAll() throws HibernateException {
		List<SavedReportDO> savedReports = null;
		Session s = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			s = dataSource.getSession();
			Criteria criteria = s.createCriteria(SavedReportDO.class);
			criteria.add(Expression.not(Expression.eq("deleted", true)));
			savedReports = criteria.list();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		return savedReports;
	}

	/* (non-Javadoc)
	 * @see com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAO#getSavedReportsForUser(int)
	 */
	@Override
	public List<SavedReportDO> getSavedReportsForUser(Long ownerId, boolean isNeedSharedReports) throws HibernateException {
		List<SavedReportDO> savedReports = null;
		Session s = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			s = dataSource.getSession();
			Criteria criteria = s.createCriteria(SavedReportDO.class);
			
			if(isNeedSharedReports) {
				 criteria.add(Expression.or(Expression.eq("ownerId", ownerId), Expression.or(Expression.eq("sharedMode", SavedReportDO.SAVED_REPORT_PUBLIC), Expression.eq("sharedMode", SavedReportDO.SAVED_REPORT_USERS))));	
			} else {
				criteria.add(Expression.eq("ownerId", ownerId));
			}
			
			criteria.add(Expression.not(Expression.eq("deleted", Boolean.TRUE)));
			criteria.add(Expression.not(Expression.eq("inDashboard", Boolean.TRUE)));
			
//			criteria.add(Expression.and(Expression.eq("inDashboard", false),Expression.and(Expression.not(Expression.eq("deleted", true)), 
//					Expression.or(Expression.eq("ownerId", ownerId), 
//							Expression.eq("shared", Boolean.TRUE)))));
			
			savedReports = criteria.list();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		return savedReports;
	}

}
