package com.nextlabs.destiny.console.dto.policyworkflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyPortingDTO;
import com.nextlabs.destiny.console.enums.ImportMechanism;
import org.json.JSONObject;

/**
 * DTO for import policy workflow execution requests.
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class MigrationImportRequestDTO {

    private PolicyPortingDTO payload;

    private String sourceHostname;

    private ImportMechanism mechanism = ImportMechanism.PARTIAL;

    private boolean cleanup;

    public MigrationImportRequestDTO(PolicyPortingDTO payload, String sourceHostname) {
        this.payload = payload;
        this.sourceHostname = sourceHostname;
    }

    public PolicyPortingDTO getPayload() {
        return payload;
    }

    public void setPayload(PolicyPortingDTO payload) {
        this.payload = payload;
    }

    public String getSourceHostname() {
        return sourceHostname;
    }

    public void setSourceHostname(String sourceHostname) {
        this.sourceHostname = sourceHostname;
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

    public String toAuditString(String destinationHost) {
        JSONObject audit = new JSONObject();
        audit.put("sourceHost", this.sourceHostname);
        audit.put("destinationHost", destinationHost);
        audit.put("mechanism", this.mechanism);
        audit.put("cleanup", this.cleanup);
        return audit.toString(2);
    }
}
