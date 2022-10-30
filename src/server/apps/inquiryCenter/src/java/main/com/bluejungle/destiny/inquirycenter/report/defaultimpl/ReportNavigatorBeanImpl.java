package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.nextlabs.destiny.configclient.Config;
import com.nextlabs.destiny.configclient.ConfigClient;
import com.nextlabs.destiny.inquirycenter.SharedUtils;
import com.nextlabs.destiny.inquirycenter.customapps.CustomAppJO;
import com.nextlabs.destiny.inquirycenter.customapps.ExternalReportAppException;
import com.nextlabs.destiny.inquirycenter.customapps.ExternalReportAppManager;
import com.nextlabs.destiny.inquirycenter.customapps.IExternalReportApplication;
import com.nextlabs.destiny.inquirycenter.report.ICustomReportPageBean;
import com.nextlabs.destiny.inquirycenter.savedreport.service.ReporterAccessControlService;
import com.nextlabs.destiny.inquirycenter.savedreport.service.impl.ReporterAccessControlServiceImpl;

public class ReportNavigatorBeanImpl {

	private static final Log LOG = LogFactory
			.getLog(ReportNavigatorBeanImpl.class.getName());

	private static final Config showSharePointConfig = ConfigClient.get("show.sharepoint");

	private String currentReportPage;
	private String myReportPageLabel;
	private String sharepointReportLabel;

	private List<SelectItem> reportPageList = new ArrayList<SelectItem>();

	private Map<String, String> reportAppLabelMap = new HashMap<String, String>();

	private boolean hasMultipleReportsPages;

	private boolean hasMonitorAccess = false;

	private String selectedReportContentInclusionFile;

	private static final String MY_REPORTS = "myReports_drop_down_item_label";
	private static final String SP_REPORTS = "sharePointReports_drop_down_item_label";
	private static final String MY_REPORTS_NAVIGATE_ACTION = "myReports";
	private static final String SHAREPOINT_BEAN_NAME = "sharePointReportsBean";
	private static final String MY_REPORTS_BEAN_NAME = "myReportsBean";
	private static final String CUSTOM_REPORTS_BEAN_NAME = "customReportsBean";
	public static final String SUPER_USER_USERNAME = "Administrator";

	// This is defined in the jspf and used here - any changes need to be done
	// at both places
	private static final String MULTI_NESTED_CONTENT_VIIEW = "multiReportsNestedContentView";
	private static final String SHAREPOINT_REPORT_CONTENT_FILE = "/WEB-INF/jspf/tiles/reports/sharePointReportsContent.jspf";
	public static final String REPORTER_ADMIN = "Report Administrator";
	public static final String SYSTEM_ADMIN = "System Administrator";
	
//	public static final String REPORTER_ANALYST = "Report Analyst";

	private ReporterAccessControlService reporterAccessControlService;
//	private LifecycleManager lifecycleManager;
	private String clientApplication;

	private Map<String, CustomAppJO> customReportMap = new HashMap<String, CustomAppJO>();

	public void load() {
		if (getLog().isDebugEnabled()) {
			getLog().debug("Loading ReportNavigationBean");
		}

		try {
			reportPageList.clear();
			reportAppLabelMap.clear();
			configureMultiReportOption();
			hasMonitorAccess = true;
		} catch (Exception e) {
			LOG.error(
					"Error encountered in checking the access permissions for the logged in user",
					e);
		}
	}

	protected Log getLog() {
		return LOG;
	}

	private void configureMultiReportOption() {
		boolean isSharepointVisible = false;
		boolean isCustomReportPresent = false;

		// all report labels other than custom
		List<String> reportAppLabels = new ArrayList<String>();

		ResourceBundle inquiryCenterBundle = MessageUtil
				.getInquiryCenterResourceBundle();
		FacesContext context = FacesContext.getCurrentInstance();
		VariableResolver resolver = context.getApplication()
				.getVariableResolver();

		// check sharepoint visibility setting
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();

        isSharepointVisible = showSharePointConfig.toBoolean();

		IExternalReportApplication extRepAppMgr = (IExternalReportApplication) compMgr
				.getComponent(ExternalReportAppManager.class);
		if (extRepAppMgr == null) {
			throw new RuntimeException(
					"Could not access the  external report "
							+ " component - the server may not have been initialized correctly");
		}
		HttpSession session = (HttpSession) context.getExternalContext()
				.getSession(false);

		try {
			Collection<CustomAppJO> customReportApps = extRepAppMgr.getAll(
					session.getId(), session.getMaxInactiveInterval());

			if (customReportApps != null && !customReportApps.isEmpty()) {
				isCustomReportPresent = true;

				ICustomReportPageBean customReportBean = getCustomReportBean();
				customReportBean.setCustomReportApps(customReportApps);
				for (CustomAppJO thisCustomReportApp : customReportApps) {
					customReportMap.put(thisCustomReportApp
							.getPolicyApplicationJO().getName(),
							thisCustomReportApp);
				}
			}
		} catch (ExternalReportAppException ex) {
			if (getLog().isErrorEnabled()) {
				getLog().error(
						"Error on trying to obtain External Report Information",
						ex);
			}
		}

		// prepare the report items to be obtained using drop-down box if needed
		if (isSharepointVisible || isCustomReportPresent) {

			// first goes the My Report page as 'Custom'
			setHasMultipleReportsPages(true);
			myReportPageLabel = getReportAppLabel(inquiryCenterBundle
					.getString(MY_REPORTS));

			setCurrentReportPage(myReportPageLabel); // default

			MyReportsPageBeanImpl myReportBean = getMyReportBean();
			reportPageList.add(new SelectItem(
					getReportAppLabel(myReportPageLabel)));

			// sharepoint is available goes next
			if (isSharepointVisible) {
				sharepointReportLabel = getReportAppLabel(inquiryCenterBundle
						.getString(SP_REPORTS));

				SharePointReportPageBeanImpl spReportBean = (SharePointReportPageBeanImpl) resolver
						.resolveVariable(context, SHAREPOINT_BEAN_NAME);
				if (spReportBean != null) {
					reportAppLabels.add(sharepointReportLabel);
				}
			}

			// custom reports
			if (isCustomReportPresent) {
				for (String thisAppName : customReportMap.keySet()) {
					reportAppLabels.add(getReportAppLabel(thisAppName));
				}
			}

			// add the sorted list of custom report - this includes all reports
			// other than
			// the original MyReports page that is labeled as 'Custom' and is
			// always at
			// the top
			Collections.sort(reportAppLabels, String.CASE_INSENSITIVE_ORDER);
			for (String thisReportAppLabel : reportAppLabels) {
				reportPageList.add(new SelectItem(thisReportAppLabel));
			}

			// configure MyReports bean since it is at the top
			myReportBean.reset();
			myReportBean.load();
			myReportBean.setNavigateAction(MY_REPORTS_NAVIGATE_ACTION);
		}
	}

	private ICustomReportPageBean getCustomReportBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		VariableResolver resolver = context.getApplication()
				.getVariableResolver();
		ICustomReportPageBean bean = (ICustomReportPageBean) resolver
				.resolveVariable(context, CUSTOM_REPORTS_BEAN_NAME);
		if (bean == null) {
			throw new RuntimeException("Could not create Custom Report Bean");
		}
		return bean;
	}

	private MyReportsPageBeanImpl getMyReportBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		VariableResolver resolver = context.getApplication()
				.getVariableResolver();
		MyReportsPageBeanImpl bean = (MyReportsPageBeanImpl) resolver
				.resolveVariable(context, MY_REPORTS_BEAN_NAME);
		if (bean == null) {
			throw new RuntimeException("Cannot create Main Report bean");
		}
		return bean;
	}

	public void setHasMultipleReportsPages(boolean hasMultipleReportsPages) {
		this.hasMultipleReportsPages = hasMultipleReportsPages;
	}

	public boolean isHasMultipleReportsPages() {
		return hasMultipleReportsPages;
	}

	public void setReportPageList(List<SelectItem> reportPageList) {
		this.reportPageList = reportPageList;
	}

	public List<SelectItem> getReportPageList() {
		return reportPageList;
	}

	public void setCurrentReportPage(String currentReportPage) {
		this.currentReportPage = currentReportPage;
		String originalPageLabel = reportAppLabelMap.get(currentReportPage);
		if (originalPageLabel == null)
			throw new IllegalArgumentException(
					"ReportNavigatorBean has no mapping for page: "
							+ currentReportPage);

		if (myReportPageLabel.equals(originalPageLabel)) {
			setSelectedReportContentInclusionFile(SHAREPOINT_REPORT_CONTENT_FILE);
			MyReportsPageBeanImpl myReportBean = getMyReportBean();
			myReportBean.reset();
			myReportBean.load();
			myReportBean.setNavigateAction(MY_REPORTS_NAVIGATE_ACTION);
			myReportBean.setExecuteNavigateAction(MY_REPORTS_NAVIGATE_ACTION);
		} else {
			setSelectedReportContentInclusionFile(SHAREPOINT_REPORT_CONTENT_FILE);
			getCustomReportBean().setCurrentCustomReportApp(
					customReportMap.get(originalPageLabel));
		}
		cleanMultiReportContentComponentTree();
	}

	private void cleanMultiReportContentComponentTree() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			UIComponent root = context.getViewRoot();
			findAndRemoveComponent(root, "");
		}

	}

	public String getCurrentReportPage() {
		return currentReportPage;
	}

	public void processValueChange(ValueChangeEvent event)
			throws AbortProcessingException {
		setCurrentReportPage(event.getNewValue().toString());

		// Skip validation of non-immediate components and invocation of the
		// submit() method.
		FacesContext.getCurrentInstance().renderResponse();

	}

	public void setSelectedReportContentInclusionFile(
			String selectedReportContentInclusionFile) {
		this.selectedReportContentInclusionFile = selectedReportContentInclusionFile;
	}

	public String getSelectedReportContentInclusionFile() {
		return selectedReportContentInclusionFile;
	}

	public void setMyReportPageLabel(String myReportPageLabel) {
		this.myReportPageLabel = myReportPageLabel;
	}

	public String getMyReportPageLabel() {
		return myReportPageLabel;
	}

	private void findAndRemoveComponent(UIComponent base, String id) {

		// Search through our facets and children
		UIComponent kid = null;
		UIComponent multiReportsNestedContentView = null;
		UIComponent result = null;
		Iterator kids = base.getFacetsAndChildren();
		while (kids.hasNext() && (result == null)) {
			kid = (UIComponent) kids.next();
			if (kid.getId().startsWith(MULTI_NESTED_CONTENT_VIIEW)) {
				multiReportsNestedContentView = kid;
				multiReportsNestedContentView.getChildren().clear();
				break;
			} else {
				findAndRemoveComponent(kid, id);
			}
		}
	}

	/**
	 * The labels of the drop-down list that is used to select each report
	 * application need to be limited to specified max characters after which
	 * the label is ended with ellipsis.
	 * 
	 * This method is used to generate and return the display labels and is used
	 * during the loading of the navigator bean page. A map holds the mapping
	 * between the display labels and the original labels provided in the ui
	 * config file.
	 * 
	 * Note that it is assumed that the custom report page label stays the same
	 * i.e. not > specified number of characters
	 * 
	 * @param originalLabel
	 * @return truncated label if originalLabel > max allowed characters
	 */
	private String getReportAppLabel(String originalLabel) {
		if (originalLabel == null)
			throw new IllegalArgumentException(
					"The original label cannot be null");
		String convertedLabel = originalLabel;

		// limit the label size to be of specified size if needed
		if (originalLabel.length() > ICustomReportPageBean.MAX_DISPLAYABLE_CHARS) {
			convertedLabel = SharedUtils.limitStringWithEllipsis(originalLabel,
					ICustomReportPageBean.MAX_DISPLAYABLE_CHARS);

			// corner case - does a different app have the same converted label?
			if (reportAppLabelMap.containsKey(convertedLabel)) {
				// do some ad-hoc stuff - add a '.' - this is not intuitive but
				// the user can
				// at least select the app
				convertedLabel += ".";
			}
		}
		reportAppLabelMap.put(convertedLabel, originalLabel);
		return convertedLabel;
	}

	public boolean isHasMonitorAccess() {
		return hasMonitorAccess;
	}

	public void setHasMonitorAccess(boolean hasMonitorAccess) {
		this.hasMonitorAccess = hasMonitorAccess;
	}

	private ReporterAccessControlService getReporterAccessControlService() {
		if (reporterAccessControlService == null) {
			reporterAccessControlService = new ReporterAccessControlServiceImpl();
		}

		return reporterAccessControlService;
	}

}
