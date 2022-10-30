/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 4, 2016
 *
 */
package com.nextlabs.destiny.console.model.delegadmin;

import java.util.LinkedList;
import java.util.List;

import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.enums.Operator;

/**
 *
 * Data model for Delegation rules applicable tags
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ApplicableTag {

    private Operator operator;
    private List<TagDTO> tags;

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public List<TagDTO> getTags() {
        if (tags == null) {
            tags = new LinkedList<>();
        }
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

}
