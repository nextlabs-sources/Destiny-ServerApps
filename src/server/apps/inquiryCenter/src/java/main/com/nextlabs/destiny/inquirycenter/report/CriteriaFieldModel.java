/*
 * Created on Jun 4, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * CriteriaFieldModel
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public class CriteriaFieldModel {

	private String name;
	private String operator;
	private String value;
	private String function;
	private List<String> values;
	private boolean multiValue;

	public CriteriaFieldModel() {

	}

	public CriteriaFieldModel(String name, String operator, boolean multiValue,
			String value) {
		this.name = name;
		this.operator = operator;
		this.value = value;
		this.multiValue = multiValue;
	}

	public CriteriaFieldModel(String name, String function, String operator,
			String value) {
		this.name = name;
		this.operator = operator;
		this.function = function;
		this.value = value;
	}

	public CriteriaFieldModel(String name, String operator, boolean multiValue,
			List<String> values) {
		this.name = name;
		this.operator = operator;
		this.values = values;
		this.multiValue = multiValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public List<String> getValues() {
		if (values == null) {
			values = new ArrayList<String>();
		}
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public boolean isMultiValue() {
		return multiValue;
	}

	public void setMultiValue(boolean multiValue) {
		this.multiValue = multiValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CriteriaFieldModel [");
		builder.append("name=");
		builder.append(name);
		builder.append(", ");
		builder.append("operator=");
		builder.append(operator);
		builder.append(", ");
		if (value != null) {
			builder.append("value=");
			builder.append(value);
			builder.append(", ");
		}
		if (values != null) {
			builder.append("values=");
			builder.append(values);
			builder.append(", ");
		}
		builder.append("multiValue=");
		builder.append(multiValue);
		builder.append("]");
		return builder.toString();
	}

}
