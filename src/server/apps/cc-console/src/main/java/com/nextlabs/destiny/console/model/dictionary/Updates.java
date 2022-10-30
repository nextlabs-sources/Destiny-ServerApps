/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 29, 2020
 *
 */
package com.nextlabs.destiny.console.model.dictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Table(name = Updates.UPDATES_TABLE)
public class Updates implements Serializable {

    private static final long serialVersionUID = 29420241305043072L;

    public static final String UPDATES_TABLE = "DICT_UPDATES";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    @Column(name = "START_TIME")
    private Long startTime;

    @Column(name = "END_TIME")
    private Long endTime;

    @Column(name = "IS_SUCCESSFUL")
    private char isSuccessful;

    @Column(name = "ERR_MSG")
    private String errMessage;

    @Column(name = "ACTIVE_FROM")
    private Long activeFrom;

    @Column(name = "ACTIVE_TO")
    private Long activeTo;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    public Updates() {

    }

    public Updates(Enrollment enrollment, Long startTime, Long endTime, char isSuccessful,
            String errMessage, Long activeFrom, Long activeTo) {
        super();
        this.enrollment = enrollment;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSuccessful = isSuccessful;
        this.errMessage = errMessage;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public char getIsSuccessful() {
        return isSuccessful;
    }

    public void setIsSuccessful(char isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public Long getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Long activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Long getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(Long activeTo) {
        this.activeTo = activeTo;
    }

}
