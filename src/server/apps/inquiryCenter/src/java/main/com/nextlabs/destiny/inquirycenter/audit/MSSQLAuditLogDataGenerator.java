package com.nextlabs.destiny.inquirycenter.audit;

import com.nextlabs.destiny.inquirycenter.report.AuditQueryModel;

import net.sf.hibernate.Session;

public class MSSQLAuditLogDataGenerator 
		extends AuditLogDataGenerator {
	
	private boolean useTop = true;
	
	public MSSQLAuditLogDataGenerator(Session session) {
		super(session);
	}
	
	@Override
	protected void generateSelectClause(AuditQueryModel criteria) {
		if(useTop && criteria.getPageSize() > 0) {
			this.stringBuilder.append("SELECT TOP ").append(criteria.getPageSize())
							  .append(" ID, TIMESTAMP, ACTION, ACTOR, ENTITY_TYPE, ENTITY_ID,")
							  .append(" OLD_VALUE, NEW_VALUE");
		} else {
			this.stringBuilder.append("SELECT ID, TIMESTAMP, ACTION, ACTOR, ENTITY_TYPE, ENTITY_ID,")
							  .append(" OLD_VALUE, NEW_VALUE");
		}
	}
	
	@Override
	protected void generatePaginationClause(AuditQueryModel criteria) {
		if(criteria != null) {
			if(criteria.getOffset() > 0) {
				stringBuilder.append(" OFFSET ")
							 .append(criteria.getOffset())
							 .append((criteria.getOffset() == 1) ? " ROW" : " ROWS");
				
				// MSSQL FETCH NEXT must use in conjunction with OFFSET
				if(!useTop && criteria.getPageSize() > 0) {
					stringBuilder.append(" FETCH NEXT ")
								 .append(criteria.getPageSize())
								 .append((criteria.getPageSize() == 1) ? " ROW ONLY": " ROWS ONLY");
				}
			}
		}
	}
}
