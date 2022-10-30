/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 24, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Validation record detail
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ValidationRecord implements Serializable {

    private static final long serialVersionUID = 2881174208254403142L;

    private Long id;
    private String name;
    private String msgCode;
    private String type;
    private String category;
    private boolean deployed;
    private List<String> messages;

    public ValidationRecord() {

    }

    /**
     * Constructor
     * 
     * @param id
     * @param name
     * @param deployed
     * @param messages
     */
    public ValidationRecord(Long id, String name, boolean deployed,
            List<String> messages) {
        super();
        this.id = id;
        this.name = name;
        this.deployed = deployed;
        this.messages = messages;
    }

    /**
     * Constructor
     * 
     * @param id
     * @param name
     * @param deployed
     */
    public ValidationRecord(Long id, String name, boolean deployed,
            String type) {
        super();
        this.id = id;
        this.name = name;
        this.deployed = deployed;
        this.type = type;
    }

    /**
     * Add message
     * 
     * @param message
     */
    public void addMessage(String message) {
        getMessages().add(message);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getMessages() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

}
