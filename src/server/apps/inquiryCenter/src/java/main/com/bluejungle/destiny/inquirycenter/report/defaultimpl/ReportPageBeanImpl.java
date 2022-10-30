/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.IReportExecutor;
import com.bluejungle.destiny.interfaces.report.v1.ReportLibraryServiceStub;
import com.bluejungle.destiny.types.basic.v1.SortDirection;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportQuerySpec;
import com.bluejungle.destiny.types.report.v1.ReportSortFieldName;
import com.bluejungle.destiny.types.report.v1.ReportSortSpec;
import com.bluejungle.destiny.types.report.v1.ReportSummaryType;
import com.bluejungle.destiny.types.report.v1.ReportTargetType;
import com.bluejungle.destiny.webui.framework.faces.ILoadable;
import com.bluejungle.destiny.webui.framework.faces.IResetableBean;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the backing page bean implementation for the "My Reports" page.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/
 *          main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/
 *          MyReportPageBeanImpl.java#1 $
 */

public abstract class ReportPageBeanImpl implements ILoadable, IResetableBean {

	private static final Log LOG = LogFactory.getLog(ReportPageBeanImpl.class.getName());
	private static final String REPORT_LIBRARY_SUFFIX = "/services/ReportLibraryService";

	/**
	 * This set contains the various forms of time grouping.
	 */
	private static final Set TIME_GROUPINGS = new HashSet();
	static {
		TIME_GROUPINGS.add(ReportSummaryType.TimeDays);
		TIME_GROUPINGS.add(ReportSummaryType.TimeMonths);
	}

	private String dacLocation;
	private Map idToReportMap = new HashMap();
	private boolean isLoaded = false;
	private ReportLibraryServiceStub libraryClient;
	private IReportExecutor reportExecutor;
	private List reportList = new ArrayList();
	private IReport selectedReport;
	private boolean reportDefinitionSelected = false;
	private boolean reportResultsSelected = false;
	private String redirectPage = "/reports/myReportsExecuteContent.jspf";

	/**
	 * Returns the log object
	 * 
	 * @return the log object
	 */
	protected Log getLog() {
		return LOG;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReportPageBean#getReportList()
	 */
	public List getReportList() {
		return this.reportList;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReportPageBean#getSelectedReport()
	 */
	public IReport getSelectedReport() {
		return this.selectedReport;
	}

	public void setSelectedReport(IReport selectedReport) {
		this.selectedReport = selectedReport;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReportPageBean#setSelectedReportId(java.lang.Long)
	 */
	public void setSelectedReportId(Long id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null.");
		}

		IReport newSelected = (IReport) this.idToReportMap.get(id);
		this.selectedReport = newSelected;
	}

	/**
	 * @see com.bluejungle.destiny.webui.framework.faces.ILoadable#isLoaded()
	 */
	public boolean isLoaded() {
		return this.isLoaded;
	}

	/**
	 * @see com.bluejungle.destiny.webui.framework.faces.ILoadable#load()
	 */
	public void load() {
		if (!isLoaded()) {
			setLoaded(true);
			setReportDefinitionSelected(true);
			setReportResultsSelected(false);
			
			/*
			ReportLibraryIF library = getReportLibraryService();
			ReportList wsReportList = null;
			try {

				// Get my reports
				wsReportList = library.getReports(getReportListQuerySpec());
				Report[] wsReports = wsReportList.getReports();
				if (wsReports != null) {
					int size = wsReports.length;
					for (int index = 0; index < size; index++) {
						Report currentWsReport = wsReports[index];
						IReport currentReport = new ReportImpl(currentWsReport);
						this.reportList.add(currentReport);
						this.idToReportMap.put(currentReport.getId(), currentReport);
					}
				}

				if (this.reportList.isEmpty()) {
					this.selectedReport = null;
				} else {
					this.selectedReport = (IReport) this.reportList.get(0);
				}

				setLoaded(true);
				setReportDefinitionSelected(true);
				setReportResultsSelected(false);
			} catch (AccessDeniedFault e) {
				// Session timeout - logout the user
				// AppContext.getContext().releaseContext();
				// FacesContext.getCurrentInstance().renderResponse();
			} catch (ServiceNotReadyFault e) {
				getLog().error("Report library service is not ready", e);
			} catch (RemoteException e) {
				getLog().error("Report library service threw an exception", e);
			}
           */
		}
	}

	/**
	 * In this implementation, this function initializes the report executor to
	 * run the specified report.
	 * 
	 * @see com.bluejungle.destiny.inquirycenter.report.IReportPageBean#onExecuteReport(javax.faces.event.ActionEvent)
	 */
	public void onExecuteReport(ActionEvent executeEvent) {
		ReportImpl reportToExecute = (ReportImpl) getSelectedReport();
		Report wsReportToExecute = reportToExecute.getWrappedReport();
		ReportSummaryType reportSummaryType = wsReportToExecute.getSummaryType();
		if (wsReportToExecute.getSortSpec() == null) {
			if (TIME_GROUPINGS.contains(reportSummaryType)) {
				ReportSortSpec sortSpec = new ReportSortSpec();
				sortSpec.setDirection(SortDirection.Ascending);
				sortSpec.setField(ReportSortFieldName.Date);
				wsReportToExecute.setSortSpec(sortSpec);
			} else if (!ReportSummaryType.None.equals(reportSummaryType)) {
				// For all other grouping, order by highest count first
				ReportSortSpec sortSpec = new ReportSortSpec();
				sortSpec.setDirection(SortDirection.Descending);
				sortSpec.setField(ReportSortFieldName.Count);
				wsReportToExecute.setSortSpec(sortSpec);
			}
		}

		wsReportToExecute.setTitle(reportToExecute.getTitle());
		wsReportToExecute.setDescription(reportToExecute.getDescription());
		wsReportToExecute.setShared(reportToExecute.isShared());

		// Trim the non necessary data
		if (ReportTargetType.ActivityJournal.equals(wsReportToExecute.getTarget())) {
			wsReportToExecute.setEffects(null);
			wsReportToExecute.setPolicies(null);
		}

		getReportExecutor().setMode(2);
		getReportExecutor().setReportToExecute(wsReportToExecute, reportToExecute.getTitle());

		setReportDefinitionSelected(false);
		setReportResultsSelected(true);
		setRedirectPage("/reports/reportDetailResultsContent.jspf");
	}

	public void onRestoreDefinition(ActionEvent actionEvent) {
		setReportDefinitionSelected(true);
		setReportResultsSelected(false);
		setRedirectPage("/reports/myReportsExecuteContent.jspf");
	}

	/**
	 * @see com.bluejungle.destiny.webui.framework.faces.ILoadable#reset()
	 */
	public void reset() {
		this.reportList.clear();
		this.idToReportMap.clear();
		this.selectedReport = null;
		this.redirectPage = "/reports/myReportsExecuteContent.jspf";
		setLoaded(false);
	}

	/**
	 * Sets the location of the data provider
	 * 
	 * @param location
	 *            location of the data provider
	 */
	public void setDataLocation(String location) {
		this.dacLocation = location;
	}

	/**
	 * Sets the report execution bean to use when executing a report
	 * 
	 * @param newReportExecutor
	 *            execution bean to use
	 */
	public void setReportExecutor(IReportExecutor newReportExecutor) {
		if (newReportExecutor == null) {
			throw new NullPointerException("newReportExecutor cannot be null.");
		}

		this.reportExecutor = newReportExecutor;
	}

	/**
	 * Retrieve the query spec to be used when building the report list to be
	 * displayed
	 * 
	 * @return the query spec to be used when building the report list to be
	 *         displayed
	 */
	protected abstract ReportQuerySpec getReportListQuerySpec();

	/**
	 * @see com.bluejungle.destiny.webui.framework.faces.ILoadable#reset()
	 */
	protected void resetAndSelectReport(Long reportToSelect) {
		this.reset();
		this.load();

		// try to select the previous report. However, new report doesn't have a
		// previous report
		if (reportToSelect != null) {
			this.setSelectedReportId(reportToSelect);
		}
		setReportDefinitionSelected(false);
		setReportResultsSelected(true);
	}

	/**
	 * Returns the report library service client
	 * 
	 * @return the report library service client
	 */
	protected ReportLibraryServiceStub getReportLibraryService() throws AxisFault {
		if (this.libraryClient == null) {
			final String libraryServiceLocation = this.dacLocation + REPORT_LIBRARY_SUFFIX;
			this.libraryClient = new ReportLibraryServiceStub(libraryServiceLocation);
		}
		return this.libraryClient;
	}

	/**
	 * Sets the loaded state
	 * 
	 * @param loaded
	 *            new loaded state
	 */
	private void setLoaded(boolean loaded) {
		this.isLoaded = loaded;
	}

	/**
	 * Remove the selected report from the report list
	 */
	protected void removeSelectedReportFromReportList() {
		// Deletes the report from the local report list and selects the
		// next (or previous) item in the list
		IReport reportToRemove = this.getSelectedReport();
		int currentIndex = this.reportList.lastIndexOf(reportToRemove);
		if (currentIndex > -1) {
			if (currentIndex < (this.reportList.size() - 1)) {
				// This is not the last selected item, select the next one
				IReport newSelectedReport = (IReport) this.reportList.get(currentIndex + 1);
				setSelectedReportId(newSelectedReport.getId());
			} else if (currentIndex >= 1) {
				// The selected item is the last in the list, so select the
				// previous one
				IReport newSelectedReport = (IReport) this.reportList.get(currentIndex - 1);
				setSelectedReportId(newSelectedReport.getId());
			} else {
				this.selectedReport = null;
			}

			this.reportList.remove(currentIndex);
			this.idToReportMap.remove(((IReport) reportToRemove).getId());
		} else {
			// Should never happen
			getLog().debug(
					"Failed to find next report to display after report removal.  Setting to first one in list or null if none exist.");
			if (this.reportList.size() > 0) {
				IReport newSelectedReport = (IReport) this.reportList.get(0);
				setSelectedReportId(newSelectedReport.getId());
			} else {
				this.selectedReport = null;
			}
		}
	}

	/**
	 * Returns the report executor
	 * 
	 * @return the report executor
	 */
	protected IReportExecutor getReportExecutor() {
		return this.reportExecutor;
	}

	/**
	 * Returns the reportDefinitionSelected.
	 * 
	 * @return the reportDefinitionSelected.
	 */
	public boolean isReportDefinitionSelected() {
		return this.reportDefinitionSelected;
	}

	/**
	 * Sets the reportDefinitionSelected
	 * 
	 * @param reportDefinitionSelected
	 *            The reportDefinitionSelected to set.
	 */
	public void setReportDefinitionSelected(boolean reportDefinitionSelected) {
		this.reportDefinitionSelected = reportDefinitionSelected;
	}

	/**
	 * Returns the reportResultsSelected.
	 * 
	 * @return the reportResultsSelected.
	 */
	public boolean isReportResultsSelected() {
		return this.reportResultsSelected;
	}

	/**
	 * Sets the reportResultsSelected
	 * 
	 * @param reportResultsSelected
	 *            The reportResultsSelected to set.
	 */
	public void setReportResultsSelected(boolean reportResultsSelected) {
		this.reportResultsSelected = reportResultsSelected;
	}

	/**
	 * Returns the redirectPage.
	 * 
	 * @return the redirectPage.
	 */
	public String getRedirectPage() {
		return this.redirectPage;
	}

	/**
	 * Sets the redirectPage
	 * 
	 * @param redirectPage
	 *            The redirectPage to set.
	 */
	public void setRedirectPage(String redirectPage) {
		this.redirectPage = redirectPage;
	}

}