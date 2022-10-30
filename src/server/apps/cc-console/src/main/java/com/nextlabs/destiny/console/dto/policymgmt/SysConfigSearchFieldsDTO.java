/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 24, 2019
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.nextlabs.destiny.console.dto.common.MultiFieldValuesDTO;
import com.nextlabs.destiny.console.dto.common.SinglevalueFieldDTO;

/**
 * 
 * @author Moushumi Seal
 *
 */
public class SysConfigSearchFieldsDTO implements Serializable {
	
    private static final long serialVersionUID = 3187686923655598100L;

    private SinglevalueFieldDTO group;
    private List<MultiFieldValuesDTO> groupOptions;

    private SinglevalueFieldDTO sort;
    private List<MultiFieldValuesDTO> sortOptions;

    public SinglevalueFieldDTO getGroup() {
		return group;
	}

	public void setGroup(SinglevalueFieldDTO group) {
		this.group = group;
	}

	public List<MultiFieldValuesDTO> getGroupOptions() {
		if (groupOptions == null)
			groupOptions = new ArrayList<>();
		return groupOptions;
	}

	public void setGroupOptions(List<MultiFieldValuesDTO> groupOptions) {
		this.groupOptions = groupOptions;
	}

    public SinglevalueFieldDTO getSort() {
        return sort;
    }

    public void setSort(SinglevalueFieldDTO sort) {
        this.sort = sort;
    }

    public List<MultiFieldValuesDTO> getSortOptions() {
        if (sortOptions == null)
            sortOptions = new ArrayList<>();
        return sortOptions;
    }

    public void setSortOptions(List<MultiFieldValuesDTO> sortOptions) {
        this.sortOptions = sortOptions;
    }
}
