/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 14, 2020
 *
 */
package com.nextlabs.destiny.console.model.scim;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bettercloud.scim2.common.BaseScimResource;
import com.bettercloud.scim2.common.annotations.Attribute;
import com.bettercloud.scim2.common.annotations.Schema;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing a SCIM 2 bulk request.
 */
@Schema(id = "urn:ietf:params:scim:api:messages:2.0:BulkRequest", name = "Bulk Request",
        description = "SCIM 2.0 Bulk Request")
public class BulkRequest
    extends BaseScimResource
        implements Iterable<BulkOperation> {

    private static final long serialVersionUID = -9009893580829308825L;
    private static final Logger log = LoggerFactory.getLogger(BulkRequest.class);

    @Attribute(
            description = "This attribute defines the number of errors that should be accepted before failing the remaining operations returning the response")
    @JsonProperty(value = "failOnErrors", required = false)
    private final int failErrorsNum;

    @Attribute(description = "Bulk Operations")
    @JsonProperty(value = "Operations", required = true)
    private final List<BulkOperation> operations;

    /**
     * Create a new Bulk Operation Request.
     *
     * @param operations The list of operations to include.
     */
    @JsonCreator
    public BulkRequest(@JsonProperty(value = "failOnErrors", required = false)
    final int failOnErrors, @JsonProperty(value = "Operations", required = true)
    final List<BulkOperation> operations) {
        this.failErrorsNum = failOnErrors;
        this.operations = Collections.unmodifiableList(operations);
    }

    /**
     * Retrieves all the individual operations in this bulk request.
     *
     * @return The individual operations in this bulk request.
     */
    public List<BulkOperation> getOperations() {
        return Collections.unmodifiableList(operations);
    }

    public int getFailErrorsNum() {
        return failErrorsNum;
    }

    @Override
    public Iterator<BulkOperation> iterator() {
        return getOperations().iterator();
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

        BulkRequest that = (BulkRequest) o;
        
        if (failErrorsNum != that.failErrorsNum) {
            return false;
        }

        return operations.equals(that.operations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + failErrorsNum;
        result = 31 * result + operations.hashCode();
        return result;
    }
}
