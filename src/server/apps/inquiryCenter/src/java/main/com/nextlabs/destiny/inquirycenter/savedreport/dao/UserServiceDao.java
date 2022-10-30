package com.nextlabs.destiny.inquirycenter.savedreport.dao;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.services.management.types.UserGroupReduced;

import net.sf.hibernate.HibernateException;

public interface UserServiceDao {

	public static final Log LOG = LogFactory.getLog(UserServiceDao.class);
	
	String getUserDisplayName(Long userId) throws SQLException, HibernateException;

	UserGroupReduced[] getAllUserGroups() throws SQLException, HibernateException;

	UserGroupReduced[] getUserGroupsByUserId(Long userId) throws SQLException, HibernateException;
}
