package com.nextlabs.destiny.console.dto.policymgmt;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.enums.DevEntityType;

/**
 * @author Sachindra Dasun
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class DeploymentRequestDTO extends BaseDTO implements Comparable<DeploymentRequestDTO> {

    @ApiModelProperty(
            value = "The category can have the following values: "
                    + "\n<ul><li><strong>FO</strong>: Folder</li>"
                    + "<li><strong>PO</strong>: Policy</li>"
                    + "<li><strong>CO</strong>: Component</li>"
                    + "<li><strong>DP</strong>: Delegation Policy</li>"
                    + "<li><strong>XP</strong>: Xacml Policy</li>"
                    + "<li><strong>LC</strong>: Location</li>"
                    + "<li><strong>DC</strong>: Delegation Component</li></ul>",
            position = 10)
    private DevEntityType type;

    @ApiModelProperty(value = "Indicates whether to push to deploy.", position = 20)
    private boolean push;

    @ApiModelProperty(
            value = "Indicates the datetime at which to execute the deployment. Provide value of -1 to deploy immediately.\n" +
                    "The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
            position = 30, example = "1573786129296")
    private long deploymentTime;

    @ApiModelProperty(value = "Indicates whether there are dependencies on other components.", position = 40)
    private boolean deployDependencies;

    public DeploymentRequestDTO() {
    }

    public DeploymentRequestDTO(Long id, DevEntityType type) {
        this.id = id;
        this.type = type;
    }

    public DeploymentRequestDTO(Long id, DevEntityType type, boolean push, long deploymentTime,
                                boolean deployDependencies) {
        this.id = id;
        this.type = type;
        this.push = push;
        this.deploymentTime = deploymentTime;
        this.deployDependencies = deployDependencies;
    }

    public DevEntityType getType() {
        return type;
    }

    public void setType(DevEntityType type) {
        this.type = type;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }

    public long getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(long deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public boolean isDeployDependencies() {
        return deployDependencies;
    }

    public void setDeployDependencies(boolean deployDependencies) {
        this.deployDependencies = deployDependencies;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeploymentRequestDTO that = (DeploymentRequestDTO) o;
        return getId() == that.getId();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public int compareTo(DeploymentRequestDTO o) {
        if (o == null || o.getId() == null || this.getId() == null) {
            return -1;
        }
        return this.getId().compareTo(o.getId());
    }

}
