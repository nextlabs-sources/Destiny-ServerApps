/*
 *  All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

public class GroupByData {

	private static final String NONE_FOUND = "NULL";
	private String dimension;
	private String category;
	private int resultCount;

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		// should not happen, but if it does happen replace with dummy str
		if (dimension == null) {
			dimension = NONE_FOUND;
		}
		this.dimension = dimension;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		if (category == null) {
			category = NONE_FOUND;
		}
		this.category = category;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
}
