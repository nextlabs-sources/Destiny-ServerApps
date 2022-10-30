/*
 * Created on Jun 12, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.sharepoint.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bluejungle.destiny.inquirycenter.enumeration.SharePointReportType;

/**
 * <p>
 * Enum class to SharePointReportTypeEnum
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public enum SharePointReportTypeEnum {

	INFOR_LIFECYCLE_DEPARTMENT(
			"SharePoint Information Lifecycle - Department",
			"SharePoint Information Lifecycle by Department",
			"Displays a summary of tracked information lifecycle activity for each department over the specified time period.",
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie")), 
			
	INFOR_LIFECYCLE_PROCESS(
			"SharePoint Information Lifecycle - Process",
			"SharePoint Information Lifecycle by Business Process",
			"Displays a summary of tracked information lifecycle activity for each business process over the specified time period.",
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie")), 
			
	INFOR_LIFECYCLE_SITE(
			"SharePoint Information Lifecycle - Site",
			"SharePoint Information Lifecycle by Site",
			"Displays a summary of tracked information lifecycle activity with each SharePoint Site contained within the specified Site over the specified time period.",
			new DynamicField("siteUrl", "Site URL", DynamicField.FieldType.TEXT),
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie")), 
			
	INFOR_LIFECYCLE_TREND(
			"SharePoint Information Lifecycle - Trend",
			"SharePoint Information Lifecycle Trend Analysis",
			"Displays a summary of tracked information lifecycle activity over the specified time period.",
			new DynamicField("frequency", "Frequency", DynamicField.FieldType.DROP_DOWN, "Monthly", "Daily"),
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie")),
			
	INFOR_LIFECYCLE_USER(
			"SharePoint Information Lifecycle - User",
			"SharePoint Information Lifecycle by User",
			"Displays a summary of tracked information lifecycle activity within the specified department or business process for each user over the specified time period.",
			new DynamicField("department", "Department", DynamicField.FieldType.TEXT), 
			new DynamicField("process", "Process", DynamicField.FieldType.TEXT),
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie")), 
			
	PLC_ACTIVITY_DEPARTMENT(
			"SharePoint Policy Activity - Department",
			 "SharePoint Policy Activity by Department",
			"Displays a summary of tracked policy activity, grouped by department, over the specified time period.",
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie")),
	
	PLC_ACTIVITY_PROCESS(
			"SharePoint Policy Activity - Process",
			"SharePoint Policy Activity by Business Process",
			"Displays a summary of tracked policy activity, grouped by business process, over the specified time period.",
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie")), 
			
	PLC_ACTIVITY_POLICY(
			"SharePoint Policy Activity - Policy",
			"SharePoint Policy Activity by Policy",
			"Displays a summary of tracked policy activity, grouped by policy, over the specified time period.",
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie")),
	
	PLC_ACTIVITY_SITE(
			"SharePoint Policy Activity - Site",
			"SharePoint Policy Activity by Site",
			"Displays a summary of tracked policy activity, grouped by site, contained within the specified site over the specified time period.",
			new DynamicField("siteUrl", "Site URL", DynamicField.FieldType.TEXT),
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie")), 
			
	PLC_ACTIVITY_TREND(
			"SharePoint Policy Activity - Trend",
			"SharePoint Policy Activity Trend Analysis",
			"Displays a trend analysis of tracked policy activity over the specified time period.",
			new DynamicField("frequency", "Frequency",  DynamicField.FieldType.DROP_DOWN, "Monthly", "Daily"),
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie")), 
			
	PLC_ACTIVITY_USER(
			"SharePoint Policy Activity - User",
			"SharePoint Policy Activity by User",
			"Displays a summary of tracked policy activity within the specified department or business process for each user over the specified time period.",
			new DynamicField("department", "Department", DynamicField.FieldType.TEXT),
			new DynamicField("process", "Process", DynamicField.FieldType.TEXT),
			new DynamicField("chartType", "Chart Type", DynamicField.FieldType.DROP_DOWN, "Bar", "Pie"));
	
	private String name;
	private SharePointReportType legacyType;
	private String title;
	private String desc;
	private List<DynamicField> fields;

	private SharePointReportTypeEnum(String name,
			String title, String desc,
			DynamicField... fields) {
		this.name = name;
		this.title = title;
		this.desc = desc;

		if (fields != null && fields.length > 0)
			this.fields = Arrays.asList(fields);
	}

	public String getName() {
		return name;
	}

	public SharePointReportType getLegacyType() {
		return legacyType;
	}

	public String getTitle() {
		return title;
	}

	public String getDesc() {
		return desc;
	}

	public List<DynamicField> getFields() {
		if(fields == null) {
			fields = new ArrayList<DynamicField>();
		}
		
		return fields;
	}
	
	public static SharePointReportTypeEnum getReportTypeByName(String reportName) {

		for (SharePointReportTypeEnum type : SharePointReportTypeEnum.values()) {
			if (type.getName().equals(reportName)) {
				return type;
			}
		}
		return null;
	}

}
