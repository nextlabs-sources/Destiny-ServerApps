/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

import static com.nextlabs.destiny.inquirycenter.SharedUtils.getFormatedDate;
import java.sql.Timestamp;
import net.sf.hibernate.Session;

/**
 * @author nnallagatla
 *
 */
public class OracleAlertDataGenerator extends AbstractAlertDataGenerator {

	public OracleAlertDataGenerator(Session session)
	{
		super(session);
	}

	@Override
	protected String getSelectDateTimeColumnForGroupBy(String name) {
		if ("day_nb".equals(name)) {
			return " to_char(to_date('1970-01-01 00','yyyy-mm-dd hh24') + ( day_nb ) /1000/60/60/24 , 'DD-MON-YYYY') ";
		} else if ("month_nb".equals(name)) {
			return " to_char(to_date('1970-01-01 00','yyyy-mm-dd hh24') + ( month_nb ) /1000/60/60/24 , 'MON-YYYY') ";
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
	
	@Override
	protected String getBoolean(boolean value) {
		return value? "1":"0";
	}
	
	@Override
	protected String getDateTimeLiteral(Timestamp ts){
		String S = getFormatedDate(ts);
		return "to_date('" + S + "','yyyy-MM-dd HH24:MI:SS')";
	}
}
