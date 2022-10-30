package com.nextlabs.destiny.inquirycenter.audit;

import java.sql.SQLException;

import com.nextlabs.destiny.inquirycenter.report.AuditQueryModel;
import com.nextlabs.report.datagen.ResultData;

public interface IAuditLogDataGenerator {
	
	public ResultData executeQuery(AuditQueryModel criteria) throws SQLException;
	public void cleanup() throws Exception;
}
