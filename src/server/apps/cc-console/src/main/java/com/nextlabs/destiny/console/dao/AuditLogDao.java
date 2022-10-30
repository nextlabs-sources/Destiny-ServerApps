/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.dao;

import java.util.List;

import com.nextlabs.destiny.console.model.AuditLog;

/**
 *
 * DAO interface for Audit Log
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface AuditLogDao extends GenericDao<AuditLog, Long> {

    /**
     * Find the audit logs by user
     * 
     * @param user
     *            id
     * @return List of {@link AuditLog}
     */
    List<AuditLog> findByUser(Long userId);

    /**
     * Find the audit logs by component
     * 
     * @param component
     *            component name
     * @return List of {@link AuditLog}
     */
    List<AuditLog> findByComponent(String componentName);

    /**
     * Find the last x Audit Logs
     * 
     * @param rowCount
     *            no fo records
     * @return List of {@link AuditLog}
     */
    List<AuditLog> findByLastXRecords(int rowCount);
    
    /**
     * Clear all audit logs.
     * 
     */
    void clearAll();

}
