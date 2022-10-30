/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 17, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 *
 * Data Indexer Service to manage indexing of console system data
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface DataIndexerService {

    /**
     * <p>
     * Create all indexes
     * </p>
     * 
     * @throws ConsoleException
     *             throws at any error
     */
    void createAllIndexes() throws ConsoleException;

    /**
     * <p>
     * Index or re-index all the system indexes
     * </p>
     * 
     * @throws ConsoleException
     *             throws at any error
     */
    void indexData() throws ConsoleException;

    /**
     * <p>
     * Index or re-index the given index data
     * </p>
     * 
     * @throws ConsoleException
     *             throws at any error
     */
    void indexByName(String indexName) throws ConsoleException;

}
