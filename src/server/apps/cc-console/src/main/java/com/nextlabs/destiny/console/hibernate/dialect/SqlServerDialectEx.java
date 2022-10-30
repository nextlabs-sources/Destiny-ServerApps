/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 18, 2016
 *
 */
package com.nextlabs.destiny.console.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.type.StandardBasicTypes;

/**
 * This version of Microsoft SQL Server dialect forces creation of string fields
 * with case-sensitive compare order.
 *
 * By default, SQL Server uses case-insensitive searches on varchar fields; this
 * dialect generates DDL with case-sensitive colation order.
 *
 * @author Amila Silva
 * @since 8.0
 */
public class SqlServerDialectEx extends SQLServer2012Dialect {
    /**
     * This constructor overrides the DDL generated for the varchar type in the
     * based class.
     */
    public SqlServerDialectEx() {
        super();
        // To get the default server collation:
        // SELECT SERVERPROPERTY('Collation')

        // To get the default collation for a database
        // select databasepropertyex(db_name(), 'Collation')
               
        registerHibernateType(Types.NCHAR, StandardBasicTypes.CHARACTER.getName());
        registerHibernateType(Types.NCHAR, 1, StandardBasicTypes.CHARACTER.getName());
        registerHibernateType(Types.NCHAR, 255, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.LONGNVARCHAR, StandardBasicTypes.TEXT.getName());
        registerHibernateType(Types.NCLOB, StandardBasicTypes.CLOB.getName());
    }

}
