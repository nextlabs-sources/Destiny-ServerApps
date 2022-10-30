/*
 * Created on Apr 7, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.savedreport.service;

import java.util.List;

import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.SavedReportDO;
import com.nextlabs.destiny.inquirycenter.exception.ReportingException;

/**
 * <p>
 *  SavedReportService
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public interface SavedReportService {
	
	/**
	 * <p>
	 * Create a new Saved Report
	 * </p>
	 * 
	 * @param savedReport
	 * @throws ReportingException
	 */
	public void create(SavedReportDO savedReport) throws ReportingException;
	
	/**
	 * <p>
	 * Update SavedReport
	 * </p>
	 * 
	 * @param savedReport
	 * @throws ReportingException
	 */
	public void update(SavedReportDO savedReport) throws ReportingException;
	
	/**
	 * <p>
	 * Delete SavedReport
	 * </p>
	 * 
	 * @param savedReport
	 * @throws ReportingException
	 */
	public void delete(SavedReportDO savedReport) throws ReportingException;
	
	/**
	 * <p>
	 * Lookup SavedReport by its ID
	 * </p>
	 * 
	 * @param savedReportId
	 * @return
	 * @throws ReportingException
	 */
	public SavedReportDO lookup(Long savedReportId) throws ReportingException;
	
	/**
	 * <p>
	 * Lookup SavedReport by its name
	 * </p>
	 * 
	 * @param savedReportName
	 * @return
	 * @throws ReportingException
	 */
	public List<SavedReportDO> lookupByReportName(String savedReportName) throws ReportingException;
	
	/**
	 * <p>
	 * Get All SavedReports
	 * </p>
	 * 
	 * @return List<SavedReportDO>
	 * @throws ReportingException
	 */
	public List<SavedReportDO> getAll() throws ReportingException;
	
	/**
	 * <p>
	 * This method should return all the SavedReports that are available
	 * to the currently logged in user. (where user is owner or 
	 * the report is marked as shared)
	 * </p>
	 * 
	 * @param ownerId ownerId
	 * @param needSharedReports needSharedReports
	 * @return List<SavedReportDO>
	 * @throws ReportingException 
	 */
	public List<SavedReportDO> getSavedReportsForUser(Long ownerId, boolean needSharedReports) throws ReportingException;

}
