package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.inquirycenter.report.CriteriaFieldModel;
import com.nextlabs.destiny.inquirycenter.report.ReportCriteriaJSONModel;
import net.sf.hibernate.Session;
import org.richfaces.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DB2DataGenerator
        extends AbstractDataGenerator {

    private static final String FROM_QRY_FRAG = " FROM "
                    + SharedLib.REPORT_PA_TABLE + " WHERE time >= ? " + // beginDate
                    " AND time <= ? "; // endDate

    private static final String TR_FROM_QRY_FRAG = " FROM "
                    + SharedLib.REPORT_TA_TABLE + " WHERE time >= ? " + // beginDate
                    " AND time <= ? "; // endDate

    private static final String EVENT_LEVEL_QRY_FRAG = " AND log_level >= ? ";
    private static final String ENFORCEMENTS_QRY_FRAG = " AND policy_decision LIKE ? ";
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

    public DB2DataGenerator(Session session) {
        super(session);
    }

    @Override
    protected String getSelectStatementPrefix(int rowCount) {
        return "SELECT ";
    }

    @Override
    protected String getSelectStatementSuffix(int rowCount, int offset) {
        // "offset" and "fetch next" can only be used if "order by" appears in the SQL
        if (rowCount > 0 && hasOrderByFields && paginated) {
            StringBuilder suffix = new StringBuilder(" ");
            suffix.append("offset ").append(offset).append(" rows ");
            suffix.append("fetch next ").append(rowCount).append(" rows only");
            return suffix.toString();
        }

        return "";
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

        log.debug("AbstractDataGenerator.genPADetailsQuerySQLGrouping: "
                        + groupByQryText);
        return groupByQryText;
    }

    protected String genPAGroupByQryText() {
        StringBuilder sb = new StringBuilder(getSelectStatementPrefix(0));
        sb.append(" * ").append(" FROM (SELECT DISTINCT ");
        sb.append(getGroupByDimension()).append(GROUPBY_QRY_FRAG);
        sb.append(FROM_QRY_FRAG);

        sb.append(RESOURCE_QRY_FRAG);

        sb.append(EVENT_LEVEL_QRY_FRAG);
        sb.append(ENFORCEMENTS_QRY_FRAG);
        if (getPolicy().split(", ").length > 1)
            sb.append(POLICY_QRY_MULTI_FRAG);
        else
            sb.append(POLICY_QRY_FRAG);

        if (getAction().split(", ").length > 1)
            sb.append(ACTION_QRY_MULTI_FRAG);
        else
            sb.append(ACTION_QRY_FRAG);

        if (getUserName().split(", ").length > 1)
            sb.append(USER_QRY_MULTI_FRAG);
        else
            sb.append(USER_QRY_FRAG);

        sb.append(" GROUP BY ").append(getGroupByDimension());
        sb.append(" ORDER BY ResultCount DESC) ");

        int limit = getPagesize();
        if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
        sb.append(getSelectStatementSuffix(limit, getOffset()));

        String groupByQryText = sb.toString();
        if (log.isTraceEnabled()) {
            log.trace("OracleDataGenerator.PAGroupByQuery: " + groupByQryText);
        }
        return groupByQryText;
    }

    protected String genPADetailsTableQryText() {
        StringBuilder sb = new StringBuilder(getSelectStatementPrefix(0));
        sb.append(" * ").append(" FROM (SELECT DISTINCT * ");
        sb.append(FROM_QRY_FRAG);

        sb.append(RESOURCE_QRY_FRAG);

        sb.append(EVENT_LEVEL_QRY_FRAG);
        sb.append(ENFORCEMENTS_QRY_FRAG);

        if (getPolicy().split(", ").length > 1)
            sb.append(POLICY_QRY_MULTI_FRAG);
        else
            sb.append(POLICY_QRY_FRAG);

        if (getAction().split(", ").length > 1)
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

        if (getUserName().split(", ").length > 1)
            sb.append(USER_QRY_MULTI_FRAG);
        else
            sb.append(USER_QRY_FRAG);

        sb.append(" ORDER BY time DESC) ");

        int limit = getPagesize();
        if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
        sb.append(getSelectStatementSuffix(limit, getOffset()));

        String paDetailsTableQryText = sb.toString();
        if (log.isTraceEnabled()) {
            log.trace("OracleDataGenerator.PADetailsTableQry: "
                            + paDetailsTableQryText);
        }
        return paDetailsTableQryText;
    }

    protected String genTRGroupByQryText() {
        StringBuilder sb = new StringBuilder(getSelectStatementPrefix(0));
        sb.append(" * ").append(" FROM (SELECT DISTINCT ");
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

        sb.append(" GROUP BY ").append(getGroupByDimension());
        sb.append(" ORDER BY ResultCount DESC) ");
        sb.append(getSelectStatementSuffix(getMaxRowCount(), getOffset()));
        String groupByQryText = sb.toString();
        if (log.isTraceEnabled()) {
            log.trace("OracleDataGenerator.TRGroupByQuery: " + groupByQryText);
        }
        return groupByQryText;
    }

    protected String genTRDetailsTableQryText() {
        StringBuilder sb = new StringBuilder(getSelectStatementPrefix(0));
        sb.append(" * ").append(" FROM (SELECT DISTINCT ");
        sb.append(" * ");
        sb.append(TR_FROM_QRY_FRAG);

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

        sb.append(" ORDER BY time DESC) ");

        int limit = getPagesize();
        if(getMaxRowCount() > 0 && getMaxRowCount() <= getPagesize() + getOffset()) {
            limit = getMaxRowCount() - getOffset();
        }
        sb.append(getSelectStatementSuffix(limit, getOffset()));

        String trDetailsTableQryText = sb.toString();
        if (log.isTraceEnabled()) {
            log.trace("OracleDataGenerator.TRDetailsTableQry: "
                            + trDetailsTableQryText);
        }
        return trDetailsTableQryText;
    }

    protected String generateSelectColumnForAggregateWithoutGrouping() throws JSONException {

        StringBuffer sBuf = new StringBuffer();

        sBuf.append("1");

        StringBuffer sBufTemp = new StringBuffer();

        sBufTemp.append(" left join (select policy_log_id, ");

        boolean isEAV = false;

        ReportCriteriaJSONModel jModel = getReportCriteriaJSONModel();

        List<String> headers = jModel.getColumnHeaders();
        List<String> orderByCols = getOrderByColNames(jModel);

        isEAV = headerColumnEAVHandler(sBuf, sBufTemp, isEAV, headers);
        isEAV = orderByEAVHandler(sBufTemp, isEAV, jModel, headers);
        isEAV = extendedAttributeQueryCreate(sBufTemp, isEAV, jModel, headers, orderByCols);

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

        return (sResult);
    }

    protected String generateSelectColumn() throws JSONException {

        StringBuffer sBuf = new StringBuffer();

        sBuf.append("id, to_char(time, 'YYYY-MM-DD HH24:MI:SS') as time,");

        StringBuffer sBufTemp = new StringBuffer();

        sBufTemp.append(" left join (select policy_log_id, ");

        boolean isEAV = false;

        ReportCriteriaJSONModel jModel = getReportCriteriaJSONModel();

        List<String> headers = jModel.getColumnHeaders();
        List<String> orderByCols = getOrderByColNames(jModel);

        isEAV = headerColumnEAVHandler(sBuf, sBufTemp, isEAV, headers);
        isEAV = orderByEAVHandler(sBufTemp, isEAV, jModel, headers);
        isEAV = extendedAttributeQueryCreate(sBufTemp, isEAV, jModel, headers, orderByCols);

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

        return (sResult);
    }

    @Override
    protected String getColumnName(String typePrefix, String colName) {

        if(typePrefix != null && ! typePrefix.isEmpty()) {
            return " LOWER(" + typePrefix + replaceInvalidChar(colName) + ") ";
        } else {
            return " LOWER(" + replaceInvalidChar(colName) + ") ";
        }
    }

    @Override
    protected String getParamValue(String value) {
        return (value != null) ? value.toLowerCase() : "";
    }
}
