/*
 * Created on Apr 3, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

import java.util.ArrayList;
import java.util.List;

import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.inquirycenter.report.CriteriaFieldModel;
import com.nextlabs.destiny.inquirycenter.report.ReportCriteriaJSONModel;

import net.sf.hibernate.Session;

/**
 * <p>
 * PostgreSQLDataGenerator class for Postgre specific SQL generation.
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public final class PostgreSQLDataGenerator extends AbstractDataGenerator {
	
    // private static final String FROM_QRY_FRAG = " FROM "
    // + SharedLib.REPORT_PA_TABLE + " WHERE time >= ? " + // beginDate
    // " AND time <= ? "; // endDate
    
    // private static final String FROM_QRY_FRAGV2 = " FROM RPA_LOG "
    // + " WHERE time >= ? " + // beginDate
    // " AND time <= ? "; // endDate

	private static final String TR_FROM_QRY_FRAG = " FROM "
			+ SharedLib.REPORT_TA_TABLE + " WHERE time >= ? " + // beginDate
			" AND time <= ? "; // endDate

    // private static final String TR_FROM_QRY_FRAGV2 = " FROM "
    // + SharedLib.REPORT_TA_TABLE + " WHERE time >= ? " + // beginDate
    // " AND time <= ? "; // endDate
	
	private static final String EVENT_LEVEL_QRY_FRAG = " AND log_level >= ? ";
	private static final String ACTION_QRY_FRAG = " AND LOWER(action) LIKE ? ";
	private static final String ACTION_QRY_MULTI_FRAG_V2 = " AND LOWER(action) IN  ";

	private static final String USER_QRY_FRAG = " AND LOWER(user_name) LIKE ? "; // Users
	private static final String USER_QRY_MULTI_FRAG_V2 = " AND LOWER(user_name) IN  ";
	
	private static final String RESOURCE_QRY_FRAG = " AND LOWER(from_resource_name) LIKE ? "; // Resource
	private static final String RESOURCE_QRY_MULTI_FRAG_V2 = " AND LOWER(from_resource_name) IN  ";
	
	private static final String POLICY_QRY_FRAG = " AND LOWER(policy_fullname) LIKE ? ";
	private static final String POLICY_QRY_MULTI_FRAG_V2 = " AND LOWER(policy_fullname) IN ";
	
	private static final String ACTION_QRY_MULTI_FRAG = " AND LOWER(action) IN "
			+ "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
	
	private static final String USER_QRY_MULTI_FRAG = " AND LOWER(user_name) IN "
			+ "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
			+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

	public PostgreSQLDataGenerator(Session session) {
		super(session);
	}
	
	@Override
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

        log.debug("AbstractDataGenerator.genPADetailsQuerySQLGrouping: "
                + groupByQryText);
        return groupByQryText;
    }
	
	@Override
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

		sb.append(" GROUP BY ").append(getGroupByDimension())
				.append(" ORDER BY ResultCount DESC ");
        int limit = getPagesize();
        if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
		sb.append(getSelectStatementSuffix(limit, getOffset()));
		String trGroupByQryText = sb.toString();
		if (log.isTraceEnabled()) {
			log.trace("PostgreSQLDataGenerator.trGroupByQryText: "
					+ trGroupByQryText);
		}
		return trGroupByQryText;
	}
	
	
	@Override
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

		sb.append(" ORDER BY time DESC ");
        int limit = getPagesize();
        if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
		sb.append(getSelectStatementSuffix(limit, getOffset()));
		String trDetailsTableQryText = sb.toString();
		if (log.isTraceEnabled()) {
			log.trace("PostgreSQLDataGenerator.TRDetailsTableQry: "
					+ trDetailsTableQryText);
		}
		return trDetailsTableQryText;
	}

	@Override
	protected String getSelectStatementPrefix(int rowCount) {
		return "SELECT ";
	}

	@Override
	protected String getColumnName(String typePrefix, String colName) {
		
		if(typePrefix != null && ! typePrefix.isEmpty()) {
			return " LOWER (" + typePrefix + replaceInvalidChar(colName) + " ) ";
		} else {
			return " LOWER (" + replaceInvalidChar(colName) + " ) ";
		}
	}
	
	@Override
	protected String getParamValue(String value) {
		return (value != null) ? value.toLowerCase() : "";
	}

	@Override
	protected String getSelectStatementSuffix(int rowCount, int offset) {
        if (rowCount <= 0 || !paginated) {
            return "";
        } else {
            StringBuilder suffix = new StringBuilder();
            suffix.append(" limit ").append(rowCount);
            if(offset > 0) {
                suffix.append(" offset ").append(offset);
            }
            suffix.append(" ");
            return suffix.toString();
        }
    }

}
