/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 13, 2015
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 *
 * DTO for Policy search criteria
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchCriteriaDTO implements Serializable {

    private static final long serialVersionUID = -37371187469002098L;

    private SearchCriteria criteria;

    @NotNull
    @ApiModelProperty(value = "SearchCriteria")
    public SearchCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(SearchCriteria criteria) {
        this.criteria = criteria;
    }

}
