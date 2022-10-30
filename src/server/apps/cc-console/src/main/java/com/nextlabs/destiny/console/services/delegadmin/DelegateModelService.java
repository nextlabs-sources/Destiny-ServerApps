/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 22, 2016
 *
 */
package com.nextlabs.destiny.console.services.delegadmin;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.PolicyModel;

/**
 *
 * Service interface for Delegate Model
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface DelegateModelService {

    /**
     * Saves or updates an {@link DelegateModel}
     * 
     * @param model
     *            {@link DelegateModel}
     * @return Saved {@link PolicyModel}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    DelegateModel save(DelegateModel model) throws ConsoleException;

    /**
     * Finds a {@link DelegateModel} by id
     * 
     * @param id
     *            id
     * @return {@link DelegateModel} if found null thrown on any error
     * 
     */
    DelegateModel findById(Long id) throws ConsoleException;

    /**
     * Finds a {@link DelegateModel} by type
     * 
     * @param id
     *            id
     * @return {@link DelegateModel} if found null thrown on any error
     * 
     */
    Page<DelegateModel> findByType(PolicyModelType type, PageRequest pageable)
            throws ConsoleException;

    /**
     * Remove an Policy Model
     * 
     * @param id
     *            id of PolicyModel
     * @throws ConsoleException
     *             thrown on any error
     */
    boolean remove(Long id) throws ConsoleException;

    /**
     * Re-index all {@link DelegateModel}
     * 
     * @throws ConsoleException
     */
    void reIndexAllModels() throws ConsoleException;
    
    /**
     * Updates DA_USER model with newly added user properties
     * 
     * @param userProperties
     * @throws ConsoleException
     */
    void updateDAModelUserAttributes(Set<String> userProperties) 
    		throws ConsoleException;

}
