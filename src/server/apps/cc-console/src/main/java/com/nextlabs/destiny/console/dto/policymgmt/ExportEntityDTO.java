package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;

import com.nextlabs.destiny.console.enums.DevEntityType;
import io.swagger.annotations.ApiModelProperty;

/**
 * Entity for export data.
 *
 * @author Sachindra Dasun
 */
public class ExportEntityDTO implements Serializable {

    private static final long serialVersionUID = -8371737434446090579L;

    @ApiModelProperty(
            value = "The entity type can have the following values: "
                    + "\n<ul><li><strong>FO</strong>: Folder</li>"
                    + "<li><strong>PO</strong>: Policy</li>"
                    + "<li><strong>CO</strong>: Component</li>"
                    + "<li><strong>DP</strong>: Delegation Policy</li>"
                    + "<li><strong>DC</strong>: Delegation Component</li></ul>",
            position = 70, example = "PO")
    private DevEntityType entityType;

    @ApiModelProperty(value = "The entity id", example = "87", required = true)
    private Long id;

    public ExportEntityDTO() {
    }

    public ExportEntityDTO(DevEntityType entityType, Long id) {
        this.entityType = entityType;
        this.id = id;
    }

    public ExportEntityDTO(PolicyLite policyLite) {
        this.entityType = DevEntityType.POLICY;
        this.id = policyLite.getId();
    }

    public ExportEntityDTO(FolderDTO folderDTO) {
        this.entityType = DevEntityType.FOLDER;
        this.id = folderDTO.getId();
    }

    public DevEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(DevEntityType entityType) {
        this.entityType = entityType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ExportEntityDTO{" +
                "entityType=" + entityType +
                ", id=" + id +
                '}';
    }

}
