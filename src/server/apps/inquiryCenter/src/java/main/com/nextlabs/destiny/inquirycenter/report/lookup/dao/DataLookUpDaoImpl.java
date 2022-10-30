package com.nextlabs.destiny.inquirycenter.report.lookup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.DBUtil;
import com.bluejungle.destiny.types.policies.v1.Policy;
import com.bluejungle.destiny.types.users.v1.User;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

public class DataLookUpDaoImpl implements DataLookUpDao {

	public static final Log LOG = LogFactory.getLog(DataLookUpDao.class);

	@Override
	public List<Policy> lookUpPolicies() throws HibernateException, SQLException {
		List<Policy> policyList = new ArrayList<Policy>();
		Session session = null;
		ResultSet rs = null;
		PreparedStatement policyLookUpStmt = null;
		Connection connection = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = "SELECT c.name, c.fullname FROM CACHED_POLICY c ORDER BY c.fullname ";
			policyLookUpStmt = connection.prepareStatement(sqlQuery);

			rs = policyLookUpStmt.executeQuery();

			while (rs.next()) {
				String fullName = rs.getString(2);
				int index = fullName.lastIndexOf('/');
				String folderName = fullName.substring(1, index);
				String folder = "/"+folderName.toLowerCase();
				Policy policy = new Policy();
				policy.setName(rs.getString(1));
				policy.setFolderName(folder);
				
				policyList.add(policy);
			}

			LOG.debug("Policy lookup data found, No of policies = " + rs.getFetchSize());

		} finally {
			if (rs != null)
				rs.close();
			if (policyLookUpStmt != null)
				policyLookUpStmt.close();
			if (connection != null)
				connection.close();
			if (session != null)
				session.close();
		}
		return policyList;
	}

	@Override
	public List<User> lookUpUsers() throws HibernateException, SQLException {
		List<User> users = new ArrayList<User>();
		Session session = null;
		ResultSet rs = null;
		PreparedStatement userLookUpStmt = null;
		Connection connection = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = "SELECT c.first_name,c.last_name,c.display_name "
					+ "FROM CACHED_USER c WHERE c.original_id != ?  " + "AND c.active_from <= ? AND c.active_to > ?";

			userLookUpStmt = connection.prepareStatement(sqlQuery);

			int currArg = 1;
			Long asOfTime = new Long(Calendar.getInstance().getTimeInMillis());
			Long unknownId = new Long(-1L);
			userLookUpStmt.setLong(currArg++, unknownId);
			userLookUpStmt.setLong(currArg++, asOfTime);
			userLookUpStmt.setLong(currArg++, asOfTime);
		 	Instant beforeExecuteQuery = Instant.now();

			rs = userLookUpStmt.executeQuery();

			Instant afterExecuteQuery = Instant.now();
			LOG.debug("user lookup >>>> query >>>> " + userLookUpStmt.toString());
			LOG.debug("executed user lookup in >>>> query >>>> " + Duration.between(beforeExecuteQuery, afterExecuteQuery).toMillis());

			while (rs.next()) {
				User user = new User();
				user.setFirstName(rs.getString(1));
				user.setLastName(rs.getString(2));
				user.setDisplayName(rs.getString(3));

				users.add(user);
			}

			LOG.debug("User lookup data found, no of users = " + rs.getFetchSize());

		} finally {
			if (rs != null)
				rs.close();
			if (userLookUpStmt != null)
				userLookUpStmt.close();
			if (connection != null)
				connection.close();
			if (session != null)
				session.close();
		}

		return users;
	}
}
