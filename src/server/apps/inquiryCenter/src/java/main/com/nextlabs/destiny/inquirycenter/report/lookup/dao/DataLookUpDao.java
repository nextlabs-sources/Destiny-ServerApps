package com.nextlabs.destiny.inquirycenter.report.lookup.dao;

import java.sql.SQLException;
import java.util.List;

import com.bluejungle.destiny.types.policies.v1.Policy;
import com.bluejungle.destiny.types.users.v1.User;

import net.sf.hibernate.HibernateException;

public interface DataLookUpDao {

	List<Policy> lookUpPolicies() throws HibernateException, SQLException;

	List<User> lookUpUsers() throws HibernateException, SQLException;

}
