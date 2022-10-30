package com.bluejungle.destiny.inquirycenter.enumeration;

import com.bluejungle.framework.patterns.EnumBase;

public class SharePointReportType extends EnumBase {
	private static final long serialVersionUID = 5062713075042897113L;

	public static final SharePointReportType INFORMATION_LIFECYCLE_DEPARTMENT = new SharePointReportType(
			"SharePoint Information Lifecycle - Department");
	public static final SharePointReportType INFORMATION_LIFECYCLE_PROCESS = new SharePointReportType(
			"SharePoint Information Lifecycle - Process");
	public static final SharePointReportType INFORMATION_LIFECYCLE_SITE = new SharePointReportType(
			"SharePoint Information Lifecycle - Site");
	public static final SharePointReportType INFORMATION_LIFECYCLE_TREND = new SharePointReportType(
			"SharePoint Information Lifecycle - Trend");
	public static final SharePointReportType INFORMATION_LIFECYCLE_USER = new SharePointReportType(
			"SharePoint Information Lifecycle - User");
	public static final SharePointReportType POLICY_ACTIVITY_DEPARTMENT = new SharePointReportType(
			"SharePoint Policy Activity - Department");
	public static final SharePointReportType POLICY_ACTIVITY_PROCESS = new SharePointReportType(
			"SharePoint Policy Activity - Process");
	public static final SharePointReportType POLICY_ACTIVITY_POLICY = new SharePointReportType(
			"SharePoint Policy Activity - Policy");
	public static final SharePointReportType POLICY_ACTIVITY_SITE = new SharePointReportType(
			"SharePoint Policy Activity - Site");
	public static final SharePointReportType POLICY_ACTIVITY_TREND = new SharePointReportType(
			"SharePoint Policy Activity - Trend");
	public static final SharePointReportType POLICY_ACTIVITY_USER = new SharePointReportType(
			"SharePoint Policy Activity - User");

	protected SharePointReportType(String name) {
		super(name);
	}
}
