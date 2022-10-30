package com.nextlabs.destiny.console.dto.policymgmt.policyvalidator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GenericEvaluationLog implements java.io.Serializable {

	private static final long serialVersionUID = -4338281007566891063L;
	private long timestamp;
	private String content;
	private boolean mappingDetails;
	private Map<String, List<String>> mappingPolicyDetails;

	/**
	 * Returns the value of property "timestamp".
	 * 
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Updates the value of property "timestamp".
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns the value of property "content".
	 * 
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Updates the value of property "content".
	 */
	public void setContent(String content) {
		this.content = content;
	}

	public boolean isMappingDetails() {
		return mappingDetails;
	}

	public void setMappingDetails(boolean mappingDetails) {
		this.mappingDetails = mappingDetails;
	}

	public Map<String, List<String>> getMappingPolicyDetails() {
		return mappingPolicyDetails;
	}

	public void setMappingPolicyDetails(Map<String, List<String>> mappingPolicyDetails) {
		this.mappingPolicyDetails = mappingPolicyDetails;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return String.format(
				"GenericEvaluationLog [timestamp=%s, content=%s, mappingDetails=%s, mappingPolicyDetails=%s]",
				timestamp, content, mappingDetails,
				mappingPolicyDetails != null ? toString(mappingPolicyDetails.entrySet(), maxLen) : null);
	}
	

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
	

}
