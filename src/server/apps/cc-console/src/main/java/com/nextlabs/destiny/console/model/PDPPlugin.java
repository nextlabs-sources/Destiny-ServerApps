package com.nextlabs.destiny.console.model;

import com.nextlabs.destiny.console.enums.PDPPluginStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity for PDP plugin configuration.
 *
 * @author Chok Shah Neng
 * @since 2020.12
 */
@Entity
@Table(name = "PDP_PLUGINS")
public class PDPPlugin {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME", length = 260, nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", length = 4000, nullable = true)
    private String description;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private PDPPluginStatus status;

    @Column(name = "ACTIVE_FROM")
    private Long activeFrom;

    @Column(name = "ACTIVE_TO")
    private Long activeTo;

    @Column(name = "CREATED_DATE")
    private Long createdDate;

    @Column(name = "MODIFIED_DATE")
    private Long modifiedDate;

    @OneToMany(mappedBy = "plugin", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ID DESC")
    private List<PDPPluginFile> pluginFiles = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public PDPPluginStatus getStatus() {
        return status;
    }

    public void setStatus(PDPPluginStatus status) {
        this.status = status;
    }

    public Long getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Long activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Long getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(Long activeTo) {
        this.activeTo = activeTo;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public List<PDPPluginFile> getPluginFiles() {
        return pluginFiles;
    }

    public void setPluginFiles(List<PDPPluginFile> pluginFiles) {
        this.pluginFiles = pluginFiles;
    }
}
