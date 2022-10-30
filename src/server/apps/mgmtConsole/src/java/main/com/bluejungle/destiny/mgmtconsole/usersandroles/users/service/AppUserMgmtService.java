package com.bluejungle.destiny.mgmtconsole.usersandroles.users.service;

import java.sql.SQLException;
import java.util.Map;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.dao.EntityAuditLogDAO;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.dao.impl.EntityAuditLogDAOImpl;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.dto.EntityAuditLogDO;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.enumeration.AuditAction;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.enumeration.AuditableEntity;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.dao.ApplicationUserDAO;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.dao.impl.ApplicationUserDAOImpl;
import com.bluejungle.destiny.webui.framework.user.InvalidPasswordException;
import com.bluejungle.destiny.webui.framework.user.PasswordHistoryException;

import net.sf.hibernate.HibernateException;
import org.json.JSONException;

/**
 * Service Layer for Application User Management
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class AppUserMgmtService {

	public static final Log LOG = LogFactory.getLog(AppUserMgmtService.class);
	private ApplicationUserDAO appUserDao;
	private EntityAuditLogDAO entityAuditLogDAO;

	/**
	 * constructor
	 */
	public AppUserMgmtService() {
		if (appUserDao == null) {
			appUserDao = new ApplicationUserDAOImpl();
		}
	}

	/**
	 * Updates password of a given user
	 * 
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @throws SQLException
	 * @throws HibernateException
	 * @throws InvalidPasswordException
	 */
	public void changePassword(Long userId, String oldPassword, String newPassword)
			throws SQLException, HibernateException, InvalidPasswordException, PasswordHistoryException {
		if (userId != null) {
			// check if super user
			if (appUserDao.isSuperUser(userId)) {
				// update super user password
				appUserDao.updateSuperUserPassword(userId, oldPassword, newPassword);
				auditLogin(userId, true);
			} else {
				// update password
				appUserDao.updatePassword(userId, oldPassword, newPassword);
				auditLogin(userId, false);
			}
		}
	}

	public void unlockUser(Long userId)
			throws SQLException, HibernateException {
		if(userId != null) {
			appUserDao.unlockUser(userId);
		}
	}



	private void auditLogin(Long userId, boolean isSuperUser) {
		try {
			Map<String, String> propsMap = appUserDao.getAuditMap(userId, isSuperUser);
			EntityAuditLogDO auditLog = new EntityAuditLogDO();
			auditLog.setAction(AuditAction.CHANGE_PASSWORD.name());
			auditLog.setActor(propsMap.get("Display Name"));
			auditLog.setActorId(userId);
			auditLog.setEntityId(userId);
			auditLog.setEntityType(AuditableEntity.APPLICATION_USER.getCode());
			auditLog.setNewValue(JsonUtil.toJsonString(propsMap));

			getEntityAuditLogDAO().create(auditLog);
		} catch (SQLException | HibernateException | JsonProcessingException | JSONException e) {
			LOG.error("Error occurred in creating login audit logs.", e);
		}
	}

	public synchronized EntityAuditLogDAO getEntityAuditLogDAO() {
		if (entityAuditLogDAO == null) {
			entityAuditLogDAO = new EntityAuditLogDAOImpl();
			LOG.debug("Entity audit log DAO initialized.");
		}

		return entityAuditLogDAO;
	}
}
