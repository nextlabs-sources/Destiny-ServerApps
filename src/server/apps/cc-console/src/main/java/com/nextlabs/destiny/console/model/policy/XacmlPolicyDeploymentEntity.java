/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 19, 2016
 *
 */
package com.nextlabs.destiny.console.model.policy;

import com.nextlabs.destiny.console.model.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 *
 * Policy Deployment Entity
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
@Entity
@Table(name = XacmlPolicyDeploymentEntity.XACML_DEPLOY_ENTITY)
public class XacmlPolicyDeploymentEntity extends BaseModel {

    private static final long serialVersionUID = -7710673981877346876L;

    static final String XACML_DEPLOY_ENTITY = "XACML_DEPLOYMENT_ENTITIES";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @org.springframework.data.annotation.Id
    private Long id;

    @Column(name = "filename", length = 800, nullable = false)
    private String fileName;

    @Lob
    @Column(name = "xml", nullable = true)
    private String xml;

    @Column(name = "file_size")
    private long fileSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
