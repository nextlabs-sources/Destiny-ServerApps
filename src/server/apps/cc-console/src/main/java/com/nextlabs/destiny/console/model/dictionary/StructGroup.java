/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.model.dictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = StructGroup.STRUCT_GROUPS_TABLE)
public class StructGroup {
	public static final String STRUCT_GROUPS_TABLE = "DICT_STRUCT_GROUPS";
	
	@Id
	@Column(name = "element_id")
	private Long elementId;
	
	private String filter;
	
	private int filterLength;

	public Long getElementId() {
		return elementId;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public int getFilterLength() {
		return filterLength;
	}

	public void setFilterLength(int filterLength) {
		this.filterLength = filterLength;
	}
	
	
}
