package com.nextlabs.destiny.inquirycenter.audit;

import com.nextlabs.destiny.inquirycenter.report.AuditQueryModel;

import net.sf.hibernate.Session;

public class OracleAuditLogDataGenerator 
		extends AuditLogDataGenerator {
	
	public OracleAuditLogDataGenerator(Session session)	{
		super(session);
	}
	
	@Override
	protected void generatePaginationClause(AuditQueryModel criteria) {
		if(criteria != null) {
			if(criteria.getOffset() > 0) {
				stringBuilder.append(" OFFSET ").append(criteria.getOffset()).append(" ROWS");
			}
			
			if(criteria.getPageSize() > 0) {
				stringBuilder.append(" FETCH NEXT ").append(criteria.getPageSize()).append(" ROWS ONLY");
			}
		}
	}
}
