/*
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

import static com.nextlabs.destiny.inquirycenter.SharedUtils.getFormatedDate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import com.nextlabs.destiny.container.shared.inquirymgr.LoggablePreparedStatement;
import com.nextlabs.destiny.inquirycenter.DateRangeSelection;
import com.nextlabs.report.datagen.ResultData;

/**
 * @author nnallagatla
 *
 */
public abstract class AbstractAlertDataGenerator implements IAlertDataGenerator {

	protected Session session = null;
	
	private static String TRIGGERED_DATETIME = "triggered_datetime";
	
	protected Log log = LogFactory
			.getLog(AbstractAlertDataGenerator.class.getName());
	
	protected Timestamp beginDate;
	protected Timestamp endDate;
	protected String reportType;
	protected boolean isGroupingReport;
	protected int rangeOption;
	protected boolean isRelativeDuration;
	protected JSONObject query;
	protected String groupingField;
	protected boolean shouldJoinTagsTable;
	protected List<String> columnsInSelectClause = new ArrayList<String>();
	
	protected final String[] TABULAR_DATA_COLUMNS = {"id", "monitor_id", "monitor_name", "monitor_uid", "triggered_datetime", "alert_level", "alert_message", "dismissed", "is_deleted"};
	
	public AbstractAlertDataGenerator(Session s)
	{
		this.session = s;
	}
	
	/* (non-Javadoc)
	 * @see com.nextlabs.destiny.inquirycenter.monitor.service.IAlertDataGenerator#getResultData(java.lang.String)
	 */
	@Override
	public ResultData getResultData(String jsonQuery) throws Exception {
		String query = generateQuery(jsonQuery);
		ResultData data = null;
		try
		{
			data =  executeQuery(query);
		}
		catch (NullPointerException e)
		{
			log.error(e);
		}
		return data;
	}
	
	/**
	 * 
	 * @param sb
	 */
	protected void generateSELECTClause(StringBuilder sb, StringBuilder orderBy)
	{ 
		boolean first = true;
		
		if (isGroupingReport)
		{
			sb.append("");
		}
		else
		{
			sb.append("SELECT ");
			
			for (String column : TABULAR_DATA_COLUMNS)
			{
				if (!first)
				{
					sb.append(", ");
				}
				else
				{
					first = false;
				}
				sb.append("a.").append(column);
				columnsInSelectClause.add(column);
			}
			
			/*
			 *for alert details always return the latest at the top 
			 */
			orderBy.append(" ORDER BY a.triggered_datetime DESC ");
		}
	}
	
	/**
	 * 
	 * @param sb
	 */
	protected void generateFROMClause(StringBuilder sb)
	{
		sb.append(" FROM ").append("alert a");
		
		if (shouldJoinTagsTable)
		{
			sb.append(" JOIN monitor m ON a.monitor_id = m.id")
			.append(" JOIN monitor_tags mt ON m.id = mt.monitor_id ");
		}
	}
	
	protected String getDateTimeLiteral(Timestamp ts)
	{
		return "'" + ts.toString() + "'";
	}
	
	/**
	 * 
	 * @param sb
	 * @throws JSONException 
	 */
	protected void generateWHEREClause(StringBuilder sb) throws JSONException
	{	
		populateDates();
		sb.append(" WHERE a.triggered_datetime >= ? ").append(" AND ")
		.append("a.triggered_datetime <= ?").append(" ");
		
		if (!query.has("filters"))
		{
			return;
		}
		JSONArray filters = query.getJSONArray("filters");
		boolean showDismissed = false;
		
		for (int i = 0 ; i< filters.length(); i++)
		{
			JSONObject obj = filters.getJSONObject(i);
			String name = obj.getString("name");
			String value = obj.getString("value");
			
			if ( name == null || name.isEmpty() || value == null)
			{
				continue;
			}
			
			if("SHOW_AUTO_DISMISS".equalsIgnoreCase(name)) {
				showDismissed = (value != null && value.equalsIgnoreCase("true")) ? true : false;
				continue;
			}
			
			
			String columnName = getColumnNameForFilter(name);
			
			sb.append(" AND ").append(columnName).append(" = ? ");
		}
		
		sb.append(" AND a.is_deleted = ? ");
		
		if ("alerts_details".equalsIgnoreCase(reportType))
		{
			if(!showDismissed) {
				sb.append(" AND a.dismissed = ? ");
			} 
		}
		sb.append(" ");
	}
	
	
	protected void replaceParameters(PreparedStatement ps) throws JSONException, SQLException
	{		
		int index = 1;
		
		ps.setTimestamp(index++, beginDate);
		ps.setTimestamp(index++, endDate);
		
		if (!query.has("filters"))
		{
			return;
		}
		JSONArray filters = query.getJSONArray("filters");
		boolean showDismissed = false;
		
		//all string params inside this loop
		for (int i = 0 ; i< filters.length(); i++)
		{
			JSONObject obj = filters.getJSONObject(i);
			String name = obj.getString("name");
			String value = obj.getString("value");
			
			if ( name == null || name.isEmpty() || value == null)
			{
				continue;
			}
			
			if("SHOW_AUTO_DISMISS".equalsIgnoreCase(name)) {
				showDismissed = (value != null && value.equalsIgnoreCase("true")) ? true : false;
				continue;
			}
			
			ps.setString(index++, getParamValue(value));
			
		}
		
		//is_deleted to false
		ps.setBoolean(index++, false);
		
		if ("alerts_details".equalsIgnoreCase(reportType))
		{
			if(!showDismissed) {
				ps.setBoolean(index++, false);
			} 
		}
	}
	
	protected String getParamValue(String value) {
		return value;
	}
	
	protected String getBoolean(boolean value) {
		return value? "true" : "false";
	}
	
	protected String getColumnNameForFilter(String name) {
		if ("TAG_NAME".equalsIgnoreCase(name))
		{
			shouldJoinTagsTable = true;
			return "mt.name";
		}
		if ("TAG_VALUE".equalsIgnoreCase(name))
		{
			shouldJoinTagsTable = true;
			return "mt.value";
		}
		if ("MONITOR".equalsIgnoreCase(name))
		{
			return "a.monitor_id";
		}
		
		if ("MONITOR_NAME".equalsIgnoreCase(name))
		{
			return "a.monitor_name";
		}
		
		if ("MONITOR_UID".equalsIgnoreCase(name))
		{
			return "a.monitor_uid";
		}
		
		return "";
	}

	/**
	 * converts the relative dates into absolute ones
	 * @throws JSONException
	 */
	protected void populateDates() throws JSONException
	{
		if (query.has("date_criteria"))
		{
			JSONObject dateFilter = query.getJSONObject("date_criteria");
			String dateMode = dateFilter.getString("mode");
			if ("fixed".equalsIgnoreCase(dateMode))
			{
				beginDate = Timestamp.valueOf(dateFilter.getString("begin_date"));
				endDate = Timestamp.valueOf(dateFilter.getString("end_date"));
			}
			
			if ("relative".equalsIgnoreCase(dateMode) || beginDate == null || endDate == null)
			{
				int n = -1;
				isRelativeDuration = true;
				rangeOption = DateRangeSelection.CURRENT_WEEK;
				String durationWindow;
				if (dateFilter.has("date_window"))
				{	
					durationWindow = dateFilter.getString("date_window");
					
					if (durationWindow == null || durationWindow.isEmpty())
					{
						rangeOption = DateRangeSelection.CURRENT_WEEK;
					}
					else if (durationWindow.equalsIgnoreCase("current_week"))
					{
						rangeOption = DateRangeSelection.CURRENT_WEEK;
					}
					else if (durationWindow.equalsIgnoreCase("current_month"))
					{
						rangeOption = DateRangeSelection.CURRENT_MONTH;
					}
					else if (durationWindow.equalsIgnoreCase("current_quarter"))
					{
						rangeOption = DateRangeSelection.CURRENT_QUARTER;
					}
					else if (durationWindow.equalsIgnoreCase("last_week"))
					{
						rangeOption = DateRangeSelection.PRIOR_WEEK;
					}
					else if (durationWindow.equalsIgnoreCase("last_month"))
					{
						rangeOption = DateRangeSelection.PRIOR_MONTH;
					}
					else if (durationWindow.equalsIgnoreCase("last_quarter"))
					{
						rangeOption = DateRangeSelection.PRIOR_QUARTER;
					}
					else if (durationWindow.equalsIgnoreCase("today"))
					{
						rangeOption = DateRangeSelection.TODAY;
					}
					else if (durationWindow.equalsIgnoreCase("yesterday"))
					{
						rangeOption = DateRangeSelection.YESTERDAY;
					}
					else if (durationWindow.equalsIgnoreCase("this_hour"))
					{
						rangeOption = DateRangeSelection.THIS_HOUR;
					}
					else if (durationWindow.equalsIgnoreCase("last_hour"))
					{
						rangeOption = DateRangeSelection.PRIOR_HOUR;
					}
					else if (durationWindow.toLowerCase().startsWith("last")) 
					{
						String[] temp = durationWindow.split("_");
						
						if (temp.length == 3)
						{
							n = Integer.parseInt(temp[1]);
							rangeOption = temp[2].equalsIgnoreCase("days") ? DateRangeSelection.LAST_N_DAYS : DateRangeSelection.LAST_N_HOURS;
						}
					}
				}
				
				DateRangeSelection range = null; 
				if ( n == -1)
				{
					range = new DateRangeSelection(rangeOption);
				}
				else
				{
					range = new DateRangeSelection(rangeOption, n);
				}
				
				beginDate = new Timestamp(range.getBeginDate().getTimeInMillis());
				endDate = new Timestamp(range.getEndDate().getTimeInMillis());
			}
		}
	}
	
	/**
	 * 
	 * @param sb
	 * @throws JSONException 
	 */
	protected void generateGROUPBYClause(StringBuilder groupBy, StringBuilder select, StringBuilder orderBy) throws JSONException
	{	
		if (("group_by_tags").equalsIgnoreCase(reportType))
		{
			groupBy.append(" GROUP BY mt.name, mt.value ");
			
			select.append(" SELECT mt.name as name, mt.value as value, count(*) as resultcount ");
			
			orderBy.append(" ORDER BY resultcount DESC");
			groupingField = reportType;
			isGroupingReport = true;
			shouldJoinTagsTable = true;
		}
		else if (("group_by_monitors").equalsIgnoreCase(reportType))
		{
			isGroupingReport = true;
			groupingField = reportType;
			select.append(" SELECT a.monitor_uid as monitor, count(*) as resultcount ");
			
			groupBy.append(" GROUP BY a.monitor_uid ");
			orderBy.append(" ORDER BY resultcount DESC");
		}
		else if (("group_by_time").equalsIgnoreCase(reportType))
		{	
			isGroupingReport = true;
			
			String field = "day_nb";
			if (isRelativeDuration)
			{
				field = getGroupByTimeGranularity();
			}
			
			select.append(" SELECT ").append(getSelectDateTimeColumnForGroupBy(field))
			.append(" as dimension, count(*) as resultcount ");
			
			groupBy.append("Group BY a.").append(field); 
			orderBy.append(" ORDER By a.").append(field).append(" ASC");
			groupingField = field;
		}
	}
	
	private String getGroupByTimeGranularity()
	{
		if (rangeOption == DateRangeSelection.CURRENT_QUARTER || rangeOption == DateRangeSelection.PRIOR_QUARTER)
		{
			return "month_nb";
		}
		
		return "day_nb"; 
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	abstract protected String getSelectDateTimeColumnForGroupBy(String name);

	/* (non-Javadoc)
	 * @see com.nextlabs.destiny.inquirycenter.monitor.service.IAlertDataGenerator#cleanup()
	 */
	@Override
	public void cleanup() throws Exception {
		session.connection().close();
	}

	/**
	 * 
	 * @param jsonQuery
	 * @return
	 * @throws JSONException
	 */
	protected String generateQuery(String jsonQuery) throws JSONException{
		StringBuilder queryStr = new StringBuilder();
		query = new JSONObject(jsonQuery);
		
		reportType = query.getString("report_type");
		
		StringBuilder whereClause = new StringBuilder();
		StringBuilder groupClause = new StringBuilder();
		StringBuilder selectClause = new StringBuilder();
		StringBuilder fromClause = new StringBuilder();
		StringBuilder orderBy = new StringBuilder();
		
		generateWHEREClause(whereClause);
		generateGROUPBYClause(groupClause, selectClause, orderBy);
		generateFROMClause(fromClause);
		generateSELECTClause(selectClause, orderBy);
		
		queryStr.append(selectClause.toString()).append(fromClause).append(whereClause)
		.append(groupClause).append(orderBy);
		
		log.info(queryStr.toString());
		return queryStr.toString();
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 * @throws SQLException 
	 * @throws JSONException 
	 */
	protected ResultData executeQuery(String query) throws SQLException, JSONException {
		String[] columnName = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		List<Object[]> lListData = new LinkedList<Object[]>();
		try {
			
			ps = new LoggablePreparedStatement(session.connection(), query);

			replaceParameters(ps);
			
			rs = ps.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int columnCount = rsmd.getColumnCount();
			columnName = new String[columnCount];
			int dateTimeColumn = -1;
			for (int i = 0; i < columnCount; ++i) {
				columnName[i] = rsmd.getColumnLabel(i + 1);
				if (columnName[i].equalsIgnoreCase(TRIGGERED_DATETIME))
				{
					dateTimeColumn = i;
				}
			}

			while (rs.next()) {

				Object[] value = new Object[columnCount];
				for (int i = 0; i < columnCount; ++i) {
					/*oracle's date field is creating problem. so explicitly reading
					that column as timestamp
					*/
					if (i == dateTimeColumn)
					{
						value[i] = getFormatedDate(rs.getTimestamp(i + 1));
					}
					else
					{
						value[i] = rs.getObject(i + 1);
					}
				}
				lListData.add(value);
			}
		} catch (SQLException e) {
			if (log.isDebugEnabled()) {
				log.debug("SQL exception error on statement: " + query
						+ "error=" + e.toString());
				log.error(e.toString(), e);
			}
			
			log.error(e);
			
		} catch (HibernateException e) {
			log.error(e.toString(), e);
		} 
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		return new ResultData(columnName,
				lListData.toArray(new Object[lListData.size()][]));
	}

	/**
	 * @return the beginDate
	 */
	public Timestamp getBeginDate() {
		return beginDate;
	}

	/**
	 * @return the endDate
	 */
	public Timestamp getEndDate() {
		return endDate;
	}

	/**
	 * @return the reportType
	 */
	public String getReportType() {
		return reportType;
	}

	/**
	 * @return the isGroupingReport
	 */
	public boolean isGroupingReport() {
		return isGroupingReport;
	}

	/**
	 * @return the groupingField
	 */
	public String getGroupingField() {
		return groupingField;
	}
}
