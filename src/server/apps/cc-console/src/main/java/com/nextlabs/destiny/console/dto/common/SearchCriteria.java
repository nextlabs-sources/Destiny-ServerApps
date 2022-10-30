/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 13, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;

/**
 *
 * Common Search Criteria model
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchCriteria implements Serializable {

    private static final long serialVersionUID = 8538516161216104795L;

    @ApiModelProperty(value = "Search fields.")
    private List<SearchField> fields;

    @ApiModelProperty(value = "Sort fields.")
    private List<SortField> sortFields;

    @ApiModelProperty(value = "Columns to search for.", example = "title")
    private List<String> columns;

    @ApiModelProperty(value = "Facet field.", example = "type")
    private String facetField;

    @Min(0)
    @ApiModelProperty(value = "Current page number.", example="0")
    private int pageNo = 0;

    @Min(0)
    @ApiModelProperty(value = "Count of policies returned.", example="10")
    private int pageSize = 10;

    public List<SearchField> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<SearchField> fields) {
        this.fields = fields;
    }

    public List<SortField> getSortFields() {
        if (sortFields == null) {
            sortFields = new ArrayList<>();
        }
        return sortFields;
    }

    public void setSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
    }

    public List<String> getColumns() {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getFacetField() {
        return facetField;
    }

    public void setFacetField(String facetField) {
        this.facetField = facetField;
    }

    @Override
    public String toString() {
        return String.format(
                "SearchCriteria [fields=%s, sortFields=%s, columns=%s, pageNo=%s, pageSize=%s]",
                fields, sortFields, columns, pageNo, pageSize);
    }
}
