/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

import net.sf.hibernate.Session;
/**
 * @author nnallagatla
 *
 */
public class PostgreSQLAlertDataGenerator extends AbstractAlertDataGenerator {

	public PostgreSQLAlertDataGenerator(Session session)
	{
		super(session);
	}

	@Override
	protected String getSelectDateTimeColumnForGroupBy(String name) {
		if ("day_nb".equals(name)) {
    		return " TO_CHAR(TO_TIMESTAMP(day_nb / 1000), 'DD-MON-YYYY') ";
    	} else if ("month_nb".equals(name)) {
    		return " TO_CHAR(TO_TIMESTAMP(month_nb / 1000), 'MON-YYYY') ";
    	}
		return "";
	}
	
	@Override
	protected String getColumnNameForFilter(String name) {
		if ("TAG_NAME".equalsIgnoreCase(name))
		{
			shouldJoinTagsTable = true;
			return "LOWER(mt.name)";
		}
		if ("TAG_VALUE".equalsIgnoreCase(name))
		{
			shouldJoinTagsTable = true;
			return "LOWER(mt.value)";
		}
		if ("MONITOR".equalsIgnoreCase(name))
		{
			return "a.monitor_id";
		}
		
		if ("MONITOR_NAME".equalsIgnoreCase(name))
		{
			return "LOWER(a.monitor_name)";
		}
		
		if ("MONITOR_UID".equalsIgnoreCase(name))
		{
			return "a.monitor_uid";
		}
		
		return "";
	}
	
	@Override
	protected String getParamValue(String value) {
		return (value != null) ? value.toLowerCase() : "";
	}
}
