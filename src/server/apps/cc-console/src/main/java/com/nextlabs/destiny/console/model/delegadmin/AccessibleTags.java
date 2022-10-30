/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 4, 2016
 *
 */
package com.nextlabs.destiny.console.model.delegadmin;

import java.util.ArrayList;
import java.util.List;

import com.nextlabs.destiny.console.enums.DelegationModelShortName;

/**
 *
 * Delegation rule accessible tags
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class AccessibleTags {

    private DelegationModelShortName obligationTagType;
    private List<ObligationTag> tags;

    /**
     * 
     */
    public AccessibleTags() {
    }

    /**
     * constructor with obligation tag type
     * 
     * @param obligationTagType
     */
    public AccessibleTags(DelegationModelShortName obligationTagType) {
        this.obligationTagType = obligationTagType;
    }

    public DelegationModelShortName getObligationTagType() {
        return obligationTagType;
    }

    public void setObligationTagType(
            DelegationModelShortName obligationTagType) {
        this.obligationTagType = obligationTagType;
    }

    public List<ObligationTag> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public void setTags(List<ObligationTag> tags) {
        this.tags = tags;
    }

}
