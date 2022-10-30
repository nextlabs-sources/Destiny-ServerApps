package com.bluejungle.destiny.mgmtconsole.usersandroles.users.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.dao.EntityAuditLogDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.enumeration.AuditableEntity;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.utils.DBUtil;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.dto.EntityAuditLogDO;
import com.nextlabs.destiny.webui.IDGenerator;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

public class EntityAuditLogDAOImpl implements EntityAuditLogDAO {
	
	private static final Log log = LogFactory.getLog(EntityAuditLogDAOImpl.class);
	
	private static final String ORACLE_INSERT_LOG = "INSERT INTO ENTITY_AUDIT_LOG (ID, TIMESTAMP, ACTION, ACTOR_ID, ACTOR, ENTITY_TYPE, ENTITY_ID, OLD_VALUE, NEW_VALUE)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String MSSQL_INSERT_LOG = "INSERT INTO ENTITY_AUDIT_LOG (TIMESTAMP, ACTION, ACTOR_ID, ACTOR, ENTITY_TYPE, ENTITY_ID, OLD_VALUE, NEW_VALUE)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	
	@Override
	public void create(EntityAuditLogDO auditLog) throws SQLException, HibernateException {
		if((auditLog.getOldValue() != null && auditLog.getOldValue().length() > 4000) 
				|| (auditLog.getNewValue() != null && auditLog.getNewValue().length() > 4000)) {
			logToFile(auditLog);
		} else {
			logToDB(auditLog);
		}
	}

	@Override
	public EntityAuditLogDO lookup(Long auditLogId) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EntityAuditLogDO> getAll() throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void logToDB(EntityAuditLogDO auditLog)
			throws SQLException, HibernateException {
		Session session = null;
		Connection connection = null;
		PreparedStatement insertLog = null;

		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();
			if(IHibernateRepository.DbType.MS_SQL.equals(dataSource.getDatabaseType())) {
				insertLog = connection.prepareStatement(MSSQL_INSERT_LOG);
	
				insertLog.setLong(1, System.currentTimeMillis());
				insertLog.setString(2, auditLog.getAction());
				insertLog.setLong(3, auditLog.getActorId());
				insertLog.setString(4, auditLog.getActor() != null ? auditLog.getActor().trim() : null);
				insertLog.setString(5, auditLog.getEntityType());
				insertLog.setLong(6, auditLog.getEntityId());
				insertLog.setString(7, auditLog.getOldValue());
				insertLog.setString(8, auditLog.getNewValue());
			} else {
				insertLog = connection.prepareStatement(ORACLE_INSERT_LOG);
				
				insertLog.setLong(1, IDGenerator.generate());
				insertLog.setLong(2, System.currentTimeMillis());
				insertLog.setString(3, auditLog.getAction());
				insertLog.setLong(4, auditLog.getActorId());
				insertLog.setString(5, auditLog.getActor() != null ? auditLog.getActor().trim() : null);
				insertLog.setString(6, auditLog.getEntityType());
				insertLog.setLong(7, auditLog.getEntityId());
				insertLog.setString(8, auditLog.getOldValue());
				insertLog.setString(9, auditLog.getNewValue());
			}

			insertLog.execute();
		} finally {
			if(insertLog != null)
				insertLog.close();
			if(connection != null)
				connection.close();
			HibernateUtils.closeSession(session, log);
		}
	}
	
	private void logToFile(EntityAuditLogDO auditLog) {
		StringBuilder logMessage = new StringBuilder();
		
		logMessage.append("Unable to insert entity audit log, because value has exceeded 4000 characters!");
		logMessage.append("\nEntity Audit Log Details:- ");
		logMessage.append("\nActor ID: " + auditLog.getActorId());
		logMessage.append("\nActor: " + auditLog.getActor());
		logMessage.append("\nAction: " + auditLog.getAction().toString());
		logMessage.append("\nEntity Type: " + AuditableEntity.getEntityType(auditLog.getEntityType()));
		logMessage.append("\nEntity ID: " + auditLog.getEntityId());
		logMessage.append("\nOld Value: " + auditLog.getOldValue());
		logMessage.append("\nNew Value: " + auditLog.getNewValue());
		logMessage.append("\nTimestamp: " + System.currentTimeMillis());
		
		log.warn(logMessage.toString());
	}
}
