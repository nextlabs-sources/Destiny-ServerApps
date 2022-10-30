package com.nextlabs.destiny.inquirycenter.report;

import java.util.Date;

public class AuditQueryModel {
	
	private Date beginDate;
	
	private Date endDate;
	
	private String action;
	
	private String entityType;
	
	private String[] users;
	
	private Long[] entityIds;
	
	private OrderByModel orderBy;

	private int pageSize;
	
	private int offset;
	
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String[] getUsers() {
		return users;
	}

	public void setUsers(String[] users) {
		this.users = users;
	}

	public Long[] getEntityIds() {
		return entityIds;
	}

	public void setEntityIds(Long[] entityIds) {
		this.entityIds = entityIds;
	}

	public OrderByModel getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderByModel orderBy) {
		this.orderBy = orderBy;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
