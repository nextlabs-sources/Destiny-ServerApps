package com.nextlabs.destiny.console.dto.policymgmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.nextlabs.destiny.console.dto.common.BaseDTO;

/**
 * @author Sachindra Dasun
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class DeploymentResponseDTO extends BaseDTO implements Comparable<DeploymentResponseDTO> {

    private static final long serialVersionUID = 621976069793636338L;
    private ArrayList<PushResultDTO> pushResults;

    public DeploymentResponseDTO() {
    }

    public DeploymentResponseDTO(Long id) {
        this.id = id;
    }

    public List<PushResultDTO> getPushResults() {
        if (pushResults == null) {
            pushResults = new ArrayList<>();
        }
        return pushResults;
    }

    public void setPushResults(List<PushResultDTO> pushResults) {
        this.pushResults = new ArrayList<>(pushResults);
    }

    @Override
    public int compareTo(DeploymentResponseDTO o) {
        if (o == null || o.getId() == null || this.getId() == null) {
            return -1;
        }
        return this.getId().compareTo(o.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeploymentResponseDTO deploymentResponseDTO = (DeploymentResponseDTO) o;
        return Objects.equals(id, deploymentResponseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
