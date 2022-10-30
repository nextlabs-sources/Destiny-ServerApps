/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 24 Aug 2016
 *
 */
package com.nextlabs.destiny.console.dto.authentication.activedirectory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.nextlabs.destiny.console.dto.common.ExternalGroupDTO;
import com.nextlabs.destiny.console.dto.common.ExternalUserDTO;

/**
 * AdSearchResponse
 *
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class AdSearchResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ExternalUserDTO> adUsers;
	private List<ExternalGroupDTO> adGroups;
	private boolean hasMoreResults = false;

	public List<ExternalUserDTO> getAdUsers() {
		if (adUsers == null) {
			adUsers = new ArrayList<>();
		}
		return adUsers;
	}

	public void setAdUsers(List<ExternalUserDTO> adUsers) {
		this.adUsers = adUsers;
	}

	public List<ExternalGroupDTO> getAdGroups() {
		if(adGroups == null) {
			adGroups = new ArrayList<>();
		}

		return adGroups;
	}

	public void setAdGroups(List<ExternalGroupDTO> adGroups) {
		this.adGroups = adGroups;
	}

	public boolean isHasMoreResults() {
		return hasMoreResults;
	}

	public void setHasMoreResults(boolean hasMoreResults) {
		this.hasMoreResults = hasMoreResults;
	}

}
