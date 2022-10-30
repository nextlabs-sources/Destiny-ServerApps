/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.nextlabs.destiny.console.dto.common.MultiFieldValuesDTO;
import com.nextlabs.destiny.console.dto.common.SinglevalueFieldDTO;
import com.nextlabs.destiny.console.model.TagLabel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * DTO for Search Fields Label, reference entity {@link TagLabel}
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class SearchFieldsDTO implements Serializable {

    private static final long serialVersionUID = 3187686923655598100L;

    @ApiModelProperty(hidden = true)
    private SinglevalueFieldDTO type;

    @ApiModelProperty(hidden = true)
    private List<MultiFieldValuesDTO> typeOptions;

    @ApiModelProperty(value = "Status field.")
    private SinglevalueFieldDTO status;

    @ApiModelProperty(value = "Options for status field.")
    private List<MultiFieldValuesDTO> statusOptions;

    @ApiModelProperty(value = "Workflow status field.")
    private SinglevalueFieldDTO workflowStatus;

    @ApiModelProperty(value = "Options for workflow status field.")
    private List<MultiFieldValuesDTO> workflowStatusOptions;

    @ApiModelProperty(value = "Field for policy effect. Applicable only for policy search.")
    private SinglevalueFieldDTO policyEffect;

    @ApiModelProperty(value = "Options for policy effect.")
    private List<MultiFieldValuesDTO> policyEffectOptions;

    @ApiModelProperty(value = "Field for modified date.")
    private SinglevalueFieldDTO modifiedDate;

    @ApiModelProperty(value = "Options for modified date.")
    private List<MultiFieldValuesDTO> modifiedDateOptions;

    @ApiModelProperty(hidden = true)
    private List<MultiFieldValuesDTO> moreFieldOptions;

    @ApiModelProperty(value = "Field for sort by.")
    private SinglevalueFieldDTO sort;

    @ApiModelProperty(value = "Options for sort by.")
    private List<MultiFieldValuesDTO> sortOptions;

    @ApiModelProperty(value = "Field for tags. Available tags are queried realtime and listed.")
    private SinglevalueFieldDTO tags; // dynamic multi-field values

    @ApiModelProperty(value = "Boolean value to indicate whether to include sub policies in the search results.")
    private SinglevalueFieldDTO subPolicySearch;

    @ApiModelProperty(value = "Enrollment field.")
    private SinglevalueFieldDTO enrollment;

    @ApiModelProperty(value = "Options for enrollment field.")
    private List<MultiFieldValuesDTO> enrollmentOptions;

    public SinglevalueFieldDTO getStatus() {
        return status;
    }

    public void setStatus(SinglevalueFieldDTO status) {
        this.status = status;
    }

    public List<MultiFieldValuesDTO> getStatusOptions() {
        if (statusOptions == null)
            statusOptions = new ArrayList<>();
        return statusOptions;
    }

    public void setStatusOptions(List<MultiFieldValuesDTO> statusOptions) {
        this.statusOptions = statusOptions;
    }

    public SinglevalueFieldDTO getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(SinglevalueFieldDTO workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public List<MultiFieldValuesDTO> getWorkflowStatusOptions() {
        if (workflowStatusOptions == null) {
            workflowStatusOptions = new ArrayList<>();
        }
        return workflowStatusOptions;
    }

    public void setWorkflowStatusOptions(List<MultiFieldValuesDTO> workflowStatusOptions) {
        this.workflowStatusOptions = workflowStatusOptions;
    }

    public SinglevalueFieldDTO getPolicyEffect() {
        return policyEffect;
    }

    public void setPolicyEffect(SinglevalueFieldDTO policyEffect) {
        this.policyEffect = policyEffect;
    }

    public List<MultiFieldValuesDTO> getPolicyEffectOptions() {
        if (policyEffectOptions == null)
            policyEffectOptions = new ArrayList<>();
        return policyEffectOptions;
    }

    public void setPolicyEffectOptions(
            List<MultiFieldValuesDTO> policyEffectOptions) {
        this.policyEffectOptions = policyEffectOptions;
    }

    public SinglevalueFieldDTO getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(SinglevalueFieldDTO modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public List<MultiFieldValuesDTO> getModifiedDateOptions() {
        if (modifiedDateOptions == null)
            modifiedDateOptions = new ArrayList<>();
        return modifiedDateOptions;
    }

    public void setModifiedDateOptions(
            List<MultiFieldValuesDTO> modifiedDateOptions) {
        this.modifiedDateOptions = modifiedDateOptions;
    }

    public List<MultiFieldValuesDTO> getMoreFieldOptions() {
        if (moreFieldOptions == null)
            moreFieldOptions = new ArrayList<>();
        return moreFieldOptions;
    }

    public void setMoreFieldOptions(
            List<MultiFieldValuesDTO> moreFieldOptions) {
        this.moreFieldOptions = moreFieldOptions;
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

    public SinglevalueFieldDTO getSubPolicySearch() {
        return subPolicySearch;
    }

    public void setSubPolicySearch(SinglevalueFieldDTO subPolicySearch) {
        this.subPolicySearch = subPolicySearch;
    }

    public SinglevalueFieldDTO getTags() {
        return tags;
    }

    public void setTags(SinglevalueFieldDTO tags) {
        this.tags = tags;
    }

    public SinglevalueFieldDTO getType() {
        return type;
    }

    public void setType(SinglevalueFieldDTO type) {
        this.type = type;
    }

    public List<MultiFieldValuesDTO> getTypeOptions() {
        if (typeOptions == null)
            typeOptions = new ArrayList<>();
        return typeOptions;
    }

    public void setTypeOptions(List<MultiFieldValuesDTO> typeOptions) {
        this.typeOptions = typeOptions;
    }

    public SinglevalueFieldDTO getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(SinglevalueFieldDTO enrollment) {
        this.enrollment = enrollment;
    }

    public List<MultiFieldValuesDTO> getEnrollmentOptions() {
        if(enrollmentOptions == null) {
            enrollmentOptions = new ArrayList<>();
        }
        return enrollmentOptions;
    }

    public void setEnrollmentOptions(List<MultiFieldValuesDTO> enrollmentOptions) {
        this.enrollmentOptions = enrollmentOptions;
    }

}
