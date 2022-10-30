/*
 * Created on Jun 27, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import com.bluejungle.destiny.inquirycenter.report.IMyReportsPageBean;
import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.ReportViewException;
import com.bluejungle.destiny.interfaces.report.v1.AccessDeniedFault;
import com.bluejungle.destiny.interfaces.report.v1.PersistenceFault;
import com.bluejungle.destiny.interfaces.report.v1.ReportLibraryServiceStub;
import com.bluejungle.destiny.interfaces.report.v1.ServiceNotReadyFault;
import com.bluejungle.destiny.interfaces.report.v1.UniqueConstraintViolationFault;
import com.bluejungle.destiny.interfaces.report.v1.UnknownEntryFault;
import com.bluejungle.destiny.types.basic.v1.Id;
import com.bluejungle.destiny.types.basic.v1.SortDirection;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportList;
import com.bluejungle.destiny.types.report.v1.ReportQuerySpec;
import com.bluejungle.destiny.types.report.v1.ReportSortFieldName;
import com.bluejungle.destiny.types.report.v1.ReportSortTerm;
import com.bluejungle.destiny.types.report.v1.ReportSortTermList;
import com.bluejungle.destiny.types.report.v1.ReportVisibilityType;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.framework.utils.ArrayUtils;
import org.apache.axis2.AxisFault;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.inquirycenter.report.IMyReportsPageBean) interfaces
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/MyReportsPageBeanImpl.java#3 $
 */
public class MyReportsPageBeanImpl extends ReportPageBeanImpl implements
		IMyReportsPageBean {

	private static ReportQuerySpec MY_REPORTS_QUERY_SPEC;
	static {
		ReportQuerySpec myReportQS = new ReportQuerySpec();

		myReportQS.setVisibility(ReportVisibilityType.All);

		ReportSortTermList sortList = new ReportSortTermList();
		ReportSortTerm nameSort = new ReportSortTerm();
		nameSort.setFieldName(ReportSortFieldName.Title);
		nameSort.setDirection(SortDirection.Ascending);
		ReportSortTerm[] sorts = { nameSort };
		sortList.setTerms(sorts);
		myReportQS.setSortSpec(sortList);

		MY_REPORTS_QUERY_SPEC = myReportQS;
	}

	private IReport selectedReport = null;
	private boolean isLoaded = false;

	/**
	 * @see com.bluejungle.destiny.webui.framework.faces.ILoadable#load()
	 */
	public void load() {
		// commonLoad();
		// // See bug #5741. Browser Back/Forward currently putting reporter in
		// bad
		// // state
		// // This is a quick fix
		// if (!(this.selectedReport instanceof ISavedReport)) {
		// List reportList = getReportList();
		// if (!(reportList.isEmpty())) {
		// ISavedReport firstSavedReport = (ISavedReport) reportList.get(0);
		// this.setSelectedReportId(firstSavedReport.getId().longValue());
		// } else {
		// // Not clear on what to do here. Need to redirect to the front
		// // page. I'm not sure if that can be done here. To risky to try now
		// }
		// }
		// }
		//
		// public void loadNew() {
		// commonLoad();
		// if (this.selectedReport instanceof ISavedReport) {
		// createNewQuickReport();
		// }
		// }
		//
		// /**
		// *
		// */
		// private void commonLoad() {
		if (!isLoaded()) {
			super.load();

			// Begin with quick report selected
			this.selectedReport = new ReportImpl();
			Calendar beginCal = Calendar.getInstance();
			beginCal.add(Calendar.DAY_OF_MONTH, -7);
			this.selectedReport.setBeginDate(beginCal.getTime());
			this.selectedReport.setEndDate(new Date());

			this.isLoaded = true;
		}
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportPageBeanImpl#getSelectedReport()
	 */
	public IReport getSelectedReport() {
		return this.selectedReport;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportPageBeanImpl#setSelectedReportId(java.lang.Long)
	 */
	public void setSelectedReportId(Long id) {
		super.setSelectedReportId(id);

		// When a report is selected which is not the quick report, select it
		// here
		this.selectedReport = super.getSelectedReport();
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReportPageBean#deleteReport(java.lang.Long)
	 */
	public void deleteSelectedReport() throws ReportViewException, AxisFault {
		ReportLibraryServiceStub library = getReportLibraryService();

		IReport selectedReport = getSelectedReport();
		if (!(selectedReport instanceof ReportImpl)) {
			throw new IllegalStateException("Unexpected report class: "
					+ selectedReport.getClass());
		}

		long id = ((ReportImpl) selectedReport).getId().longValue();

		try {
			Id reportId = new Id();
			reportId.setId(id);
			library.deleteReport(reportId);

			removeSelectedReportFromReportList();

			IReport newSelectedReport = super.getSelectedReport();
			if (newSelectedReport == null) {
				createNewQuickReport();
			} else {
				this.selectedReport = newSelectedReport;
			}
		} catch (AccessDeniedFault exception) {
			StringBuffer errorMessage = buildReportDeleteFailedMessagePrefix(id);
			errorMessage.append("  Current user denied access.");
			throw new ReportViewException(errorMessage.toString(), exception);
		} catch (ServiceNotReadyFault exception) {
			StringBuffer errorMessage = buildReportDeleteFailedMessagePrefix(id);
			errorMessage.append("  Server is not available.");
			throw new ReportViewException(errorMessage.toString(), exception);
		} catch (PersistenceFault exception) {
			StringBuffer errorMessage = buildReportDeleteFailedMessagePrefix(id);
			errorMessage.append("  Persistence failure occurred.");
			throw new ReportViewException(errorMessage.toString(), exception);
		} catch (UnknownEntryFault exception) {
			StringBuffer errorMessage = buildReportDeleteFailedMessagePrefix(id);
			errorMessage.append("  Failed to find current persistent record.");
			throw new ReportViewException(errorMessage.toString(), exception);
		} catch (RemoteException exception) {
			StringBuffer errorMessage = buildReportDeleteFailedMessagePrefix(id);
			throw new ReportViewException(errorMessage.toString(), exception);
		}
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReportPageBean#updateSelectedReport()
	 */
	public void updateSelectedReport() throws ReportViewException, AxisFault {
		ReportLibraryServiceStub library = getReportLibraryService();
		IReport selectedReport = getSelectedReport();
		if (!(selectedReport instanceof ReportImpl)) {
			throw new IllegalStateException("Unexpected report class: "
					+ selectedReport.getClass());
		}

		ReportImpl reportToSave = (ReportImpl) selectedReport;

		try {
			library.updateReport(reportToSave.getWrappedReport());
		} catch (AccessDeniedFault exception) {
			StringBuffer errorMessage = buildReportUpdateFailedMessagePrefix(reportToSave);
			errorMessage.append("  Current user denied access.");
			throw new ReportViewException(errorMessage.toString(), exception);
		} catch (ServiceNotReadyFault exception) {
			StringBuffer errorMessage = buildReportUpdateFailedMessagePrefix(reportToSave);
			errorMessage.append("  Server is not available.");
			throw new ReportViewException(errorMessage.toString(), exception);
		} catch (UniqueConstraintViolationFault exception) {
			StringBuffer errorMessage = buildReportUpdateFailedMessagePrefix(reportToSave);
			String[] constrainingFields = exception.getFaultMessage().getUniqueConstraintViolationFault().getConstrainingField();
			if (constrainingFields == null || constrainingFields.length == 0) {
				errorMessage
						.append("  There is an unique constraint violation.");
			} else {
				errorMessage.append("  A report with same ");
				errorMessage.append(ArrayUtils
						.asString(constrainingFields, ","));
				errorMessage.append(" is already defined.");
			}
			throw new ReportViewException(errorMessage.toString(), exception);
		} catch (PersistenceFault exception) {
			StringBuffer errorMessage = buildReportUpdateFailedMessagePrefix(reportToSave);
			errorMessage.append("  Persistence failure occurred.");
			throw new ReportViewException(errorMessage.toString(), exception);
		} catch (UnknownEntryFault exception) {
			StringBuffer errorMessage = buildReportUpdateFailedMessagePrefix(reportToSave);
			errorMessage.append("  Failed to find current persistent record.");
			throw new ReportViewException(errorMessage.toString(), exception);
		} catch (RemoteException exception) {
			StringBuffer errorMessage = buildReportUpdateFailedMessagePrefix(reportToSave);
			throw new ReportViewException(errorMessage.toString(), exception);
		}
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IMyReportsPageBean#cancelSelectedReportEdit()
	 */
	public void cancelSelectedReportEdit() {
		IReport selectedReport = getSelectedReport();
		if (!(selectedReport instanceof ReportImpl)) {
			throw new IllegalStateException("Unexpected report class: "
					+ selectedReport.getClass());
		}

		ReportImpl selectedReportToCancelEdit = (ReportImpl) selectedReport;

		/**
		 * Resetting and reloading is possibliy expensive and more that we need
		 * to do here. However, undoing the changes is a non-trivial coding
		 * task. The full reset shouldn't be a huge problem, do to the low
		 * volume of users and low number of reports. If we find this not to be
		 * the case, we'll revisit
		 */
		resetAndSelectReport(selectedReportToCancelEdit.getId());
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReportPageBean#createNewQuickReport()
	 */
	public void createNewQuickReport() {
		this.selectedReport = new ReportImpl();
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReportPageBean#insertSelectedReport(com.bluejungle.destiny.inquirycenter.report.IReportInsertionInfoBean)
	 */
	public void insertSelectedReport() throws ReportViewException, AxisFault {
		ReportLibraryServiceStub library = getReportLibraryService();
		IReport selectedReport = getSelectedReport();
		if (!(selectedReport instanceof ReportImpl)) {
			throw new IllegalStateException("Unexpected report class: "
					+ selectedReport.getClass());
		}

		String insertTitle = selectedReport.getTitle();
		if (getReportsTitle().contains(insertTitle)) {
			throw new ReportViewException(
					"Failed to insert report with title, \"" + insertTitle
							+ "\". The same title already exists.", null);
		}

		ReportImpl reportToSave = (ReportImpl) this.getSelectedReport();
		if (reportToSave.isTrackingActivitySelected()) {
			reportToSave.setPolicies("");
			reportToSave.setEnforcementsAsList(new ArrayList());
		}

		try {
			Report savedReport = library.insertReport(reportToSave
					.getWrappedReport());
			resetAndSelectReport(new Long(savedReport.getId().getId()));
			setReportDefinitionSelected(false);
			setReportResultsSelected(true);
		} catch (AccessDeniedFault exception) {
			throw new ReportViewException(
					"Failed to insert report with title, " + insertTitle
							+ ".  Current user denied access.", exception);
		} catch (ServiceNotReadyFault exception) {
			throw new ReportViewException(
					"Failed to insert report with title, " + insertTitle
							+ ".  Server is not available.", exception);
		} catch (UniqueConstraintViolationFault exception) {
			StringBuilder message = new StringBuilder();
			message.append("Failed to insert report with title, ");
			message.append(insertTitle).append(".  "); // double space
			String[] constrainingFields = exception.getFaultMessage().getUniqueConstraintViolationFault().getConstrainingField();
			if (constrainingFields == null || constrainingFields.length == 0) {
				message.append("There is an unique constraint violation.");
			} else {
				message.append("A report with the same ");
				message.append(ArrayUtils.asString(constrainingFields, ","));
				message.append(" is already defined.");
			}
			throw new ReportViewException(message.toString(), exception);
		} catch (PersistenceFault exception) {
			cancelSelectedReportEdit();
			throw new ReportViewException(
					"Failed to insert report with title, " + insertTitle
							+ ".  Persistence failure occurred.", exception);
		} catch (RemoteException exception) {
			cancelSelectedReportEdit();
			throw new ReportViewException(
					"Failed to insert report with title, " + insertTitle,
					exception);
		}
	}

	/**
	 * @see com.bluejungle.destiny.webui.framework.faces.ILoadable#isLoaded()
	 */
	public boolean isLoaded() {
		return ((super.isLoaded()) && (this.isLoaded == true));
	}

	/**
	 * Returns the query specification to use when fetching report
	 * 
	 * @return the query specification object
	 */
	protected ReportQuerySpec getReportListQuerySpec() {
		return MY_REPORTS_QUERY_SPEC;
	}

	/**
	 * Build the error message prefix generated when a report update fails
	 * 
	 * @param reportToSave
	 * @return
	 */
	private StringBuffer buildReportUpdateFailedMessagePrefix(
			ReportImpl reportToSave) {
		StringBuffer errorMessage = new StringBuffer(
				"Failed to update report with id, ");
		errorMessage.append(reportToSave.getId());
		errorMessage.append(", and title, ");
		errorMessage.append(reportToSave.getTitle());
		errorMessage.append(".");

		return errorMessage;
	}

	/**
	 * Build the error message prefix generated when a report delete fails
	 * 
	 * @param reportToSave
	 * @return
	 */
	private StringBuffer buildReportDeleteFailedMessagePrefix(long reportId) {
		StringBuffer errorMessage = new StringBuffer(
				"Failed to delete report with id, ");
		errorMessage.append(reportId);
		errorMessage.append(".");

		return errorMessage;
	}

	/**
	 * connect to the server and get a list of all report title.
	 * 
	 * @return all report title
	 */
	private List<String> getReportsTitle() throws AxisFault {
		List<String> titles = new ArrayList<String>();
		ReportLibraryServiceStub library = getReportLibraryService();
		try {
			ReportList wsReportList = library
					.getReports(getReportListQuerySpec());
			Report[] wsReports = wsReportList.getReports();
			if (wsReports != null) {
				for (int index = 0; index < wsReports.length; index++) {
					titles.add(wsReports[index].getTitle());
				}
			}
		} catch (AccessDeniedFault e) {
			// Session timeout - logout the user
			AppContext.getContext().releaseContext();
			FacesContext.getCurrentInstance().renderResponse();
		} catch (ServiceNotReadyFault e) {
			getLog().error("Report library service is not ready", e);
		} catch (RemoteException e) {
			getLog().error("Report library service threw an exception", e);
		}
		// List<String> titles = new ArrayList<String>();
		// ReportLibraryIF library = getReportLibraryService();
		// try {
		// SavedReportList wsReportList =
		// library.getReports(getReportListQuerySpec());
		// SavedReport[] wsReports = wsReportList.getReports();
		// if (wsReports != null) {
		// for (int index = 0; index < wsReports.length; index++) {
		// titles.add(wsReports[index].getTitle());
		// }
		// }
		// } catch (AccessDeniedFault e) {
		// // Session timeout - logout the user
		// AppContext.getContext().releaseContext();
		// FacesContext.getCurrentInstance().renderResponse();
		// } catch (ServiceNotReadyFault e) {
		// getLog().error("Report library service is not ready", e);
		// } catch (RemoteException e) {
		// getLog().error("Report library service threw an exception", e);
		// }
		//
		return titles;
	}

	public void onExecuteReport(ActionEvent executeEvent) {
		Timestamp beginTimestamp = new Timestamp(getSelectedReport()
				.getBeginDate().getTime());
		Timestamp endTimestamp = new Timestamp(getSelectedReport().getEndDate()
				.getTime());
		MessageUtil.checkReportQueryRange(beginTimestamp, endTimestamp,
				selectedReport.isPolicyActivitySelected());
		super.onExecuteReport(executeEvent);
	}

	private String navigateAction = "myReports";
	private String executeNavigateAction = "myReportsExecute";

	private void initNavigateActions() {
		navigateAction = "myReports";
		executeNavigateAction = "myReportsExecute";
	}

	public String getNavigateAction() {
		return navigateAction;
	}

	public void setNavigateAction(String navigateAction) {
		this.navigateAction = navigateAction;
	}

	public void setExecuteNavigateAction(String executeNavigateAction) {
		this.executeNavigateAction = executeNavigateAction;
	}

	public String getExecuteNavigateAction() {
		return executeNavigateAction;
	}

	public void reset() {
		super.reset();
		initNavigateActions();
	}

}
