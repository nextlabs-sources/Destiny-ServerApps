package com.nextlabs.destiny.inquirycenter.savedreport.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.destiny.inquirycenter.savedreport.dto.EntityAuditLogDO;

import net.sf.hibernate.HibernateException;

public interface EntityAuditLogDAO {
	public static final Log LOG = LogFactory.getLog(EntityAuditLogDAO.class);
	
	/**
	 * Create a new EntityAuditLog record
	 * @param auditLog
	 * @throws HibernateException
	 */
	public void create(EntityAuditLogDO auditLog) throws SQLException, HibernateException;
	
	/**
	 * Lookup EntityAuditLog by its ID
	 * @param auditLogId
	 * @return
	 * @throws HibernateException
	 */
	public EntityAuditLogDO lookup(Long auditLogId) throws HibernateException;
	
	/**
	 * Get All EntityAuditLog
	 * @return
	 * @throws HibernateException
	 */
	public List<EntityAuditLogDO> getAll() throws HibernateException;
}
