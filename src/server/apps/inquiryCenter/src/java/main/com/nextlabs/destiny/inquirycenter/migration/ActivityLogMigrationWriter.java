/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.bluejungle.framework.datastore.hibernate.BatchWriter;
import com.bluejungle.framework.datastore.hibernate.exceptions.DataSourceException;
import com.nextlabs.destiny.container.dac.datasync.log.ReportPolicyActivityLog;

/**
 * @author nnallagatla
 *
 */
public class ActivityLogMigrationWriter<T extends ReportPolicyActivityLog> extends BatchWriter<T> {

    public static final int INSERT_LOG_QUERY_INDEX          = 0;
    public static final int INSERT_CUSTOM_ATTR_QUERY_INDEX  = 1;
	
    private final String insertLogQuery;
    private final String insertCustomAttrQuery;
    
    public ActivityLogMigrationWriter(String insertLogQuery, String insertCustomAttrQuery)
    {
    	this.insertLogQuery = insertLogQuery;
    	this.insertCustomAttrQuery = insertCustomAttrQuery;
    }
    
	@Override
	protected PreparedStatement[] createPreparedStatements(Connection conn)
			throws SQLException, DataSourceException {
        PreparedStatement[] statements = new PreparedStatement[2];
        statements[INSERT_LOG_QUERY_INDEX]          = conn.prepareStatement(insertLogQuery);
        statements[INSERT_CUSTOM_ATTR_QUERY_INDEX]  = conn.prepareStatement(insertCustomAttrQuery);
        return statements;
	}

	@Override
	protected void setValues(PreparedStatement statement, T row, int index)
			throws SQLException, DataSourceException {
        switch (index) {
        case INSERT_LOG_QUERY_INDEX:
            row.setValue(statement);
            break;
        case INSERT_CUSTOM_ATTR_QUERY_INDEX:
            row.setCustomAttributesValue(statement);
            break;
        default:
            throw new IllegalArgumentException("unknown index: " + index);
	}
	}
}
