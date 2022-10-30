package com.bluejungle.destiny.mgmtconsole.usersandroles.users.dao;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	 * Check if given userId is super user
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
	 */
	void updateSuperUserPassword(Long userId, String oldPassword, String newPassword)
			throws SQLException, HibernateException, InvalidPasswordException, PasswordHistoryException;
	
	/**
	 * Unlock user account
	 * @param userId
	 * @throws SQLException
	 * @throws HibernateException
	 */
	void unlockUser(Long userId) throws SQLException, HibernateException;

	Map<String, String> getAuditMap(Long userId, boolean isSuperUser);
}
