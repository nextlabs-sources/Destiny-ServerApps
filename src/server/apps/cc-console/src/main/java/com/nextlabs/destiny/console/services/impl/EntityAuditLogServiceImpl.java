package com.nextlabs.destiny.console.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.EntityAuditLogService;

/**
 * Implementation of the Entity Audit Log service
 * @since 9.0
 * 
 * @author Moushumi Seal
 *
 */
@Service
public class EntityAuditLogServiceImpl implements EntityAuditLogService{

	@Autowired
    private EntityAuditLogDao entityAuditLogDao;
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
	public void addEntityAuditLog(AuditAction action, String entityType, Long entityId, String oldValue,String newValue) 
			throws ConsoleException {
		entityAuditLogDao.addEntityAuditLog(action, entityType, entityId, oldValue, newValue);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void addEntityAuditLog(AuditAction action, String entityType, String name, Long entityId, String oldValue,
			String newValue) throws ConsoleException {
		entityAuditLogDao.addEntityAuditLog(action, entityType, name, entityId, oldValue, newValue);
	}

}
