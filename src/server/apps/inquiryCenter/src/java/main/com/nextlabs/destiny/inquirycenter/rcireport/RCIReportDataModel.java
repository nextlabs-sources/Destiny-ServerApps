/*
 * Created on Jun 18, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.rcireport;

import com.nextlabs.destiny.inquirycenter.customapps.CustomAppJO;

/**
 * <p>
 * RCIReportDataModel
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public class RCIReportDataModel {

	private int key;
	private String title;
	private String description;
	private CustomAppJO customAppJO;

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CustomAppJO getCustomAppJO() {
		return customAppJO;
	}

	public void setCustomAppJO(CustomAppJO customAppJO) {
		this.customAppJO = customAppJO;
	}

}
