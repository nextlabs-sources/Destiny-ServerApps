package com.nextlabs.destiny.console.dao;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.EntityAuditLog;
import com.nextlabs.destiny.console.utils.SecurityContextUtil;

@Repository("entityAuditLogDao")
public class EntityAuditLogDao 
		extends GenericDaoImpl<EntityAuditLog, Long> {
	
	private static final Logger log = LoggerFactory.getLogger(EntityAuditLogDao.class);
	
    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    @Transactional
	public void addEntityAuditLog(AuditAction action, String entityType, Long entityId, String oldValue, String newValue)
			throws ConsoleException {
		EntityAuditLog entityAuditLog = new EntityAuditLog();
		
		try {
			entityAuditLog.setActorId(SecurityContextUtil.getCurrentUser().getUserId());
			entityAuditLog.setActor(SecurityContextUtil.getCurrentUser().getDisplayName().trim());
			entityAuditLog.setAction(action);
			entityAuditLog.setEntityType(entityType);
			entityAuditLog.setEntityId(entityId);
			entityAuditLog.setOldValue(oldValue);
			entityAuditLog.setNewValue(newValue);
			entityAuditLog.setTimestamp(System.currentTimeMillis());
			
			if((oldValue != null && oldValue.length() > 4000) || (newValue != null && newValue.length() > 4000)) {
				logToFile(entityAuditLog);
			} else {
				create(entityAuditLog);
			}
		} catch(Exception e) {
			throw new ConsoleException(e);
		}
	}
	
	public void addEntityAuditLog(AuditAction action, String entityType, String name, Long userId, String oldValue, String newValue)
			throws ConsoleException {
		EntityAuditLog entityAuditLog = new EntityAuditLog();
		
		try {
			entityAuditLog.setActorId(userId);
			entityAuditLog.setActor(name.trim());
			entityAuditLog.setAction(action);
			entityAuditLog.setEntityType(entityType);
			entityAuditLog.setEntityId(userId);
			entityAuditLog.setOldValue(oldValue);
			entityAuditLog.setNewValue(newValue);
			entityAuditLog.setTimestamp(System.currentTimeMillis());
			
			if((oldValue != null && oldValue.length() > 4000) || (newValue != null && newValue.length() > 4000)) {
				logToFile(entityAuditLog);
			} else {
				create(entityAuditLog);
			}
		} catch(Exception e) {
			throw new ConsoleException(e);
		}
	}
	
	private void logToFile(EntityAuditLog auditLog) {
    	if(log.isWarnEnabled()) {
			StringBuilder logMessage = new StringBuilder();

			logMessage.append(
							"Unable to insert entity audit log, because value has exceeded 4000 characters!");
			logMessage.append("\nEntity Audit Log Details:- ");
			logMessage.append("\nActor ID: " + auditLog.getActorId());
			logMessage.append("\nActor: " + auditLog.getActor());
			logMessage.append("\nAction: " + auditLog.getAction().toString());
			logMessage.append("\nEntity Type: " + AuditableEntity.getEntityType(auditLog.getEntityType()));
			logMessage.append("\nEntity ID: " + auditLog.getEntityId());
			logMessage.append("\nOld Value: " + auditLog.getOldValue());
			logMessage.append("\nNew Value: " + auditLog.getNewValue());
			logMessage.append("\nTimestamp: " + auditLog.getTimestamp());

			log.warn(logMessage.toString());
		}
	}
}
