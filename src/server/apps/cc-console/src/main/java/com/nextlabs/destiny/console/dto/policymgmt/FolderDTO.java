package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;

import com.nextlabs.destiny.console.dto.Authorizable;
import com.nextlabs.destiny.console.enums.FolderType;
import com.nextlabs.destiny.console.model.Tag;
import com.nextlabs.destiny.console.model.policy.Folder;

/**
 * DTO for folders
 *
 * @author Sachindra Dasun
 */
public class FolderDTO implements Authorizable, Serializable {

    private static final long serialVersionUID = 5414561098511675321L;

    private Long id;
    private String name;
    private FolderType type;
    private Long parentId;
    private String folderPath;
    private List<FolderDTO> children;
    private List<Long> parentIds;
    private List<GrantedAuthority> authorities;
    private long createdDate;
    private long lastUpdatedDate;

    public FolderDTO() {
    }

    public FolderDTO(Long id, String name, FolderType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.folderPath = name;
    }

    public FolderDTO(FolderLite folderLite) {
        this.id = folderLite.getFolderId();
        this.name = folderLite.getName();
        this.type = FolderType.valueOf(folderLite.getType());
        this.parentId = folderLite.getParentId();
        this.parentIds = folderLite.getParentIds();
        this.folderPath = folderLite.getFolderPath();
        this.createdDate = folderLite.getCreatedDate();
        this.lastUpdatedDate = folderLite.getLastUpdatedDate();
    }

    public FolderDTO(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();
        this.type = folder.getType();
        this.parentId = folder.getParentId();
        this.createdDate = folder.getCreatedDate().getTime();
        this.lastUpdatedDate = folder.getLastUpdatedDate().getTime();
        this.setChildren(folder.getChildren().stream().map(FolderDTO::new).collect(Collectors.toList()));
        this.folderPath = folder.getFolderPath();
        this.parentIds = folder.getParentIds();
    }

    @Override
    public Set<? extends Tag> getTags() {
        return new HashSet<>();
    }

    @Override
    public Long getFolderId() {
        return getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public FolderType getType() {
        return type;
    }

    public void setType(FolderType type) {
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

    public List<FolderDTO> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<FolderDTO> children) {
        this.children = children;
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

    public List<GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = new ArrayList<>();
        }
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
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

}
