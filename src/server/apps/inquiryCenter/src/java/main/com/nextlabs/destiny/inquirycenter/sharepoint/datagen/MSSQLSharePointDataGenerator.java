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
 *  MSSQLSharePointDataGenerator
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class MSSQLSharePointDataGenerator extends
		AbstractSharePointDataGenerator {

	/**
	 * <p>
	 * Constructor 
	 * </p>
	 *
	 * @param session
	 */
	public MSSQLSharePointDataGenerator(Session session) {
		super(session);
	}

	@Override
	protected String getSelectColumnFormatting(String columnName) {
		if ("day_nb".equals(columnName)) {
			return " CONVERT(VARCHAR(10), DATEADD(s, day_nb/1000,'1970-01-01'), 106) ";
		} else if ("month_nb".equals(columnName)) {
			return " RIGHT(CONVERT(varchar, DATEADD(s, month_nb/1000,'1970-01-01'), 106), 8) ";
		} else
			return columnName;
	}
	

}
