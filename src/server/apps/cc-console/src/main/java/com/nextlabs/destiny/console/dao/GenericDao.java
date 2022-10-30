/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.dao;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Generic DAO interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface GenericDao<E, PK extends Serializable> {

    /**
     * <p>
     * Persists the newInstance entity into database.
     * </p>
     * 
     * @param newInstance
     *            Entity to be saved
     * @return the saved object
     * 
     */
    public E create(E newInstance);

    /**
     * 
     * <p>
     * Updates the given inTransientObject.
     * </p>
     * 
     * @param inTransientObject
     *            Entity to be updated
     * @return the updated inTransientObject
     * 
     */
    public E update(final E inTransientObject);

    /**
     * Removes an entity from persistent storage in the database.
     * 
     * @param persistentObject
     *            the object to be deleted
     */
    public void delete(E persistentObject);

    /**
     * Retrieves an entity that was previously persisted to the database using
     * the indicated id as primary key.
     * 
     * @param id
     *            the entity id
     * @return the entity with the given id
     * 
     */
    public E findById(PK id);

    /**
     * 
     * <p>
     * Returns all entities from type <code>E</code> from the database.
     * </p>
     * 
     * @return A list of found entities
     * 
     */
    public List<E> findAll();

    /**
     * Return the total number of persisted entities of type <code>E</code>.
     * 
     * @return the total number of persisted entities of type <code>E</code>
     */
    long countAll();
}
