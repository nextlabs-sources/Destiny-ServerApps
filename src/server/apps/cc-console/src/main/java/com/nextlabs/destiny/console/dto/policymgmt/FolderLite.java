package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.nextlabs.destiny.console.model.policy.Folder;

/**
 * Folder entity used in Elasticsearch index.
 *
 * @author Sachindra Dasun
 */
@Document(indexName = "folders")
@Setting(settingPath = "/search_config/index-settings.json")
public class FolderLite implements Serializable {

    private static final long serialVersionUID = 4316762793385746167L;

    @Id
    @Field(type = FieldType.Long, store = true)
    private Long folderId;
    private String name;

    @Field(type = FieldType.Keyword, store = true)
    private String type;
    private Long parentId;
    private String folderPath;
    private List<Long> parentIds;
    private long createdDate;
    private long lastUpdatedDate;

    @Transient
    private List<FolderLite> children;

    public FolderLite() {
    }

    public FolderLite(FolderDTO folderDTO) {
        this.folderId = folderDTO.getId();
        this.name = folderDTO.getName();
        this.type = folderDTO.getType().name();
        this.parentId = folderDTO.getParentId();
        this.createdDate = folderDTO.getCreatedDate();
        this.lastUpdatedDate = folderDTO.getLastUpdatedDate();
    }

    public FolderLite(Folder folder) {
        this.folderId = folder.getId();
        this.name = folder.getName();
        this.type = folder.getType().name();
        this.parentId = folder.getParentId();
        this.folderPath = folder.getFolderPath();
        this.parentIds = folder.getParentIds();
        this.createdDate = folder.getCreatedDate().getTime();
        this.lastUpdatedDate = folder.getLastUpdatedDate().getTime();
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public List<Long> getParentIds() {
        if (parentIds == null) {
            parentIds = new ArrayList<>();
        }
        return parentIds;
    }

    public void setParentIds(List<Long> parentIds) {
        this.parentIds = parentIds;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public List<FolderLite> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<FolderLite> children) {
        this.children = children;
    }

}
