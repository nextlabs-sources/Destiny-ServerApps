/*
 * Created on Jun 12, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.sharepoint.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * DynamicField
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public class DynamicField {

	private String name;
	private String displayName;
	private FieldType type;
	private List<String> values;

	public DynamicField(String name, String displayName, FieldType type, String... values) {
		this.name = name;
		this.displayName =  displayName;
		this.type = type;

		if (values != null && values.length > 0) {
			this.values = Arrays.asList(values);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<String> getValues() {
		if(values == null) {
			values = new ArrayList<String>();
		}
		return values;
	}

	public enum FieldType {

		DATE, TEXT, DROP_DOWN, RADIO;
	}

}
