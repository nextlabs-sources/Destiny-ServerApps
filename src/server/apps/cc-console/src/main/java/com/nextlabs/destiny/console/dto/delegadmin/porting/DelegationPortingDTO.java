/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 25, 2016
 *
 */
package com.nextlabs.destiny.console.dto.delegadmin.porting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nextlabs.destiny.console.dto.delegadmin.DelegationRuleDTO;

/**
 *
 * DTO for Delegation Rule and related components porting
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DelegationPortingDTO implements Serializable {

	private static final long serialVersionUID = 5885578319574413567L;

	private List<DelegationRuleDTO> delegateRules;
	private List<Map<Long, String>> delegateComponents;
	private List<Map<Long, String>> delegateModels;

	public List<DelegationRuleDTO> getDelegateRules() {
		if (delegateRules == null) {
			delegateRules = new ArrayList<>();
		}
		return delegateRules;
	}

	public void setDelegateRules(List<DelegationRuleDTO> delegateRules) {
		this.delegateRules = delegateRules;
	}

	public List<Map<Long, String>> getDelegateComponents() {
		if (delegateComponents == null) {
			delegateComponents = new ArrayList<>();
		}
		return delegateComponents;
	}

	public void setDelegateComponents(List<Map<Long, String>> delegateComponents) {
		this.delegateComponents = delegateComponents;
	}

	public List<Map<Long, String>> getDelegateModels() {
		if (delegateModels == null) {
			delegateModels = new ArrayList<>();
		}
		return delegateModels;
	}

	public void setDelegateModels(List<Map<Long, String>> delegateModels) {
		this.delegateModels = delegateModels;
	}

	@Override
	public String toString() {
		return String.format("DelegationPortingDTO [delegationRuleDTOs=%s, delegateComponents=%s, delegateModels=%s]",
				this.delegateRules.size(), this.delegateComponents.size(), this.delegateModels.size());
	}

}
