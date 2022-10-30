/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 22, 2016
 *
 */
package com.nextlabs.destiny.console.dao.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.ApplicationUserDao;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.UserCategory;
import com.nextlabs.destiny.console.model.ApplicationUser;

/**
 *
 * Application user DAO implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class ApplicationUserDaoImpl extends
        GenericDaoImpl<ApplicationUser, Long> implements ApplicationUserDao {

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.nextlabs.destiny.console.dao.ApplicationUserDao#findByUsername(java.
     * lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public ApplicationUser findByUsername(String username) {

        username = username.toLowerCase();

        Query query;
        boolean isSuperUser = false;
        if (ApplicationUser.SUPER_USERNAME.equalsIgnoreCase(username)) {
        	isSuperUser = true;
            query = entityManager.createNativeQuery(
                    "SELECT ID, USERNAME, FIRST_NAME, LAST_NAME, DISPLAYNAME, DOMAIN_ID, PRIMARY_GROUP_ID, LAST_LOGGED_TIME, HIDE_SPLASH, LAST_UPDATED, EMAIL"
                            + " FROM SUPER_APPLICATION_USER a WHERE LOWER(a.username) = :username");
        } else {
            query = entityManager.createNativeQuery(
                    "SELECT ID, USERNAME, FIRST_NAME, LAST_NAME, DISPLAYNAME, DOMAIN_ID, PRIMARY_GROUP_ID, LAST_LOGGED_TIME, HIDE_SPLASH, LAST_UPDATED, EMAIL, "
                    + " USER_TYPE, USER_CATEGORY, AUTH_HANDLER_ID, VERSION USER_VERSION, MANUAL_PROVISION FROM APPLICATION_USER a WHERE LOWER(a.username) = :username");
        }
        query.setParameter("username", username);

        List<Object[]> results = query.getResultList();
        if (results.size() == 1) {
            Object[] row = results.get(0);
            ApplicationUser user = new ApplicationUser();
            user.setId(readLong(row[0]));
            user.setUsername(readString(row[1]));
            user.setFirstName(readString(row[2]));
            user.setLastName((readString(row[3])));
            String concatenatedDisplayName = (user.getFirstName() + " " + user.getLastName()).trim();
            String storedDisplayName = readString(row[4]).trim();
            if(!concatenatedDisplayName.equals(storedDisplayName)
                && StringUtils.isNotBlank(storedDisplayName)) {
                user.setDisplayName(storedDisplayName);
            } else {
                user.setDisplayName(concatenatedDisplayName);
            }
            if(StringUtils.isBlank(user.getDisplayName())) {
                user.setDisplayName(user.getUsername());
            }
            user.setDomainId(readLong(row[5]));
            user.setLoggedInTime(readLong(row[7]));
            user.setHideSplash(readBool(row[8]));
            user.setLastUpdatedDate(new Date(readTimestamp(row[9])));
            user.setEmail(readString(row[10]));
			if (!isSuperUser) {
				user.setUserType(readString(row[11]));
				user.setUserCategory(readString(row[12]));
				user.setAuthHandlerId(readLong(row[13]));
				user.setVersion((int) readLong(row[14]));
                user.setManualProvision(readBool(row[15]));
			} else {
				user.setUserType(ApplicationUser.USER_TYPE_INTERNAL);
				user.setUserCategory(UserCategory.ADMINISTRATOR.getCode());
				user.setManualProvision(true);
			}
            return user;
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ApplicationUser> findAllActive() {
    	 Query query = null;
    	 List<ApplicationUser> allUsers = new ArrayList<>();
         
         query = entityManager.createNativeQuery(
                     "SELECT ID, USERNAME, FIRST_NAME, LAST_NAME, DOMAIN_ID, PRIMARY_GROUP_ID, LAST_LOGGED_TIME, HIDE_SPLASH, LAST_UPDATED, USER_TYPE, USER_CATEGORY, AUTH_HANDLER_ID, MANUAL_PROVISION"
                             + "    FROM APPLICATION_USER a WHERE a.status = :status");
         
         query.setParameter("status", Status.ACTIVE.name());

         List<Object[]> results = query.getResultList();
         for (Object[] row : results) {
             ApplicationUser user = new ApplicationUser();
             user.setId(readLong(row[0]));
             user.setUsername(readString(row[1]));
             user.setFirstName(readString(row[2]));
             user.setLastName((readString(row[3])));
             user.setLoggedInTime(readLong(row[6]));
             user.setHideSplash((readBool(row[7])));
             user.setLastUpdatedDate(new Date(readTimestamp(row[8])));
             user.setUserType(readString(row[9]));
             user.setUserCategory(readString(row[10]));
             user.setAuthHandlerId(readLong(row[11]));
             user.setManualProvision(readBool(row[12]));
             user.setDisplayName(user.getFirstName() + " " + user.getLastName());
             allUsers.add(user);
         } 
         return allUsers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Long getLocalDomainId() {

        Query query = entityManager.createNativeQuery(
                "SELECT ID, NAME FROM APPLICATION_USER_DOMAIN d WHERE LOWER(d.NAME) = :domain ");
        query.setParameter("domain", "local");

        List<Object[]> results = query.getResultList();
        if (results.size() == 1) {
            Object[] row = results.get(0);
            return readLong(row[0]);
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<String> findAllImportedByHandlerId(Long authHandlerId){
    	 Query query = null;
    	 List<String> importedUsers = new ArrayList<>();
         
         query = entityManager.createNativeQuery(
                     "SELECT USERNAME FROM APPLICATION_USER a WHERE a.USER_TYPE = :userType "
                     + "AND a.AUTH_HANDLER_ID = :handlerId");
         
         query.setParameter("userType", ApplicationUser.USER_TYPE_IMPORTED);
         query.setParameter("handlerId", authHandlerId);

         List<Object> results = query.getResultList();
         for (Object row : results) {
        	 importedUsers.add(readString(row));
         } 
         return importedUsers;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ApplicationUser findByEmail(String email) {

        email = email.toLowerCase(); 
        
        String emailSql = "SELECT a.USERNAME, a.EMAIL, a.STATUS FROM APPLICATION_USER a WHERE LOWER(a.email) = :email "
        		+ "UNION SELECT s.USERNAME, s.EMAIL, 'ACTIVE' FROM SUPER_APPLICATION_USER s WHERE LOWER(s.email) = :email ";

        Query query = entityManager.createNativeQuery(emailSql);
        query.setParameter("email", email);
    
        List<Object[]> results = query.getResultList();
        if (!results.isEmpty()) {
            Object[] row = results.get(0);
            ApplicationUser user = new ApplicationUser();
            user.setUsername(readString(row[0]));
            user.setEmail(readString(row[1]));
            user.setStatus(Status.get(readString(row[2])));
            return user;
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.nextlabs.destiny.console.dao.ApplicationUserDao#findAllActiveOrOtherHandler()
     */
    @Override
    public List<ApplicationUser> findAllActiveOrOtherHandler(Long authHandlerId) {
   	 Query query = null;
   	 List<ApplicationUser> allUsers = new ArrayList<>();
        
        query = entityManager.createNativeQuery(
                    "SELECT ID, USERNAME, FIRST_NAME, LAST_NAME, DOMAIN_ID, PRIMARY_GROUP_ID, LAST_LOGGED_TIME, HIDE_SPLASH, LAST_UPDATED, USER_TYPE, USER_CATEGORY, AUTH_HANDLER_ID, MANUAL_PROVISION"
                            + "    FROM APPLICATION_USER a WHERE a.status = :status OR a.AUTH_HANDLER_ID != :handlerId"
                            + " UNION ALL " 
                            + "SELECT ID, USERNAME, FIRST_NAME, LAST_NAME, DOMAIN_ID, PRIMARY_GROUP_ID, LAST_LOGGED_TIME, HIDE_SPLASH, LAST_UPDATED, 'internal', 'ADMIN', -1, MANUAL_PROVISION from SUPER_APPLICATION_USER");
        
        query.setParameter("status", Status.ACTIVE.name());
        query.setParameter("handlerId", authHandlerId);

        @SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
        for (Object[] row : results) {
            ApplicationUser user = new ApplicationUser();
            user.setId(readLong(row[0]));
            user.setUsername(readString(row[1]));
            user.setFirstName(readString(row[2]));
            user.setLastName((readString(row[3])));
            user.setLoggedInTime(readLong(row[6]));
            user.setHideSplash((readBool(row[7])));
            user.setLastUpdatedDate(new Date(readTimestamp(row[8])));
            user.setUserType(readString(row[9]));
            user.setUserCategory(readString(row[10]));
            user.setAuthHandlerId(readLong(row[11]));
            user.setManualProvision(readBool(row[12]));
            user.setDisplayName(user.getFirstName() + " " + user.getLastName());
            allUsers.add(user);
        } 
        return allUsers;
   }

    @Override
    public List<String> findAllGroupUsers(Long authHandlerId){
        Query query;
        List<String> groupUsers = new ArrayList<>();

        query = entityManager.createNativeQuery(
                        "SELECT MANUAL_PROVISION, USERNAME FROM APPLICATION_USER a WHERE a.USER_TYPE = :userType "
                                        + "AND a.status = :status AND a.AUTH_HANDLER_ID = :handlerId");

        query.setParameter("userType", ApplicationUser.USER_TYPE_IMPORTED);
        query.setParameter("status", Status.ACTIVE.name());
        query.setParameter("handlerId", authHandlerId);

        List<Object[]> results = query.getResultList();
        for (Object[] row : results) {
            if(!readBool(row[0])) {
                groupUsers.add(readString(row[1]));
            }
        }
        return groupUsers;
    }

    @Override
    public Long getActiveUserCountByHandlerId(Long authHandlerId) {
        Query query =  entityManager.createNativeQuery("SELECT COUNT(ID) FROM APPLICATION_USER"
                        + " WHERE STATUS = :status AND AUTH_HANDLER_ID = :handlerId");
        query.setParameter("status", Status.ACTIVE.name());
        query.setParameter("handlerId", authHandlerId);
        
        return readLong(query.getSingleResult());
    }
    
    private String readString(Object val) {
        if (val != null) {
            return (String) val;
        }
        return "";
    }

    private boolean readBool(Object val) {
        if (val != null) {
            if (val instanceof BigDecimal) {
                return (((BigDecimal) val).longValue() != 0);
            } else if (val instanceof Integer) {
                return (((Integer) val).intValue() != 0);
            } else if (val instanceof Short) {
                return (((Short) val).shortValue() != 0);
            } else if (val instanceof Byte) {
                return (((Byte) val).byteValue() != 0);
            } else {
                return (Boolean) val;
            }
        }
        return false;
    }

    private long readLong(Object val) {
        if (val != null) {
            if (val instanceof Integer) {
                return ((Integer) val).longValue();
            } else if (val instanceof BigDecimal){
                return ((BigDecimal) val).longValue();
            } else if (val instanceof BigInteger){
                return ((BigInteger) val).longValue();
            }
        }
        return 0L;
    }

	private long readTimestamp(Object val) {
		if (val instanceof java.sql.Timestamp) {
             Timestamp lastUpdatedTimestamp = (Timestamp) val;
             return lastUpdatedTimestamp.getTime();
		}
		return 0L;
	}

	@Override
	public void resetGAuthTokenByUsername(String username) {
		Query query1 = null;
		Query query2 = null;

		query1 = entityManager.createNativeQuery("DELETE FROM MFA_GOOGLE_AUTH_ACCOUNT WHERE LOWER(USERNAME) = :username");
		query1.setParameter("username", username.toLowerCase());
		query1.executeUpdate();

		query2 = entityManager.createNativeQuery("DELETE FROM MFA_GOOGLE_AUTH_TOKEN WHERE LOWER(USERNAME) = :username");
		query2.setParameter("username", username.toLowerCase());
		query2.executeUpdate();
	}
}
