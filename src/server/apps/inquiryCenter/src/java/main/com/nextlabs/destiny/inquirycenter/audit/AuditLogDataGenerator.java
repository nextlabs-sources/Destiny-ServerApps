package com.nextlabs.destiny.inquirycenter.audit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.inquirycenter.enumeration.AuditableEntity;
import com.nextlabs.destiny.container.shared.inquirymgr.LoggablePreparedStatement;
import com.nextlabs.destiny.inquirycenter.SharedUtils;
import com.nextlabs.destiny.inquirycenter.report.AuditQueryModel;
import com.nextlabs.report.datagen.ResultData;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

public abstract class AuditLogDataGenerator 
		implements IAuditLogDataGenerator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	protected Session session;
	protected StringBuilder stringBuilder;
	
	public AuditLogDataGenerator(Session session) {
		super();
		this.session = session;
		this.stringBuilder = new StringBuilder();
	}

	public ResultData executeQuery(AuditQueryModel criteria)
			throws SQLException {
		String[] columnNames = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		List<Object[]> dataList = new LinkedList<Object[]>();
		
		try {
			preparedStatement = new LoggablePreparedStatement(session.connection(), generateQueryString(criteria));
			populateParameters(preparedStatement, criteria);
			resultSet = preparedStatement.executeQuery();
			
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			int timestampColumn = -1;
			int entityTypeColumn = -1;
			columnNames = new String[columnCount];
			
			for (int i = 0; i < columnCount; i++) {
				columnNames[i] = resultSetMetaData.getColumnLabel(i + 1);
				if (columnNames[i].equalsIgnoreCase("TIMESTAMP")) {
					timestampColumn = i;
				} else if(columnNames[i].equalsIgnoreCase("ENTITY_TYPE")) {
					entityTypeColumn = i;
				}
			}

			while(resultSet.next()) {
				Object[] values = new Object[columnCount];
				
				for(int i = 0; i < columnCount; i++) {
					if(i == timestampColumn) {
						values[i] = SharedUtils.getFormatedDate(new Date(resultSet.getLong(i + 1)));
					} else if(i == entityTypeColumn) {
						AuditableEntity entityType = AuditableEntity.getEntityType(resultSet.getString(i + 1));
						values[i] = entityType == null ? resultSet.getString(i + 1) : entityType.getDisplayName();
					} else {
						values[i] = resultSet.getObject(i + 1);
					}
				}
				dataList.add(values);
			}
		} catch(SQLException e) {
			log.error(e.getMessage(), e);
		} catch(HibernateException e) {
			log.error(e.getMessage(), e);
		} finally {
			if(resultSet != null) {
				resultSet.close();
			}
			
			if(preparedStatement != null) {
				preparedStatement.close();
			}
		}
		
		return new ResultData(columnNames, dataList.toArray(new Object[dataList.size()][]));
	}

	public void cleanup()
		throws Exception {
		if(this.session != null) {
			this.session.connection().close();
			this.session = null;
		}
	}

	protected String generateQueryString(AuditQueryModel criteria) {
		generateSelectClause(criteria);
		generateFromClause(criteria);
		generateWhereClause(criteria);
		generateGroupByClause(criteria);
		generateOrderClause(criteria);
		generatePaginationClause(criteria);
		
		log.info("Query: " + this.stringBuilder.toString());
		return this.stringBuilder.toString();
	}

	protected void generateSelectClause(AuditQueryModel criteria) {
		this.stringBuilder.append("SELECT ID, TIMESTAMP, ACTION, ACTOR, ENTITY_TYPE, ENTITY_ID,")
						  .append(" OLD_VALUE, NEW_VALUE");
	}

	protected void generateFromClause(AuditQueryModel criteria) {
		this.stringBuilder.append(" FROM ENTITY_AUDIT_LOG");
	}

	protected void generateWhereClause(AuditQueryModel criteria) {
		if(criteria != null) {
			this.stringBuilder.append(" WHERE TIMESTAMP >= ?");
			this.stringBuilder.append(" AND TIMESTAMP <= ?");

			if(!"ALL".equals(criteria.getAction())) {
				this.stringBuilder.append(" AND ACTION = ?");
			}

			if(!"ALL".equals(criteria.getEntityType())) {
				this.stringBuilder.append(" AND ENTITY_TYPE = ?");
			}

			if(criteria.getUsers() != null
					&& criteria.getUsers().length > 0) {
				// Create n numbers of ? placeholder
				String[] wildcards = new String[criteria.getUsers().length];
				for(int i = 0; i < wildcards.length; i++) {
					wildcards[i] = "?";
				}
				
				this.stringBuilder.append(" AND LOWER(ACTOR) IN (")
								  .append(StringUtils.join(wildcards, ", "))
								  .append(")");
			}

			if(criteria.getEntityIds() != null
					&& criteria.getEntityIds().length > 0) {
				// Create n numbers of ? placeholder
				String[] wildcards = new String[criteria.getEntityIds().length];
				for(int i = 0; i < wildcards.length; i++) {
					wildcards[i] = "?";
				}
				
				this.stringBuilder.append(" AND ENTITY_ID IN (")
								  .append(StringUtils.join(wildcards, ", "))
								  .append(")");
			}
		}
	}

	protected void generateGroupByClause(AuditQueryModel criteria) {
		this.stringBuilder.append(" GROUP BY ID, TIMESTAMP, ACTION, ACTOR, ENTITY_TYPE, ENTITY_ID,")
		  .append(" OLD_VALUE, NEW_VALUE");
	}
	
	protected void generateOrderClause(AuditQueryModel criteria) {
		if(criteria != null
				&& criteria.getOrderBy() != null) {
			this.stringBuilder.append(" ORDER BY ")
							  .append(criteria.getOrderBy().getColumnName())
							  .append(" ")
							  .append(criteria.getOrderBy().getSortOrder());
		}
	}

	protected void populateParameters(PreparedStatement statement, AuditQueryModel criteria)
			throws SQLException {
		if(statement != null
				&& criteria != null) {
			int parameterIndex = 1;
			
			statement.setLong(parameterIndex++, criteria.getBeginDate().getTime());
			statement.setLong(parameterIndex++, criteria.getEndDate().getTime());
			
			if(!"ALL".equals(criteria.getAction())) {
				statement.setString(parameterIndex++, criteria.getAction());
			}

			if(!"ALL".equals(criteria.getEntityType())) {
				statement.setString(parameterIndex++, criteria.getEntityType());
			}

			if(criteria.getUsers() != null
					&& criteria.getUsers().length > 0) {
				for(int i = 0; i < criteria.getUsers().length; i++) {
					statement.setString(parameterIndex++, criteria.getUsers()[i].toLowerCase());
				}
			}

			if(criteria.getEntityIds() != null
					&& criteria.getEntityIds().length > 0) {
				for(int i = 0; i < criteria.getEntityIds().length; i++) {
					statement.setLong(parameterIndex++, criteria.getEntityIds()[i]);
				}
			}
		}
	}

	protected abstract void generatePaginationClause(AuditQueryModel criteria);
}
