/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 29, 2016
 *
 */
package com.nextlabs.destiny.console.dto.delegadmin;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.enums.Operator;

/**
 * Tag filter model
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class TagsFilter implements Serializable {

    private static final long serialVersionUID = 6721682963464218834L;

    private Operator operator;
    private Set<TagDTO> tags;

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Set<TagDTO> getTags() {
        if (tags == null) {
            tags = new TreeSet<>();
        }
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return String.format("TagsFilter [operator=%s, tags=%s]", operator,
                tags);
    }

}
