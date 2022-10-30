/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 16, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt.porting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * Node of the {@link PolicyTree}
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node implements Serializable {

    private static final long serialVersionUID = 3342124971719114497L;

    @ApiModelProperty(value = "The name of the folder.", example = "ROOT_51375", position = 10)
    private String folderName;

    @ApiModelProperty(value = "Indicates whether the node is a folder.", position = 20)
    private boolean folder;

    @ApiModelProperty(value = "The path of the node.", example = "ROOT_51375/Sample Policy", position = 30)
    private String path;

    @ApiModelProperty(position = 40)
    private PolicyDTO data;

    @ApiModelProperty(value = "Child nodes of type Node.", position = 50)
    private List<Node> children = new ArrayList<>();

    public Node() {
    }

    public Node(PolicyDTO data) {
        super();
        this.data = data;
    }

    public PolicyDTO getData() {
        return data;
    }

    public List<Node> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    @Override
    public String toString() {
        return String.format(
                "Node [folderName=%s, folder=%s, path=%s, data=%s, children=%s]",
                folderName, folder, path, data, children);
    }

}
