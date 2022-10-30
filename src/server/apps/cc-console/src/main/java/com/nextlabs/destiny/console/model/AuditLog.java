/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * Entity for Audit Log
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = "AUDIT_LOGS")
@NamedQuery(name = AuditLog.FIND_BY_USER_ID, query = "SELECT a FROM AuditLog a WHERE a.ownerId = :ownerId ORDER BY a.createdDate DESC")
@NamedQuery(name = AuditLog.FIND_LAST_X_RECORDS, query = "SELECT a FROM AuditLog a ORDER BY a.createdDate DESC")
@NamedQuery(name = AuditLog.FIND_BY_COMPONENT, query = "SELECT a FROM AuditLog a WHERE a.component = :component ORDER BY a.createdDate DESC")
public class AuditLog extends BaseModel {

    private static final long serialVersionUID = 6887074200026954345L;

    public static final String FIND_BY_USER_ID = "auditLog.findByUser";
    public static final String FIND_BY_COMPONENT = "auditLog.findByComponent";
    public static final String FIND_LAST_X_RECORDS = "auditLog.lastXRecords";

    private static final ObjectMapper mapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "component")
    private String component;

    @Column(name = "msgCode")
    private String msgCode;

    @Column(name = "msgParams")
    private String msgParams;

    @Column(name = "hidden")
    private boolean hidden;

    @Transient
    private List<String> params;

    @Transient
    private String ownerDisplayName;

    @Transient
    private String activityMsg;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    @PrePersist
    public void prePersist() {
        super.prePersist();
        this.paramsASFlatString(getParams());
    }

    @PostLoad
    public void postLoad() {
        this.flatStringAsParams(this.msgParams);
    }

    public void paramsASFlatString(List<String> params) {
        try {
            msgParams = mapper.writeValueAsString(params);
        } catch (Exception e) {
            msgParams = "";
        }
    }

    @SuppressWarnings("unchecked")
    public void flatStringAsParams(String paramStr) {
        try {
            params = mapper.readValue(paramStr, List.class);
        } catch (Exception e) {
            params = Collections.emptyList();
        }
    }

    public String[] getParamsAsArray() {
        return getParams().toArray(new String[getParams().size()]);
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsgParams() {
        return msgParams;
    }

    public void setMsgParams(String msgParams) {
        this.msgParams = msgParams;
    }

    public List<String> getParams() {
        if (params == null) {
            params = new ArrayList<>();
        }
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    public String getActivityMsg() {
        return activityMsg;
    }

    public void setActivityMsg(String activityMsg) {
        this.activityMsg = activityMsg;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

}
