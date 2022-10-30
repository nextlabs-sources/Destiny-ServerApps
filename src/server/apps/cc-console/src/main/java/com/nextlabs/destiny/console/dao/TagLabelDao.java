/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.dao;

import java.util.List;

import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.model.TagLabel;

/**
 *
 * Tag Label Dao interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface TagLabelDao extends GenericDao<TagLabel, Long> {

    /**
     * 
     * Find the Tag labels by type
     * 
     * @param type
     * @return List of {@link TagLabel}
     */
    List<TagLabel> findByType(TagType type);

    /**
     * Find the Tag labels by label start with and type, if type is null will
     * consider all types
     * 
     * @param type
     * @return List of {@link TagLabel}
     */
    List<TagLabel> findByStartwith(String labelStartswith, TagType type);

    /**
     * Find the Tag labels by key
     * 
     * @param key
     * @param type
     * @return List of {@link TagLabel}
     */
    List<TagLabel> findByKey(String key, TagType type);

}
