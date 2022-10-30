/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 18, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 *
 * Data Initializer Service to create initialization data during startup
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public interface DataInitializerService {

    /**
     * <p>
     * Initializes operator configuration
     * </p>
     * 
     * @throws ConsoleException
     *             throws at any error
     */
    void initializeOperatorConfig() throws ConsoleException;

    /**
     * <p>
     * Reloads Help Contents from xlsx files
     * </p>
     * 
     * @throws ConsoleException
     *             throws at any error
     */
    void reloadHelpContent() throws ConsoleException;

    /**
     * <p>
     * Creates Seed Data for Delegated Administration
     * </p>
     * 
     * @throws ConsoleException
     *             throws at any error
     * @throws CircularReferenceException 
     */
    void createDASeedData() throws ConsoleException, CircularReferenceException;

    /**
     * Create Hidden tag labels
     * 
     * @throws ConsoleException
     */
    void createHiddenTagLables() throws ConsoleException;

}
