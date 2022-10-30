package com.nextlabs.destiny.inquirycenter.audit;

import com.nextlabs.destiny.inquirycenter.report.AuditQueryModel;

import net.sf.hibernate.Session;

public class PostgreSQLAuditLogDataGenerator 
		extends AuditLogDataGenerator {
	
	public PostgreSQLAuditLogDataGenerator(Session session) {
		super(session);
	}
	
	@Override
	protected void generatePaginationClause(AuditQueryModel criteria) {
		if(criteria != null) {
			if(criteria.getPageSize() > 0) {
				stringBuilder.append(" LIMIT ").append(criteria.getPageSize());
			}
			
			if(criteria.getOffset() > 0) {
				stringBuilder.append(" OFFSET ").append(criteria.getOffset());
			}
		}
	}
}
