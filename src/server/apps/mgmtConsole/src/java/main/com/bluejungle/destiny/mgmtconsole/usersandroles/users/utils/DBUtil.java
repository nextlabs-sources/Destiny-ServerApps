package com.bluejungle.destiny.mgmtconsole.usersandroles.users.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;

/**
 * @author nnallagatla
 * Util class to close Connection, Statement & ResultSet in a graceful way
 */
public class DBUtil {
	
	private static final Log LOG = LogFactory.getLog(DBUtil.class
			.getName());
	
	/**
	 * 
	 * @param con
	 * @param stmt
	 * @param rs
	 */
	public static void close(Connection con, Statement stmt, ResultSet rs)
	{
		closeResultSet(rs);
		closeStatement(stmt);
		closeConnection(con);
	}
	
	/**
	 * 
	 * @param con
	 */
	public static void closeConnection(Connection con)
	{
		try
		{
			if (con == null || con.isClosed())
			{
				return;
			}
			con.close();
		}
		catch (SQLException e)
		{
			LOG.error("Error closing connection", e);
		}
	}
	
	/**
	 * 
	 * @param stmt
	 */
	public static void closeStatement(Statement stmt)
	{
		try
		{
			if (stmt == null)
			{
				return;
			}
			stmt.close();
		}
		catch (SQLException e)
		{
			LOG.error("Error closing statement", e);
		}
	}
	
	/**
	 * 
	 * @param rs
	 */
	public static void closeResultSet(ResultSet rs)
	{
		try
		{
			if (rs == null)
			{
				return;
			}
			rs.close();
		}
		catch (SQLException e)
		{
			LOG.error("Error closing resultset", e);
		}
	}
	
	/**
	 * Get the data source for doing DB operations
	 * @return
	 */
	public static IHibernateRepository getActivityDataSource()
	{
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		return (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());
	}
	
	/**
	 * Get the data source for doing DB operations
	 * @return
	 */
	public static IHibernateRepository getManagementDataSource()
	{
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		return (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.MANAGEMENT_REPOSITORY.getName());
	}
	
	/**
	 * Get the data source for doing DB operations
	 * @return
	 */
	public static IHibernateRepository getPFDataSource()
	{
		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		return (IHibernateRepository) compMgr
				.getComponent(DestinyRepository.POLICY_FRAMEWORK_REPOSITORY.getName());
	}
	
	
}

