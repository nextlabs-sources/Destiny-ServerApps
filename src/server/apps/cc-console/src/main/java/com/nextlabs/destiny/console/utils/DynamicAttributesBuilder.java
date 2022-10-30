/**
 * 
 */
package com.nextlabs.destiny.console.utils;

import java.util.Collection;
import java.util.Map;

import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.domain.destiny.action.IDAction;
import com.bluejungle.pf.domain.destiny.subject.IDSubject;
import com.bluejungle.pf.domain.epicenter.resource.IResource;

/**
 * Dynamic Attributes builder class
 *
 * @author Amila Silva 
 * @author kyu
 * @since 8.0.8
 *
 */
public final class DynamicAttributesBuilder {
	private DynamicAttributes attr = null;

	public DynamicAttributesBuilder() {
		super();
		this.attr = new DynamicAttributes();
	}

	@SuppressWarnings("unchecked")
	public DynamicAttributesBuilder build(String key, Object value) {
		if (value instanceof String) {
			this.attr.put(key, (String) value);
		} else if (value instanceof Long) {
			this.attr.put(key, (Long) value);
		} else if (value instanceof String[]) {
			this.attr.put(key, (String[]) value);
		} else if (value instanceof Collection) {
			this.attr.put(key, (Collection<String>) value);
		} else {
			this.attr.put(key, String.valueOf(value));
		}
		return this;
	}

	/**
	 * 
	 * Build dynamic attributes from Subject
	 * 
	 * @param subject
	 * @return {@link DynamicAttributesBuilder}
	 */
	public DynamicAttributesBuilder build(IDSubject subject) {
		this.attr.put("id", subject.getUid());
		for (Map.Entry<String, IEvalValue> entry : subject.getEntrySet()) {
			this.build(entry.getKey(), entry.getValue().getValue());
		}
		return this;
	}
	
	/**
	 * 
	 * Build dynamic attributes from Subject
	 * 
	 * @param subject
	 * @return {@link DynamicAttributesBuilder}
	 */
	public DynamicAttributesBuilder buildSendTo(IDSubject[] sendToArr) {
		StringBuffer sBuf = new StringBuffer();
		for(IDSubject subject: sendToArr) {
			sBuf.append(subject.getName()).append(",");
		}
		this.attr.put("email/s", sBuf.toString());
		return this;
	}
	
	/**
	 * 
	 * Build dynamic attributes from Subject
	 * 
	 * @param subject
	 * @return {@link DynamicAttributesBuilder}
	 */
	public DynamicAttributesBuilder buildEnvAttributes(DynamicAttributes attributes) {
		for(Map.Entry<String, IEvalValue> entry: attributes.entrySet()) {
			String key = entry.getKey();
			if(key.equals("0_debugenabled") || key.equals("0_log_id")) {
				continue;
			}
			this.build(key, entry.getValue().getValue());
		}
		return this;
	}
	
	/**
	 * 
	 * Build dynamic attributes from Action
	 * 
	 * @param subject
	 * @return {@link DynamicAttributesBuilder}
	 */
	public DynamicAttributesBuilder build(IDAction action) {
		this.attr.put("name", action.getName());
		return this;
	}

	/**
	 * 
	 * Build dynamic attributes from Action
	 * 
	 * @param subject
	 * @return {@link DynamicAttributesBuilder}
	 */
	public DynamicAttributesBuilder build(IResource resource) {
		for (Map.Entry<String, IEvalValue> entry : resource.getEntrySet()) {
			this.build(entry.getKey(), entry.getValue().getValue());
		}
		return this;
	}

	/**
	 * @return the attr
	 */
	public DynamicAttributes build() {
		return attr;
	}
}
