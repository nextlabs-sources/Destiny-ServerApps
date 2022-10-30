package com.nextlabs.destiny.inquirycenter.user.dao.impl;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.webui.framework.authentication.SecurePasswordEncoderConfig;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import net.sf.hibernate.Transaction;
import org.bouncycastle.util.Arrays;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.DBUtil;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupReducedList;
import com.bluejungle.destiny.webui.framework.user.InvalidPasswordException;
import com.bluejungle.destiny.webui.framework.user.PasswordHistoryException;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository.DbType;
import com.nextlabs.destiny.configclient.Config;
import com.nextlabs.destiny.configclient.ConfigClient;
import com.nextlabs.destiny.inquirycenter.user.dao.ApplicationUserDAO;
import com.nextlabs.destiny.webui.IDGenerator;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 
 * DAO Implementation for Application User
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class ApplicationUserDAOImpl implements ApplicationUserDAO {

	private final Config enforcePasswordHistoryConfig = ConfigClient.get("enforce.password.history");

	PasswordEncoder delegatingPasswordEncoder = SecurePasswordEncoderConfig.getDelegatingPasswordEncoder();

    @Override
    public UserDTO findByUsername(String username) throws SQLException, HibernateException {
        UserDTO userDTO = new UserDTO();
        Session session = null;
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement usersStmt = null;
        try {
            IHibernateRepository dataSource = DBUtil.getActivityDataSource();
            session = dataSource.getSession();
            connection = session.connection();

            String sqlQuery = "SELECT USERNAME, FIRST_NAME, LAST_NAME, PASSWORD FROM APP_USER_VIEW WHERE USERNAME = ? ";
            username = username.trim();
            usersStmt = connection.prepareStatement(sqlQuery);
            usersStmt.setString(1, username);

            rs = usersStmt.executeQuery();

            while (rs.next()) {
                userDTO.setUniqueName(rs.getString(1));
                userDTO.setFirstName(rs.getString(2));
                userDTO.setLastName(rs.getString(3));
                userDTO.setPassword(rs.getString(4));
            }
            LOG.debug("User found by username  = " + username + " firstName = " + userDTO.getFirstName());
        } finally {
            if (rs != null)
                rs.close();
            if(usersStmt != null)
            	usersStmt.close();
            if(connection != null)
                connection.close();
            if (session != null)
                session.close();
        }
        return userDTO;
    }
	
	@Override
	public UserDTOList getAllApplicationUsers() throws SQLException, HibernateException {
		UserDTOList usersDTOList = new UserDTOList();

		List<UserDTO> usersList = new ArrayList<UserDTO>();

		Session session = null;
		ResultSet rs = null;
		PreparedStatement getAllUsersStmt = null;
		Connection connection = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = "SELECT u.ID, u.USER_TYPE, u.USERNAME, u.FIRST_NAME, u.LAST_NAME FROM APPLICATION_USER u WHERE u.STATUS != ? ";
			getAllUsersStmt = connection.prepareStatement(sqlQuery);
			int currArg = 1;
			getAllUsersStmt.setString(currArg++, "DELETED");

			rs = getAllUsersStmt.executeQuery();

			while (rs.next()) {
				UserDTO userDTO = new UserDTO();
				Long idVal = rs.getLong(1);
				ID userId = new ID();
				userId.setID(BigInteger.valueOf(idVal));
				userDTO.setId(userId);
				userDTO.setType(rs.getString(2));
				userDTO.setUniqueName(rs.getString(3));
				userDTO.setFirstName(rs.getString(4));
				userDTO.setLastName(rs.getString(5));
				usersList.add(userDTO);
				LOG.info("UserId and Username are = " + userId + "  " + userDTO.getUniqueName());
			}

			LOG.info("Application users loaded successfully, total no of users = " + usersList.size());

		} finally {
			if (rs != null)
				rs.close();
			if (getAllUsersStmt != null)
				getAllUsersStmt.close();
			if (connection != null)
				connection.close();
			if (session != null)
				session.close();
		}

		UserDTO[] users = new UserDTO[usersList.size()];
		users = usersList.toArray(users);
		usersDTOList.setUsers(users);

		return usersDTOList;
	}

	@Override
	public UserDTOList getAllUsers() throws SQLException, HibernateException {
		UserDTOList usersDTOList = new UserDTOList();
		List<UserDTO> usersList = new ArrayList<UserDTO>();
		
		Session session = null;
		ResultSet rs = null;
		PreparedStatement getAllUsersStmt = null;
		Connection connection = null;

		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();
			
			String sqlQuery = "SELECT u.ID, u.USER_TYPE, u.USERNAME, u.FIRST_NAME, u.LAST_NAME FROM APPLICATION_USER u UNION " +
							  "SELECT su.ID, 'internal', su.USERNAME, su.FIRST_NAME, su.LAST_NAME FROM SUPER_APPLICATION_USER su";
			getAllUsersStmt = connection.prepareStatement(sqlQuery);
			rs = getAllUsersStmt.executeQuery();

			while (rs.next()) {
				UserDTO userDTO = new UserDTO();
				Long idVal = rs.getLong(1);
				ID userId = new ID();
				userId.setID(BigInteger.valueOf(idVal));
				userDTO.setId(userId);
				userDTO.setType(rs.getString(2));
				userDTO.setUniqueName(rs.getString(3));
				userDTO.setFirstName(rs.getString(4));
				userDTO.setLastName(rs.getString(5));
				usersList.add(userDTO);
				LOG.info("UserId and Username are = " + userId + "  " + userDTO.getUniqueName());
			}

			LOG.info("Application users loaded successfully, total no of users = " + usersList.size());
		} finally {
			if(rs != null)
				rs.close();
			
			if(getAllUsersStmt != null)
				getAllUsersStmt.close();
			
			if(connection != null)
				connection.close();
			
			if(session != null)
				session.close();
		}

		UserDTO[] users = new UserDTO[usersList.size()];
		users = usersList.toArray(users);
		usersDTOList.setUsers(users);

		return usersDTOList;
	}
	
	public UserGroupReducedList getAllUserGroups() throws SQLException, HibernateException {
		UserGroupReducedList userGroupsList = new UserGroupReducedList();

		List<UserGroupReduced> userGroupList = new ArrayList<UserGroupReduced>();

		Session session = null;
		ResultSet rs = null;
		PreparedStatement getAllUserGroupStmt = null;
		Connection connection = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = " SELECT g.ID, g.TITLE FROM ACCESS_GROUP g ";
			getAllUserGroupStmt = connection.prepareStatement(sqlQuery);

			rs = getAllUserGroupStmt.executeQuery();

			while (rs.next()) {
				UserGroupReduced userGroup = new UserGroupReduced();
				Long idVal = rs.getLong(1);
				ID groupId = new ID();
				groupId.setID(BigInteger.valueOf(idVal));
				userGroup.setId(groupId);
				userGroup.setTitle(rs.getString(2));
				userGroupList.add(userGroup);
				LOG.info("GroupId and Username are = " + groupId + "  " + userGroup.getTitle());
			}

			LOG.info("Application user groups loaded successfully, total no of users = " + userGroupList.size());

		} finally {
			if (rs != null)
				rs.close();
			if (getAllUserGroupStmt != null)
				getAllUserGroupStmt.close();
			if (connection != null)
				connection.close();
			if (session != null)
				session.close();
		}

		UserGroupReduced[] userGroups = new UserGroupReduced[userGroupList.size()];
		userGroups = userGroupList.toArray(userGroups);
		userGroupsList.setUserGroupReduced(userGroups);

		return userGroupsList;
	}
	
	@Override
	public boolean isSuperUser(Long userId) throws SQLException, HibernateException {
		Session session = null;
		ResultSet rs = null;
		PreparedStatement selectSuperUserStmt = null;
		Connection connection = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = "SELECT COUNT(ID) AS TOTAL FROM SUPER_APPLICATION_USER WHERE ID = ? ";
			selectSuperUserStmt = connection.prepareStatement(sqlQuery);
			selectSuperUserStmt.setLong(1, userId);

			rs = selectSuperUserStmt.executeQuery();

			if (rs.next()) {
				return (rs.getInt("TOTAL") > 0);
			}
		} finally {
			if (rs != null)
				rs.close();
			if(selectSuperUserStmt != null)
				selectSuperUserStmt.close();
			if(connection != null)
				connection.close();
			if (session != null)
				session.close();
		}
		return false;
	}
	
	@Override
	public UserGroupReduced findByUserGroupId(Long groupId) throws SQLException, HibernateException {
		UserGroupReduced userGroup = null;
		
		Session session = null;
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement userGroupStmt = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = "SELECT ID, TITLE FROM ACCESS_GROUP WHERE ID = ?";
			userGroupStmt = connection.prepareStatement(sqlQuery);
			userGroupStmt.setLong(1, groupId);

			rs = userGroupStmt.executeQuery();

			if (rs.next()) {
				userGroup = new UserGroupReduced();
				Long id = rs.getLong(1);
				ID userGroupId = new ID();
				userGroupId.setID(BigInteger.valueOf(id));
				userGroup.setId(userGroupId);
				userGroup.setTitle(rs.getString(2));

				LOG.debug("User group found by groupId  = " + groupId + ", title = " + userGroup.getTitle());
			}
		} finally {
			if (rs != null)
				rs.close();
			if (userGroupStmt != null)
				userGroupStmt.close();
			if (connection != null)
				connection.close();
			if (session != null)
				session.close();
		}
		
		return userGroup;
	}
	
	@Override
	public void updatePassword(Long userId, String oldPassword, String newPassword)
			throws SQLException, HibernateException, InvalidPasswordException, PasswordHistoryException {
		Session session = null;
		ResultSet rs = null;
		PreparedStatement updatePwd = null;
		Connection connection = null;

		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = "SELECT ID, PASSWORD FROM APPLICATION_USER WHERE ID = ? ";
			updatePwd = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			updatePwd.setLong(1, userId);
			
			rs = updatePwd.executeQuery();
			
			while(rs.next()) {
				byte[] encodedOldPassword = rs.getBytes(2);
				if(!delegatingPasswordEncoder.matches(oldPassword, new String(encodedOldPassword)))
					throw new InvalidPasswordException("Current password not valid, user authetication fails");

				int enforcePasswordHistory = enforcePasswordHistoryConfig.toInt();
				if(enforcePasswordHistory > 0 && isPasswordRepeated(connection, userId, newPassword, enforcePasswordHistory))
					throw new PasswordHistoryException("Unable to update the password. The value provided for the new password does not meet the password history requirement.");

				rs.updateBytes(2, delegatingPasswordEncoder.encode(newPassword).getBytes());
				rs.updateRow();
				
				insertPasswordHistory(connection, userId, encodedOldPassword, dataSource.getDatabaseType());
			}

			LOG.info("User password updated successfully");
		} finally {
			if (rs != null)
				rs.close();
			if(updatePwd != null)
				updatePwd.close();
			if(connection != null)
				connection.close();
			if (session != null)
				session.close();
		}
	}
	
	@Override
	public void updateSuperUserPassword(Long userId, String oldPassword, String newPassword)
			throws SQLException, HibernateException, InvalidPasswordException, PasswordHistoryException {
		Session session = null;
		ResultSet rs = null;
		PreparedStatement updatePwd = null;
		Connection connection = null;

		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = "SELECT ID, PASSWORD FROM SUPER_APPLICATION_USER WHERE ID = ? ";
			updatePwd = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			updatePwd.setLong(1, userId);
			
			rs = updatePwd.executeQuery();
			
			while(rs.next()) {
				byte [] encodedOldPassword = rs.getBytes(2);
				if(!delegatingPasswordEncoder.matches(oldPassword, new String(encodedOldPassword)))
					throw new InvalidPasswordException("Current password not valid, user authetication fails");

				int enforcePasswordHistory = enforcePasswordHistoryConfig.toInt();
				if(enforcePasswordHistory > 0 && isPasswordRepeated(connection, userId, newPassword, enforcePasswordHistory))
					throw new PasswordHistoryException("Unable to update the password. The value provided for the new password does not meet the password history requirement.");

				rs.updateBytes(2, delegatingPasswordEncoder.encode(newPassword).getBytes());
				rs.updateRow();
				
				insertPasswordHistory(connection, userId, encodedOldPassword, dataSource.getDatabaseType());
			}

			LOG.info("Super User password updated successfully");
		} finally {
			if (rs != null)
				rs.close();
			if(updatePwd != null)
				updatePwd.close();
			if(connection != null)
				connection.close();
			if (session != null)
				session.close();
		}
	}

	private boolean isPasswordRepeated(Connection connection, Long userId, String newPassword, int enforcePasswordHistory)
		throws SQLException {
		ResultSet passwordHistories = null;
		PreparedStatement passwordHistory = null;
		
		try {
			int loopCount = 1;
			String sql = "SELECT PASSWORD FROM PASSWORD_HISTORY WHERE USER_ID = ? ORDER BY TIMESTAMP DESC";
			passwordHistory = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			passwordHistory.setLong(1, userId);
			
			passwordHistories = passwordHistory.executeQuery();
			
			while(loopCount < enforcePasswordHistory && passwordHistories.next()) {
				if(delegatingPasswordEncoder.matches(newPassword, new String(passwordHistories.getBytes(1))))
					return true;
				
				loopCount++;
			}
		} finally {
			if(passwordHistories != null)
				passwordHistories.close();
			
			if(passwordHistory != null)
				passwordHistory.close();
		}
		
		return false;
	}
	
	private void insertPasswordHistory(Connection connection, Long userId, byte[] password, DbType dbType)
		throws SQLException {
		PreparedStatement passwordHistory = null;
		
		try {
			if(IHibernateRepository.DbType.MS_SQL.equals(dbType)) {
				String sql = "INSERT INTO PASSWORD_HISTORY (USER_ID, PASSWORD, TIMESTAMP) VALUES (?, ?, ?)";
				passwordHistory = connection.prepareStatement(sql);
				passwordHistory.setLong(1, userId);
				passwordHistory.setBytes(2, password);
				passwordHistory.setLong(3, System.currentTimeMillis());
			} else {
				String sql = "INSERT INTO PASSWORD_HISTORY (ID, USER_ID, PASSWORD, TIMESTAMP) VALUES (?, ?, ?, ?)";
				passwordHistory = connection.prepareStatement(sql);
				passwordHistory.setLong(1, IDGenerator.generate());
				passwordHistory.setLong(2, userId);
				passwordHistory.setBytes(3, password);
				passwordHistory.setLong(4, System.currentTimeMillis());
			}
			
			passwordHistory.execute();
		} finally {
			if(passwordHistory != null)
				passwordHistory.close();
		}
	}

	public Map<String, String> getAuditMap(Long userId, boolean isSuperUser) {
		Session session = null;
		Transaction t = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "";
		Map<String, String> propsMap = new LinkedHashMap<>();
		Map<String, String> userAttributes = new LinkedHashMap<>();
		try {
			if (isSuperUser) {
				query = "SELECT u.ID, u.USERNAME, u.DISPLAYNAME, u.FIRST_NAME, u.EMAIL, u.LAST_NAME, u.DOMAIN_ID, d.NAME  as DOMAIN, 'internal' as USER_TYPE, 'ADMIN' as USER_CATEGORY,"
						+ "  '0' as AUTH_HANDLER_ID FROM SUPER_APPLICATION_USER u LEFT JOIN APPLICATION_USER_DOMAIN d ON ( d.ID = u.DOMAIN_ID)"
						+ "  WHERE u.ID = ? ";
			} else {
				query = "SELECT u.ID, u.USERNAME, u.DISPLAYNAME, u.FIRST_NAME, u.Email, u.LAST_NAME, u.DOMAIN_ID, d.NAME as DOMAIN, u.USER_TYPE, u.USER_CATEGORY,"
						+ " u.AUTH_HANDLER_ID FROM APPLICATION_USER u LEFT JOIN APPLICATION_USER_DOMAIN d ON ( d.ID = u.DOMAIN_ID)"
						+ " WHERE u.ID = ? ";
			}

			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			t = session.beginTransaction();
			Connection conn = session.connection();
			stmt = conn.prepareStatement(query);
			stmt.setLong(1, userId);

			rs = stmt.executeQuery();

			while (rs.next()) {
				propsMap.put("User Type", rs.getString("USER_TYPE"));
				propsMap.put("User Category", rs.getString("USER_CATEGORY"));
				propsMap.put("First Name", rs.getString("FIRST_NAME"));
				propsMap.put("Last Name", rs.getString("LAST_NAME"));
				propsMap.put("Username", rs.getString("USERNAME"));
				propsMap.put("Display Name", rs.getString("DISPLAYNAME"));
				propsMap.put("Email", rs.getString("EMAIL"));
				propsMap.put("Domain ID", rs.getString("ID"));
				propsMap.put("Authentication Handler ID", rs.getString("AUTH_HANDLER_ID"));
			}

			rs.close();
			stmt.close();
			t.commit();
		} catch (Exception e) {
			LOG.error("Error occurred during fetch application user properties", e);
		} finally {
			if(rs != null) {
				 try {
				 	rs.close();
				 } catch (SQLException err) {
				 	// ignore
				 }
			}

			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException err) {
					// ignore
				}
			}

			HibernateUtils.closeSession(session, LOG);
		}
		return propsMap;
	}
}
