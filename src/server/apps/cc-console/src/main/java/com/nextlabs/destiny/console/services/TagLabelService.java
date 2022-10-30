/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.TagLabel;

/**
 *
 * Tag Label Service interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface TagLabelService {

    /**
     * Create or Update the Tag in the system
     * 
     * @param {@link
     *            TagLabel}
     * @return Saved TagLabel entity
     * @throws ConsoleException
     *             throws at any error
     */
    TagLabel saveTag(TagLabel tagLabel) throws ConsoleException;

    /**
     * Find tag by Id
     * 
     * @param id
     *            primary key of the entity
     * @return TagLabel entity
     * @throws ConsoleException
     *             throws at any error
     */
    TagLabel findById(Long id) throws ConsoleException;

    /**
     * Remove tag by Id
     * 
     * @param id
     *            primary key of the entity
     * @throws ConsoleException
     *             throws at any error
     */
    void removeTag(Long id) throws ConsoleException;

    /**
     * Find tags for given type
     * 
     * @param tagType
     * @param showHidden
     *            show hidden tags if true
     * @param pageable
     *            pagination data
     * @return Pages of TagLabel
     * @throws ConsoleException
     *             throws at any error
     */
    Page<TagLabel> findByType(String tagType, boolean showHidden, Pageable pageable)
            throws ConsoleException;

    /**
     * Find tags for given label start with characters and tag type
     * 
     * @param labelStartwith
     *            label starting characters
     * @param tagType
     * @param showHidden
     *            show hidden tags if true
     * @param Pages
     *            of TagLabel
     * 
     * @return collection of TagLabel
     * @throws ConsoleException
     *             throws at any error
     */
    Page<TagLabel> findByLabelStartWithAndType(String labelStartwith,
            String tagType, boolean showHidden, Pageable pageable)
            throws ConsoleException;

    /**
     * Find tags for given label and type
     * 
     * @param label
     *            tag label exact match
     * @param tagType
     *            tag type
     * @param showHidden
     *            show hidden tags if true
     * @param pageable
     *            pagination data
     * @return Pages of TagLabel
     * @throws ConsoleException
     *             throws at any error
     */
    Page<TagLabel> findByLabelAndType(String label, String tagType,
            boolean showHidden, Pageable pageable) throws ConsoleException;

    /**
     * Find Tag by given key directly from database.
     * 
     * @param key
     * @param tagType
     * @return list of {@link TagLabel}
     * @throws ConsoleException
     */
    List<TagLabel> findByKey(String key, TagType tagType)
            throws ConsoleException;

    /**
     * Re index all the tags from database for fast searching
     * 
     * @throws ConsoleException
     */
    void reIndexAllTags() throws ConsoleException;

}
