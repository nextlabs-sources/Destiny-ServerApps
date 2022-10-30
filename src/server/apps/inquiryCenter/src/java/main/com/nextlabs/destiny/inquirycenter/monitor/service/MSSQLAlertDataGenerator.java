/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

import net.sf.hibernate.Session;

/**
 * @author nnallagatla
 *
 */
public class MSSQLAlertDataGenerator extends AbstractAlertDataGenerator {
	
	public MSSQLAlertDataGenerator(Session session)
	{
		super(session);
	}

	@Override
	protected String getSelectDateTimeColumnForGroupBy(String name) {
		if ("day_nb".equals(name)) {
    		return " REPLACE(CONVERT(VARCHAR, DATEADD(s, day_nb/1000,'1970-01-01'), 106), ' ', '-') ";
    	} else if ("month_nb".equals(name)) {
    		return " REPLACE(RIGHT(CONVERT(VARCHAR, DATEADD(s, month_nb/1000,'1970-01-01'), 106), 8), ' ', '-') ";
    	}
		return "";
	}
	
	@Override
	protected String getBoolean(boolean value) {
		return value? "1":"0";
	}
}
