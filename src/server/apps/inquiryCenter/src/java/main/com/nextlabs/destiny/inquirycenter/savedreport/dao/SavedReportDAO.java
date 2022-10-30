/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.savedreport.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.SavedReportDO;


import net.sf.hibernate.HibernateException;

/**
 * 
 * <p>
 *  SavedReportDAO to access saved reports related data.
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public interface SavedReportDAO {
	public static final Log LOG = LogFactory.getLog(SavedReportDAO.class);
	
	/**
	 * Create a new SavedReport
	 * @param savedReport
	 * @throws HibernateException
	 */
	public void create(SavedReportDO savedReport) throws HibernateException;
	
	/**
	 * Update SavedReport
	 * 
	 * @param savedReport
	 * @throws HibernateException
	 */
	public void update(SavedReportDO savedReport) throws HibernateException;
	
	/**
	 * Delete SavedReport
	 * @param savedReport
	 * @throws HibernateException
	 */
	public void delete(SavedReportDO savedReport) throws HibernateException;
	
	/**
	 * Lookup SavedReport by its ID
	 * @param savedReportId
	 * @return
	 * @throws HibernateException
	 */
	public SavedReportDO lookup(Long savedReportId) throws HibernateException;
	
	/**
	 * Lookup SavedReport by its report name
	 * @param savedReportName
	 * @return {@link SavedReportDO}
	 * @throws HibernateException
	 */
	public List<SavedReportDO> lookupByReportName(String savedReportName) throws HibernateException;
	
	/**
	 * Get All SavedReports
	 * @return
	 * @throws HibernateException
	 */
	public List<SavedReportDO> getAll() throws HibernateException;
	
	/**
	 * This method should return all the SavedReports that are available
	 * to the currently logged in user. (where user is owner or 
	 * the report is marked as shared)
	 * @param ownerId
	 * @param isNeedSharedReports true results will contain the reports flagged for share otherwise user reports only 
	 * @return
	 * @throws HibernateException 
	 */
	public List<SavedReportDO> getSavedReportsForUser(Long ownerId, boolean isNeedSharedReports) throws HibernateException;
}
