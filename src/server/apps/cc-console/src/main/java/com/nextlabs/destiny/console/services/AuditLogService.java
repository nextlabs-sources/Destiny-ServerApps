/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 30, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import java.util.List;

import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.AuditLog;

/**
 *
 * Audit Log manager service
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface AuditLogService {

    /**
     * Create or Update the AuditLog in the system
     * 
     * @param component
     *            name of the component or micro-service
     * @param msgCode
     *            message code
     * @param msgParams
     *            message parameters
     * @return Saved AuditLog entity
     * @throws ConsoleException
     *             throws at any error
     */
    AuditLog save(String component, String msgCode, String... msgParams)
            throws ConsoleException;

    /**
     * Find audit log by Id
     * 
     * @param id
     *            primary key of the entity
     * @return AuditLog entity
     * @throws ConsoleException
     *             throws at any error
     */
    AuditLog findById(Long id) throws ConsoleException;

    /**
     * Find audit logs by user
     * 
     * @param id
     *            user id
     * @return Collection of AuditLog entity
     * @throws ConsoleException
     *             throws at any error
     */
    List<AuditLog> findByUser(Long userId) throws ConsoleException;

    /**
     * Find audit logs by component
     * 
     * @param component
     *            component name
     * @return Collection of AuditLog entity
     * @throws ConsoleException
     *             throws at any error
     */
    List<AuditLog> findByComponent(String component) throws ConsoleException;

    /**
     * Find last x audit logs
     * 
     * @param rowCount
     *            no of audit logs
     * 
     * @return Collection of AuditLog entity
     * @throws ConsoleException
     *             throws at any error
     */
    List<AuditLog> findByLastXRecords(int rowCount) throws ConsoleException;

}
