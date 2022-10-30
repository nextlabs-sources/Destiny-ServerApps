/*
 * Created on Jun 12, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.sharepoint.datagen;

import net.sf.hibernate.Session;

/**
 * <p>
 * PostgreSQLSharePointDataGenerator
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public class PostgreSQLSharePointDataGenerator extends
		AbstractSharePointDataGenerator {

	/**
	 * <p>
	 * Constructor
	 * </p>
	 * 
	 * @param session
	 */
	public PostgreSQLSharePointDataGenerator(Session session) {
		super(session);
	}

	@Override
	protected String getSelectColumnFormatting(String columnName) {
		if ("day_nb".equals(columnName)) {
			return " TO_CHAR(TO_TIMESTAMP(day_nb / 1000), 'DD-MON-YYYY') ";
		} else if ("month_nb".equals(columnName)) {
			return " TO_CHAR(TO_TIMESTAMP(month_nb / 1000), 'MON-YYYY') ";
		}
		return columnName;
	}

	
}
