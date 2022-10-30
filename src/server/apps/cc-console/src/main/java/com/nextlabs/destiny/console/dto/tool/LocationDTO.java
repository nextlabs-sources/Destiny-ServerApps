/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 6, 2020
 *
 */
package com.nextlabs.destiny.console.dto.tool;

import java.util.LinkedHashMap;
import java.util.Map;

import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.subject.Location;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.utils.JsonUtil;


public class LocationDTO extends BaseDTO {

    private static final long serialVersionUID = -3814693256858901732L;

    private Long parentId;
    private String name;
    private String value;

    public LocationDTO() {

    }

    public LocationDTO(Long id, Location location) {
        this.parentId = id;
        this.name = location.getName();
        this.value = location.getValue();
    }
    
    public LocationDTO(PolicyDevelopmentEntity entity) throws PQLException {
        DomainObjectBuilder dob = new DomainObjectBuilder(entity.getPql());
        Location loc = dob.processLocation();

        this.parentId = entity.getId();
        this.name = loc.getName();
        this.value = loc.getValue();

    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toAuditString() throws ConsoleException {
        try {
            Map<String, Object> audit = new LinkedHashMap<>();
            audit.put("Location ID", getParentId());
            audit.put("Name", getName());
            audit.put("Value", getValue());
            return JsonUtil.toJsonString(audit);
        } catch (Exception e) {
            throw new ConsoleException(e);
        }

    }

}
