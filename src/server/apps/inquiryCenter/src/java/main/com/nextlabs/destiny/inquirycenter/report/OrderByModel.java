/*
 * Created on Jun 4, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report;

/**
 * <p>
 * OrderByModel
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public class OrderByModel {

	private String columnName;
	private String sortOrder;

	public OrderByModel() {
		super();
	}

	public OrderByModel(String columnName, String sortOrder) {
		super();
		this.columnName = columnName;
		this.sortOrder = sortOrder;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

}
