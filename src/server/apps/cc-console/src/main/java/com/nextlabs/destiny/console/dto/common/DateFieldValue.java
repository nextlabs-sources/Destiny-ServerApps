/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 13, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nextlabs.destiny.console.enums.DateOption;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * Search Criteria date field value
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@ApiModel(description ="Date value to search for", parent = FieldValue.class, value = "Date")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateFieldValue extends FieldValue{

    private long fromDate;
    private long toDate;
    private String dateOption;

    @JsonIgnore
    private DateOption dateOpt;

    @ApiModelProperty(value = "Start date in Epoch time", example="1573626776292")
    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    @ApiModelProperty(value = "End date in Epoch time", example="1573626776292")
    public long getToDate() {
        return toDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    @ApiModelProperty(value = "Date criteria", allowableValues = "PAST_7_DAYS, PAST_30_DAYS, PAST_3_MONTHS, PAST_6_MONTHS, PAST_1_YEAR, CUSTOM")
    public String getDateOption() {
        return dateOption;
    }

    public void setDateOption(String dateOption) {
        this.dateOption = dateOption;
    }

    public DateOption getDateOpt() {
        return DateOption.get(this.dateOption);
    }

    @Override
    public String toString() {
        return String.format(
                "DateFieldValue [fromDate=%s, toDate=%s, dateOption=%s]",
                fromDate, toDate, dateOption);
    }

}
