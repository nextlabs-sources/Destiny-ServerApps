package com.nextlabs.destiny.console.services;

import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 * Entity Audit Log manager service
 * @since 9.0
 * 
 * @author Moushumi Seal
 *
 */
public interface EntityAuditLogService {
	/**
     * Creates a new Entity Audit Log
     * 
     * @param action
     * @param entityType
     * @param entityId
     * @param oldValue
     * @param newValue
     * @throws ConsoleException
     */
	void addEntityAuditLog(AuditAction action, String entityType, Long entityId, String oldValue, String newValue)
			throws ConsoleException;
	
	/**
     * Creates a new Entity Audit Log
     * 
     * @param action
     * @param entityType
     * @param entityId
     * @param oldValue
     * @param newValue
     * @throws ConsoleException
     */
	void addEntityAuditLog(AuditAction action, String entityType, String name, Long entityId, String oldValue, String newValue)
			throws ConsoleException;
}
