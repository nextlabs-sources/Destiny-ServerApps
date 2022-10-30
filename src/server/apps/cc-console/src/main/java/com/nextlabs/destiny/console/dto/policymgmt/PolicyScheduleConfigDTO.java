/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 7, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * DTO for policy schedule configuration
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@ApiModel(description="The configuration at which the policy recurs")
public class PolicyScheduleConfigDTO implements Serializable {

    private static final long serialVersionUID = -5976465231934670997L;

    private String startDateTime; // Format - Apr 4, 2016 6:48:00 PM
    private String endDateTime;
    private String recurrenceStartTime;
    private String recurrenceEndTime;
    private String timezone;
    private long recurrenceDateOfMonth = -1;
    private long recurrenceDayInMonth = -1;
    private boolean sunday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;

    @ApiModelProperty(value = "The date on which the policy enforcement should commence.", example = "Jul 6, 2020 12:00:00 AM", position = 10)
    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    @ApiModelProperty(value = "The date on which the policy enforcement should end.", example = "Dec 31, 2099 11:59:59 PM", position = 20)
    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    @ApiModelProperty(value = "The time at which the policy enforcement should commence.", example = "12:00:00 AM", position = 30)
    public String getRecurrenceStartTime() {
        return recurrenceStartTime;
    }

    public void setRecurrenceStartTime(String recurrenceStartTime) {
        this.recurrenceStartTime = recurrenceStartTime;
    }

    @ApiModelProperty(value = "The time at which the policy enforcement should end.", example = "11:59:00 PM", position = 40)
    public String getRecurrenceEndTime() {
        return recurrenceEndTime;
    }

    public void setRecurrenceEndTime(String recurrenceEndTime) {
        this.recurrenceEndTime = recurrenceEndTime;
    }

    @ApiModelProperty(value = "The TZ database name of the timezone.", example = "America/Los_Angeles", position = 50)
    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @ApiModelProperty(position = 50)
    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    @ApiModelProperty(position = 60)
    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    @ApiModelProperty(position = 70)
    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    @ApiModelProperty(position = 80)
    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    @ApiModelProperty(position = 90)
    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    @ApiModelProperty(position = 100)
    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    @ApiModelProperty(position = 110)
    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    @ApiModelProperty(value = "Recurrence date in a month. Set value as -1 if it's not recurring.", position = 120, example = "-1")
    public long getRecurrenceDateOfMonth() {
        return recurrenceDateOfMonth;
    }

    public void setRecurrenceDateOfMonth(long recurrenceDateOfMonth) {
        this.recurrenceDateOfMonth = recurrenceDateOfMonth;
    }

    @ApiModelProperty(value = "Recurrence day in a month. Set value as -1 if it's not recurring.", position = 130, example = "-1")
    public long getRecurrenceDayInMonth() {
        return recurrenceDayInMonth;
    }

    public void setRecurrenceDayInMonth(long recurrenceDayInMonth) {
        this.recurrenceDayInMonth = recurrenceDayInMonth;
    }

    @Override
    public String toString() {
        return String.format(
                "PolicyScheduleConfigDTO [startDateTime=%s, endDateTime=%s, recurrenceStartTime=%s, recurrenceEndTime=%s, recurrenceDateOfMonth=%s, recurrenceDayInMonth=%s, sunday=%s, monday=%s, tuesday=%s, wednesday=%s, thursday=%s, friday=%s, saturday=%s]",
                startDateTime, endDateTime, recurrenceStartTime,
                recurrenceEndTime, recurrenceDateOfMonth, recurrenceDayInMonth,
                sunday, monday, tuesday, wednesday, thursday, friday, saturday);
    }

}
