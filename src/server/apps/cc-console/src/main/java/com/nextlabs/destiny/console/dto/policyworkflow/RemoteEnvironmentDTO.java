package com.nextlabs.destiny.console.dto.policyworkflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.model.remoteenv.RemoteEnvironment;
import org.json.JSONObject;

/**
 * DTO for remote policy workflow environments.
 *
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class RemoteEnvironmentDTO extends BaseDTO implements Auditable {

    private String name;
    private String host;
    private String port;
    private String clientId;
    private String username;
    private String password;
    private boolean isActive;
    private long createdDate;
    private Long ownerId;
    private String ownerDisplayName;
    private long lastUpdatedDate;
    private Long modifiedById;
    private String modifiedBy;
    private int version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Long getModifiedById() {
        return modifiedById;
    }

    public void setModifiedById(Long modifiedById) {
        this.modifiedById = modifiedById;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public static RemoteEnvironmentDTO getDTO(RemoteEnvironment remoteEnvironment, boolean retrievePassword){
        RemoteEnvironmentDTO remoteEnvironmentDTO = new RemoteEnvironmentDTO();
        remoteEnvironmentDTO.setId(remoteEnvironment.getId());
        remoteEnvironmentDTO.setName(remoteEnvironment.getName());
        remoteEnvironmentDTO.setHost(remoteEnvironment.getHost());
        remoteEnvironmentDTO.setPort(remoteEnvironment.getPort());
        remoteEnvironmentDTO.setClientId(remoteEnvironment.getClientId());
        remoteEnvironmentDTO.setUsername(remoteEnvironment.getUsername());
        remoteEnvironmentDTO.setIsActive(remoteEnvironment.isActive());
        if (retrievePassword) {
            remoteEnvironmentDTO.setPassword(remoteEnvironment.getPassword());
        }
        remoteEnvironmentDTO.setOwnerId(remoteEnvironment.getOwnerId());
        remoteEnvironmentDTO.setCreatedDate(remoteEnvironment.getCreatedDate().getTime());
        remoteEnvironmentDTO.setLastUpdatedDate(remoteEnvironment.getLastUpdatedDate().getTime());
        remoteEnvironmentDTO.setModifiedById(remoteEnvironment.getLastUpdatedBy());
        remoteEnvironmentDTO.setVersion(remoteEnvironment.getVersion());
        return remoteEnvironmentDTO;
    }

    public static RemoteEnvironment setEntityValues(RemoteEnvironmentDTO remoteEnvironmentDTO, RemoteEnvironment remoteEnvironment){
        remoteEnvironment.setName(remoteEnvironmentDTO.getName());
        remoteEnvironment.setHost(remoteEnvironmentDTO.getHost());
        remoteEnvironment.setPort(remoteEnvironmentDTO.getPort());
        remoteEnvironment.setClientId(remoteEnvironmentDTO.getClientId());
        remoteEnvironment.setUsername(remoteEnvironmentDTO.getUsername());
        remoteEnvironment.setPassword(remoteEnvironmentDTO.getPassword());
        return remoteEnvironment;
    }

    public String toAuditString() {
        JSONObject audit = new JSONObject();
        audit.put("Name", this.name);
        audit.put("Host", this.host);
        audit.put("Port", this.port);
        audit.put("Client ID", this.clientId);
        audit.put("Username", this.username);
        audit.put("Is active", this.isActive);
        return audit.toString(2);
    }
}
