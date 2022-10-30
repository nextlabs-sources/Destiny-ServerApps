/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 11, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Collection of data response handles by this DTO
 *
 * @param <T>
 * 
 * @author Amila Silva
 * @since 8.0
 *
 */
public class CollectionDataResponseDTO<M extends Object> extends ResponseDTO
        implements Serializable {

    private static final long serialVersionUID = 9184302244043782118L;

    @ApiModelProperty(value = "Response object values.")
    private Collection<M> data;

    @ApiModelProperty(value = "Current page number.", example = "1")
    private int pageNo = 0;

    @ApiModelProperty(value = "Number of items in a page.", example = "10")
    private int pageSize = 10;

    @ApiModelProperty(value = "Total number of pages.", example = "5")
    private int totalPages = 1;

    @ApiModelProperty(value = "Total number of records.", example = "49")
    private long totalNoOfRecords = 0;

    /**
     * Collection data response DTO constructor
     * 
     * @param statusCode
     * @param message
     */
    private CollectionDataResponseDTO(String statusCode, String message) {
        super(statusCode, message);

    }

    @SuppressWarnings("rawtypes")
    public static CollectionDataResponseDTO create(String statusCode,
            String message) {
        return new CollectionDataResponseDTO(statusCode, message);
    }

    public static <T> CollectionDataResponseDTO<T> createWithType(String statusCode,
                                                                  String message) {
        return new CollectionDataResponseDTO<>(statusCode, message);
    }

    public Collection<M> getData() {
        if (data == null)
            data = new ArrayList<>();
        return data;
    }

    public void setData(Collection<M> data) {
        this.data = data;

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

    public long getTotalNoOfRecords() {
        return totalNoOfRecords;
    }

    public void setTotalNoOfRecords(long totalNoOfRecords) {
        this.totalNoOfRecords = totalNoOfRecords;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
