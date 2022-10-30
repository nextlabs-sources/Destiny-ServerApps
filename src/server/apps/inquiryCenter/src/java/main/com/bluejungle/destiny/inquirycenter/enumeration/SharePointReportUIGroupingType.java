package com.bluejungle.destiny.inquirycenter.enumeration;

import com.bluejungle.framework.patterns.EnumBase;

public class SharePointReportUIGroupingType extends EnumBase {
	private static final long serialVersionUID = 1L;

	public static final SharePointReportUIGroupingType GROUP_SITE = new SharePointReportUIGroupingType(
			"Site");
	public static final SharePointReportUIGroupingType GROUP_DEPARTMENT = new SharePointReportUIGroupingType(
			"Department");
	public static final SharePointReportUIGroupingType GROUP_BUSINESS_PROCESS = new SharePointReportUIGroupingType(
			"Business Process");
	public static final SharePointReportUIGroupingType GROUP_USER = new SharePointReportUIGroupingType(
			"User");
	public static final SharePointReportUIGroupingType GROUP_TIME = new SharePointReportUIGroupingType(
			"Time");

	protected SharePointReportUIGroupingType(String name) {
		super(name);
	}
}
