/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 4, 2016
 *
 */
package com.nextlabs.destiny.console.model.delegadmin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.nextlabs.destiny.console.enums.ActionType;

/**
 *
 * Delegation rule obligation tag
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ObligationTag {

    private List<ApplicableTag> viewTags;
    private List<ApplicableTag> editTags;
    private List<ApplicableTag> deleteTags;
    private List<ApplicableTag> deployTags;
    private List<ApplicableTag> moveTags;
    private List<ApplicableTag> insertTags;

    public List<ApplicableTag> getViewTags() {
        if (viewTags == null) {
            viewTags = new LinkedList<>();
        }
        return viewTags;
    }

    public void setViewTags(List<ApplicableTag> viewTags) {
        this.viewTags = viewTags;
    }

    public List<ApplicableTag> getEditTags() {
        if (editTags == null) {
            editTags = new LinkedList<>();
        }
        return editTags;
    }

    public void setEditTags(List<ApplicableTag> editTags) {
        this.editTags = editTags;
    }

    public List<ApplicableTag> getDeleteTags() {
        if (deleteTags == null) {
            deleteTags = new LinkedList<>();
        }
        return deleteTags;
    }

    public void setDeleteTags(List<ApplicableTag> deleteTags) {
        this.deleteTags = deleteTags;
    }

    public List<ApplicableTag> getDeployTags() {
        if (deployTags == null) {
            deployTags = new LinkedList<>();
        }
        return deployTags;
    }

    public void setDeployTags(List<ApplicableTag> deployTags) {
        this.deployTags = deployTags;
    }

    public List<ApplicableTag> getMoveTags() {
        if(moveTags == null) {
            this.moveTags = new LinkedList<>();
        }
        return moveTags;
    }

    public void setMoveTags(List<ApplicableTag> moveTags) {
        this.moveTags = moveTags;
    }
    
    public List<ApplicableTag> getInsertTags() {
        if (insertTags == null) {
        	insertTags = new LinkedList<>();
        }
        return insertTags;
    }

    public void setInsertTags(List<ApplicableTag> insertTags) {
        this.insertTags = insertTags;
    }

    public List<ApplicableTag> getTagsByActionType(ActionType actionType) {
        switch (actionType) {
            case RENAME:
            case VIEW:
                return viewTags;
            case EDIT:
                return editTags;
            case INSERT:
                return insertTags;
            case DELETE:
                return deleteTags;
            case DEPLOY:
                return deployTags;
            case MOVE:
                return moveTags;
            default:
                return new ArrayList<>();
        }
    }

}
