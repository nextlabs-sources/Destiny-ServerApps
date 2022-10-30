/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 14, 2020
 *
 */
package com.nextlabs.destiny.console.model.scim;

import com.bettercloud.scim2.common.exceptions.BadRequestException;
import com.bettercloud.scim2.common.exceptions.ScimException;
import com.bettercloud.scim2.common.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "method",
        visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = BulkOperation.PostOperation.class, name = "POST"),
        @JsonSubTypes.Type(value = BulkOperation.PutOperation.class, name = "PUT"),
        @JsonSubTypes.Type(value = BulkOperation.PatchOperation.class, name = "PATCH"),
        @JsonSubTypes.Type(value = BulkOperation.DeleteOperation.class, name = "DELETE")})
public abstract class BulkOperation {

    private BulkMethodType method;
    private final String bulkId;
    private final String path;
    private final JsonNode data;



    static final class PostOperation extends BulkOperation {
        @JsonCreator
        private PostOperation(@JsonProperty(value = "bulkId", required = true)
        final String bulkId, @JsonProperty(value = "path", required = true)
        final String path, @JsonProperty(value = "data", required = true)
        final JsonNode data) throws ScimException {
            super(bulkId, path, data);

            if (bulkId == null || bulkId.isEmpty() || path == null || path.isEmpty()) {
                throw BadRequestException.invalidSyntax("bulkId field must not be null");
            }


            if (data == null || data.isNull()
                    || ((data.isArray() || data.isObject()) && data.size() == 0)) {
                throw BadRequestException
                        .invalidSyntax("data field must not be null or an empty container");
            }
        }
    }
    
    static final class PutOperation extends BulkOperation {

        @JsonCreator
        private PutOperation(@JsonProperty(value = "bulkId")
        final String bulkId, @JsonProperty(value = "path", required = true)
        final String path, @JsonProperty(value = "data", required = true)
        final JsonNode data) throws ScimException {
            super(bulkId, path, data);

            if (path == null || path.isEmpty()) {
                throw BadRequestException
                        .invalidSyntax("bulkId field must not be null");
            }

            if (data == null || data.isNull()
                    || ((data.isArray() || data.isObject()) && data.size() == 0)) {
                throw BadRequestException
                        .invalidSyntax("data field must not be null or an empty container");
            }
        }
    }

    static final class PatchOperation extends BulkOperation {

        @JsonCreator
        private PatchOperation(@JsonProperty(value = "bulkId")
        final String bulkId, @JsonProperty(value = "path", required = true)
        final String path, @JsonProperty(value = "data", required = true)
        final JsonNode data) throws ScimException {
            super(bulkId, path, data);

            if (path == null || path.isEmpty()) {
                throw BadRequestException.invalidSyntax("bulkId field must not be null");
            }

            if (data == null || data.isNull()
                    || ((data.isArray() || data.isObject()) && data.size() == 0)) {
                throw BadRequestException
                        .invalidSyntax("data field must not be null or an empty container");
            }
        }
    }

    static final class DeleteOperation extends BulkOperation {

        @JsonCreator
        private DeleteOperation(@JsonProperty(value = "bulkId")
        final String bulkId, @JsonProperty(value = "path", required = true)
        final String path, @JsonProperty(value = "data")
        final JsonNode data) throws ScimException {
            super(bulkId, path, data);

            if (path == null || path.isEmpty()) {
                throw BadRequestException.invalidSyntax("bulkId field must not be null");
            }

        }
    }

    public BulkOperation(final String bulkId, final String path,
            final JsonNode data) {
        this.bulkId = bulkId;
        this.path = path;
        this.data = data;
    }

    public BulkMethodType getMethod() {
        return method;
    }

    public void setMethod(BulkMethodType method) {
        this.method = method;
    }

    public String getBulkId() {
        return bulkId;
    }

    
    public String getPath() {
        return path;
    }

    public JsonNode getData() {
        return data;
    }
    
    public <T> T getData(final Class<T> cls) throws JsonProcessingException {
        if (data.isArray()) {
            throw new IllegalArgumentException("Bulk data contains " + "multiple values");
        }
        return JsonUtils.getObjectReader().treeToValue(data, cls);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BulkOperation that = (BulkOperation) o;
        if (getBulkId() != null ? !getBulkId().equals(that.getBulkId())
                : that.getBulkId() != null) {
            return false;
        }

        if (getPath() != null ? !getPath().equals(that.getPath()) : that.getPath() != null) {
            return false;
        }
        return getData().equals(that.getData());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getBulkId() != null ? getBulkId().hashCode() : 0);
        result = 31 * result + (getPath() != null ? getPath().hashCode() : 0);
        result = 31 * result + (getData() != null ? getData().hashCode() : 0);
        return result;
    }

}
