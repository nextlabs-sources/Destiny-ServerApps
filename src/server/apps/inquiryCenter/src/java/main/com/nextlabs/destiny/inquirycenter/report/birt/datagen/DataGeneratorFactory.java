/*
 *  All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.inquirycenter.audit.IAuditLogDataGenerator;
import com.nextlabs.destiny.inquirycenter.audit.DB2AuditLogDataGenerator;
import com.nextlabs.destiny.inquirycenter.audit.MSSQLAuditLogDataGenerator;
import com.nextlabs.destiny.inquirycenter.audit.OracleAuditLogDataGenerator;
import com.nextlabs.destiny.inquirycenter.audit.PostgreSQLAuditLogDataGenerator;
import com.nextlabs.destiny.inquirycenter.monitor.service.IAlertDataGenerator;
import com.nextlabs.destiny.inquirycenter.monitor.service.DB2AlertDataGenerator;
import com.nextlabs.destiny.inquirycenter.monitor.service.MSSQLAlertDataGenerator;
import com.nextlabs.destiny.inquirycenter.monitor.service.OracleAlertDataGenerator;
import com.nextlabs.destiny.inquirycenter.monitor.service.PostgreSQLAlertDataGenerator;
import com.nextlabs.destiny.inquirycenter.sharepoint.datagen.ISharePointDataGenerator;
import com.nextlabs.destiny.inquirycenter.sharepoint.datagen.DB2SharePointDataGenerator;
import com.nextlabs.destiny.inquirycenter.sharepoint.datagen.MSSQLSharePointDataGenerator;
import com.nextlabs.destiny.inquirycenter.sharepoint.datagen.OracleSharePointDataGenerator;
import com.nextlabs.destiny.inquirycenter.sharepoint.datagen.PostgreSQLSharePointDataGenerator;

import net.sf.hibernate.Session;

/**
 * The factory that provides the correct type of data generator.
 * TODO: Fix this so that we can get the db type in a cleaner way.
 * @author Shantanu
 *
 */
public class DataGeneratorFactory {

    private IHibernateRepository.DbType dbType;
    private Log log = LogFactory.getLog(DataGeneratorFactory.class.getName());

    static DataGeneratorFactory instance = new DataGeneratorFactory();

    private DataGeneratorFactory() {
        init();
    }

    private void init() {
        IHibernateRepository activityDataSrc = SharedLib.getActivityDataSource();
        dbType = activityDataSrc.getDatabaseType();
    }

    public static final DataGeneratorFactory getInstance() {
        return instance;
    }

    public IDataGenerator getDataGenerator(Session session) {
        IDataGenerator dataGen;
        switch (dbType) {
        case POSTGRESQL:
            dataGen = new PostgreSQLDataGenerator(session);
            break;
        case ORACLE:
            dataGen = new OracleDataGenerator(session);
            break;
        case MS_SQL:
            dataGen = new MSSQLDataGenerator(session);
            break;
        case DB2:
            dataGen = new DB2DataGenerator(session);
            break;
        default:
            dataGen = null;
            break;
        }
        return dataGen;
    }
    
	public ISharePointDataGenerator getSharePointDataGenerator()
			throws Exception {
		ISharePointDataGenerator dataGen;
		Session session = SharedLib.getActivityDataSource().getSession();
		switch (dbType) {
		case POSTGRESQL:
			dataGen = new PostgreSQLSharePointDataGenerator(session);
			break;
		case ORACLE:
			dataGen = new OracleSharePointDataGenerator(session);
			break;
		case MS_SQL:
			dataGen = new MSSQLSharePointDataGenerator(session);
			break;
        case DB2:
            dataGen = new DB2SharePointDataGenerator(session);
            break;
		default:
			dataGen = null;
			break;
		}
		return dataGen;
	}
	
    public IAlertDataGenerator getAlertDataGenerator(Session session) {
        IAlertDataGenerator dataGen;
        switch (dbType) {
        case POSTGRESQL:
            dataGen = new PostgreSQLAlertDataGenerator(session);
            break;
        case ORACLE:
            dataGen = new OracleAlertDataGenerator(session);
            break;
        case MS_SQL:
            dataGen = new MSSQLAlertDataGenerator(session);
            break;
        case DB2:
            dataGen = new DB2AlertDataGenerator(session);
            break;
        default:
            dataGen = null;
            break;
        }
        return dataGen;
    }
    
    public IAuditLogDataGenerator getAuditLogDataGenerator(Session session) {
    	switch(dbType) {
    		case POSTGRESQL:	return new PostgreSQLAuditLogDataGenerator(session);
    		case ORACLE:		return new OracleAuditLogDataGenerator(session);
    		case MS_SQL:		return new MSSQLAuditLogDataGenerator(session);
            case DB2:           return new DB2AuditLogDataGenerator(session);
    	}
    	
    	return null;
    }
}
