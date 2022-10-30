package com.nextlabs.destiny.inquirycenter.user.dao;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupReducedList;
import com.bluejungle.destiny.webui.framework.user.InvalidPasswordException;
import com.bluejungle.destiny.webui.framework.user.PasswordHistoryException;

import net.sf.hibernate.HibernateException;

/**
 * 
 * DAO Interface for Application User
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public interface ApplicationUserDAO {

	public static final Log LOG = LogFactory.getLog(ApplicationUserDAO.class);
	
	/**
	 * Find a user by username
	 * 
	 * @param username
	 * @return
	 * @throws SQLException
	 * @throws HibernateException
	 */
    UserDTO findByUsername(String username) throws SQLException, HibernateException;
	
	/**
	 * Fetches the list of all active application users
	 * 
	 * @return UserDTOList
	 * @throws SQLException
	 * @throws HibernateException
	 */
	UserDTOList getAllApplicationUsers() throws SQLException, HibernateException;

	/**
	 * Fetch all users in the system, regardless of status and type.
	 * Including administrators
	 * 
	 * @return UserDTOList
	 * @throws SQLException
	 * @throws HibernateException
	 */
	UserDTOList getAllUsers() throws SQLException, HibernateException;

	/**
	 * Fetches all user groups
	 * 
	 * @return UserGroupReducedList
	 * @throws SQLException
	 * @throws HibernateException
	 */
	UserGroupReducedList getAllUserGroups() throws SQLException, HibernateException;
	
	/**
	 * Find user group by group id
	 * 
	 * @param groupId
	 * @return
	 * @throws SQLException
	 * @throws HibernateException
	 */
	UserGroupReduced findByUserGroupId(Long groupId) throws SQLException, HibernateException;
	
	/**
	 * Check if the given userId is super user
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws HibernateException
	 */
	boolean isSuperUser(Long userId) throws SQLException, HibernateException;
	
	/**
	 * Updates password of a given user
	 * 
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @throws SQLException
	 * @throws HibernateException
	 * @throws InvalidPasswordException
	 * @throws PasswordHistoryException
	 */
	void updatePassword (Long userId, String oldPassword, String newPassword)
			throws SQLException, HibernateException, InvalidPasswordException, PasswordHistoryException;
	
	/**
	 * Updates password of super user
	 * 
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @throws SQLException
	 * @throws HibernateException
	 * @throws InvalidPasswordException
	 * @throws PasswordHistoryException
	 */
	void updateSuperUserPassword(Long userId, String oldPassword, String newPassword)
			throws SQLException, HibernateException, InvalidPasswordException, PasswordHistoryException;

	Map<String, String> getAuditMap(Long userId, boolean isSuperUser);
}
