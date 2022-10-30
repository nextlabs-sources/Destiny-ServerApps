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
@Table(name = EnumGroup.ENUM_GROUPS_TABLE)
public class EnumGroup {
	public static final String ENUM_GROUPS_TABLE = "DICT_ENUM_GROUPS";
	
	
	@Id
	@Column(name="element_id")
	private Long elementId;

	public Long getElementId() {
		return elementId;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}
	
	
}
