/*
 * Created on May 12, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

/**
 * <p>
 *  AttributeMappingData
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class AttributeMappingData {
	
	private Long attributeId;
	private String name;
	private String value;
	private String type;
	private boolean fromMainTable;

	public Long getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(Long attributeId) {
		this.attributeId = attributeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFromMainTable() {
		return fromMainTable;
	}

	public void setFromMainTable(boolean fromMainTable) {
		this.fromMainTable = fromMainTable;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AttributeMappingData [attributeId=");
		builder.append(attributeId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", value=");
		builder.append(value);
		builder.append(", fromMainTable=");
		builder.append(fromMainTable);
		builder.append("]");
		return builder.toString();
	}
	
	
}
