package com.nextlabs.destiny.console.model.policy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.nextlabs.destiny.console.dto.Authorizable;
import com.nextlabs.destiny.console.dto.policymgmt.FolderDTO;
import com.nextlabs.destiny.console.enums.FolderType;
import com.nextlabs.destiny.console.model.BaseModel;
import com.nextlabs.destiny.console.model.Tag;

/**
 * Entity for folder.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "FOLDER")
public class Folder extends BaseModel implements Authorizable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME", length = 4000)
    private String name;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private FolderType type;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_ID", insertable = false, updatable = false)
    private Folder parent;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_ID")
    private List<Folder> children;

    @Transient
    private List<Long> parentIds;

    public Folder() {
    }

    public Folder(FolderDTO folderDTO) {
        this.id = folderDTO.getId();
        this.name = folderDTO.getName();
        this.type = folderDTO.getType();
        this.parentId = folderDTO.getParentId();
    }

    @Override
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

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public List<Folder> getChildren() {
        if (children == null) {
            return new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<Folder> children) {
        this.children = children;
    }

    @Override
    public Set<? extends Tag> getTags() {
        return new HashSet<>();
    }

    @Override
    public Long getFolderId() {
        return getId();
    }

    public String getFolderPath() {
        Folder currentFolder = this;
        StringBuilder folderPath = new StringBuilder(currentFolder.getName());
        this.parentIds = new ArrayList<>();
        while (currentFolder.getParent() != null) {
            currentFolder = currentFolder.getParent();
            folderPath.insert(0, String.format("%s/", currentFolder.getName()));
            this.parentIds.add(currentFolder.getId());
        }
        return folderPath.toString();
    }

    public List<Long> getParentIds() {
        if (parentIds == null) {
            parentIds = new ArrayList<>();
        }
        return parentIds;
    }

    public String toAuditString() {
        Folder currentFolder = this;
        StringBuilder folderPath = new StringBuilder(currentFolder.getName());
        while (currentFolder.getParent() != null) {
            currentFolder = currentFolder.getParent();
            folderPath.insert(0, String.format("%s/", currentFolder.getName()));
        }
        return new JSONObject().put("Id", id)
                .put("Name", name)
                .put("Type", type)
                .put("Folder Path", folderPath).toString();
    }

    public String toAuditString(String folderPath) {
        return new JSONObject().put("Id", id)
                .put("Name", name)
                .put("Type", type)
                .put("Folder Path", folderPath).toString();
    }

}
