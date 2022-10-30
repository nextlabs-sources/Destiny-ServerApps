/*
 * Created on Apr 3, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;


import net.sf.hibernate.Session;

/**
 * <p>
 *  MSSQLDataGenerator class for MSSQL specific SQL generation
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class MSSQLDataGenerator extends AbstractDataGenerator {

    public MSSQLDataGenerator(Session session) {
        super(session);
    }
    
    @Override
	protected String getSelectStatementPrefix(int rowCount) {
        return "SELECT ";
	}


	@Override
	protected String getSelectStatementSuffix(int rowCount, int offset) {
        if (rowCount > 0 && hasOrderByFields && paginated) {
            // "offset" and "fetch next" can only be used if "order by" appears in the SQL
            StringBuilder suffix = new StringBuilder();
            suffix.append(" offset ").append(offset).append(" rows");
            suffix.append(" fetch next ").append(rowCount).append(" rows only");
            return suffix.toString();
        }

        return "";
    }

}
