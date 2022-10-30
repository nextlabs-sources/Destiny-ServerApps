/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 24 Aug 2016
 *
 */
package com.nextlabs.destiny.console.dto.authentication.activedirectory;

import java.io.Serializable;

/**
 * AdSearchRequest DTO
 * 
 * @author Amila Silva
 *
 */
public class AdSearchRequest implements Serializable {

	private Long handlerId;
	private int pageSize;
	private String searchText;

	public Long getHandlerId() {
		return handlerId;
	}

	public void setHandlerId(Long handlerId) {
		this.handlerId = handlerId;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

}
