package com.nextlabs.destiny.console.dto.common;

import java.util.Objects;

import com.nextlabs.destiny.console.model.Agent;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Sachindra Dasun
 */
public class AgentDTO extends BaseDTO implements Comparable<AgentDTO> {

    @ApiModelProperty(value = "The agent hostname value registered by PDP.", example = "jpc-prod-01")
    private String host;

    @ApiModelProperty(value = "Type of agent.", example = "PORTAL", allowableValues = "FILE_SERVER, PORTAL, DESKTOP")
    private String type;

    public AgentDTO() {
    }

    public AgentDTO(long id, String host, String type) {
        this.id = id;
        this.host = host;
        this.type = type;
    }

    public static AgentDTO getDTO(Agent agent) {
        return new AgentDTO(agent.getId(), agent.getHost(), agent.getType());
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(AgentDTO o) {
        if (o == null)
            return -1;
        if (this.getId() == null || o.getId() == null) {
            return -1;
        }
        return this.getId().compareTo(o.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getHost(), getType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentDTO agentDTO = (AgentDTO) o;
        return Objects.equals(getId(), agentDTO.getId()) &&
                Objects.equals(getHost(), agentDTO.getHost()) &&
                Objects.equals(getType(), agentDTO.getType());
    }
}
