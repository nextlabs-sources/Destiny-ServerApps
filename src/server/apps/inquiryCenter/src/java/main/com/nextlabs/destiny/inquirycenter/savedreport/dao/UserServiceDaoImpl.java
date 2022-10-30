package com.nextlabs.destiny.inquirycenter.savedreport.dao;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.DBUtil;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

public class UserServiceDaoImpl implements UserServiceDao {
	
	@Override
	public String getUserDisplayName(Long userId)
			throws SQLException, HibernateException {
		Session session = null;
		Connection connection = null;
		PreparedStatement getAppUserStmt = null;
		PreparedStatement getSuperUserStmt = null;
		ResultSet appUserResultSet = null;
		ResultSet superUserResultSet = null;
		
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();
			
			getAppUserStmt = connection.prepareStatement("SELECT DISPLAYNAME, FIRST_NAME, LAST_NAME FROM APPLICATION_USER WHERE ID = ?");
			getAppUserStmt.setLong(1, userId);
			appUserResultSet = getAppUserStmt.executeQuery();
			
			if(appUserResultSet.next()) {
				String displayName = appUserResultSet.getString("DISPLAYNAME");
				String firstName = appUserResultSet.getString("FIRST_NAME");
				String lastName = appUserResultSet.getString("LAST_NAME");
				
				if(displayName != null
						&& displayName.trim().length() > 0) {
					return displayName; 
				} else {
					displayName = firstName + " " + (lastName == null ? "" : lastName);
					return displayName.trim();
				}
			} else {
				getSuperUserStmt = connection.prepareStatement("SELECT DISPLAYNAME, FIRST_NAME, LAST_NAME FROM SUPER_APPLICATION_USER WHERE ID = ?");
				getSuperUserStmt.setLong(1, userId);
				superUserResultSet = getSuperUserStmt.executeQuery();
				
				if(superUserResultSet.next()) {
					String displayName = superUserResultSet.getString("DISPLAYNAME");
					String firstName = superUserResultSet.getString("FIRST_NAME");
					String lastName = superUserResultSet.getString("LAST_NAME");
					
					if(displayName != null
							&& displayName.trim().length() > 0) {
						return displayName;
					} else {
						displayName = firstName + " " + (lastName == null ? "" : lastName);
						return displayName.trim();
					}
				}
			}
		} finally {
			if(appUserResultSet != null)
				appUserResultSet.close();
			
			if(superUserResultSet != null)
				superUserResultSet.close();
			
			if(getAppUserStmt != null) 
				getAppUserStmt.close();
			
			if(getSuperUserStmt != null)
				getSuperUserStmt.close();
			
			if(connection != null)
				connection.close();
			
			if(session != null)
				session.close();
		}
		
		return "Unknown";
	}
	
	@Override
	public UserGroupReduced[] getAllUserGroups() throws SQLException, HibernateException {
		List<UserGroupReduced> groupsList = new ArrayList<UserGroupReduced>();
		Session session = null;
		ResultSet rs = null;
		PreparedStatement getUserGroupsStmt = null;
		Connection connection = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = "SELECT gm.access_group_id FROM APP_USER_GROUP_MEMBERSHIP gm";
			getUserGroupsStmt = connection.prepareStatement(sqlQuery);

			rs = getUserGroupsStmt.executeQuery();

			while (rs.next()) {

				UserGroupReduced userGroup = new UserGroupReduced();
				Long groupId = rs.getLong(1);
				ID userGroupId = new ID();
				userGroupId.setID(BigInteger.valueOf(groupId));
				userGroup.setId(userGroupId);
				groupsList.add(userGroup);
			}

			LOG.debug("All user groups loaded successfully, size of user groups list" + groupsList.size());

		} finally {
			if (rs != null)
				rs.close();
			if (getUserGroupsStmt != null)
				getUserGroupsStmt.close();
			if (connection != null)
				connection.close();
			if (session != null)
				session.close();
		}
		UserGroupReduced[] userGroups = new UserGroupReduced[groupsList.size()];
		userGroups = groupsList.toArray(userGroups);
		return userGroups;
	}

	@Override
	public UserGroupReduced[] getUserGroupsByUserId(Long userId) throws SQLException, HibernateException {
		List<UserGroupReduced> groupsList = new ArrayList<UserGroupReduced>();
		Session session = null;
		ResultSet rs = null;
		PreparedStatement getUserGroupsStmt = null;
		Connection connection = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = "SELECT gm.access_group_id FROM APP_USER_GROUP_MEMBERSHIP gm WHERE gm.application_user_id = ? ";
			getUserGroupsStmt = connection.prepareStatement(sqlQuery);
			int currArg = 1;
			getUserGroupsStmt.setLong(currArg++, userId);

			rs = getUserGroupsStmt.executeQuery();

			while (rs.next()) {

				UserGroupReduced userGroup = new UserGroupReduced();
				Long groupId = rs.getLong(1);
				ID userGroupId = new ID();
				userGroupId.setID(BigInteger.valueOf(groupId));
				userGroup.setId(userGroupId);
				groupsList.add(userGroup);
			}

			LOG.debug("User groups found by user id, Size of user groups list" + groupsList.size());

		} finally {
			if (rs != null)
				rs.close();
			if (getUserGroupsStmt != null)
				getUserGroupsStmt.close();
			if (connection != null)
				connection.close();
			if (session != null)
				session.close();
		}
		UserGroupReduced[] userGroups = new UserGroupReduced[groupsList.size()];
		userGroups = groupsList.toArray(userGroups);
		return userGroups;
	}

}
