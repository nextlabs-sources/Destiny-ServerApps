/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 15, 2020
 *
 */
package com.nextlabs.destiny.console.model.scim;

import java.util.Collections;
import java.util.List;

import com.bettercloud.scim2.common.BaseScimResource;
import com.bettercloud.scim2.common.annotations.Attribute;
import com.bettercloud.scim2.common.annotations.Schema;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Schema(id = "urn:ietf:params:scim:api:messages:2.0:BulkResponse", name = "Bulk Response",
        description = "SCIM 2.0 Bulk Response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkResponse extends BaseScimResource {

    private static final long serialVersionUID = 92904145944809446L;

    @Attribute(description = "Bulk Operations")
    @JsonProperty(value = "Operations", required = true)
    private List<BulkResponseOperation> operations;


    @JsonCreator
    public BulkResponse(@JsonProperty(value = "Operations", required = true)
    final List<BulkResponseOperation> operations) {
        this.operations = Collections.unmodifiableList(operations);
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BulkResponse that = (BulkResponse) o;
        return !operations.equals(that.operations);
            
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + operations.hashCode();
        return result;
    }
}
