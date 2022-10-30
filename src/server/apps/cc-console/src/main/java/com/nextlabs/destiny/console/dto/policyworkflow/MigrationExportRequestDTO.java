package com.nextlabs.destiny.console.dto.policyworkflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextlabs.destiny.console.dto.policymgmt.ExportEntityDTO;
import com.nextlabs.destiny.console.enums.ImportMechanism;

import java.util.List;

/**
 * DTO for export policy workflow execution requests.
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class MigrationExportRequestDTO {

    private List<ExportEntityDTO> exportEntityDTOS;

    private long destinationEnvId;

    private ImportMechanism mechanism = ImportMechanism.PARTIAL;

    private boolean cleanup;

    public List<ExportEntityDTO> getExportEntityDTOS() {
        return exportEntityDTOS;
    }

    public void setExportEntityDTOS(List<ExportEntityDTO> exportEntityDTOS) {
        this.exportEntityDTOS = exportEntityDTOS;
    }

    public long getDestinationEnvId() {
        return destinationEnvId;
    }

    public void setDestinationEnvId(long destinationEnvId) {
        this.destinationEnvId = destinationEnvId;
    }

    public ImportMechanism getMechanism() {
        return mechanism;
    }

    public void setMechanism(ImportMechanism mechanism) {
        this.mechanism = mechanism;
    }

    public boolean isCleanup() {
        return cleanup;
    }

    public void setCleanup(boolean cleanup) {
        this.cleanup = cleanup;
    }
}
