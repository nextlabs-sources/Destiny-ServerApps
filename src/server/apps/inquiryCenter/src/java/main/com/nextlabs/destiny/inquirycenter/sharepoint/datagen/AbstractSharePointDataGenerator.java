/*
 * Created on Jun 12, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.sharepoint.datagen;

import static com.nextlabs.destiny.inquirycenter.sharepoint.datagen.SharePointReportTypeEnum.PLC_ACTIVITY_POLICY;
import static com.nextlabs.destiny.inquirycenter.sharepoint.datagen.SharePointReportTypeEnum.getReportTypeByName;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.destiny.container.shared.inquirymgr.LoggablePreparedStatement;
import com.nextlabs.destiny.inquirycenter.report.CriteriaFieldModel;
import com.nextlabs.destiny.inquirycenter.report.OrderByModel;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.GroupByData;

/**
 * <p>
 * Abstract class to handle data generation of the Share point reports.
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public abstract class AbstractSharePointDataGenerator implements
		ISharePointDataGenerator {

	protected Log log = LogFactory.getLog(AbstractSharePointDataGenerator.class
			.getName());

	protected PreparedStatement sharePointQueryStmt;
	protected Session session;

	/**
	 * <p>
	 * Constructor
	 * </p>
	 * 
	 * @param session
	 */
	AbstractSharePointDataGenerator(Session session) {
		this.session = session;
	}

	@Override
	public List<GroupByData> generateSQLAndGetResults(
			SharePointCriteriaJSONModel jsonModel) {
		List<GroupByData> data = new ArrayList<GroupByData>();
		try {
			log.info("Request came to generate SQL and get results");
			
			SharePointReportTypeEnum reportType = getReportTypeByName(jsonModel
					.getReportName());

			String sqlQuery = queryGenerator(reportType, jsonModel);
			
			log.info("Share point SQL query :" + sqlQuery);

			sharePointQueryStmt = new LoggablePreparedStatement(
					session.connection(), sqlQuery);

			int currArg = 1;
			sharePointQueryStmt.setInt(currArg++, 3); // log level
			sharePointQueryStmt.setString(currArg++, "sharepoint://%"); // from_resource_name
			sharePointQueryStmt.setTimestamp(currArg++,
					Timestamp.valueOf(jsonModel.getStartDate())); // from date
			sharePointQueryStmt.setTimestamp(currArg++,
					Timestamp.valueOf(jsonModel.getEndDate())); // to date
			sharePointQueryStmt.setString(currArg++,
					getObligationNameByType(reportType)); // ObligationName
			
			log.info("Dynamic parameter Fields :" + jsonModel.getFields().size());

			for (CriteriaFieldModel field : jsonModel.getFields()) {
				String likeSuffix = (field.getOperator().equals("like")) ? "%"
						: "";
				String value = (field.getValue() != null && !field.getValue()
						.isEmpty()) ? field.getValue().trim().toLowerCase() + likeSuffix : "%";

				log.info("Dynamic parameter settings :" + value);
				sharePointQueryStmt.setString(currArg++, value);
			}

			ResultSet rs = sharePointQueryStmt.executeQuery();
			try {

				while (rs.next()) {
					GroupByData groupData = new GroupByData();

					switch (reportType) {
					case INFOR_LIFECYCLE_DEPARTMENT:
					case INFOR_LIFECYCLE_PROCESS:
					case INFOR_LIFECYCLE_SITE:
					case INFOR_LIFECYCLE_TREND:
					case INFOR_LIFECYCLE_USER:
						groupData.setDimension(rs.getString(1));
						groupData.setCategory(rs.getString(2));
						groupData.setResultCount(rs.getInt(3));
						break;
					case PLC_ACTIVITY_DEPARTMENT:
					case PLC_ACTIVITY_POLICY:
					case PLC_ACTIVITY_PROCESS:
					case PLC_ACTIVITY_SITE:
					case PLC_ACTIVITY_TREND:
					case PLC_ACTIVITY_USER:
						groupData.setDimension(rs.getString(1));
						groupData.setResultCount(rs.getInt(2));
						break;

					default:
						break;
					}
					data.add(groupData);
				}

			} finally {
				if (rs != null)
					rs.close();
				sharePointQueryStmt.close();
			}

			log.info("Share point report data row count :" + data.size());

		} catch (Exception e) {
			 log.error("Error encountered while generate SQL and get results,", e);
		}
		return data;
	}
	

	@Override
	public void cleanUp() throws Exception {
		if(session != null) {
			session.connection().close();
			session.close();
		}
	}

	/**
	 * <p>
	 * 
	 * </p>
	 * 
	 * @param reportType
	 * @return
	 */
	private String getObligationNameByType(SharePointReportTypeEnum reportType) {
		switch (reportType) {
		case INFOR_LIFECYCLE_DEPARTMENT:
		case INFOR_LIFECYCLE_PROCESS:
		case INFOR_LIFECYCLE_SITE:
		case INFOR_LIFECYCLE_TREND:
		case INFOR_LIFECYCLE_USER:
			return "SPLOGACTIVITY";

		case PLC_ACTIVITY_DEPARTMENT:
		case PLC_ACTIVITY_POLICY:
		case PLC_ACTIVITY_PROCESS:
		case PLC_ACTIVITY_SITE:
		case PLC_ACTIVITY_TREND:
		case PLC_ACTIVITY_USER:
			return "SPLOGCONTROL";

		default:
			return "";
		}
	}

	protected String queryGenerator(SharePointReportTypeEnum reportType,
			SharePointCriteriaJSONModel jsonModel) throws Exception {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(" SELECT ");
		queryBuilder.append(getSelectFragment(jsonModel));
		queryBuilder.append(getFromClause(reportType));
		queryBuilder.append(" AND log_level = ? ");
		queryBuilder.append(" AND from_resource_name LIKE ? ");
		queryBuilder.append(" AND time >= ? AND time <= ?  ");
		queryBuilder.append(" AND obl.name = ? ");
		queryBuilder.append(getActionColumnCriteria(reportType));
		queryBuilder.append(addOtherCriteria(reportType));
		queryBuilder.append(groupByCriteria(reportType, jsonModel));
		queryBuilder.append(orderByCriteria(reportType, jsonModel));

		log.info("Share point SQL :" + queryBuilder.toString());

		return queryBuilder.toString();

	}

	private static final String RPA_LOG = "RPA_LOG";
	private static final String RPT_OBLIG_LOG = "REPORT_OBLIGATION_LOG";
	private static final String CACHED_POLICY = "CACHED_POLICY";

	private String addOtherCriteria(SharePointReportTypeEnum reportType) {
		switch (reportType) {
		case INFOR_LIFECYCLE_SITE:
		case PLC_ACTIVITY_SITE:
			return " AND lower(attr_three) LIKE ? ";
		case INFOR_LIFECYCLE_USER:
		case PLC_ACTIVITY_USER:
			return " AND lower(attr_one) LIKE ? AND lower(attr_two) LIKE ? ";
		default:
			return " ";
		}
	}

	private String getFromClause(SharePointReportTypeEnum reportType) {
		switch (reportType) {
		case PLC_ACTIVITY_POLICY:
			return "FROM "
					+ RPA_LOG
					+ " rpa_log, "
					+ RPT_OBLIG_LOG
					+ "  obl,  "
					+ CACHED_POLICY
					+ " cp "
					+ " WHERE rpa_log.id = obl.ref_log_id AND rpa_log.policy_id = cp.id ";
		default:
			return " FROM " + RPA_LOG + " rpa_log, " + RPT_OBLIG_LOG + "  obl "
					+ " WHERE  rpa_log.id = obl.ref_log_id ";
		}

	}

	private String getSelectFragment(SharePointCriteriaJSONModel jsonModel)
			throws Exception {
		List<String> columns = jsonModel.getSelectColumns();
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < columns.size(); i++) {
			String formattedCol = getSelectColumnFormatting(columns.get(i));
			stringBuilder.append(formattedCol).append(" ");

			if (i < columns.size() - 1) {
				stringBuilder.append(", ");
			}
		}

		if (jsonModel.getAggregator() != null) {
			CriteriaFieldModel field = jsonModel.getAggregator();
			stringBuilder.append(", ");
			stringBuilder.append(field.getFunction()).append("(")
					.append(field.getValue()).append(") ")
					.append(" as ").append(field.getName()).append(" ");
		}
		return stringBuilder.toString();
	}
	
	/**
	 * <p>
	 * Column data formatting columns. 
	 * </p>
	 *
	 * @param columnName name of the column
	 * @return formatted string for field
	 */
	protected abstract String getSelectColumnFormatting(String columnName);
	

	private String groupByCriteria(SharePointReportTypeEnum reportType,
			SharePointCriteriaJSONModel jsonModel) throws Exception {
		List<String> groupByFields = jsonModel.getGroupByFields();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(" GROUP BY ");

		if (PLC_ACTIVITY_POLICY == reportType) {
			stringBuilder.append(" cp.name ");
			return stringBuilder.toString();
		}

		for (int i = 0; i < groupByFields.size(); i++) {
			stringBuilder.append(groupByFields.get(i));

			if (i < groupByFields.size() - 1) {
				stringBuilder.append(", ");
			}
		}
		return stringBuilder.toString();
	}

	private String orderByCriteria(SharePointReportTypeEnum reportType,
			SharePointCriteriaJSONModel jsonModel) throws Exception {
		List<OrderByModel> orderByFields = jsonModel.getOrderByFields();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(" ORDER BY ");

		if (PLC_ACTIVITY_POLICY == reportType) {
			stringBuilder.append(" cp.name ");
			return stringBuilder.toString();
		}

		for (int i = 0; i < orderByFields.size(); i++) {
			OrderByModel model = orderByFields.get(i);

			stringBuilder.append(model.getColumnName()).append(" ")
					.append(model.getSortOrder());

			if (i < orderByFields.size() - 1) {
				stringBuilder.append(", ");
			}

		}
		return stringBuilder.toString();
	}

	private String getActionColumnCriteria(SharePointReportTypeEnum reportType) {
		switch (reportType) {
		case INFOR_LIFECYCLE_DEPARTMENT:
		case INFOR_LIFECYCLE_PROCESS:
		case INFOR_LIFECYCLE_SITE:
		case INFOR_LIFECYCLE_TREND:
		case INFOR_LIFECYCLE_USER:
			return " AND (action = 'Op' OR action = 'Ed' OR action = 'Pr' OR action = 'Ex' OR action = 'Rn' OR action = 'Co' OR action = 'Mo' OR action = 'De') ";
		default:
			return " ";
		}

	}

}
