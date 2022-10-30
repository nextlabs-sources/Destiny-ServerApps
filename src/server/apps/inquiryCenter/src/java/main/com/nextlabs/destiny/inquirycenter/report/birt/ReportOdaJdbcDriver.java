/*
 * Created on Apr 30, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.eclipse.birt.report.data.oda.jdbc.OdaJdbcDriver;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/birt/ReportOdaJdbcDriver.java#1 $
 */

public class ReportOdaJdbcDriver extends OdaJdbcDriver {

//    private com.bluejungle.framework.datastore.hibernate.PoolBackedHibernateRepositoryImpl passedInRepository;
    private Connection passedInRepository;
    
    /**
     * @see org.eclipse.birt.report.data.oda.jdbc.OdaJdbcDriver#setAppContext(java.lang.Object)
     */
    @Override
    public void setAppContext(Object context) throws OdaException {
        HashMap ctx = (HashMap)context;  
//        passedInConnection = (PoolBackedHibernateRepositoryImpl)ctx.get("com.nextlabs.destiny.inquirycenter.report.birt.ReportOdaJdbcDriver");
        Object unknownObject = ctx.get("com.nextlabs.destiny.inquirycenter.report.birt.ReportOdaJdbcDriver");
        Class unknownClass = unknownObject.getClass();
        
//        if(unknownClass != com.bluejungle.framework.datastore.hibernate.PoolBackedHibernateRepositoryImpl.class){
//            System.out.println("HK: different class");
//        }
//        
//        if (!unknownClass.getName().equals(com.bluejungle.framework.datastore.hibernate.PoolBackedHibernateRepositoryImpl.class.getName())) {
//            System.out.println("HK: different classname");
//        }
        
        ClassLoader given = unknownClass.getClassLoader();
        ClassLoader fromHere = com.bluejungle.framework.datastore.hibernate.PoolBackedHibernateRepositoryImpl.class.getClassLoader();
        ClassLoader thisClass = this.getClass().getClassLoader();
        
//        if (!unknownClass.getClassLoader().equals(com.bluejungle.framework.datastore.hibernate.PoolBackedHibernateRepositoryImpl.class.getClassLoader())) {
//            System.out.println("HK: different classLoader");
//        }
        
//        if(unknownObject instanceof com.bluejungle.framework.datastore.hibernate.PoolBackedHibernateRepositoryImpl){
//            passedInRepository = (com.bluejungle.framework.datastore.hibernate.PoolBackedHibernateRepositoryImpl)unknownObject;
            passedInRepository = (Connection)unknownObject;
//        }else{
//            System.out.println(unknownObject.getClass().getName());
//            throw new OdaException("Robert is here.");
//        }
        
    }

    /**
     * @see org.eclipse.birt.report.data.oda.jdbc.OdaJdbcDriver#getConnection(java.lang.String)
     */
    @Override
    public IConnection getConnection(String arg0) throws OdaException {
//        if( passedInRepository != null){   
//            return new ReportDBConnection();  
//        }else{   
//            return new org.eclipse.birt.report.data.oda.jdbc.Connection();  
//        }
        return new ReportDBConnection();
    }

    private class ReportDBConnection extends org.eclipse.birt.report.data.oda.jdbc.Connection {

        public void open(Properties connProperties) throws OdaException {  
//            try {
//                Session session;
//                session = passedInRepository.getSession();
//                jdbcConn = session.connection();
//            } catch (HibernateException e){
//                throw new OdaException(e.getMessage());
//            }
            jdbcConn = passedInRepository;
        }

        public void close( ) throws OdaException {  
            if ( jdbcConn == null ) {    
                return;                  
            } else { 
                jdbcConn = null;
            }
        }
    }
}
