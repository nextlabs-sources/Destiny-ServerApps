package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.nextlabs.destiny.console.enums.DevEntityType;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class DeploymentDependency implements Serializable {

    private long id;
    private DevEntityType type;
    private String group;
    private String name;
    private String folderPath;
    private boolean optional;
    private boolean provided;
    private boolean sub;

    public DeploymentDependency(long id, DevEntityType type, String name, String folderPath, boolean optional, boolean provided,
                                boolean sub) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.folderPath = folderPath;
        this.optional = optional;
        this.provided = provided;
        this.sub = sub;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DevEntityType getType() {
        return type;
    }

    public void setType(DevEntityType type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isProvided() {
        return provided;
    }

    public void setProvided(boolean provided) {
        this.provided = provided;
    }

    public boolean isSub() {
        return sub;
    }

    public void setSub(boolean sub) {
        this.sub = sub;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeploymentDependency that = (DeploymentDependency) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
