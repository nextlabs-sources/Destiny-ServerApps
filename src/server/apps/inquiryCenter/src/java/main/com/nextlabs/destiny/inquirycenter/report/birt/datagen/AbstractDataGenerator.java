/*
 *  All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.nextlabs.destiny.container.shared.inquirymgr.LoggablePreparedStatement;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.AttributeColumnMappingDAO;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.AttributeColumnMappingDAOImpl;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.AttributeColumnMappingDO;
import com.nextlabs.destiny.inquirycenter.report.CriteriaFieldModel;
import com.nextlabs.destiny.inquirycenter.report.OrderByModel;
import com.nextlabs.destiny.inquirycenter.report.ReportActions;
import com.nextlabs.destiny.inquirycenter.report.ReportCriteriaJSONModel;
import com.nextlabs.destiny.inquirycenter.report.ReportResultData;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

/**
 * This abstract implementation contains most of the data generation mechanism.
 * The database specific implementations needs to override certain methods to
 * provide database specific SQL statements if necessary.
 * 
 * The ideal goal is not to use any database specific subclasses but to use
 * hibernate to obtain the database specific items such as the TOP or LIMIT
 * keywords. For now, the implementation classes provide these.
 * 
 * @author ssen
 * @see com.nextlabs.destiny.inquirycenter.report.birt.datagen.MSSQLDataGenerator
 * @see com.nextlabs.destiny.inquirycenter.report.birt.datagen.OracleDataGenerator
 * @see com.nextlabs.destiny.inquirycenter.report.birt.datagen.PostgreSQLDataGenerator
 */
public abstract class AbstractDataGenerator implements IDataGenerator {

	protected Session session;

	protected PreparedStatement groupByQueryStmt;
	protected PreparedStatement paDetailsTableQueryStmt;
	protected PreparedStatement paDetailsTableTotalQueryStmt;
	protected PreparedStatement paMainAttrQueryStmt;
	protected PreparedStatement paCustAttrQueryStmt;
	protected PreparedStatement paMappingtAttrQueryStmt;
	protected PreparedStatement oblQueryStmt;
	protected PreparedStatement paSingleLogDetailsQueryStmt;

	protected Log log = LogFactory
			.getLog(AbstractDataGenerator.class.getName());

	private String groupByDimension;
	private Timestamp beginDate;
	private Timestamp endDate;
	private String enforcement;
	private String eventLevel;
	private String resource;
	private String policy;
	private String action;
	private String userName;
	private String timeDimension;
	private Long idForDetailsLog;
	private String sData;
	protected JSONArray jArrDataHeader;
	private ReportCriteriaJSONModel criteriaModel;

    // Every query should specify a page size, but this is a good default if they don't
    private static final int DEFAULT_PAGE_SIZE = 50;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DAY_MONTH_YEAR_FORMAT = "dd-MMM-yyyy";
    private static final String MONTH_YEAR_FORMAT = "MMM-yyyy";

	DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
			.appendPattern(DATE_TIME_FORMAT)
			.appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
			.toFormatter();

	DateTimeFormatter dayMonthYearFormatter = new DateTimeFormatterBuilder()
			.appendPattern(DAY_MONTH_YEAR_FORMAT).toFormatter();

	DateTimeFormatter monthYearFormatter = new DateTimeFormatterBuilder()
			.appendPattern(MONTH_YEAR_FORMAT).toFormatter();

	private static final String ACTION_FIELD = "action";
	private static final String POLICY_DECISION_FIELD = "policy_decision";
	private static final String TIME_FIELD = "time_text";
	private static final String DIMENSION_FIELD = "Dimension";
	private static final String LOG_LEVEL_FIELD = "log_level";

	protected static final String ALIAS_MONTH = "month_nb";
	protected static final String ALIAS_DAY = "day_nb";
	protected static final String ALIAS_FROM_RESOURCE_NAME_STRING = "from_resource_name";
	protected static final String GROUPBY_POLICY_COLUMN_NAME = "policy_fullname";
	protected static final String GROUPBY_MONTH = "month_nb";
	protected static final String GROUPBY_DAY = "day_nb";
	protected static final String ORDERBY_RESULT_COUNT = "ResultCount";
	protected static final String ALIAS_USERNAME_STRING = "user_name";

	protected static final String ALIAS_ATTR_ID_STRING = "ATTR_ID";
	protected static final String ALIAS_ATTR_VALUE_STRING = "ATTR_VALUE";

	protected static final String GROUPBY_QRY_FRAG = " AS " + DIMENSION_FIELD + ", count(*) AS ResultCount ";

	protected static final String FROM_QRY_FRAG = " FROM "
			+ SharedLib.REPORT_PA_TABLE
			+ " AS t_policy_activity_log WHERE time >= ? " + // beginDate
			" AND time <= ? "; // endDate

	protected static final String FROM_QRY_FRAGV2 = " FROM RPA_LOG"
			+ " t_policy_activity_log WHERE time >= ? " + // beginDate
			" AND time <= ? "; // endDate

	// We allow MAX_MULTI_PARMS in our query. Make sure the number of ? marks
	// in the IN clauses match this constant!
	protected static final int MAX_MULTI_PARMS = 40;

	protected static final String RESOURCE_QRY_FRAG = " AND from_resource_name LIKE ? "; // Resource
	protected static final String RESOURCE_QRY_MULTI_FRAG_V2 = " AND from_resource_name IN  ";

	protected static final String RESOURCE_QRY_MULTI_FRAG = " AND from_resource_name IN "
			+ "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

	protected static final String POLICY_QRY_FRAG = " AND policy_fullname LIKE ? ";
	protected static final String POLICY_QRY_MULTI_FRAG_V2 = " AND policy_fullname IN ";

	protected static final String POLICY_QRY_MULTI_FRAG = " AND policy_fullname IN "
			+ "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

	protected static final String ACTION_QRY_FRAG = " AND action LIKE ? ";
	protected static final String ACTION_QRY_MULTI_FRAG_V2 = " AND action IN  ";

	protected static final String ACTION_QRY_MULTI_FRAG = " AND action IN "
			+ "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

	protected static final String EVENT_LEVEL_QRY_FRAG = " AND log_level >= ? ";

	protected static final String ENFORCEMENTS_QRY_FRAG = " AND policy_decision LIKE ? ";

	protected static final String USER_QRY_FRAG = " AND user_name LIKE ? "; // Users
	protected static final String USER_QRY_MULTI_FRAG_V2 = " AND user_name IN  ";

	protected static final String USER_QRY_MULTI_FRAG = " AND user_name IN "
			+ "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

	protected static final String TIMEDIMENSION_MONTH_QRY_FRAG = " AND month_nb = ? ";

	protected static final String TIMEDIMENSION_DAY_QRY_FRAG = " AND day_nb = ? ";

	protected static final String SELECT_PA_CUST_ATTR_QRY = "select * from "
			+ SharedLib.REPORT_PA_CUST_ATTR_TABLE + " where policy_log_id = ?";

	protected static final String SELECT_PA_LOG_DETAILS_QRY_V2 = "SELECT ID, TIME, MONTH_NB, DAY_NB, HOST_ID, "
			+ "HOST_IP, HOST_NAME, USER_ID, USER_NAME, APPLICATION_ID, APPLICATION_NAME, ACTION, POLICY_ID, "
			+ "POLICY_FULLNAME, POLICY_NAME, POLICY_DECISION, LOG_LEVEL, FROM_RESOURCE_NAME, FROM_RESOURCE_PREFIX, "
			+ "FROM_RESOURCE_PATH, FROM_RESOURCE_SHORT_NAME, TO_RESOURCE_NAME FROM RPA_LOG WHERE id= ? ";

	protected static final String SELECT_OBL_QRY = "select name, attr_one, attr_two, attr_three from "
			+ SharedLib.REPORT_PA_OBLIGATION_TABLE + " where ref_log_id = ?";

	protected static final String SELECT_PA_DETAILS_QRY = "select * from "
			+ SharedLib.REPORT_PA_TABLE + " where id = ?";

	protected static final String TYPE_RESOURCE = "RESOURCE";
	protected static final String TYPE_USER = "USER";
	protected static final String TYPE_POLICY = "POLICY";
	protected static final String TYPE_OTHERS = "OTHERS";

	protected static final String TYPE_RESOURCE_PREFIX = "res_";
	protected static final String TYPE_USER_PREFIX = "usr_";
	protected static final String TYPE_POLICY_PREFIX = "plc_";
	protected static final String TYPE_OTHERS_PREFIX = "oth_";

	protected static final String OPERATOR_IN = "in";
	protected static final String OPERATOR_NOT_IN = "not_in";
	protected static final String OPERATOR_EQ = "eq";
	protected static final String OPERATOR_NE = "ne";
	protected static final String OPERATOR_LIKE = "like";
	protected static final String OPERATOR_GT = "gt";
	protected static final String OPERATOR_GE = "ge";
	protected static final String OPERATOR_LT = "lt";
	protected static final String OPERATOR_LE = "le";

	private static String RPT_TYPE_TABULAR = "TABULAR";
	private static String RPT_TYPE_BAR = "BAR_CHART";
	private static String RPT_TYPE_BAR_HORIZ = "HORZ_BAR_CHART";
	private static String RPT_TYPE_PIE = "PIE_CHART";

	protected static String FROM_RESOURCE_FILE_SIZE = "FILE_SIZE";
	protected static String NUMBER_OF_RECORDS = "NUMBER_RECORDS";
	protected static String FUNCTION_SUM = "sum";
	protected static String FUNCTION_AVERAGE = "average";

	protected static HashMap<String, Object> hmResourceMapping = new HashMap<String, Object>();
	protected static HashMap<String, Object> hmUserMapping = new HashMap<String, Object>();
	protected static HashMap<String, Object> hmPolicyMapping = new HashMap<String, Object>();
	protected static HashMap<String, Object> hmOthersMapping = new HashMap<String, Object>();

	private static AttributeColumnMappingDAO colMappingDAO = new AttributeColumnMappingDAOImpl();

	public enum GroupByOption{
        GROUP_BY_USER, GROUP_BY_RESOURCE, GROUP_BY_POLICY, GROUP_BY_DAY, GROUP_BY_MONTH
    }

	protected static List<String> listIgnore = Arrays.asList("activity",
			"duration", "log_level", "action", "decision", "user_name",
			"resource_path", "policy_name");
	protected static Set<String> setIgnore = new TreeSet<String>(
			String.CASE_INSENSITIVE_ORDER);

	protected static final String QRY_SINGLE_FRA = "?";

	protected static final String QRY_MULTI_FRAG = " ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

	protected ArrayList<String> arrCustomFields = new ArrayList<String>();

    protected boolean hasOrderByFields = true;

    protected boolean paginated;

	AbstractDataGenerator(Session session) {
		this.session = session;
		populateMappingData();
		setIgnore.addAll(listIgnore);
	}

	public void populateMappingData() {

		List<AttributeColumnMappingDO> colMappings = null;

		try {
			colMappings = colMappingDAO.getAll(TYPE_RESOURCE);

			for (AttributeColumnMappingDO colMap : colMappings) {
				hmResourceMapping.put(colMap.getAttributeName(), colMap);
			}

			colMappings = colMappingDAO.getAll(TYPE_USER);

			for (AttributeColumnMappingDO colMap : colMappings) {
				hmUserMapping.put(colMap.getAttributeName(), colMap);
			}

			colMappings = colMappingDAO.getAll(TYPE_POLICY);

			for (AttributeColumnMappingDO colMap : colMappings) {
				hmPolicyMapping.put(colMap.getAttributeName(), colMap);
			}

			colMappings = colMappingDAO.getAll(TYPE_OTHERS);

			for (AttributeColumnMappingDO colMap : colMappings) {
				hmOthersMapping.put(colMap.getAttributeName(), colMap);
			}
		} catch (HibernateException e) {
			log.error("Operation failed", e);
		}

	}

	public void setGroupByDimension(String groupByDimension) {
		this.groupByDimension = groupByDimension;
	}

	public String getGroupByDimension() {
		if (groupByDimension == null)
			return "";

		if (groupByDimension.toLowerCase().matches(
				"user|policy|resource|time_day|time_month")) {
			if (groupByDimension.equalsIgnoreCase("user"))
				return ALIAS_USERNAME_STRING;
			if (groupByDimension.equalsIgnoreCase("policy"))
				return GROUPBY_POLICY_COLUMN_NAME;
			if (groupByDimension.equalsIgnoreCase("resource"))
				return ALIAS_FROM_RESOURCE_NAME_STRING;
			if (groupByDimension.equalsIgnoreCase("time_day"))
				return ALIAS_DAY;
			if (groupByDimension.equalsIgnoreCase("time_month"))
				return ALIAS_MONTH;
		} else
			throw new RuntimeException(
					"groupByDimension has an invalid value: "
							+ groupByDimension);
		return "";
	}

	public void setBeginDate(Timestamp beginDate) {
		this.beginDate = beginDate;
	}

	public void setEndDate(Timestamp endDate) {
        // Hack. If the second value is "59" then the odds are good that the caller
        // wanted all times before the end of the minute (consider 1:00:00-1:59:59).
        if (endDate.getSeconds() == 59) {
            endDate.setNanos(999999999);
        }
		this.endDate = endDate;
	}

	public Timestamp getBeginDate() {
		return beginDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEnforcement(String enforcement) {
		if ((enforcement == null)
				|| (enforcement != null && enforcement.isEmpty())
				|| ((enforcement != null) && (enforcement.equalsIgnoreCase("B")))) {
			this.enforcement = "%";
		} else
			this.enforcement = enforcement;
	}

	public String getEnforcement() {
		if (this.enforcement.matches("[AD%]"))
			return this.enforcement;
		else
			throw new RuntimeException("enforcement has an invalid value: "
					+ this.enforcement);
	}

	public void setReportCriteriaJSONModel(ReportCriteriaJSONModel jsonModel) {
		this.criteriaModel = jsonModel;
	}

	public ReportCriteriaJSONModel getReportCriteriaJSONModel() {
		return criteriaModel;
	}

	public void setEventLevel(String eventLevel) {
		this.eventLevel = eventLevel;
	}

	public String getEventLevel() {
		return eventLevel;
	}

	public String getAction() {
		return action;
	}

	public void setResource(String resource) {
		if ((resource == null)
				|| ((resource != null) && (resource.length() == 0))
				|| ((resource != null) && (resource
						.equalsIgnoreCase("Any Resource")))) {
			this.resource = "%";
		} else
			this.resource = resource;
	}

	public String getResource() {
		return resource.toLowerCase();
	}

	public void setPolicy(String policy) {
		if ((policy == null)
				|| ((policy != null) && (policy.length() == 0))
				|| ((policy != null) && (policy.equalsIgnoreCase("Any Policy")))) {
			this.policy = "%";
		} else
			this.policy = policy;
	}

	public String getPolicy() {
		return policy.toLowerCase();
	}

	public void setAction(String action) {
		if ((action == null) || ((action != null) && (action.length() == 0))) {
			this.action = "%";
		} else
			this.action = action;
	}

	public void setUserName(String userName) {
		if ((userName == null)
				|| ((userName != null) && (userName.length() == 0))
				|| ((userName != null) && (userName
						.equalsIgnoreCase("Any User")))) {
			this.userName = "%";
		} else
			this.userName = userName;
	}

	public String getUserName() {
		return userName.toLowerCase();

	}

	public void setJSONData(String sData) {

		this.sData = sData;

		try {

			JSONObject obj = new JSONObject(sData);

			jArrDataHeader = obj.getJSONArray("header");

		} catch (JSONException e) {
			log.error("Error encountered in set JSONData , " + e);
		}

	}

	public String getJSONData() {
		return sData;

	}

	public void setIdForDetailsLog(String idForDetailsLog) {
		if (idForDetailsLog == null) {
			throw new IllegalArgumentException("ID of log cannot be null");
		}
		this.idForDetailsLog = Long.valueOf(idForDetailsLog);
	}

	public Long getIdForDetailsLog() {
		return this.idForDetailsLog;
	}

	public String getTimeDimension() {
		return timeDimension;
	}

	public void setTimeDimension(String timeDimension) {
		this.timeDimension = timeDimension;
	}

	protected int handlePossibleMultiStringParam(PreparedStatement ps,
			int currArg, List<String> paramValues, String genericValue)
			throws Exception {

		if (paramValues.size() == 1) {
			// Handle simple, single-value case
			String value = paramValues.get(0);
			if (value == null || value.trim().isEmpty()
					|| value.trim().equalsIgnoreCase(genericValue)) {
				value = "%";
			}
			value = value.replace("*", "%");
			ps.setString(currArg++,  getParamValue(value));
			return currArg;
		} else {
			// Handle multi-value case
			if (paramValues.size() > MAX_MULTI_PARMS) {
				throw new RuntimeException("Can not use more than "
						+ MAX_MULTI_PARMS + " search values in query.");
			}
			for (int i = 0; i < paramValues.size(); i++) {
				ps.setString(currArg++, getParamValue(paramValues.get(i).trim()));
			}
			return currArg;
		}
	}
	
	protected int handlePossibleMultiStringParam(PreparedStatement ps,
            int currArg, List<String> paramValues)
            throws Exception {

        if (paramValues.size() == 1) {
            // Handle simple, single-value case
            String value = paramValues.get(0);
            ps.setString(currArg++,  getParamValue(value));
            return currArg;
        } else {
            // Handle multi-value case
            if (paramValues.size() > MAX_MULTI_PARMS) {
                throw new RuntimeException("Can not use more than "
                        + MAX_MULTI_PARMS + " search values in query.");
            }
            for (int i = 0; i < paramValues.size(); i++) {
                ps.setString(currArg++, getParamValue(paramValues.get(i).trim()));
            }
            return currArg;
        }
    }
	
	protected String getParamValue(String value) {
		return value;
	}

	protected int handlePossibleMultiStringParam(PreparedStatement ps,
			int currArg, String paramValue) throws Exception {

		String[] paramValues = paramValue.split(",");
		if (paramValues.length == 1) {

			String value = paramValues[0].trim();
			value = value.replace("*", "%");
			ps.setString(currArg, getParamValue(value));
			return currArg;
		} else {
			// Handle multi-value case
			if (paramValues.length > MAX_MULTI_PARMS) {
				throw new RuntimeException("Can not use more than "
						+ MAX_MULTI_PARMS + " search values in query.");
			}
			int i = 0;
			// Plug in the multi-values, up to MAX_MULTI_PARMS limit
			while (i < paramValues.length) {
				ps.setString(currArg + i, getParamValue(paramValues[i].trim()));
				i++;
			}

			while (i < MAX_MULTI_PARMS) {
				ps.setNull(currArg + i, Types.VARCHAR);
				i++;
			}
			return currArg + MAX_MULTI_PARMS - 1;
		}
	}

	public List<GroupByData> generatePAGroupByData() throws Exception {
		List<GroupByData> dataList = new ArrayList<GroupByData>();
		if (groupByQueryStmt == null) {
			groupByQueryStmt = new LoggablePreparedStatement(
					session.connection(), genPAGroupByQryText());
		}
		int currArg = 0;
		groupByQueryStmt.setTimestamp(++currArg, getBeginDate());
		groupByQueryStmt.setTimestamp(++currArg, getEndDate());
		groupByQueryStmt.setString(++currArg, getResource());
		groupByQueryStmt.setInt(++currArg, Integer.parseInt(getEventLevel()));
		groupByQueryStmt.setString(++currArg, getEnforcement());
		currArg = handlePossibleMultiStringParam(groupByQueryStmt, ++currArg,
				getPolicy());
		currArg = handlePossibleMultiStringParam(groupByQueryStmt, ++currArg,
				getAction());
		currArg = handlePossibleMultiStringParam(groupByQueryStmt, ++currArg,
				getUserName());

		try (ResultSet rs = groupByQueryStmt.executeQuery()) {
			while (rs.next()) {
				GroupByData thisData = new GroupByData();
				thisData.setDimension(rs.getString(1));
				thisData.setResultCount(rs.getInt(2));
				dataList.add(thisData);
			}
		} finally {
			groupByQueryStmt.close();
		}
		return dataList;
	}

	public List<PADetailsTableData> generatePADetailsTableData()
			throws Exception {
		List<PADetailsTableData> dataList = new ArrayList<PADetailsTableData>();
		if (paDetailsTableQueryStmt == null) {
			paDetailsTableQueryStmt = new LoggablePreparedStatement(
					session.connection(), genPADetailsTableQryText());
		}
		int currArg = 0;
		paDetailsTableQueryStmt.setTimestamp(++currArg, getBeginDate());
		paDetailsTableQueryStmt.setTimestamp(++currArg, getEndDate());
		paDetailsTableQueryStmt.setString(++currArg, getResource());
		paDetailsTableQueryStmt.setInt(++currArg,
				Integer.parseInt(getEventLevel()));
		paDetailsTableQueryStmt.setString(++currArg, getEnforcement());
		currArg = handlePossibleMultiStringParam(paDetailsTableQueryStmt,
				++currArg, getPolicy());
		currArg = handlePossibleMultiStringParam(paDetailsTableQueryStmt,
				++currArg, getAction());
		if ((getTimeDimension() != null)
				&& (getTimeDimension().isEmpty() == false)) {
			if (getGroupByDimension().equalsIgnoreCase(ALIAS_DAY)
					|| getGroupByDimension().equalsIgnoreCase(ALIAS_MONTH)
					|| getGroupByDimension().isEmpty())
				paDetailsTableQueryStmt.setLong(++currArg,
						Long.parseLong(getTimeDimension()));
		}
		currArg = handlePossibleMultiStringParam(paDetailsTableQueryStmt,
				++currArg, getUserName());

		try (ResultSet rs = paDetailsTableQueryStmt.executeQuery()) {
			while (rs.next()) {
				PADetailsTableData thisData = new PADetailsTableData();
				setIndividualPADetailsTableData(rs, thisData);
				dataList.add(thisData);
			}
		} catch (SQLException e) {
			if (log.isDebugEnabled()) {
				log.debug("SQL exception error on statement: "
						+ paDetailsTableQueryStmt + "\nerror=" + e.toString());
			} else {
				log.error(e.toString(), e);
			}
		} finally {
			paDetailsTableQueryStmt.close();
		}

		if (log.isDebugEnabled()) {
			log.debug("Query return size: " + dataList.size());
		}
		return dataList;
	}

	public ReportResultData generatePADetailsTableDataV2() throws Exception {

		log.debug("generatePADetailsTableDataV2 -->> [Started]");
		Instant startgeneratePADetailsTableDataV2 = Instant.now();

		String[] columnName = null;
		List<Object[]> lListData = new LinkedList<Object[]>();
		if (paDetailsTableQueryStmt == null) {
			paDetailsTableQueryStmt = new LoggablePreparedStatement(
					session.connection(), genPADetailsTableQryTextV2());
		}
		int currArg = 1;
		paDetailsTableQueryStmt.setTimestamp(currArg++, getBeginDate());
		paDetailsTableQueryStmt.setTimestamp(currArg++, getEndDate());
		paDetailsTableQueryStmt.setInt(currArg++,
				Integer.parseInt(getEventLevel()));
		ReportCriteriaJSONModel jModel = getReportCriteriaJSONModel();
		if(!getEnforcement().equals("%"))
		    paDetailsTableQueryStmt.setString(currArg++, getEnforcement());

        // action field
        if (jModel.getGeneralActionField().getValues().size() > 1 || (!jModel.getGeneralActionField().getValues().get(0).equals("")
                && !jModel.getGeneralActionField().getValues().get(0).equals("*") && !jModel.getGeneralActionField().getValues().get(0).equals("%"))) {
            currArg = handlePossibleMultiStringParam(paDetailsTableQueryStmt,
                    currArg, jModel.getGeneralActionField().getValues());
        }

        // user lookup field
        if (jModel.getUserCriteriaLookUpField().getValues().size() > 1 || (!jModel.getUserCriteriaLookUpField().getValues().get(0).equals("Any User")
                && !jModel.getUserCriteriaLookUpField().getValues().get(0).equals("*")
                && !jModel.getUserCriteriaLookUpField().getValues().get(0).equals("%"))) {
            currArg = handlePossibleMultiStringParam(paDetailsTableQueryStmt,
                    currArg, jModel.getUserCriteriaLookUpField().getValues());
        }

        // resource lookup field
        if (jModel.getResourceCriteriaLookUpField().getValues().size() > 1
                || (!jModel.getResourceCriteriaLookUpField().getValues().get(0).equals("Any Resource")
                        && !jModel.getResourceCriteriaLookUpField().getValues().get(0).equals("*")
                        && !jModel.getResourceCriteriaLookUpField().getValues().get(0).equals("%"))) {
            currArg = handlePossibleMultiStringParam(paDetailsTableQueryStmt,
                    currArg, jModel.getResourceCriteriaLookUpField().getValues());
        }

        // policy lookup field
        if (jModel.getPolicyCriteriaLookUpField().getValues().size() > 1 || (!jModel.getPolicyCriteriaLookUpField().getValues().get(0).equals("Any Policy")
                && !jModel.getPolicyCriteriaLookUpField().getValues().get(0).equals("*")
                && !jModel.getPolicyCriteriaLookUpField().getValues().get(0).equals("%"))) {
            currArg = handlePossibleMultiStringParam(paDetailsTableQueryStmt,
                    currArg, jModel.getPolicyCriteriaLookUpField().getValues());
        }
		

		for (String value : arrCustomFields) {
			paDetailsTableQueryStmt.setString(currArg++, getParamValue(value));
		}
		long sStartTime = System.nanoTime();
		boolean hasMore = false;
		
		try (ResultSet rs = paDetailsTableQueryStmt.executeQuery()) {
			Map<Integer, String> columnIndexMap = new HashMap<Integer, String>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			columnName = new String[columnCount];
			for (int i = 0; i < columnCount; ++i) {
				int colIndex = i + 1;
				columnName[i] = rsmd.getColumnLabel(colIndex);
				String colName = rsmd.getColumnLabel(colIndex);
				mapColumnIndexes(columnIndexMap, colIndex, colName);
			}

			int limit = Integer.MAX_VALUE;
			int pagesize = getPagesize();
			if(pagesize > 0) {
                limit = pagesize;
                if (getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
                    limit = getMaxRowCount() - getOffset();
                }
            }
            int processed = 0;

	        // always check processed first
			while (processed++ < limit && rs.next()) {

				Object[] value = new Object[columnCount];
				for (int i = 0; i < columnCount; ++i) {
					int colIndex = i + 1;

					if (!columnIndexMap.isEmpty()
							&& columnIndexMap.containsKey(colIndex)) {
						String colName = columnIndexMap.get(colIndex);
						String colValue = rs.getString(colIndex);
						if (ACTION_FIELD.equalsIgnoreCase(colName)) {
							colValue = ReportActions.getDisplayValue(colValue);
						} else if (LOG_LEVEL_FIELD.equalsIgnoreCase(colName)) {
							colValue = logLevelDisplayValue(colValue);
						} else if (POLICY_DECISION_FIELD.equalsIgnoreCase(colName)) {
							colValue = accessDisplayValue(colValue);
						} else if (TIME_FIELD.equalsIgnoreCase(colName)){
						    colValue = formatDateTime(colValue);
                        } else if (DIMENSION_FIELD.equalsIgnoreCase(colName)){
							//format date values
							if (GroupByOption.GROUP_BY_DAY.toString().equals(getReportCriteriaJSONModel().getGroupingMode())){
								colValue = formatDate(rs.getString(colIndex), false);
							} else if(GroupByOption.GROUP_BY_MONTH.toString().equals(getReportCriteriaJSONModel().getGroupingMode())){
								colValue = formatDate(rs.getString(colIndex), true);
							}
						}
						value[i] = colValue;
					} else {
						value[i] = rs.getObject(colIndex);
					}
				}
				lListData.add(value);
			}
			hasMore = (getMaxRowCount() == -1 || getMaxRowCount() > getPagesize()) && rs.next();

		} catch (SQLException e) {
			if (log.isDebugEnabled()) {
				log.debug("SQL exception error on statement: "
						+ paDetailsTableQueryStmt + "\nerror=" + e.toString());
			} else {
                log.error(e.toString(), e);
            }
		} finally {
			paDetailsTableQueryStmt.close();
		}

		log.debug("Row count is --> " + lListData.size());

		log.debug("Time use for query is --> "
				+ ((System.nanoTime() - sStartTime) / 1000000.00)
				+ " miliseconds");

		return new ReportResultData(columnName,
				lListData.toArray(new Object[lListData.size()][]), hasMore);
	}
	
	public int generatePADetailsTableDataTotalV2() throws Exception {

        log.debug("generatePADetailsTableDataTotalV2 -->> [Started]");

        if (paDetailsTableTotalQueryStmt == null) {
            paDetailsTableTotalQueryStmt = session.connection().prepareStatement(genPADetailsTableQryTotalTextV2(), 
                    ResultSet.TYPE_SCROLL_INSENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
        }
        int currArg = 1;
        paDetailsTableTotalQueryStmt.setTimestamp(currArg++, getBeginDate());
        paDetailsTableTotalQueryStmt.setTimestamp(currArg++, getEndDate());
        paDetailsTableTotalQueryStmt.setInt(currArg++,
                Integer.parseInt(getEventLevel()));
        paDetailsTableTotalQueryStmt.setString(currArg++, getEnforcement());

        ReportCriteriaJSONModel jModel = getReportCriteriaJSONModel();

        // action field
        if (!jModel.getGeneralActionField().getValues().isEmpty()) {
            currArg = handlePossibleMultiStringParam(paDetailsTableTotalQueryStmt,
                    currArg, jModel.getGeneralActionField().getValues(), "");
        }

        // user lookup field
        if (!jModel.getUserCriteriaLookUpField().getValues().isEmpty()) {
            currArg = handlePossibleMultiStringParam(paDetailsTableTotalQueryStmt,
                    currArg, jModel.getUserCriteriaLookUpField().getValues(),
                    "Any User");
        }

        // resource lookup field
        if (!jModel.getResourceCriteriaLookUpField().getValues().isEmpty()) {
            currArg = handlePossibleMultiStringParam(paDetailsTableTotalQueryStmt,
                    currArg, jModel.getResourceCriteriaLookUpField()
                            .getValues(), "Any Resource");
        }

        // policy lookup field
        if (!jModel.getPolicyCriteriaLookUpField().getValues().isEmpty()) {
            currArg = handlePossibleMultiStringParam(paDetailsTableTotalQueryStmt,
                    currArg, jModel.getPolicyCriteriaLookUpField().getValues(),
                    "Any Policy");
        }

        for (String value : arrCustomFields) {
            paDetailsTableTotalQueryStmt.setString(currArg++, getParamValue(value));
        }

		long sStartTime = System.nanoTime();
		try (ResultSet rs = paDetailsTableTotalQueryStmt.executeQuery()) {
			if (rs.next()) {
				int size = rs.getInt(1);
				if (getMaxRowCount() > 0 && size > getMaxRowCount())
					size = getMaxRowCount();
				return size;
			}

		} catch (SQLException e) {
			if (log.isDebugEnabled()) {
				log.debug("SQL exception error on statement: "
						+ paDetailsTableTotalQueryStmt + "\nerror=" + e.toString());
			} else {
				log.error(e.toString(), e);
			}
		} finally {
			log.debug("Time use for query is --> "
					+ ((System.nanoTime() - sStartTime) / 1000000.00)
					+ " miliseconds");
			paDetailsTableTotalQueryStmt.close();
		}
        return 0;
    }

	private void mapColumnIndexes(Map<Integer, String> columnIndexMap,
			int colIndex, String colName) {
		if (ACTION_FIELD.equalsIgnoreCase(colName)) {
			columnIndexMap.put((colIndex), ACTION_FIELD);
		} else if (LOG_LEVEL_FIELD.equalsIgnoreCase(colName)) {
			columnIndexMap.put((colIndex), LOG_LEVEL_FIELD);
		} else if (POLICY_DECISION_FIELD.equalsIgnoreCase(colName)) {
			columnIndexMap.put((colIndex), POLICY_DECISION_FIELD);
		} else if (TIME_FIELD.equalsIgnoreCase(colName)){
			columnIndexMap.put((colIndex), TIME_FIELD);
		} else if (DIMENSION_FIELD.equalsIgnoreCase(colName)){
			columnIndexMap.put((colIndex), DIMENSION_FIELD);
		}
	}

	private String accessDisplayValue(String accessLevel) {
		if ("A".equalsIgnoreCase(accessLevel)) {
			return "Allowed";
		} else if ("D".equalsIgnoreCase(accessLevel)) {
			return "Denied";
		}
		return accessLevel;
	}

	private String logLevelDisplayValue(String logLevel) {
		if ("3".equals(logLevel.trim())) {
			return "User Events";
		} else if ("2".equals(logLevel.trim())) {
			return "Application Events";
		} else if ("1".equals(logLevel.trim())) {
			return "All System Events";
		}
		return logLevel;
	}

	private int setCriteriaParameter(int currArg, CriteriaFieldModel field)
			throws Exception {
		if (field.isMultiValue()) {
			currArg = handlePossibleMultiStringParam(paDetailsTableQueryStmt,
					++currArg, field.getValues(), "");
		} else {
			currArg = handlePossibleMultiStringParam(paDetailsTableQueryStmt,
					++currArg, field.getValue());
		}
		return currArg;
	}

	public PALogDetailsData generatePALogDetailsData() throws Exception {
		PALogDetailsData logDetailsData = new PALogDetailsData();

		setCustAttrData(logDetailsData);
		setObligationLogData(logDetailsData);
		setPASingleLogDetailsData(logDetailsData);

		return logDetailsData;
	}

	public PALogDetailsData generatePALogDetailsDataV2() throws Exception {
		PALogDetailsData logDetailsData = new PALogDetailsData();

		Map<String, AttributeMappingData> mappingData = getMappingTableData();

		setCustAttrDataV2(logDetailsData, mappingData);
		setObligationLogData(logDetailsData);
		setPASingleLogDetailsDataV2(logDetailsData);

		return logDetailsData;
	}

	public void cleanup() throws Exception {
		session.connection().close();
	}

	protected static final String SELECT_MAPPING_DATA_V2 = "SELECT id, mapped_column, name, attr_type FROM RPA_LOG_MAPPING";

	protected Map<String, AttributeMappingData> getMappingTableData()
			throws Exception {
		Map<String, AttributeMappingData> mappingData = new HashMap<String, AttributeMappingData>();

		if (paMappingtAttrQueryStmt == null) {
			paMappingtAttrQueryStmt = new LoggablePreparedStatement(
					session.connection(), SELECT_MAPPING_DATA_V2);
		}

		try (ResultSet rs = paMappingtAttrQueryStmt.executeQuery()) {
			while (rs.next()) {
				AttributeMappingData mapData = new AttributeMappingData();
				mapData.setAttributeId(rs.getLong("id"));
				mapData.setType(rs.getString("attr_type"));
				mapData.setName(rs.getString("name"));

				String mappingCol = rs.getString("mapped_column");
				if (mappingCol == null) {
					mapData.setFromMainTable(false);
					mappingCol = "MAP_ATTRIB_" + mapData.getName();
				} else {
					mapData.setFromMainTable(true);
				}

				mappingData.put(mappingCol, mapData);
			}
		} finally {
			paMappingtAttrQueryStmt.close();
		}

		return mappingData;
	}

	protected String getRPALOGAttributeDataQry(
			Map<String, AttributeMappingData> mappingData) {
		StringBuffer sBuf = new StringBuffer();
		sBuf.append("SELECT ");

		List<String> colNames = new ArrayList<String>();

		for (String key : mappingData.keySet()) {

			if (key.startsWith("MAP_ATTRIB_")) {
				continue;
			}

			colNames.add(key);
		}

		int count = 0;
		for (String colName : colNames) {

			count++;
			sBuf.append(colName);

			if (count < colNames.size()) {
				sBuf.append(", ");
			}
		}

		sBuf.append(" FROM RPA_LOG WHERE id = ?");
		return sBuf.toString();
	}

	private List<CustomAttributeData> getMainLogTableAttributeData(
			Map<String, AttributeMappingData> mappingData) throws Exception {
		List<CustomAttributeData> custAttrDataList = new ArrayList<CustomAttributeData>();
		paMainAttrQueryStmt = new LoggablePreparedStatement(
				session.connection(), getRPALOGAttributeDataQry(mappingData));

		paMainAttrQueryStmt.setLong(1, getIdForDetailsLog());

		int count = 1;
		try (ResultSet rs = paMainAttrQueryStmt.executeQuery()) {
			if (rs.next()) {

				for (Map.Entry<String, AttributeMappingData> entry : mappingData
						.entrySet()) {
					String colName = entry.getKey();
					AttributeMappingData mapData = entry.getValue();
					if (colName.toLowerCase().startsWith("attr")) {
						CustomAttributeData custAttrData = new CustomAttributeData();
						custAttrData.setId(count++);
						custAttrData.setLogId(getIdForDetailsLog());
						String attrType = mapData.getType().substring(0,1) + mapData.getType().substring(1).toLowerCase();
						custAttrData.setAttributeName(String.format("%s.%s", attrType, mapData.getName()));
						custAttrData.setAttributeValue(rs.getString(colName));
						custAttrDataList.add(custAttrData);
					}
				}
			}

		} catch (Exception e) {
			log.error("Error occurrend in getMainLogTableAttributeData.", e);
		} finally {
			paMainAttrQueryStmt.close();
		}
		return custAttrDataList;
	}

	protected static final String SELECT_RPA_LOG_ATTR_QRY_V2 = "SELECT ATTR_ID, POLICY_LOG_ID, ATTR_VALUE  FROM RPA_LOG_ATTR WHERE policy_log_id = ?";

	protected void setCustAttrDataV2(PALogDetailsData logDetailsData,
			Map<String, AttributeMappingData> mappingData) throws Exception {

		ResultSet rs = null;
		try {
			List<CustomAttributeData> custAttrDataList = new ArrayList<CustomAttributeData>();
			custAttrDataList.addAll(getMainLogTableAttributeData(mappingData));
			paCustAttrQueryStmt = new LoggablePreparedStatement(
					session.connection(), SELECT_RPA_LOG_ATTR_QRY_V2);

			paCustAttrQueryStmt.setLong(1, getIdForDetailsLog());

			int count = custAttrDataList.size();

			rs = paCustAttrQueryStmt.executeQuery();

			while (rs.next()) {
				CustomAttributeData custAttrData = new CustomAttributeData();
				custAttrData.setId(count++);
				custAttrData.setLogId(rs.getLong("POLICY_LOG_ID"));

				long attrId = rs.getLong("ATTR_ID");
				String attrType = getAttributeTypeById(attrId, mappingData);
				attrType = attrType.substring(0,1) + attrType.substring(1).toLowerCase();
				custAttrData.setAttributeName(String.format("%s.%s",
						attrType,
						getAttributeNameById(attrId, mappingData)));
				custAttrData.setAttributeValue(rs.getString("ATTR_VALUE"));
				custAttrDataList.add(custAttrData);
			}
			logDetailsData.setCustAttrData(custAttrDataList);
		} catch (Exception e) {
			log.error("Error occurrend in setCustAttrDataV2.", e);
		} finally {
			if (rs != null)
				rs.close();
			paCustAttrQueryStmt.close();
		}
	}

	private String getAttributeNameById(long attrbId, Map<String, AttributeMappingData> mappingData) {
		for (AttributeMappingData value : mappingData.values()) {
			if (value.getAttributeId() == attrbId) {
				return value.getName();
			}
		}
		return " ";
	}

	private String getAttributeTypeById(long attrbId, Map<String, AttributeMappingData> mappingData) {
		for (AttributeMappingData value : mappingData.values()) {
			if (value.getAttributeId() == attrbId) {
				return value.getType();
			}
		}
		return " ";
	}

	protected void setCustAttrData(PALogDetailsData logDetailsData)
			throws Exception {
		if (paCustAttrQueryStmt == null) {
			paCustAttrQueryStmt = new LoggablePreparedStatement(
					session.connection(), SELECT_PA_CUST_ATTR_QRY);
		}
		paCustAttrQueryStmt.setLong(1, getIdForDetailsLog());

		try (ResultSet rs = paCustAttrQueryStmt.executeQuery()) {
			List<CustomAttributeData> custAttrDataList = new ArrayList<CustomAttributeData>();
			while (rs.next()) {
				CustomAttributeData custAttrData = new CustomAttributeData();
				custAttrData.setId(rs.getLong(1));
				custAttrData.setLogId(rs.getLong(2));
				custAttrData.setAttributeName(rs.getString(3));
				custAttrData.setAttributeValue(rs.getString(4));
				custAttrDataList.add(custAttrData);
			}
			logDetailsData.setCustAttrData(custAttrDataList);
		} finally {
			paCustAttrQueryStmt.close();
		}
	}

	protected void setObligationLogData(PALogDetailsData logDetailsData)
			throws Exception {
		if (oblQueryStmt == null) {
			oblQueryStmt = new LoggablePreparedStatement(session.connection(),
					SELECT_OBL_QRY);
		}
		if (log.isTraceEnabled()) {
			log.trace("AbstractDataGenerator.setObligationLogData: "
					+ SELECT_OBL_QRY);
		}
		oblQueryStmt.setLong(1, getIdForDetailsLog());

		try (ResultSet rs = oblQueryStmt.executeQuery()) {
			List<ObligationLogData> oblDataList = new ArrayList<ObligationLogData>();

			while (rs.next()) {
				ObligationLogData obligationLogData = new ObligationLogData();
				obligationLogData.setName(rs.getString(1));
				obligationLogData.setAttributeOne(rs.getString(2));
				obligationLogData.setAttributeTwo(rs.getString(3));
				obligationLogData.setAttributeThree(rs.getString(4));
				oblDataList.add(obligationLogData);
			}
			logDetailsData.setObligationLogData(oblDataList);
		} finally {
			oblQueryStmt.close();
		}
	}

	protected void setPASingleLogDetailsDataV2(PALogDetailsData logDetailsData)
			throws Exception {
		paSingleLogDetailsQueryStmt = new LoggablePreparedStatement(
				session.connection(), SELECT_PA_LOG_DETAILS_QRY_V2);
		paSingleLogDetailsQueryStmt.setLong(1, getIdForDetailsLog());

		try (ResultSet rs = paSingleLogDetailsQueryStmt.executeQuery()) {
			if (rs.next()) {
				PADetailsTableData thisData = new PADetailsTableData();
				setIndividualPADetailsTableData(rs, thisData);
				logDetailsData.setSingleLogDetailsData(thisData);
			}
		} finally {
			paSingleLogDetailsQueryStmt.close();
		}
	}

	protected void setPASingleLogDetailsData(PALogDetailsData logDetailsData)
			throws Exception {
		if (paSingleLogDetailsQueryStmt == null) {
			paSingleLogDetailsQueryStmt = new LoggablePreparedStatement(
					session.connection(), SELECT_PA_DETAILS_QRY);
		}
		paSingleLogDetailsQueryStmt.setLong(1, getIdForDetailsLog());

		try (ResultSet rs = paSingleLogDetailsQueryStmt.executeQuery()) {
			if (rs.next()) {
				PADetailsTableData thisData = new PADetailsTableData();
				setIndividualPADetailsTableData(rs, thisData);
				logDetailsData.setSingleLogDetailsData(thisData);
			}
		} finally {
			paSingleLogDetailsQueryStmt.close();
		}
	}

	/**
	 * Implemented by the concrete class to return the select statement prefix
	 * 
	 * @return Cannot be null. Can be empty string.
	 */
	abstract protected String getSelectStatementPrefix(int rowCount);
	
	protected String getSelectTotalStatementPrefix() {
	    return "select count(1) FROM RPA_LOG RPA ";
	}

	/**
	 * Implemented by the concrete class to return the select statement suffix
	 * 
	 * @return Cannot be null. Can be empty string.
	 */
	abstract protected String getSelectStatementSuffix(int rowCount, int offset);

	private void setIndividualPADetailsTableData(ResultSet rs,
			PADetailsTableData thisData) throws Exception {
		
		thisData.setId(rs.getLong(1));
		thisData.setTime(rs.getTimestamp(2));
		thisData.setMonthNb(rs.getLong(3));
		thisData.setDayNb(rs.getLong(4));
		thisData.setHostId(rs.getLong(5));
		thisData.setHostIP(rs.getString(6));
		thisData.setHostName(rs.getString(7));
		thisData.setUserId(rs.getLong(8));
		thisData.setUserName(rs.getString(9));
		thisData.setApplicationId(rs.getLong(10));
		thisData.setApplicationName(rs.getString(11));
		thisData.setAction(rs.getString(12));
		thisData.setPolicyId(rs.getLong(13));
        // Not a typo, as far as I know. We appear to prefer to use
        // #15 (policy_name) for both name and full name
		thisData.setPolicyFullName(rs.getString(15));
		thisData.setPolicyName(rs.getString(15));
		thisData.setPolicyDecision(rs.getString(16));
		thisData.setLogLevel(rs.getInt(17));
		thisData.setFromResourceName(rs.getString(18));
		thisData.setFromResourcePrefix(rs.getString(19));
		thisData.setFromResourcePath(rs.getString(20));
		thisData.setFromResourceShortName(rs.getString(21));
		thisData.setToResourceName(rs.getString(22));
	}

	// /////////////////////////////////////////////////////////////
	// / All Tracking activity related code should be here ///
	// ///////////////////////////////////////////////////////////
	protected static final String TR_FROM_QRY_FRAG = " FROM "
			+ SharedLib.REPORT_TA_TABLE
			+ " AS t_tracking_activity_log WHERE time >= ? " + // beginDate
			" AND time <= ? "; // endDate

	protected static final String SELECT_TA_DETAILS_QRY = "select * from "
			+ SharedLib.REPORT_TA_TABLE + " where id = ?";

	protected static final String SELECT_TA_CUST_ATTR_QRY = "select * from "
			+ SharedLib.REPORT_TA_CUST_ATTR_TABLE
			+ " where tracking_log_id = ?";

	protected PreparedStatement trCustAttrQueryStmt;
	protected PreparedStatement trDetailsTableQueryStmt;
	protected PreparedStatement taSingleLogDetailsQueryStmt;
	protected PreparedStatement trGroupByQueryStmt;

	public List<GroupByData> generateTRGroupByData() throws Exception {
		List<GroupByData> dataList = new ArrayList<GroupByData>();
		if (trGroupByQueryStmt == null) {
			trGroupByQueryStmt = new LoggablePreparedStatement(
					session.connection(), genTRGroupByQryText());
		}
		int currArg = 0;
		trGroupByQueryStmt.setTimestamp(++currArg, getBeginDate());
		trGroupByQueryStmt.setTimestamp(++currArg, getEndDate());
		trGroupByQueryStmt.setString(++currArg, getResource());
		trGroupByQueryStmt.setInt(++currArg, Integer.parseInt(getEventLevel()));
		currArg = handlePossibleMultiStringParam(trGroupByQueryStmt, ++currArg,
				getAction());
		currArg = handlePossibleMultiStringParam(trGroupByQueryStmt, ++currArg,
				getUserName());

		try (ResultSet rs = trGroupByQueryStmt.executeQuery()) {
			while (rs.next()) {
				GroupByData thisData = new GroupByData();
				thisData.setDimension(rs.getString(1));
				thisData.setResultCount(rs.getInt(2));
				dataList.add(thisData);
			}
		} finally {
			trGroupByQueryStmt.close();
		}
		return dataList;
	}

	public List<DetailsTableData> generateTRDetailsTableData() throws Exception {
		List<DetailsTableData> dataList = new ArrayList<DetailsTableData>();
		if (trDetailsTableQueryStmt == null) {
			trDetailsTableQueryStmt = new LoggablePreparedStatement(
					session.connection(), genTRDetailsTableQryText());
		}
		int currArg = 0;
		trDetailsTableQueryStmt.setTimestamp(++currArg, getBeginDate());
		trDetailsTableQueryStmt.setTimestamp(++currArg, getEndDate());
		trDetailsTableQueryStmt.setString(++currArg, getResource());
		trDetailsTableQueryStmt.setInt(++currArg,
				Integer.parseInt(getEventLevel()));
		currArg = handlePossibleMultiStringParam(trDetailsTableQueryStmt,
				++currArg, getAction());
		if ((getTimeDimension() != null)
				&& (getTimeDimension().isEmpty() == false)) {
			if (getGroupByDimension().equalsIgnoreCase(ALIAS_DAY)
					|| getGroupByDimension().equalsIgnoreCase(ALIAS_MONTH)
					|| getGroupByDimension().isEmpty())
				trDetailsTableQueryStmt.setLong(++currArg,
						Long.parseLong(getTimeDimension()));
		}
		currArg = handlePossibleMultiStringParam(trDetailsTableQueryStmt,
				++currArg, getUserName());

		try (ResultSet rs = trDetailsTableQueryStmt.executeQuery()) {
			while (rs.next()) {
				DetailsTableData thisData = new DetailsTableData();
				setIndividualTADetailsTableData(rs, thisData);
				dataList.add(thisData);
			}
		} finally {
			trDetailsTableQueryStmt.close();
		}
		return dataList;
	}

	public TRLogDetailsData generateTRLogDetailsData() throws Exception {
		TRLogDetailsData logDetailsData = new TRLogDetailsData();

		setCustAttrData(logDetailsData);
		setTASingleLogDetailsData(logDetailsData);

		return logDetailsData;
	}

	protected String genPAGroupByQryText() {
		StringBuilder sb = new StringBuilder(
				getSelectStatementPrefix(getMaxRowCount()));
		sb.append(getGroupByDimension()).append(GROUPBY_QRY_FRAG);
		sb.append(FROM_QRY_FRAG);

		sb.append(RESOURCE_QRY_FRAG);

		sb.append(EVENT_LEVEL_QRY_FRAG);
		sb.append(ENFORCEMENTS_QRY_FRAG);

		if (getPolicy().split(",").length > 1)
			sb.append(POLICY_QRY_MULTI_FRAG);
		else
			sb.append(POLICY_QRY_FRAG);

		if (getAction().split(",").length > 1)
			sb.append(ACTION_QRY_MULTI_FRAG);
		else
			sb.append(ACTION_QRY_FRAG);

		if (getUserName().split(",").length > 1)
			sb.append(USER_QRY_MULTI_FRAG);
		else
			sb.append(USER_QRY_FRAG);

        hasOrderByFields = true;
		sb.append(" GROUP BY ").append(getGroupByDimension())
				.append(" ORDER BY ResultCount DESC ");
		
        int limit = getPagesize();
        if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
        sb.append(getSelectStatementSuffix(limit, getOffset()));
        
		String groupByQryText = sb.toString();
		if (log.isTraceEnabled()) {
			log.trace("AbstractDataGenerator.groupByQuery: " + groupByQryText);
		}
		return groupByQryText;
	}

	protected String genTRGroupByQryText() {
		StringBuilder sb = new StringBuilder(
				getSelectStatementPrefix(getMaxRowCount()));
		sb.append(getGroupByDimension()).append(GROUPBY_QRY_FRAG);
		sb.append(TR_FROM_QRY_FRAG);

		sb.append(RESOURCE_QRY_FRAG);

		sb.append(EVENT_LEVEL_QRY_FRAG);

		if (getAction().split(",").length > 1)
			sb.append(ACTION_QRY_MULTI_FRAG);
		else
			sb.append(ACTION_QRY_FRAG);

		if (getUserName().split(",").length > 1)
			sb.append(USER_QRY_MULTI_FRAG);
		else
			sb.append(USER_QRY_FRAG);

        hasOrderByFields = true;
		sb.append(" GROUP BY ").append(getGroupByDimension())
				.append(" ORDER BY ResultCount DESC ");
		
        int limit = getPagesize();
        if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
        sb.append(getSelectStatementSuffix(limit, getOffset()));
        
		String trGroupByQryText = sb.toString();
		if (log.isTraceEnabled()) {
			log.trace("AbstractDataGenerator.trGroupByQryText: "
					+ trGroupByQryText);
		}
		return trGroupByQryText;
	}

	protected String genPADetailsTableQryText() {
		StringBuilder sb = new StringBuilder(
				getSelectStatementPrefix(getMaxRowCount()));
		sb.append(" * ");
		sb.append(FROM_QRY_FRAG);

		sb.append(RESOURCE_QRY_FRAG);

		sb.append(EVENT_LEVEL_QRY_FRAG);
		sb.append(ENFORCEMENTS_QRY_FRAG);

		if (getPolicy().split(",").length > 1)
			sb.append(POLICY_QRY_MULTI_FRAG);
		else
			sb.append(POLICY_QRY_FRAG);

		if (getAction().split(",").length > 1)
			sb.append(ACTION_QRY_MULTI_FRAG);
		else
			sb.append(ACTION_QRY_FRAG);

		if ((getTimeDimension() != null)
				&& (getTimeDimension().isEmpty() == false)) {
			if (getGroupByDimension().equalsIgnoreCase(ALIAS_DAY))
				sb.append(TIMEDIMENSION_DAY_QRY_FRAG);
			else if (getGroupByDimension().equalsIgnoreCase(ALIAS_MONTH))
				sb.append(TIMEDIMENSION_MONTH_QRY_FRAG);
			else if (getGroupByDimension().isEmpty())
				sb.append(TIMEDIMENSION_DAY_QRY_FRAG);
		}

		if (getUserName().split(",").length > 1)
			sb.append(USER_QRY_MULTI_FRAG);
		else
			sb.append(USER_QRY_FRAG);

        hasOrderByFields = true;
		sb.append(" ORDER BY time DESC ");
		
		int limit = getPagesize();
        if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
		sb.append(getSelectStatementSuffix(limit, getOffset()));
		
		String paDetailsTableQryText = sb.toString();
		if (log.isTraceEnabled()) {
			log.trace("AbstractDataGenerator.PADetailsTableQry: "
					+ paDetailsTableQryText);
		}
		return paDetailsTableQryText;
	}

	protected String genPADetailsTableQryTextV2() {

		try {
			log.debug("genPADetailsTableQryTextV2 query creation --> [Started]");
			String reportType = getReportCriteriaJSONModel()
					.getSavedInfoDetails("report_type");

			if (RPT_TYPE_TABULAR.equalsIgnoreCase(reportType)) {
				paginated = true;
				return genPADetailsQuerySQLNoGrouping();

			} else {
				String groupingMode = getReportCriteriaJSONModel()
						.getGroupingMode();
				List<OrderByModel> orderByModels = getReportCriteriaJSONModel()
						.getOrderByList();

				String orderByCol = "ResultCount";
				String sortBy = "DESC";

				if (!orderByModels.isEmpty()) {
					OrderByModel orderBy = orderByModels.get(0);
					orderByCol = orderBy.getColumnName();
					sortBy = orderBy.getSortOrder();
				}

				return genPADetailsQuerySQLGrouping(groupingMode, orderByCol, sortBy);
			}
		} catch (Exception e) {
			log.error("Error encountered in genPADetailsTableQryTextV2", e);
		}
		return "";
	}
	
	protected String genPADetailsTableQryTotalTextV2() {

        try {
            log.debug("genPADetailsTableQryTotalTextV2 query creation --> [Started]");
            String reportType = getReportCriteriaJSONModel()
                    .getSavedInfoDetails("report_type");

            if (RPT_TYPE_TABULAR.equalsIgnoreCase(reportType)) {
                return genPADetailsTotalQuerySQLNoGrouping();
            } else {
                throw new Exception(reportType + " type pagination not supported");
            }
        } catch (Exception e) {
            log.error("Error encountered in genPADetailsTableQryTextV2", e);
        }
        return "";
    }

	/**
	 * <p>
	 * Get the Group by column according to the current working database
	 * </p>
	 * 
	 * @param groupingMode
	 *            grouping mode.
	 * @return group by query part
	 */
	public String getSelectColumnForGroupBy(String groupingMode) {
		if (groupingMode == null)
			return "";

		if (GroupByOption.GROUP_BY_USER.toString().equals(groupingMode)) {
			return ALIAS_USERNAME_STRING;
		} else if (GroupByOption.GROUP_BY_RESOURCE.toString().equals(groupingMode)) {
			return ALIAS_FROM_RESOURCE_NAME_STRING;
		} else if (GroupByOption.GROUP_BY_POLICY.toString().equals(groupingMode)) {
			return GROUPBY_POLICY_COLUMN_NAME;
		} else if (GroupByOption.GROUP_BY_DAY.toString().equals(groupingMode)) {
			return GROUPBY_DAY;
		} else if (GroupByOption.GROUP_BY_MONTH.toString().equals(groupingMode)) {
			return GROUPBY_MONTH;
		}
		return groupingMode;
	}

	public String getGroupByColumn(String groupingMode) {
		if (groupingMode == null)
			return "";

		if (GroupByOption.GROUP_BY_USER.toString().equals(groupingMode)) {
			return ALIAS_USERNAME_STRING;
		} else if (GroupByOption.GROUP_BY_RESOURCE.toString().equals(groupingMode)) {
			return ALIAS_FROM_RESOURCE_NAME_STRING;
		} else if (GroupByOption.GROUP_BY_POLICY.toString().equals(groupingMode)) {
			return GROUPBY_POLICY_COLUMN_NAME;
		} else if (GroupByOption.GROUP_BY_DAY.toString().equals(groupingMode)) {
			return GROUPBY_DAY;
		} else if (GroupByOption.GROUP_BY_MONTH.toString().equals(groupingMode)) {
			return GROUPBY_MONTH;
		}
		return ALIAS_USERNAME_STRING;
	}

	public String getOrderByColumnForGrpSQL(String groupingMode,
			String orderByCol, String sortBy) {
        hasOrderByFields = true;
        
		if (groupingMode == null) {
            hasOrderByFields = false;
			return "";
        }
        
		if (GroupByOption.GROUP_BY_USER.toString().equals(groupingMode)) {

			orderByCol = (ORDERBY_RESULT_COUNT.equals(orderByCol)) ? ORDERBY_RESULT_COUNT
					: ALIAS_USERNAME_STRING;
			return " ORDER BY " + orderByCol + " " + sortBy + " ";

		} else if (GroupByOption.GROUP_BY_RESOURCE.toString().equals(groupingMode)) {

			orderByCol = (ORDERBY_RESULT_COUNT.equals(orderByCol)) ? ORDERBY_RESULT_COUNT
					: ALIAS_FROM_RESOURCE_NAME_STRING;
			return " ORDER BY " + orderByCol + " " + sortBy + " ";

		} else if (GroupByOption.GROUP_BY_POLICY.toString().equals(groupingMode)) {

			orderByCol = (ORDERBY_RESULT_COUNT.equals(orderByCol)) ? ORDERBY_RESULT_COUNT
					: GROUPBY_POLICY_COLUMN_NAME;
			return " ORDER BY " + orderByCol + " " + sortBy + " ";

		} else if (GroupByOption.GROUP_BY_DAY.toString().equals(groupingMode)) {

			orderByCol = (ORDERBY_RESULT_COUNT.equals(orderByCol)) ? ORDERBY_RESULT_COUNT
					: GROUPBY_DAY;
			return " ORDER BY " + orderByCol + " " + sortBy + " ";

		} else if (GroupByOption.GROUP_BY_MONTH.toString().equals(groupingMode)) {

			orderByCol = (ORDERBY_RESULT_COUNT.equals(orderByCol)) ? ORDERBY_RESULT_COUNT
					: GROUPBY_MONTH;
			return " ORDER BY " + orderByCol + " " + sortBy + " ";
		}
		return ORDERBY_RESULT_COUNT;
	}

	protected String genPADetailsQuerySQLGrouping(String groupByMode,
			String orderByCol, String sortBy) throws Exception {

		ReportCriteriaJSONModel jModel = getReportCriteriaJSONModel();
		StringBuffer sBufTemp = new StringBuffer();
		StringBuilder sb = new StringBuilder(
				getSelectStatementPrefix(getMaxRowCount()));
		
		// group by column
		sb.append(getSelectColumnForGroupBy(groupByMode)).append(
				GROUPBY_QRY_FRAG);

		sBufTemp.append(" left join (select policy_log_id, ");
		boolean isEAV = extendedAttributeQueryCreate(sBufTemp, false, jModel,
				new ArrayList<String>(), new ArrayList<String>());

        sb = new StringBuilder(sb.toString().trim());
        
		String sEAVResult = sBufTemp.toString().trim();

		if (sb.charAt(sb.length() - 1) == ',') {
			sb.setLength(sb.length() - 1);
		}

		sb.append(" FROM RPA_LOG RPA ");

		if (isEAV) {

		    sb.append(sEAVResult);
		    if (sb.charAt(sb.length() - 1) == ',') {
	            sb.setLength(sb.length() - 1);
	        }
		    sb.append(" FROM RPA_LOG_ATTR GROUP BY POLICY_LOG_ID ) RPALA ON RPA.id = RPALA.policy_log_id ");
		}

		sb.append(" WHERE time >= ?  AND time <= ? ");

		sb.append(EVENT_LEVEL_QRY_FRAG);
		
		if(!getEnforcement().equals("%"))
            sb.append(ENFORCEMENTS_QRY_FRAG);

        // action field
		if (jModel.getGeneralActionField().getValues().size() > 1 || (!jModel.getGeneralActionField().getValues().get(0).equals("")
                && !jModel.getGeneralActionField().getValues().get(0).equals("*") && !jModel.getGeneralActionField().getValues().get(0).equals("%"))) {
            if (jModel.getGeneralActionField().isMultiValue()) {
                sb.append(ACTION_QRY_MULTI_FRAG_V2);
                sb.append(createParametersForInQuery(jModel.getGeneralActionField().getValues()));
            } else {
                sb.append(ACTION_QRY_FRAG);
            }
        }

        // user lookup field
        if (jModel.getUserCriteriaLookUpField().getValues().size() > 1 || (!jModel.getUserCriteriaLookUpField().getValues().get(0).equals("Any User")
                && !jModel.getUserCriteriaLookUpField().getValues().get(0).equals("*")
                && !jModel.getUserCriteriaLookUpField().getValues().get(0).equals("%"))) {
            if (jModel.getUserCriteriaLookUpField().isMultiValue()) {
                sb.append(USER_QRY_MULTI_FRAG_V2);
                sb.append(createParametersForInQuery(jModel.getUserCriteriaLookUpField().getValues()));
            } else {
                sb.append(USER_QRY_FRAG);
            }
        }

        // resource lookup field
        if (jModel.getResourceCriteriaLookUpField().getValues().size() > 1
                || (!jModel.getResourceCriteriaLookUpField().getValues().get(0).equals("Any Resource")
                        && !jModel.getResourceCriteriaLookUpField().getValues().get(0).equals("*")
                        && !jModel.getResourceCriteriaLookUpField().getValues().get(0).equals("%"))) {
            if (jModel.getResourceCriteriaLookUpField().isMultiValue()) {
                sb.append(RESOURCE_QRY_MULTI_FRAG_V2);
                sb.append(createParametersForInQuery(jModel.getResourceCriteriaLookUpField().getValues()));
            } else {
                sb.append(RESOURCE_QRY_FRAG);
            }
        }

        // policy lookup field
        if (jModel.getPolicyCriteriaLookUpField().getValues().size() > 1 || (!jModel.getPolicyCriteriaLookUpField().getValues().get(0).equals("Any Policy")
                && !jModel.getPolicyCriteriaLookUpField().getValues().get(0).equals("*")
                && !jModel.getPolicyCriteriaLookUpField().getValues().get(0).equals("%"))) {
            if (jModel.getPolicyCriteriaLookUpField().isMultiValue()) {
                sb.append(POLICY_QRY_MULTI_FRAG_V2);
                sb.append(createParametersForInQuery(jModel.getPolicyCriteriaLookUpField().getValues()));
            } else {
                sb.append(POLICY_QRY_FRAG);
            }
        }
        
		sb.append(generateWhereCondition());

		sb.append(" GROUP BY ").append(getGroupByColumn(groupByMode));

		List<CriteriaFieldModel> aggregatorList = jModel.getAggregators();
		sb.append(getHavingClause(aggregatorList));
		
		sb.append(getOrderByColumnForGrpSQL(groupByMode, orderByCol, sortBy));
		
        int limit = getPagesize();
        if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
        sb.append(getSelectStatementSuffix(limit, getOffset()));
        
		String groupByQryText = sb.toString();

		if(log.isDebugEnabled()) {
            log.debug("genPADetailsQuerySQLGrouping: " + groupByQryText);
		}
		return groupByQryText;
	}

	protected String generateSelectClause(
			List<CriteriaFieldModel> aggregatorList) throws JSONException {
		if (aggregatorList != null && aggregatorList.size() > 0) {
			return generateSelectColumnForAggregateWithoutGrouping();
		}
		return generateSelectColumn();
	}

	protected String generateSelectColumnForAggregateWithoutGrouping()
			throws JSONException {
		StringBuffer sBuf = new StringBuffer();
		StringBuffer sBufTemp = new StringBuffer();

		sBuf.append(" 1 ");
		sBufTemp.append(" left join (select policy_log_id, ");

		boolean isEAV = false;

		ReportCriteriaJSONModel jModel = getReportCriteriaJSONModel();

		List<String> headers = jModel.getColumnHeaders();
		List<String> orderByCols = getOrderByColNames(jModel);

		isEAV = headerColumnEAVHandler(sBuf, sBufTemp, isEAV, headers);
		isEAV = orderByEAVHandler(sBufTemp, isEAV, jModel, headers);
		isEAV = extendedAttributeQueryCreate(sBufTemp, isEAV, jModel, headers,
				orderByCols);

		String sResult = sBuf.toString().trim();
		String sEAVResult = sBufTemp.toString().trim();

		if (sResult.endsWith(",")) {
			sResult = sResult.substring(0, sResult.length() - 1);
		}

		sResult = sResult + " FROM RPA_LOG RPA ";

		if (isEAV) {

			if (sEAVResult.endsWith(",")) {
				sEAVResult = sEAVResult.substring(0, sEAVResult.length() - 1);
			}

			sEAVResult = sEAVResult
					+ (" FROM RPA_LOG_ATTR GROUP BY POLICY_LOG_ID ) RPALA ON rpa.id = RPALA.policy_log_id");
			sResult = sResult + sEAVResult;
		}

		return sResult;
	}

	protected String getHavingClause(List<CriteriaFieldModel> aggregatorList) {

		if (aggregatorList == null || aggregatorList.size() == 0) {
			log.debug("Aggregator list is Empty");
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (CriteriaFieldModel model : aggregatorList) {
			if (FROM_RESOURCE_FILE_SIZE.equalsIgnoreCase(model.getName())) {
				if (!first) {
					sb.append(" AND ");
				}
				String function = model.getFunction();
				if (FUNCTION_SUM.equalsIgnoreCase(function)) {
					sb.append(" SUM(from_resource_size) ")
							.append(getRealOperator(model.getOperator()))
							.append(" ").append(model.getValue());
					first = false;
				} else if (FUNCTION_AVERAGE.equalsIgnoreCase(function)) {
					sb.append(" AVG(from_resource_size) ")
							.append(getRealOperator(model.getOperator()))
							.append(" ").append(model.getValue());
					first = false;
				}
			} else if (NUMBER_OF_RECORDS.equalsIgnoreCase(model.getName())) {
				if (!first) {
					sb.append(" AND ");
				}
				String function = model.getFunction();
				if (FUNCTION_SUM.equalsIgnoreCase(function)) {
					sb.append(" COUNT(*) ")
							.append(getRealOperator(model.getOperator()))
							.append(" ").append(model.getValue());
					first = false;
				}
				/*
				 * following case does not make sense
				 */
				/*
				 * else if (FUNCTION_AVERAGE.equalsIgnoreCase(function)) {
				 * sb.append
				 * ("AVG(from_resource_size) ").append(getRealOperator(model
				 * .getOperator())).append(" ").append(model.getValue()); first
				 * = false; }
				 */
			}
		}
		if (sb.toString().isEmpty()) {
			return "";
		}
		return " HAVING " + sb.append(" ").toString();
	}

	private String genPADetailsQuerySQLNoGrouping() throws Exception {
		StringBuilder sb = new StringBuilder(
				getSelectStatementPrefix(getMaxRowCount()));

		ReportCriteriaJSONModel jModel = getReportCriteriaJSONModel();
		List<CriteriaFieldModel> aggregatorList = jModel.getAggregators();

		sb.append(generateSelectClause(aggregatorList));
		
		sb.append(" WHERE time >= ?  AND time <= ? ");
		sb.append(EVENT_LEVEL_QRY_FRAG);
		
		if(!getEnforcement().equals("%"))
		    sb.append(ENFORCEMENTS_QRY_FRAG);

        // action field
        if (jModel.getGeneralActionField().getValues().size() > 1 || (!jModel.getGeneralActionField().getValues().get(0).equals("")
                && !jModel.getGeneralActionField().getValues().get(0).equals("*") && !jModel.getGeneralActionField().getValues().get(0).equals("%"))) {
            if (jModel.getGeneralActionField().isMultiValue()) {
                sb.append(ACTION_QRY_MULTI_FRAG_V2);
                sb.append(createParametersForInQuery(jModel.getGeneralActionField().getValues()));
            } else {
                sb.append(ACTION_QRY_FRAG);
            }
        }

        // user lookup field
        if (jModel.getUserCriteriaLookUpField().getValues().size() > 1 || (!jModel.getUserCriteriaLookUpField().getValues().get(0).equals("Any User")
                && !jModel.getUserCriteriaLookUpField().getValues().get(0).equals("*")
                && !jModel.getUserCriteriaLookUpField().getValues().get(0).equals("%"))) {
            if (jModel.getUserCriteriaLookUpField().isMultiValue()) {
                sb.append(USER_QRY_MULTI_FRAG_V2);
                sb.append(createParametersForInQuery(jModel.getUserCriteriaLookUpField().getValues()));
            } else {
                sb.append(USER_QRY_FRAG);
            }
        }

        // resource lookup field
        if (jModel.getResourceCriteriaLookUpField().getValues().size() > 1
                || (!jModel.getResourceCriteriaLookUpField().getValues().get(0).equals("Any Resource")
                        && !jModel.getResourceCriteriaLookUpField().getValues().get(0).equals("*")
                        && !jModel.getResourceCriteriaLookUpField().getValues().get(0).equals("%"))) {
            if (jModel.getResourceCriteriaLookUpField().isMultiValue()) {
                sb.append(RESOURCE_QRY_MULTI_FRAG_V2);
                sb.append(createParametersForInQuery(jModel.getResourceCriteriaLookUpField().getValues()));
            } else {
                sb.append(RESOURCE_QRY_FRAG);
            }
        }

        // policy lookup field
        if (jModel.getPolicyCriteriaLookUpField().getValues().size() > 1 || (!jModel.getPolicyCriteriaLookUpField().getValues().get(0).equals("Any Policy")
                && !jModel.getPolicyCriteriaLookUpField().getValues().get(0).equals("*")
                && !jModel.getPolicyCriteriaLookUpField().getValues().get(0).equals("%"))) {
            if (jModel.getPolicyCriteriaLookUpField().isMultiValue()) {
                sb.append(POLICY_QRY_MULTI_FRAG_V2);
                sb.append(createParametersForInQuery(jModel.getPolicyCriteriaLookUpField().getValues()));
            } else {
                sb.append(POLICY_QRY_FRAG);
            }
        }
        
		sb.append(generateWhereCondition());

		sb.append(getHavingClause(aggregatorList));

		sb.append(generateOrderString());

		int limit = getPagesize() + 1; // load one more record
		// if it's the last page and requested for less one page
		if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
        sb.append(getSelectStatementSuffix(limit, getOffset()));
        
		String paDetailsTableQryText = sb.toString();

		if (log.isDebugEnabled()) {
			log.debug("PADetailsQuerySQLNoGrouping: " + paDetailsTableQryText);
		}
		return paDetailsTableQryText;
	}
	
	protected String genPADetailsTotalQuerySQLNoGrouping() throws Exception {
        StringBuilder sb = new StringBuilder(getSelectTotalStatementPrefix());

        ReportCriteriaJSONModel jModel = getReportCriteriaJSONModel();

        sb.append(" WHERE time >= ?  AND time <= ? ");
        sb.append(EVENT_LEVEL_QRY_FRAG);
        sb.append(ENFORCEMENTS_QRY_FRAG);

        if (jModel.getGeneralActionField().isMultiValue()) {
            sb.append(ACTION_QRY_MULTI_FRAG_V2);
            sb.append(createParametersForInQuery(jModel.getGeneralActionField()
                    .getValues()));
        } else {
            sb.append(ACTION_QRY_FRAG);
        }

        // user criteria lookup
        if (jModel.getUserCriteriaLookUpField().isMultiValue()) {
            sb.append(USER_QRY_MULTI_FRAG_V2);
            sb.append(createParametersForInQuery(jModel
                    .getUserCriteriaLookUpField().getValues()));
        } else {
            sb.append(USER_QRY_FRAG);
        }

        // resource criteria lookup
        if (jModel.getResourceCriteriaLookUpField().isMultiValue()) {
            sb.append(RESOURCE_QRY_MULTI_FRAG_V2);
            sb.append(createParametersForInQuery(jModel
                    .getResourceCriteriaLookUpField().getValues()));
        } else {
            sb.append(RESOURCE_QRY_FRAG);
        }

        // policy criteria lookup
        if (jModel.getPolicyCriteriaLookUpField().isMultiValue()) {
            sb.append(POLICY_QRY_MULTI_FRAG_V2);
            sb.append(createParametersForInQuery(jModel
                    .getPolicyCriteriaLookUpField().getValues()));
        } else {
            sb.append(POLICY_QRY_FRAG);
        }

        sb.append(generateWhereCondition());

        hasOrderByFields = false;
        sb.append(getSelectStatementSuffix(getMaxRowCount(), 0));
        
        String paDetailsTableQryText = sb.toString();

        log.debug("AbstractDataGenerator.PADetailsTotalQuerySQLNoGrouping: "
                + paDetailsTableQryText);

        if (log.isTraceEnabled()) {
            log.trace("AbstractDataGenerator.PADetailsTotalQuerySQLNoGrouping: "
                    + paDetailsTableQryText);
            log.trace(toString());
        }
        return paDetailsTableQryText;
    }

	protected String createParametersForInQuery(List<String> values) {
		StringBuilder sb = new StringBuilder();
		sb.append(" ( ");
		for (int i = 0; i < values.size(); i++) {
			String data = values.get(i);
			if (data != null && !data.trim().isEmpty()) {
				sb.append("?");
				if (i < values.size() - 1) {
					sb.append(", ");
				}
			}
		}
		sb.append(" ) ");
		return sb.toString();
	}

	protected String genTRDetailsTableQryText() {
		StringBuilder sb = new StringBuilder(
				getSelectStatementPrefix(getMaxRowCount()));
		sb.append(" * ").append(TR_FROM_QRY_FRAG);
		sb.append(RESOURCE_QRY_FRAG);

		sb.append(EVENT_LEVEL_QRY_FRAG);
		if (getAction().split(",").length > 1)
			sb.append(ACTION_QRY_MULTI_FRAG);
		else
			sb.append(ACTION_QRY_FRAG);

		if ((getTimeDimension() != null)
				&& (getTimeDimension().isEmpty() == false)) {
			if (getGroupByDimension().equalsIgnoreCase(ALIAS_DAY))
				sb.append(TIMEDIMENSION_DAY_QRY_FRAG);
			else if (getGroupByDimension().equalsIgnoreCase(ALIAS_MONTH))
				sb.append(TIMEDIMENSION_MONTH_QRY_FRAG);
			else if (getGroupByDimension().isEmpty())
				sb.append(TIMEDIMENSION_DAY_QRY_FRAG);
		}

		if (getUserName().split(",").length > 1)
			sb.append(USER_QRY_MULTI_FRAG);
		else
			sb.append(USER_QRY_FRAG);

        hasOrderByFields = true;
		sb.append(" ORDER BY time DESC ");
		
        int limit = getPagesize();
        if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
        sb.append(getSelectStatementSuffix(limit, getOffset()));
        
		String trDetailsTableQryText = sb.toString();
		if (log.isTraceEnabled()) {
			log.trace("AbstractDataGenerator.TRDetailsTableQry: "
					+ trDetailsTableQryText);
		}
		return trDetailsTableQryText;
	}

	protected void setCustAttrData(TRLogDetailsData logDetailsData)
			throws Exception {
		if (paCustAttrQueryStmt == null) {
			paCustAttrQueryStmt = new LoggablePreparedStatement(
					session.connection(), SELECT_TA_CUST_ATTR_QRY);
		}
		paCustAttrQueryStmt.setLong(1, getIdForDetailsLog());

		try (ResultSet rs = paCustAttrQueryStmt.executeQuery()) {
			List<CustomAttributeData> custAttrDataList = new ArrayList<CustomAttributeData>();
			while (rs.next()) {
				CustomAttributeData custAttrData = new CustomAttributeData();
				custAttrData.setId(rs.getLong(1));
				custAttrData.setLogId(rs.getLong(2));
				custAttrData.setAttributeName(rs.getString(3));
				custAttrData.setAttributeName(rs.getString(4));
				custAttrDataList.add(custAttrData);
			}
			logDetailsData.setCustAttrData(custAttrDataList);
		} finally {
			paCustAttrQueryStmt.close();
		}
	}

	protected void setTASingleLogDetailsData(TRLogDetailsData logDetailsData)
			throws Exception {
		if (taSingleLogDetailsQueryStmt == null) {
			taSingleLogDetailsQueryStmt = new LoggablePreparedStatement(
					session.connection(), SELECT_TA_DETAILS_QRY);
		}
		taSingleLogDetailsQueryStmt.setLong(1, getIdForDetailsLog());

		try (ResultSet rs = taSingleLogDetailsQueryStmt.executeQuery()) {
			if (rs.next()) {
				DetailsTableData thisData = new DetailsTableData();
				setIndividualTADetailsTableData(rs, thisData);
				logDetailsData.setSingleLogDetailsData(thisData);
			}
		} finally {
			taSingleLogDetailsQueryStmt.close();
		}
	}

	protected String generateSelectColumn() throws JSONException {

		StringBuffer sBuf = new StringBuffer();
		StringBuffer sBufTemp = new StringBuffer();

		sBuf.append(" id, ").append(getTimeConvert()).append(", ");
		sBufTemp.append(" left join (select policy_log_id, ");

		boolean isEAV = false;

		ReportCriteriaJSONModel jModel = getReportCriteriaJSONModel();

		List<String> headers = jModel.getColumnHeaders();
		List<String> orderByCols = getOrderByColNames(jModel);

		isEAV = headerColumnEAVHandler(sBuf, sBufTemp, isEAV, headers);
		isEAV = orderByEAVHandler(sBufTemp, isEAV, jModel, headers);
		isEAV = extendedAttributeQueryCreate(sBufTemp, isEAV, jModel, headers,
				orderByCols);

		String sResult = sBuf.toString().trim();
		String sEAVResult = sBufTemp.toString().trim();

		if (sResult.endsWith(",")) {
			sResult = sResult.substring(0, sResult.length() - 1);
		}

		sResult = sResult + " FROM RPA_LOG RPA ";

		if (isEAV) {

			if (sEAVResult.endsWith(",")) {
				sEAVResult = sEAVResult.substring(0, sEAVResult.length() - 1);
			}

			sEAVResult = sEAVResult
					+ (" FROM RPA_LOG_ATTR GROUP BY POLICY_LOG_ID ) RPALA ON rpa.id = RPALA.policy_log_id");
			sResult = sResult + sEAVResult;
		}

		return sResult;
	}
	
	protected String getTimeConvert() {
		return " time AS time_text ";
		// keep consistency with postgres
	}

	protected List<String> getOrderByColNames(ReportCriteriaJSONModel jModel)
			throws JSONException {
		List<String> orderByCols = new ArrayList<String>();
		List<OrderByModel> orderByModels = jModel.getOrderByList();
		for (OrderByModel orderBy : orderByModels) {
			orderByCols.add(orderBy.getColumnName());
		}
		return orderByCols;
	}

	protected boolean headerColumnEAVHandler(StringBuffer sBuf,
			StringBuffer sBufTemp, boolean isEAV, List<String> headers) {
		for (String headerColumn : headers) {
			String colName = headerColumn;

			if (colName.startsWith(TYPE_USER_PREFIX)) {
				colName = sanitizeColumnMappingKey(colName.substring(TYPE_USER_PREFIX.length()));
				AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmUserMapping
						.get(colName);
				isEAV = createSelectFragment(TYPE_USER_PREFIX,
						ALIAS_ATTR_ID_STRING, sBuf, sBufTemp, isEAV, colName,
						mappingDO);
			} else if (colName.startsWith(TYPE_RESOURCE_PREFIX)) {
				colName = sanitizeColumnMappingKey(colName.substring(TYPE_RESOURCE_PREFIX.length()));
				AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmResourceMapping
						.get(colName);

				isEAV = createSelectFragment(TYPE_RESOURCE_PREFIX,
						ALIAS_ATTR_ID_STRING, sBuf, sBufTemp, isEAV, colName,
						mappingDO);
			} else if (colName.startsWith(TYPE_POLICY_PREFIX)) {
				colName = sanitizeColumnMappingKey(colName.substring(TYPE_POLICY_PREFIX.length()));
				AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmPolicyMapping
						.get(colName);

				isEAV = createSelectFragment(TYPE_POLICY_PREFIX,
						ALIAS_ATTR_ID_STRING, sBuf, sBufTemp, isEAV, colName,
						mappingDO);
			} else if (colName.startsWith(TYPE_OTHERS_PREFIX)) {
				colName = sanitizeColumnMappingKey(colName.substring(TYPE_OTHERS_PREFIX.length()));
				AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmOthersMapping
						.get(colName);

				isEAV = createSelectFragment(TYPE_OTHERS_PREFIX,
						ALIAS_ATTR_ID_STRING, sBuf, sBufTemp, isEAV, colName,
						mappingDO);
			}
		}
		return isEAV;
	}

	protected boolean orderByEAVHandler(StringBuffer sBufTemp, boolean isEAV,
			ReportCriteriaJSONModel jModel, List<String> headers)
			throws JSONException {
		List<OrderByModel> orderByList = jModel.getOrderByList();

		for (OrderByModel orderBy : orderByList) {
			String columnName = orderBy.getColumnName();

			if (headers.contains(columnName)) {
				log.debug(columnName + " already present in display header");
				continue;
			}

			if (columnName.startsWith(TYPE_USER_PREFIX)) {
				String columnMapKey = sanitizeColumnMappingKey(columnName.substring(TYPE_USER_PREFIX
						.length()));
				AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmUserMapping
						.get(columnMapKey);
				if (mappingDO.getColumnName() == null) {
					isEAV = createSelectFragment(TYPE_USER_PREFIX,
							ALIAS_ATTR_ID_STRING, null, sBufTemp, isEAV,
							columnMapKey, mappingDO);
				}
			} else if (columnName.startsWith(TYPE_RESOURCE_PREFIX)) {
				String columnMapKey = sanitizeColumnMappingKey(columnName.substring(TYPE_RESOURCE_PREFIX
						.length()));
				AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmResourceMapping
						.get(columnMapKey);
				if (mappingDO.getColumnName() == null) {
					isEAV = createSelectFragment(TYPE_RESOURCE_PREFIX,
							ALIAS_ATTR_ID_STRING, null, sBufTemp, isEAV,
							columnMapKey, mappingDO);
				}
			} else if (columnName.startsWith(TYPE_POLICY_PREFIX)) {
				String columnMapKey = sanitizeColumnMappingKey(columnName.substring(TYPE_POLICY_PREFIX
						.length()));
				AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmPolicyMapping
						.get(columnMapKey);
				if (mappingDO.getColumnName() == null) {
					isEAV = createSelectFragment(TYPE_POLICY_PREFIX,
							ALIAS_ATTR_ID_STRING, null, sBufTemp, isEAV,
							columnMapKey, mappingDO);
				}
			} else if (columnName.startsWith(TYPE_OTHERS_PREFIX)) {
				String columnMapKey = sanitizeColumnMappingKey(columnName.substring(TYPE_OTHERS_PREFIX
						.length()));
				AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmOthersMapping
						.get(columnMapKey);
				if (mappingDO.getColumnName() == null) {
					isEAV = createSelectFragment(TYPE_OTHERS_PREFIX,
							ALIAS_ATTR_ID_STRING, null, sBufTemp, isEAV,
							columnMapKey, mappingDO);
				}
			}
		}
		return isEAV;
	}

	protected boolean extendedAttributeQueryCreate(StringBuffer sBufTemp,
			boolean isEAV, ReportCriteriaJSONModel jModel,
			List<String> headers, List<String> orderByCols) {
		try {
			List<CriteriaFieldModel> fields = jModel.getUserCriteriaFields();
			log.debug("User criteria fields for extended attributes "
					+ fields.size());
			isEAV = generateEAVQueryForWhereClauseOnlyExtendedAttrib(sBufTemp,
					TYPE_USER_PREFIX, hmUserMapping, fields, isEAV, headers,
					orderByCols);

			// resource criteria
			fields = jModel.getResourceCriteriaFields();
			log.debug("Resource criteria fields for extended attributes  "
					+ fields.size());
			isEAV = generateEAVQueryForWhereClauseOnlyExtendedAttrib(sBufTemp,
					TYPE_RESOURCE_PREFIX, hmResourceMapping, fields, isEAV,
					headers, orderByCols);

			// policy criteria
			fields = jModel.getPolicyCriteriaFields();
			log.debug("Policy criteria fields for extended attributes  "
					+ fields.size());
			isEAV = generateEAVQueryForWhereClauseOnlyExtendedAttrib(sBufTemp,
					TYPE_POLICY_PREFIX, hmPolicyMapping, fields, isEAV,
					headers, orderByCols);

			// other criteria
			fields = jModel.getOtherCriteriaFields();
			log.debug("Other criteria fields for extended attributes "
					+ fields.size());
			isEAV = generateEAVQueryForWhereClauseOnlyExtendedAttrib(sBufTemp,
					TYPE_OTHERS_PREFIX, hmOthersMapping, fields, isEAV,
					headers, orderByCols);

		} catch (Exception e) {
			log.error("Error encountered in generating extended attribute query clause", e);
		}
		return isEAV;
	}

	private boolean createSelectFragment(String typePrefix,
			String attrbIdString, StringBuffer sBuf, StringBuffer sBufTemp,
			boolean isEAV, String colName, AttributeColumnMappingDO mappingDO) {
		// for EAV model
		if (mappingDO.getColumnName() == null
				|| mappingDO.getColumnName().isEmpty()) {

			if (sBuf != null) {
				sBuf.append(typePrefix).append(replaceInvalidChar(colName))
						.append(", ");
			}

			if (sBufTemp != null) {
				sBufTemp.append(" max(case when ")
						.append(attrbIdString)
						.append("=")
						.append(mappingDO.getId())
						.append(" then ")
						.append(ALIAS_ATTR_VALUE_STRING)
						.append(" end) as ")
						.append(typePrefix)
						.append(replaceInvalidChar(mappingDO.getAttributeName()))
						.append(", ");
			}
			isEAV = true;
		} else {
			// For normal model
			if (sBuf != null) {
				sBuf.append(mappingDO.getColumnName()).append(", ");
			}
		}
		return isEAV;
	}

	protected String replaceInvalidChar(String sIn) {
		/*
		 * Oracle has issues with - in the selected column
		 */
		return sIn.replaceAll("-", "_").replaceAll(" ", "_");

	}

	private void setIndividualTADetailsTableData(ResultSet rs,
			DetailsTableData thisData) throws Exception {
		thisData.setId(rs.getLong(1));
		thisData.setTime(rs.getTimestamp(2));
		thisData.setMonthNb(rs.getLong(3));
		thisData.setDayNb(rs.getLong(4));
		thisData.setHostId(rs.getLong(5));
		thisData.setHostIP(rs.getString(6));
		thisData.setHostName(rs.getString(7));
		thisData.setUserId(rs.getLong(8));
		thisData.setUserName(rs.getString(9));
		thisData.setApplicationId(rs.getLong(11));
		thisData.setApplicationName(rs.getString(12));
		thisData.setAction(rs.getString(13));
		thisData.setLogLevel(rs.getInt(14));
		thisData.setFromResourceName(rs.getString(15));
		thisData.setFromResourcePrefix(rs.getString(20));
		thisData.setFromResourcePath(rs.getString(21));
		thisData.setFromResourceShortName(rs.getString(22));
		thisData.setToResourceName(rs.getString(23));
	}

	protected static final String JSON_ORDER_BY = "orderby";
	protected static final String JSON_FILTERS = "filters";
	protected static final String JSON_SAVE_INFO = "save_info";
	protected static final String JSON_GROUPING_MODE = "grouping_mode";
	protected static final String ORDER_BY_SQL = " ORDER BY ";
	protected static final String AND_OPER = " AND ";

	protected String generateWhereCondition() {
		StringBuffer sBuf = new StringBuffer();
		arrCustomFields.clear();
		log.debug("Generate where condition - [Started]");

		try {
			ReportCriteriaJSONModel jModel = getReportCriteriaJSONModel();
			// user criteria
			List<CriteriaFieldModel> fields = jModel.getUserCriteriaFields();
			log.debug("User criteria fields for where condition - "
					+ fields.size());
			createQueryFragment(sBuf, TYPE_USER_PREFIX, hmUserMapping, fields);

			// resource criteria
			fields = jModel.getResourceCriteriaFields();
			log.debug("Resource criteria fields for where condition - "
					+ fields.size());
			createQueryFragment(sBuf, TYPE_RESOURCE_PREFIX, hmResourceMapping,
					fields);

			// policy criteria
			fields = jModel.getPolicyCriteriaFields();
			log.debug("Policy criteria fields for where condition - "
					+ fields.size());
			createQueryFragment(sBuf, TYPE_POLICY_PREFIX, hmPolicyMapping,
					fields);

			// other criteria
			fields = jModel.getOtherCriteriaFields();
			log.debug("Other criteria fields for where condition - "
					+ fields.size());
			createQueryFragment(sBuf, TYPE_OTHERS_PREFIX, hmOthersMapping,
					fields);

		} catch (Exception e) {
			log.error("Error encountered in generating where conditions, ", e);
		}

		return sBuf.toString();
	}

	private boolean generateEAVQueryForWhereClauseOnlyExtendedAttrib(
			StringBuffer sBufTemp, String typePrefix,
			Map<String, Object> hmMapping, List<CriteriaFieldModel> fields,
			boolean isEAV, List<String> headers, List<String> orderByCols) {
		try {

			for (CriteriaFieldModel field : fields) {
				String colName = sanitizeColumnMappingKey(field.getName().substring(typePrefix.length()));
				AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmMapping
						.get(colName);

				if (headers.contains(field.getName())
						|| orderByCols.contains(field.getName()))
					continue;

				if (mappingDO.getColumnName() == null
						|| mappingDO.getColumnName().isEmpty()) {

					if (sBufTemp != null) {
						sBufTemp.append(" max(case when ")
								.append(ALIAS_ATTR_ID_STRING)
								.append("=")
								.append(mappingDO.getId())
								.append(" then ")
								.append(ALIAS_ATTR_VALUE_STRING)
								.append(" end) as ")
								.append(typePrefix)
								.append(replaceInvalidChar(mappingDO
										.getAttributeName())).append(", ");
						isEAV = true;
					}
				}
			}

		} catch (Exception e) {
			log.error("Error encountered in generating where conditions, ", e);
		}
		return isEAV;
	}

	private void createQueryFragment(StringBuffer sBuf, String typePrefix,
			Map<String, Object> hmMapping, List<CriteriaFieldModel> fields) {

		log.debug("createQueryFragment - [ Type :" + typePrefix + " ] ");

		for (CriteriaFieldModel field : fields) {
			log.debug("Processing field : " + field);
			String colName = field.getName();
			String sQryFrag = QRY_SINGLE_FRA;

			if (field.isMultiValue()) {
				sQryFrag = " ( ";
				for (int i = 0; i < field.getValues().size(); i++) {
					String data = field.getValues().get(i);

					if (data != null && !data.trim().isEmpty()) {
						sQryFrag += "?";
						if (i < field.getValues().size() - 1) {
							sQryFrag += ", ";
						}
						arrCustomFields.add(data.trim());
					}
				}
				sQryFrag += " ) ";

			} else {
				sQryFrag = QRY_SINGLE_FRA;
				arrCustomFields.add(field.getValue().trim());
			}

			colName = sanitizeColumnMappingKey(colName.substring(typePrefix.length()));
			AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmMapping
					.get(colName);
			log.debug("AttributeColumnMappingDO for field : [ Column Name :"
					+ colName + ", Mapping :" + mappingDO.getColumnName() + ","
					+ " Attribute name :" + mappingDO.getAttributeName()
					+ ", Attrib Id :" + mappingDO.getId() + "]");
			createQueryFragment(sBuf, typePrefix, colName, field.getOperator(),
					sQryFrag, mappingDO);
		}
	}

	protected void createQueryFragment(StringBuffer sBuf, String typePrefix,
			String colName, String operator, String sQryFrag,
			AttributeColumnMappingDO mappingDO) {
		// for EAV model
		if (mappingDO.getColumnName() == null
				|| mappingDO.getColumnName().isEmpty()) {

			sBuf.append(AND_OPER).append(getColumnName(typePrefix, colName)).append(" ")
					.append(getRealOperator(operator)).append(" ")
					.append(sQryFrag);
		} else {
			sBuf.append(AND_OPER).append(getColumnName("", mappingDO.getColumnName())).append(" ")
					.append(getRealOperator(operator)).append(" ")
					.append(sQryFrag);
		}
	}

   protected String getColumnName(String typePrefix, String colName) {
		
		if(typePrefix != null && ! typePrefix.isEmpty()) {
			return  typePrefix + replaceInvalidChar(colName) + " ";
		} else {
			return  replaceInvalidChar(colName) + " ";
		}
	}
	
	
	protected int getMaxRowCount() {
		try {

			int maxRow = getReportCriteriaJSONModel().getMaxRows();

			return maxRow;
		} catch (Exception e) {
			log.error("Error encountered in get Max row count, ", e);
		}
		return -1;
	}
	
	protected int getOffset() {
	    try {
            return this.getReportCriteriaJSONModel().getOffset();
        } catch (JSONException e) {
            log.debug("Error encountered in get offset, defaults to 0");
            return 0;
        }
	}
    
    protected int getPagesize() {
        try {
            return this.getReportCriteriaJSONModel().getPagesize();
        } catch (JSONException e) {
            log.debug("Error encountered in get pagesize, defaults to " + DEFAULT_PAGE_SIZE);
            return DEFAULT_PAGE_SIZE;
        }
    }

	protected String generateOrderString() throws Exception {
		StringBuffer sBuffer = new StringBuffer();
		try {
            hasOrderByFields = true;
            
			List<OrderByModel> orderByModels = getReportCriteriaJSONModel()
					.getOrderByList();

            if (orderByModels.isEmpty()) {
                hasOrderByFields = false;
                return "";
            }
            
			for (OrderByModel orderBy : orderByModels) {
				String columnName = orderBy.getColumnName();
				if (columnName.startsWith(TYPE_USER_PREFIX)) {
					String columnMapKey = sanitizeColumnMappingKey(columnName.substring(TYPE_USER_PREFIX
							.length()));
					AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmUserMapping
							.get(columnMapKey);
					createOrderBy(TYPE_USER_PREFIX, sBuffer, columnName,
							orderBy.getSortOrder(), mappingDO);
				} else if (columnName.startsWith(TYPE_RESOURCE_PREFIX)) {
					String columnMapKey = sanitizeColumnMappingKey(columnName
							.substring(TYPE_RESOURCE_PREFIX.length()));
					AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmResourceMapping
							.get(columnMapKey);
					createOrderBy(TYPE_RESOURCE_PREFIX, sBuffer, columnName,
							orderBy.getSortOrder(), mappingDO);
				} else if (columnName.startsWith(TYPE_POLICY_PREFIX)) {
					String columnMapKey = sanitizeColumnMappingKey(columnName
							.substring(TYPE_POLICY_PREFIX.length()));
					AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmPolicyMapping
							.get(columnMapKey);
					createOrderBy(TYPE_POLICY_PREFIX, sBuffer, columnName,
							orderBy.getSortOrder(), mappingDO);
				} else if (columnName.startsWith(TYPE_OTHERS_PREFIX)) {
					String columnMapKey = sanitizeColumnMappingKey(columnName
							.substring(TYPE_OTHERS_PREFIX.length()));
					AttributeColumnMappingDO mappingDO = (AttributeColumnMappingDO) hmOthersMapping
							.get(columnMapKey);
					createOrderBy(TYPE_OTHERS_PREFIX, sBuffer, columnName,
							orderBy.getSortOrder(), mappingDO);
				}
			}

		} catch (JSONException e) {
			log.error("Error encountered in generating order string, ", e);
		}
		return sBuffer.toString();
	}

	private void createOrderBy(String typePrefix, StringBuffer sBuffer,
			String column, String sort, AttributeColumnMappingDO mappingDO) {

		// for EAV model
		if (mappingDO.getColumnName() == null
				|| mappingDO.getColumnName().isEmpty()) {

			sBuffer.append(ORDER_BY_SQL).append(replaceInvalidChar(column))
					.append(" ").append(sort);
		} else {
			sBuffer.append(ORDER_BY_SQL).append(mappingDO.getColumnName())
					.append(" ").append(sort);
		}
	}

	private String sanitizeColumnMappingKey(String originalColumnName) {
		if(originalColumnName != null
				&& originalColumnName.indexOf(".") > -1
					&& !originalColumnName.endsWith(".")) {
			return originalColumnName.substring(originalColumnName.indexOf(".")+1);
		}

		return originalColumnName;
	}

	protected String getRealOperator(String sIn) {

		if (sIn.equalsIgnoreCase(OPERATOR_EQ)) {
			return "=";
		} else if (sIn.equalsIgnoreCase(OPERATOR_NE)) {
			return "!=";
		} else if (sIn.equalsIgnoreCase(OPERATOR_IN)) {
			return "in";
		} else if (sIn.equalsIgnoreCase(OPERATOR_NOT_IN)) {
			return "not in";
		} else if (sIn.equalsIgnoreCase(OPERATOR_LIKE)) {
			return "like";
		} else if (sIn.equalsIgnoreCase(OPERATOR_GT)) {
			return ">";
		} else if (sIn.equalsIgnoreCase(OPERATOR_GE)) {
			return ">=";
		} else if (sIn.equalsIgnoreCase(OPERATOR_LT)) {
			return "<";
		} else if (sIn.equalsIgnoreCase(OPERATOR_LE)) {
			return "<=";
		}
		return sIn;
	}

    protected String formatDateTime(String dbValue){
        // strip milliseconds
        return LocalDateTime.parse(dbValue, dateTimeFormatter)
                .truncatedTo(ChronoUnit.SECONDS).format(dateTimeFormatter);
    }

    private String formatDate(String epochDate, boolean stripDay){
		long epochValue = Long.parseLong(epochDate);
		LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochValue), ZoneId.systemDefault());
		if(stripDay){
			return date.format(monthYearFormatter);
		} else {
			return date.format(dayMonthYearFormatter);
		}
	}

}
