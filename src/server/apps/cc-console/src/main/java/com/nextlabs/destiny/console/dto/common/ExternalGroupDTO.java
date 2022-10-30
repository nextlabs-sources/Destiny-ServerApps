package com.nextlabs.destiny.console.dto.common;

import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ProvisionedUserGroup;
import com.nextlabs.destiny.console.utils.JsonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExternalGroupDTO
        implements Auditable {

    private Long id;
    private String externalId;
    private String name;
    private String description;
    private String email;
    private Long authHandlerId;
    private String authHandlerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAuthHandlerId() {
        return authHandlerId;
    }

    public void setAuthHandlerId(Long authHandlerId) {
        this.authHandlerId = authHandlerId;
    }

    public String getAuthHandlerName() {
        return authHandlerName;
    }

    public void setAuthHandlerName(String authHandlerName) {
        this.authHandlerName = authHandlerName;
    }

    public static ExternalGroupDTO getDTO(ProvisionedUserGroup userGroup) {
        ExternalGroupDTO dto = new ExternalGroupDTO();
        dto.setAuthHandlerId(userGroup.getAuthHandlerId());
        dto.setExternalId(userGroup.getGroupId());

        return dto;
    }

    @Override
    public String toAuditString()
                    throws ConsoleException {
        try {
            Map<String, Object> audit = new LinkedHashMap<>();

            audit.put("Authentication Handler", this.authHandlerId);
            audit.put("Group Id", this.externalId);
            audit.put("Group Name", this.name);
            audit.put("Description", this.description);

            return JsonUtil.toJsonString(audit);
        } catch(Exception e) {
            throw new ConsoleException(e);
        }
    }
}
