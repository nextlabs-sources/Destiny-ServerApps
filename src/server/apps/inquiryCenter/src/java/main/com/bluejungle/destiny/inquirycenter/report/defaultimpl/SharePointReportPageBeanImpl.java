package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import com.bluejungle.destiny.types.basic.v1.Id;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.inquirycenter.enumeration.SharePointReportType;
import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.IReportPageBean;
import com.bluejungle.destiny.server.shared.configuration.IReporterComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.webui.framework.message.MessageManager;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyConfigurationStoreImpl;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.configuration.IDestinyConfigurationStore;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.inquirycenter.report.IReportGenerator;
import com.nextlabs.destiny.inquirycenter.report.defaultimpl.ReportGenerator;

public class SharePointReportPageBeanImpl implements IReportPageBean {
	private static final Log LOG = LogFactory
			.getLog(SharePointReportPageBeanImpl.class.getName());

	private static final String SITE_PREFIX = "sharepoint://";
	private static String folder;
	private static List<IReport> reportList;

	// configuration flag
	private boolean showSharePointTab = false;

	private boolean isLoaded = false;

	private boolean reportResultsSelected = true;
	private boolean reportDefinitionSelected = false;

	private Date beginDate, endDate;
	private String timeDimension, siteURL = SITE_PREFIX;
	private String process, department;

	private Long selectedReportId = Long.valueOf(0);

	private static final Log log = LogFactory.getLog(SharePointReportPageBeanImpl.class);

	private enum Category {
		Consumption, Storage, Modification, Deletion, Total;
	}

	private class Item {
		private String name;
		private Category category;
		private int quantity;

		public Item(String name, Category category, int quantity) {
			this.name = name;
			this.category = category;
			this.quantity = quantity;
		}

		public Item(String name, int quantity) {
			this.name = name;
			this.category = null;
			this.quantity = quantity;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public Category getCategory() {
			return category;
		}

		public void setCategory(Category category) {
			this.category = category;
		}
	}

	public List<IReport> getReportList() {
		if (reportList == null) {
			reportList = new ArrayList<IReport>();
			int i = 0;
			Report report = new Report();
			Id reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report.setTitle(SharePointReportType.INFORMATION_LIFECYCLE_TREND
					.getName());
			IReport iReport = new ReportImpl(report);
			reportList.add(iReport);

			report = new Report();
			reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report.setTitle(SharePointReportType.INFORMATION_LIFECYCLE_USER
					.getName());
			iReport = new ReportImpl(report);
			reportList.add(iReport);

			report = new Report();
			reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report.setTitle(SharePointReportType.INFORMATION_LIFECYCLE_PROCESS
					.getName());
			iReport = new ReportImpl(report);
			reportList.add(iReport);

			report = new Report();
			reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report.setTitle(SharePointReportType.INFORMATION_LIFECYCLE_SITE
					.getName());
			iReport = new ReportImpl(report);
			reportList.add(iReport);

			report = new Report();
			reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report
					.setTitle(SharePointReportType.INFORMATION_LIFECYCLE_DEPARTMENT
							.getName());
			iReport = new ReportImpl(report);
			reportList.add(iReport);

			report = new Report();
			reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report.setTitle(SharePointReportType.POLICY_ACTIVITY_DEPARTMENT
					.getName());
			iReport = new ReportImpl(report);
			reportList.add(iReport);

			report = new Report();
			reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report.setTitle(SharePointReportType.POLICY_ACTIVITY_PROCESS
					.getName());
			iReport = new ReportImpl(report);
			reportList.add(iReport);

			report = new Report();
			reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report.setTitle(SharePointReportType.POLICY_ACTIVITY_POLICY
					.getName());
			iReport = new ReportImpl(report);
			reportList.add(iReport);

			report = new Report();
			reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report
					.setTitle(SharePointReportType.POLICY_ACTIVITY_SITE
							.getName());
			iReport = new ReportImpl(report);
			reportList.add(iReport);

			report = new Report();
			reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report.setTitle(SharePointReportType.POLICY_ACTIVITY_TREND
					.getName());
			iReport = new ReportImpl(report);
			reportList.add(iReport);

			report = new Report();
			reportId = new Id();
			reportId.setId(i++);
			report.setId(reportId);
			report
					.setTitle(SharePointReportType.POLICY_ACTIVITY_USER
							.getName());
			iReport = new ReportImpl(report);
			reportList.add(iReport);
		}
		return reportList;
	}

	public IReport getSelectedReport() {
		return getReportList().get(selectedReportId.intValue());
	}

	public String getTitle() {
		IReport report = getSelectedReport();
		String title = report.getTitle();
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_DEPARTMENT
				.getName())) {
			return "SharePoint Information Lifecycle by Department";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_PROCESS
				.getName())) {
			return "SharePoint Information Lifecycle by Business Process";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_SITE
				.getName())) {
			return "SharePoint Information Lifecycle by Site";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_TREND
				.getName())) {
			return "SharePoint Information Lifecycle Trend Analysis";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_USER
				.getName())) {
			return "SharePoint Information Lifecycle by User";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_DEPARTMENT
				.getName())) {
			return "SharePoint Policy Activity by Department";
		}
		if (title
				.equals(SharePointReportType.POLICY_ACTIVITY_PROCESS.getName())) {
			return "SharePoint Policy Activity by Business Process";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_POLICY.getName())) {
			return "SharePoint Policy Activity by Policy";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_SITE.getName())) {
			return "SharePoint Policy Activity by Site";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_TREND.getName())) {
			return "SharePoint Policy Activity Trend Analysis";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_USER.getName())) {
			return "SharePoint Policy Activity by User";
		}
		return "";
	}

	public String getDescription() {
		IReport report = getSelectedReport();
		String title = report.getTitle();
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_DEPARTMENT
				.getName())) {
			return "Displays a summary of tracked information lifecycle activity for each department over the specified time period.";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_PROCESS
				.getName())) {
			return "Displays a summary of tracked information lifecycle activity for each business process over the specified time period.";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_SITE
				.getName())) {
			return "Displays a summary of tracked information lifecycle activity with each SharePoint Site contained within the specified Site over the specified time period.";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_TREND
				.getName())) {
			return "Displays a summary of tracked information lifecycle activity over the specified time period.";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_USER
				.getName())) {
			return "Displays a summary of tracked information lifecycle activity within the specified department or business process for each user over the specified time period.";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_DEPARTMENT
				.getName())) {
			return "Displays a summary of tracked policy activity, grouped by department, over the specified time period.";
		}
		if (title
				.equals(SharePointReportType.POLICY_ACTIVITY_PROCESS.getName())) {
			return "Displays a summary of tracked policy activity, grouped by business process, over the specified time period.";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_POLICY.getName())) {
			return "Displays a summary of tracked policy activity, grouped by policy, over the specified time period.";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_SITE.getName())) {
			return "Displays a summary of tracked policy activity, grouped by site, contained within the specified site over the specified time period.";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_TREND.getName())) {
			return "Displays a trend analysis of tracked policy activity over the specified time period.";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_USER.getName())) {
			return "Displays a summary of tracked policy activity within the specified department or business process for each user over the specified time period.";
		}
		return "";
	}

	public String getFolder() {
		if (!isReportDefinitionSelected()) {
			return "";
		}
		if (folder == null) {
			IComponentManager compMgr = ComponentManagerFactory
					.getComponentManager();
			ReportGenerator generator = (ReportGenerator) compMgr
					.getComponent(IReportGenerator.COMP_NAME);
			folder = (String) generator.getConfiguration().get(
					ReportGenerator.REPORT_OUTPUT_LOCATION);
		}
		return folder;
	}

	public void onExecuteReport(ActionEvent event) {
		this.reportDefinitionSelected = true;

		prepareData();
	}

	public void setSelectedReportId(Long id) {
		this.selectedReportId = id;
		reportResultsSelected = true;

		// set the default value
		beginDate = getDefaultBeginDate();
		endDate = getDefaultEndDate();
		timeDimension = "";
		siteURL = SITE_PREFIX;
		process = "";
		department = "";
		reportDefinitionSelected = false;
	}

	public boolean isReportResultsSelected() {
		return reportResultsSelected;
	}

	public void setReportResultsSelected(boolean reportResultsSelected) {
		this.reportResultsSelected = reportResultsSelected;
	}

	public Date getBeginDate() {
		if (beginDate == null) {
			return getDefaultBeginDate();
		}
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		if (beginDate != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTimeInMillis(beginDate.getTime());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			this.beginDate = cal.getTime();
		}
	}

	public Date getEndDate() {
		if (endDate == null) {
			return getDefaultEndDate();
		}
		return endDate;
	}

	public void setEndDate(Date endDate) {
		if (endDate != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTimeInMillis(endDate.getTime());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			this.endDate = cal.getTime();
		}
	}

	public Date getDefaultBeginDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -6);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public Date getDefaultEndDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	public String getDateString(Date time) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		return format.format(time);
	}

	public String getReportTitle() {
		if (!isReportDefinitionSelected()) {
			return "";
		}
		String title = getSelectedReport().getTitle();
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_TREND
				.getName())) {
			if (getTimeDimension().equals("month")) {
				return "Lifecycle Activity by Month "
						+ getDateString(getBeginDate()) + " - "
						+ getDateString(getEndDate());
			}
			if (getTimeDimension().equals("day")) {
				return "Lifecycle Activity by Day "
						+ getDateString(getBeginDate()) + " - "
						+ getDateString(getEndDate());
			}
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_PROCESS
				.getName())) {
			return "Lifecycle Activity by Process "
					+ getDateString(getBeginDate()) + " - "
					+ getDateString(getEndDate());
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_SITE
				.getName())) {
			return "Lifecycle Activity by Site " + getSiteURL() + " "
					+ getDateString(getBeginDate()) + " - "
					+ getDateString(getEndDate());
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_USER
				.getName())) {
			return "Lifecycle Activity by User "
					+ getDateString(getBeginDate()) + " - "
					+ getDateString(getEndDate());
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_DEPARTMENT
				.getName())) {
			return "Lifecycle Activity by Department "
					+ getDateString(getBeginDate()) + " - "
					+ getDateString(getEndDate());
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_DEPARTMENT
				.getName())) {
			return "Lifecycle Activity by Business Process "
					+ getDateString(getBeginDate()) + " - "
					+ getDateString(getEndDate());
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_TREND.getName())) {
			if (getTimeDimension().equals("month")) {
				return "Policy Activity by Month "
						+ getDateString(getBeginDate()) + " - "
						+ getDateString(getEndDate());
			}
			if (getTimeDimension().equals("day")) {
				return "Policy Activity by Day "
						+ getDateString(getBeginDate()) + " - "
						+ getDateString(getEndDate());
			}
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_SITE.getName())) {
			return "Policy Activity by Site " + getSiteURL() + " "
					+ getDateString(getBeginDate()) + " - "
					+ getDateString(getEndDate());
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_USER.getName())) {
			return "Policy Activity by User " + getDateString(getBeginDate())
					+ " - " + getDateString(getEndDate());
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_POLICY.getName())) {
			return "Policy Activity by Policy " + getDateString(getBeginDate())
					+ " - " + getDateString(getEndDate());
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_DEPARTMENT
				.getName())) {
			return "Policy Activity by Department "
					+ getDateString(getBeginDate()) + " - "
					+ getDateString(getEndDate());
		}
		if (title
				.equals(SharePointReportType.POLICY_ACTIVITY_PROCESS.getName())) {
			return "Policy Activity by Business Process "
					+ getDateString(getBeginDate()) + " - "
					+ getDateString(getEndDate());
		}
		return getSelectedReport().getTitle();
	}

	public String getBIRTReportName() {
		if (!isReportDefinitionSelected()) {
			return "";
		}

		String title = getSelectedReport().getTitle();

		// reports for SharePoint policy activities
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_DEPARTMENT
				.getName())) {
			return "SharePointActivity_Department.rptdesign";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_POLICY.getName())) {
			return "SharePointActivity_Policy.rptdesign";
		}
		if (title
				.equals(SharePointReportType.POLICY_ACTIVITY_PROCESS.getName())) {
			return "SharePointActivity_Process.rptdesign";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_SITE.getName())) {
			return "SharePointActivity_Site.rptdesign";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_TREND.getName())) {
			return "SharePointActivity_Trend.rptdesign";
		}
		if (title.equals(SharePointReportType.POLICY_ACTIVITY_USER.getName())) {
			return "SharePointActivity_User.rptdesign";
		}

		// reports for SharePoint information life cycle
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_DEPARTMENT
				.getName())) {
			return "SharePointLifecycle_Department.rptdesign";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_PROCESS
				.getName())) {
			return "SharePointLifecycle_Process.rptdesign";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_SITE
				.getName())) {
			return "SharePointLifecycle_Site.rptdesign";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_TREND
				.getName())) {
			return "SharePointLifecycle_Trend.rptdesign";
		}
		if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_USER
				.getName())) {
			return "SharePointLifecycle_User.rptdesign";
		}

		return getSelectedReport().getTitle();
	}

	public boolean isReportDefinitionSelected() {
		return reportDefinitionSelected;
	}

	public void setReportDefinitionSelected(boolean reportDefinitionSelected) {
		this.reportDefinitionSelected = reportDefinitionSelected;
	}

	public String getTimeDimension() {
		return timeDimension;
	}

	public void setTimeDimension(String timeDimension) {
		this.timeDimension = timeDimension;
	}

	private String normalizeSiteURL(String siteURL) {
		String site = "";
		// filter out the unwanted chars
		for (int i = 0, n = siteURL.length(); i < n; i++) {
			char c = siteURL.charAt(i);
			if (c == '\"' || c == '\'' || c == '#' || c == '&' || c == '%') {
				continue;
			}
			site += c;
		}
		// check the beginning of the url
		if (!site.startsWith(SITE_PREFIX)) {
			site = SITE_PREFIX;
		}
		return site.toLowerCase();
	}

	public String getSiteURL() {
		return normalizeSiteURL(siteURL);
	}

	public void setSiteURL(String siteURL) {
		this.siteURL = normalizeSiteURL(siteURL);
	}

	public boolean isTimeDimensionNeeded() {
		if (getSelectedReport().getTitle().toLowerCase().indexOf("trend") != -1) {
			return true;
		}
		return false;
	}

	public boolean isSiteURLNeeded() {
		if (getSelectedReport().getTitle().toLowerCase().indexOf("site") != -1) {
			return true;
		}
		return false;
	}

	public boolean isDepartmentProcessNeeded() {
		if (getSelectedReport().getTitle().toLowerCase().indexOf("user") != -1) {
			return true;
		}
		return false;
	}

	public String getConvertedBeginDate() {
		if (getBeginDate() != null) {
			return String.valueOf(getBeginDate().getTime());
		} else {
			return String.valueOf((new Date()).getTime());
		}
	}

	public String getConvertedEndDate() {
		if (getEndDate() != null) {
			return String.valueOf(getEndDate().getTime());
		} else {
			return String.valueOf((new Date()).getTime());
		}
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department.toLowerCase();
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process.toLowerCase();
	}

	public String getConvertedDepartment() {
		if (department == null || department.length() == 0) {
			return "";
		} else {
			return " AND lower(attr_one) LIKE '*" + department.trim() + "*' ";
		}
	}

	public String getConvertedProcess() {
		if (process == null || process.length() == 0) {
			return "";
		} else {
			return " AND lower(attr_two) LIKE '*" + process.trim() + "*' ";
		}
	}

	public boolean getShowSharePointTab() {
		return showSharePointTab;
	}

	public void setShowSharePointTab(boolean showSharePointTab) {
		this.showSharePointTab = showSharePointTab;
	}

	public boolean isLoaded() {
		return this.isLoaded;
	}

	public void load() {
		this.showSharePointTab = true;
		this.isLoaded = true;
	}

	private Category getCategory(String action) {
		if (action.equals("Pr")) {
			return Category.Consumption;
		} else if (action.equals("Ex")) {
			return Category.Storage;
		} else if (action.equals("Ed")) {
			return Category.Modification;
		} else if (action.equals("Op")) {
			return Category.Consumption;
		} else if (action.equals("Rn")) {
			return Category.Modification;
		} else if (action.equals("Co")) {
			return Category.Storage;
		} else if (action.equals("Mo")) {
			return Category.Storage;
		} else if (action.equals("De")) {
			return Category.Deletion;
		}
		return null;
	}

	synchronized private void prepareData() {
		if (!isReportDefinitionSelected()) {
		}

		Session session = null;
		Connection connection = null;
		ResultSet resultSet = null;
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		IHibernateRepository activityDataSrc = (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());
		String filename = getFolder();
		String title = getSelectedReport().getTitle();
		Timestamp beginTimestamp = new Timestamp(getBeginDate().getTime());
		Timestamp endTimestamp = new Timestamp(getEndDate().getTime());
		MessageUtil.checkReportQueryRange(
		        beginTimestamp, endTimestamp, true);

		try {
			session = activityDataSrc.getSession();
			connection = session.connection();
			StringBuffer sql = new StringBuffer();
			if (title.equals(SharePointReportType.INFORMATION_LIFECYCLE_TREND
					.getName())) {
				List<Item> result = new ArrayList<Item>();
				filename += "/lifecycle_trend.csv";
				if (getTimeDimension().equals("month")) {
					sql
							.append(
									"SELECT month_nb, action, count(*) FROM report_policy_activity_log, report_obligation_log")
							.append(
									" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
							.append(SITE_PREFIX)
							.append("%' AND time >= ? AND time <= ?")
							.append(" AND name = 'SPLOGACTIVITY'")
							.append(
									" AND (action = 'Op' OR action = 'Ed' OR action = 'Pr' OR action = 'Ex' OR action = 'Rn' OR action = 'Co' OR action = 'Mo' OR action = 'De')")
							.append(
									" GROUP BY month_nb, action ORDER BY month_nb");
				} else if (getTimeDimension().equals("day")) {
					sql
							.append(
									"SELECT day_nb, action, count(*) FROM report_policy_activity_log, report_obligation_log")
							.append(
									" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
							.append(SITE_PREFIX)
							.append("%' AND time >= ? AND time <= ?")
							.append(" AND name = 'SPLOGACTIVITY'")
							.append(
									" AND (action = 'Op' OR action = 'Ed' OR action = 'Pr' OR action = 'Ex' OR action = 'Rn' OR action = 'Co' OR action = 'Mo' OR action = 'De')")
							.append(" GROUP BY day_nb, action ORDER BY day_nb");
				}
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				while (resultSet.next()) {
					long time = resultSet.getLong(1);
					String name = "";
					Date now = new Date();
					now.setTime(time);
					if (getTimeDimension().equals("month")) {
						SimpleDateFormat format = new SimpleDateFormat(
								"MMM yyyy");
						name = format.format(now);
					} else if (getTimeDimension().equals("day")) {
						SimpleDateFormat format = new SimpleDateFormat(
								"MMM d yyyy");
						name = format.format(now);
					}
					String action = resultSet.getString(2);
					int quantity = resultSet.getInt(3);
					Category category = getCategory(action);
					findAndAdd(result, name, category, quantity);
				}
				outputFile(filename, result, true, -1);
			} else if (title
					.equals(SharePointReportType.INFORMATION_LIFECYCLE_USER
							.getName())) {
				Map<String, Map<Category, Integer>> result = new HashMap<String, Map<Category, Integer>>();
				String department = getDepartment();
				if (department != null && department.length() > 0) {
					department = " AND lower(attr_one) LIKE '%"
							+ department.toLowerCase() + "%'";
				} else {
					department = "";
				}

				String process = getProcess();
				if (process != null && process.length() > 0) {
					process = " AND lower(attr_two) LIKE '%"
							+ process.toLowerCase() + "%'";
				} else {
					process = "";
				}

				filename += "/lifecycle_user.csv";
				sql
						.append(
								"SELECT user_name, action, count(*) FROM report_policy_activity_log, report_obligation_log")
						.append(
								" WHERE  report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
						.append(SITE_PREFIX)
						.append("%' AND time >= ? AND time <= ?")
						.append(" AND name = 'SPLOGACTIVITY'")
						.append(
								" AND (action = 'Op' OR action = 'Ed' OR action = 'Pr' OR action = 'Ex' OR action = 'Rn' OR action = 'Co' OR action = 'Mo' OR action = 'De')")
						.append(department)
						.append(process)
						.append(
								" GROUP BY user_name, action ORDER BY user_name");
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				while (resultSet.next()) {
					String name = resultSet.getString(1);
					String action = resultSet.getString(2);
					int quantity = resultSet.getInt(3);
					Category category = getCategory(action);
					findAndAdd(result, name, category, quantity);
				}
				outputLifecycleFile(filename, result, 40);
			} else if (title
					.equals(SharePointReportType.INFORMATION_LIFECYCLE_PROCESS
							.getName())) {
				Map<String, Map<Category, Integer>> result = new HashMap<String, Map<Category, Integer>>();
				filename += "/lifecycle_process.csv";
				sql
						.append(
								"SELECT attr_two, action, count(*) FROM report_obligation_log, report_policy_activity_log")
						.append(
								" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
						.append(SITE_PREFIX)
						.append("%' AND time >= ? AND time <= ?")
						.append(" AND name = 'SPLOGACTIVITY'")
						.append(
								" AND (action = 'Op' OR action = 'Ed' OR action = 'Pr' OR action = 'Ex' OR action = 'Rn' OR action = 'Co' OR action = 'Mo' OR action = 'De')")
						.append(" GROUP BY attr_two, action ORDER BY attr_two");
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				File file = new File(filename);
				if (file.exists()) {
					if(!file.delete()) {
						log.warn("Unable to delete file " + filename + ", this file will be overwritten.");
					}
				}
				while (resultSet.next()) {
					String name = resultSet.getString(1);
					String action = resultSet.getString(2);
					int quantity = resultSet.getInt(3);
					Category category = getCategory(action);
					findAndAdd(result, name, category, quantity);
				}
				outputLifecycleFile(filename, result, 40);
			} else if (title
					.equals(SharePointReportType.INFORMATION_LIFECYCLE_SITE
							.getName())) {
				Map<String, Map<Category, Integer>> result = new HashMap<String, Map<Category, Integer>>();
				filename += "/lifecycle_site.csv";
				sql
						.append(
								"SELECT attr_three, action, count(*) FROM report_obligation_log LEFT JOIN report_policy_activity_log ON report_policy_activity_log.id = report_obligation_log.ref_log_id")
						.append(
								" WHERE log_level = 3 AND from_resource_name LIKE '")
						.append(SITE_PREFIX)
						.append("%' AND time >= ? AND time <= ?")
						.append(" AND name = 'SPLOGACTIVITY'")
						.append(
								" AND (action = 'Op' OR action = 'Ed' OR action = 'Pr' OR action = 'Ex' OR action = 'Rn' OR action = 'Co' OR action = 'Mo' OR action = 'De')")
						.append(" AND lower(attr_three) LIKE '")
						.append(getSiteURL().toLowerCase())
						.append(
								"%' GROUP BY attr_three, action ORDER BY attr_three");
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				while (resultSet.next()) {
					String name = resultSet.getString(1).toLowerCase();
					if (name == null || name.length() == 0) {
						continue;
					}
					String action = resultSet.getString(2);
					int quantity = resultSet.getInt(3);
					Category category = getCategory(action);

					String site = "";
					String url = getSiteURL();
					if (!url.endsWith("/")) {
						url = getSiteURL() + "/";
					}
					if (!name.endsWith("/")) {
						name = name + "/";
					}
					if (name.length() < getSiteURL().length()) {
						continue;
					}
					if (name.equals(url)) {
						site = "/";
						findAndAdd(result, site, category, quantity);
					} else if (name.startsWith(url)) {
						site = name.substring(url.length());
						int index = site.indexOf("/");
						site = "/" + site.substring(0, index);
						findAndAdd(result, site, category, quantity);
					}
				}
				outputLifecycleFile(filename, result, 40);
			} else if (title
					.equals(SharePointReportType.INFORMATION_LIFECYCLE_DEPARTMENT
							.getName())) {
				Map<String, Map<Category, Integer>> result = new HashMap<String, Map<Category, Integer>>();
				filename += "/lifecycle_department.csv";
				sql
						.append(
								"SELECT attr_one, action, count(*) FROM report_obligation_log, report_policy_activity_log")
						.append(
								" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
						.append(SITE_PREFIX)
						.append("%' AND time >= ? AND time <= ?")
						.append(" AND name = 'SPLOGACTIVITY'")
						.append(
								" AND (action = 'Op' OR action = 'Ed' OR action = 'Pr' OR action = 'Ex' OR action = 'Rn' OR action = 'Co' OR action = 'Mo' OR action = 'De')")
						.append(" GROUP BY attr_one, action ORDER BY attr_one");
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				while (resultSet.next()) {
					String name = resultSet.getString(1);
					String action = resultSet.getString(2);
					int quantity = resultSet.getInt(3);
					Category category = getCategory(action);
					findAndAdd(result, name, category, quantity);
				}
				outputLifecycleFile(filename, result, 40);
			} else if (title
					.equals(SharePointReportType.POLICY_ACTIVITY_DEPARTMENT
							.getName())) {
				Map<String, Integer> result = new HashMap<String, Integer>();
				filename += "/activity_department.csv";
				sql
						.append(
								"SELECT attr_one, count(*) FROM report_obligation_log, report_policy_activity_log")
						.append(
								" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
						.append(SITE_PREFIX).append(
								"%' AND time >= ? AND time <= ?").append(
								" AND name = 'SPLOGCONTROL'").append(
								" GROUP BY attr_one ORDER BY attr_one");
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				while (resultSet.next()) {
					String name = resultSet.getString(1).toLowerCase();
					int quantity = resultSet.getInt(2);
					findAndAdd(result, name, quantity);
				}
				outputActivityFile(filename, result, 40);
			} else if (title
					.equals(SharePointReportType.POLICY_ACTIVITY_PROCESS
							.getName())) {
				Map<String, Integer> result = new HashMap<String, Integer>();
				filename += "/activity_process.csv";
				sql
						.append(
								"SELECT attr_two, count(*) FROM report_obligation_log, report_policy_activity_log")
						.append(
								" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
						.append(SITE_PREFIX).append(
								"%' AND time >= ? AND time <= ?").append(
								" AND name = 'SPLOGCONTROL'").append(
								" GROUP BY attr_two ORDER BY attr_two");
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				File file = new File(filename);
				if (file.exists()) {
					if(!file.delete()) {
						log.warn("Unable to delete file " + filename + ", this file will be overwritten.");
					}
				}
				while (resultSet.next()) {
					String name = resultSet.getString(1).toLowerCase();
					int quantity = resultSet.getInt(2);
					findAndAdd(result, name, quantity);
				}
				outputActivityFile(filename, result, 10);
			} else if (title.equals(SharePointReportType.POLICY_ACTIVITY_POLICY
					.getName())) {
				Map<String, Integer> result = new HashMap<String, Integer>();
				filename += "/activity_policy.csv";
				sql
						.append(
								"SELECT cached_policy.name, count(*) FROM report_policy_activity_log, cached_policy, report_obligation_log")
						.append(
								" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND report_policy_activity_log.policy_id = cached_policy.id AND log_level = 3 AND from_resource_name LIKE '")
						.append(SITE_PREFIX)
						.append("%' AND time >= ? AND time <= ?")
						.append(
								" AND report_obligation_log.name = 'SPLOGCONTROL'")
						.append(
								" GROUP BY cached_policy.name ORDER BY cached_policy.name");
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				while (resultSet.next()) {
					String name = resultSet.getString(1).toLowerCase();
					int quantity = resultSet.getInt(2);
					findAndAdd(result, name, quantity);
				}
				outputActivityFile(filename, result, 10);
			} else if (title.equals(SharePointReportType.POLICY_ACTIVITY_SITE
					.getName())) {
				Map<String, Integer> result = new HashMap<String, Integer>();
				filename += "/activity_site.csv";
				sql
						.append(
								"SELECT attr_three, count(*) FROM report_obligation_log, report_policy_activity_log")
						.append(
								" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
						.append(SITE_PREFIX).append(
								"%' AND time >= ? AND time <= ?").append(
								" AND name = 'SPLOGCONTROL'").append(
								" AND lower(attr_three) LIKE '").append(
								getSiteURL().toLowerCase()).append(
								"%' GROUP BY attr_three ORDER BY attr_three");
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				while (resultSet.next()) {
					String name = resultSet.getString(1).toLowerCase();
					int quantity = resultSet.getInt(2);

					String site = "";
					String url = getSiteURL();
					if (!url.endsWith("/")) {
						url = getSiteURL() + "/";
					}
					if (!name.endsWith("/")) {
						name = name + "/";
					}
					if (name.length() < getSiteURL().length()) {
						continue;
					}
					if (name.equals(url)) {
						site = "/";
						findAndAdd(result, site, quantity);
					} else if (name.startsWith(url)) {
						site = name.substring(url.length());
						int index = site.indexOf("/");
						site = "/" + site.substring(0, index);
						findAndAdd(result, site, quantity);
					}
				}
				outputActivityFile(filename, result, 10);
			} else if (title.equals(SharePointReportType.POLICY_ACTIVITY_TREND
					.getName())) {
				List<Item> result = new ArrayList<Item>();
				filename += "/activity_trend.csv";
				if (getTimeDimension().equals("month")) {
					sql
							.append(
									"SELECT month_nb, count(*) FROM report_policy_activity_log, report_obligation_log")
							.append(
									" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
							.append(SITE_PREFIX).append(
									"%' AND time >= ? AND time <= ?").append(
									" AND name = 'SPLOGCONTROL'").append(
									" GROUP BY month_nb ORDER BY month_nb");
				} else if (getTimeDimension().equals("day")) {
					sql
							.append(
									"SELECT day_nb, count(*) FROM report_policy_activity_log, report_obligation_log")
							.append(
									" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
							.append(SITE_PREFIX).append(
									"%' AND time >= ? AND time <= ?").append(
									" AND name = 'SPLOGCONTROL'").append(
									" GROUP BY day_nb ORDER BY day_nb");
				}
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				while (resultSet.next()) {
					long time = resultSet.getLong(1);
					String name = "";
					Date now = new Date();
					now.setTime(time);
					if (getTimeDimension().equals("month")) {
						SimpleDateFormat format = new SimpleDateFormat(
								"MMM yyyy");
						name = format.format(now);
					} else if (getTimeDimension().equals("day")) {
						SimpleDateFormat format = new SimpleDateFormat(
								"MMM d yyyy");
						name = format.format(now);
					}
					int quantity = resultSet.getInt(2);
					findAndAdd(result, name, quantity);
				}
				outputFile(filename, result, false, -1);
			} else if (title.equals(SharePointReportType.POLICY_ACTIVITY_USER
					.getName())) {
				Map<String, Integer> result = new HashMap<String, Integer>();
				String department = getDepartment();
				if (department != null && department.length() > 0) {
					department = " AND lower(attr_one) LIKE '%"
							+ department.toLowerCase() + "%'";
				} else {
					department = "";
				}

				String process = getProcess();
				if (process != null && process.length() > 0) {
					process = " AND lower(attr_two) LIKE '%"
							+ process.toLowerCase() + "%'";
				} else {
					process = "";
				}

				filename += "/activity_user.csv";
				sql
						.append(
								"SELECT user_name, count(*) FROM report_policy_activity_log, report_obligation_log")
						.append(
								" WHERE report_policy_activity_log.id = report_obligation_log.ref_log_id AND log_level = 3 AND from_resource_name LIKE '")
						.append(SITE_PREFIX).append(
								"%' AND time >= ? AND time <= ?").append(
								" AND name = 'SPLOGCONTROL'")
						.append(department).append(process).append(
								" GROUP BY user_name ORDER BY user_name");
				PreparedStatement stmt = connection.prepareStatement(sql
						.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setTimestamp(1, beginTimestamp);
				stmt.setTimestamp(2, endTimestamp);
				resultSet = stmt.executeQuery();
				while (resultSet.next()) {
					String name = resultSet.getString(1).toLowerCase();
					int quantity = resultSet.getInt(2);
					findAndAdd(result, name, quantity);
				}
				outputActivityFile(filename, result, 10);
			}
		} catch (HibernateException e) {
			LOG.error("HibernateException", e);
		} catch (IOException e) {
			LOG.error("IOException", e);
		} catch (SQLException e) {
			LOG.error("SQLException", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					LOG.error("SQLException", e);
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (HibernateException e) {
					LOG.error("HibernateException", e);
				}
			}
		}
	}

	/**
	 * Output data file for the report.
	 * 
	 * @param filename
	 *            the file name for the data file
	 * @param result
	 *            the data set
	 * @param limit
	 *            limitation of output data row, -1 for no limitation
	 */
	private void outputLifecycleFile(String filename,
			final Map<String, Map<Category, Integer>> result, int limit)
			throws IOException {
		List<Item> list = new ArrayList<Item>();
		for (String key : result.keySet()) {
			Map<Category, Integer> value = result.get(key);
			Item item = new Item(key, Category.Consumption, value
					.get(Category.Consumption));
			list.add(item);
			item = new Item(key, Category.Modification, value
					.get(Category.Modification));
			list.add(item);
			item = new Item(key, Category.Storage, value.get(Category.Storage));
			list.add(item);
			item = new Item(key, Category.Deletion, value
					.get(Category.Deletion));
			list.add(item);
		}

		Collections.sort(list, new Comparator<Item>() {
			public int compare(Item o1, Item o2) {
				Integer quantity1 = result.get(o1.getName())
						.get(Category.Total);
				Integer quantity2 = result.get(o2.getName())
						.get(Category.Total);
				if (quantity1.equals(quantity2)) {
					return o2.getName().compareTo(o1.getName());
				}
				return quantity2.compareTo(quantity1);
			}
		});

		outputFile(filename, list, true, limit);
	}

	private void outputActivityFile(String filename,
			Map<String, Integer> result, int limit) throws IOException {
		List<Item> list = new ArrayList<Item>();
		for (String key : result.keySet()) {
			Integer value = result.get(key);
			Item item = new Item(key, value);
			list.add(item);
		}
		Collections.sort(list, new Comparator<Item>() {
			public int compare(Item o1, Item o2) {
				Integer quantity1 = o1.getQuantity();
				Integer quantity2 = o2.getQuantity();
				if (quantity1.equals(quantity2)) {
					return o2.getName().compareTo(o1.getName());
				}
				return quantity2.compareTo(quantity1);
			}
		});
		outputFile(filename, list, false, limit);
	}

	private void outputFile(String filename, List<Item> result,
			boolean isExtended, int limit) throws IOException {
		// make sure to delete the original data file first
		File file = new File(filename);
		if (file.exists()) {
			if(!file.delete()) {
				log.warn("Unable to delete file " + filename + ", this file will be overwritten.");
			}
		}

		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		if (isExtended) {
			out.write("Name,Category,Quantity\n");
		} else {
			out.write("Name,Quantity\n");
		}

		int size = result.size();
		int n = (limit == -1 ? size : Math.min(size, limit));
		for (int i = 0; i < n; i++) {
			Item item = result.get(i);
			StringBuffer output = new StringBuffer(item.getName());
			output.append(',');
			if (isExtended) {
				output.append(item.getCategory().name());
				output.append(',');
			}
			if (item.getQuantity() != 0) {
				output.append(item.getQuantity());
			}
			output.append('\n');
			out.write(output.toString());
		}
		out.close();
	}

	/**
	 * find item in the list, if found, aggregate the quantity to the existing
	 * value; if not, create a new item and assign the value to it
	 */
	private void findAndAdd(Map<String, Integer> result, String name,
			int quantity) {
		name = name.toLowerCase();
		Integer value = result.get(name);
		if (value == null) {
			result.put(name, quantity);
		} else {
			result.put(name, value + quantity);
		}
	}

	/**
	 * find item in the list, if found, aggregate the quantity to the existing
	 * value; if not, create a new item and assign the value to it
	 */
	private void findAndAdd(Map<String, Map<Category, Integer>> result,
			String name, Category category, int quantity) {
		if (name == null) {
			name = "unknown";
		}
		name = name.toLowerCase();
		Map<Category, Integer> categories = result.get(name);
		if (categories == null) {
			Map<Category, Integer> items = new HashMap<Category, Integer>();

			items.put(Category.Consumption,
					category == Category.Consumption ? quantity : 0);
			items.put(Category.Storage, category == Category.Storage ? quantity
					: 0);
			items.put(Category.Modification,
					category == Category.Modification ? quantity : 0);
			items.put(Category.Deletion,
					category == Category.Deletion ? quantity : 0);
			items.put(Category.Total, quantity);

			result.put(name, items);
		} else {
			categories.put(category, categories.get(category) + quantity);
			categories.put(Category.Total, categories.get(Category.Total)
					+ quantity);
		}
	}

	/**
	 * find item in the list, if found, aggregate the quantity to the existing
	 * value; if not, create a new item and assign the value to it
	 */
	private void findAndAdd(List<Item> result, String name, Category category,
			int quantity) {
		int index = -1;
		for (int i = 0, n = result.size(); i < n; i++) {
			Item item = result.get(i);
			if (item.getName().equals(name) && item.getCategory() == category) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			Item item = result.get(index);
			item.setQuantity(item.getQuantity() + quantity);
			result.set(index, item);
		} else {
			Item item = new Item(name, Category.Consumption, 0);
			result.add(item);

			item = new Item(name, Category.Storage, 0);
			result.add(item);

			item = new Item(name, Category.Modification, 0);
			result.add(item);

			item = new Item(name, Category.Deletion, 0);
			result.add(item);

			findAndAdd(result, name, category, quantity);
		}
	}

	private void findAndAdd(List<Item> result, String name, int quantity) {
		Item item = new Item(name, quantity);
		result.add(item);
	}
}
