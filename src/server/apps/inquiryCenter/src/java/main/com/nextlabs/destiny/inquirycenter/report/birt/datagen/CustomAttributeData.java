package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

public class CustomAttributeData {
	long id;
	long logId;
	String attributeName;
	String attributeValue;
	
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getLogId() {
		return logId;
	}
	
	public void setLogId(long policyLogId) {
		this.logId = policyLogId;
	}
	public String getAttributeName() {
		return attributeName;
	}
	
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	
	public String getAttributeValue() {
		return attributeValue;
	}
	
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CustomAttributeData [id=");
		builder.append(id);
		builder.append(", logId=");
		builder.append(logId);
		builder.append(", attributeName=");
		builder.append(attributeName);
		builder.append(", attributeValue=");
		builder.append(attributeValue);
		builder.append("]");
		return builder.toString();
	}
}
