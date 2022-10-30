package com.nextlabs.destiny.inquirycenter.user.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map;

import com.bluejungle.destiny.inquirycenter.enumeration.AuditAction;
import com.bluejungle.destiny.inquirycenter.enumeration.AuditableEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nextlabs.destiny.inquirycenter.JsonUtil;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.EntityAuditLogDAO;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.EntityAuditLogDAOImpl;
import com.nextlabs.destiny.inquirycenter.savedreport.dto.EntityAuditLogDO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.shared.applicationusers.core.IApplicationUserManager;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupReducedList;
import com.bluejungle.destiny.webui.framework.user.InvalidPasswordException;
import com.bluejungle.destiny.webui.framework.user.PasswordHistoryException;
import com.nextlabs.destiny.inquirycenter.user.dao.ApplicationUserDAO;
import com.nextlabs.destiny.inquirycenter.user.dao.impl.ApplicationUserDAOImpl;

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
	 * Returns the list of Application Users
	 * 
	 * @return UserDTOList
	 * @throws SQLException
	 * @throws HibernateException
	 */
	public UserDTOList getAllApplicationUsers() throws SQLException, HibernateException {
		UserDTOList userDTOList = appUserDao.getAllApplicationUsers();
		return userDTOList;
	}

	public UserDTOList getAllUsers() throws SQLException, HibernateException {
		return appUserDao.getAllUsers();
	}
	
	/**
	 * 
	 * Returns the list of User Groups
	 * 
	 * @return UserGroupReducedList
	 * @throws SQLException
	 * @throws HibernateException
	 */
	public UserGroupReducedList getAllUserGroups() throws SQLException, HibernateException {
		UserGroupReducedList userGroupList = appUserDao.getAllUserGroups();
		return userGroupList;
	}

	/**
	 * 
	 * @param username
	 * @return
	 * @throws SQLException
	 * @throws HibernateException
	 */
	public UserDTO findByUsername(String username) throws SQLException, HibernateException {
		UserDTO userDTO = appUserDao.findByUsername(username);
		return userDTO;
	}

	public UserGroupReduced findByUserGroupId(Long groupId) throws SQLException, HibernateException {
		return appUserDao.findByUserGroupId(groupId);
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

	public EntityAuditLogDAO getEntityAuditLogDAO() {
		if (entityAuditLogDAO == null) {
			synchronized (ApplicationUserDAOImpl.class) {
				if (entityAuditLogDAO == null) {
					entityAuditLogDAO = new EntityAuditLogDAOImpl();
					LOG.debug("Entity audit log DAO initialized.");
				}
			}
		}

		return entityAuditLogDAO;
	}
}
