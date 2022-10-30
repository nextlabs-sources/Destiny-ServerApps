/**
 * 
 */
package com.nextlabs.destiny.console.dto.policymgmt.policyvalidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.nextlabs.destiny.console.dto.common.SearchField;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
public class PolicyValidationDTO implements Serializable {

	private static final long serialVersionUID = -872661406828655010L;

	private List<Attribute> subject = new ArrayList<>();
	private List<Attribute> resource = new ArrayList<>();
	private List<Attribute> toResource = new ArrayList<>();
	private List<Attribute> action = new ArrayList<>();
	private List<Attribute> recipient = new ArrayList<>();
	private List<Attribute> application = new ArrayList<>();
	private List<Attribute> host = new ArrayList<>();
	private List<Attribute> environment = new ArrayList<>();
	private List<OtherCategories> otherCategories = new ArrayList<>();
	private List<String> responseFormat = new ArrayList<>();
	private List<Long> onDemandPolicyIds = new ArrayList<>();
	private List<SearchField> onDemandPolicyCriteria;

	private boolean debug;

	/**
	 * Returns the value of property "subject".
	 * 
	 */
	public List<Attribute> getSubject() {
		return subject;
	}

	/**
	 * Updates the value of property "subject".
	 */
	public void setSubject(List<Attribute> subject) {
		this.subject = subject;
	}

	/**
	 * Returns the value of property "resource".
	 * 
	 */
	public List<Attribute> getResource() {
		return resource;
	}

	/**
	 * Updates the value of property "resource".
	 */
	public void setResource(List<Attribute> resource) {
		this.resource = resource;
	}

	/**
	 * Returns the value of property "toResource".
	 * 
	 */
	public List<Attribute> getToResource() {
		return toResource;
	}

	/**
	 * Updates the value of property "toResource".
	 */
	public void setToResource(List<Attribute> toResource) {
		this.toResource = toResource;
	}

	/**
	 * Returns the value of property "action".
	 * 
	 */
	public List<Attribute> getAction() {
		return action;
	}

	/**
	 * Updates the value of property "action".
	 */
	public void setAction(List<Attribute> action) {
		this.action = action;
	}

	/**
	 * Returns the value of property "recipient".
	 * 
	 */
	public List<Attribute> getRecipient() {
		return recipient;
	}

	/**
	 * Updates the value of property "recipient".
	 */
	public void setRecipient(List<Attribute> recipient) {
		this.recipient = recipient;
	}

	/**
	 * Returns the value of property "application".
	 * 
	 */
	public List<Attribute> getApplication() {
		return application;
	}

	/**
	 * Updates the value of property "application".
	 */
	public void setApplication(List<Attribute> application) {
		this.application = application;
	}

	/**
	 * Returns the value of property "host".
	 * 
	 */
	public List<Attribute> getHost() {
		return host;
	}

	/**
	 * Updates the value of property "host".
	 */
	public void setHost(List<Attribute> host) {
		this.host = host;
	}

	/**
	 * Returns the value of property "environment".
	 * 
	 */
	public List<Attribute> getEnvironment() {
		return environment;
	}

	/**
	 * Updates the value of property "environment".
	 */
	public void setEnvironment(List<Attribute> environment) {
		this.environment = environment;
	}

	/**
	 * Returns the value of property "otherCategories".
	 * 
	 */
	public List<OtherCategories> getOtherCategories() {
		return otherCategories;
	}

	/**
	 * Updates the value of property "otherCategories".
	 */
	public void setOtherCategories(List<OtherCategories> otherCategories) {
		this.otherCategories = otherCategories;
	}

	/**
	 * Returns the value of property "responseFormat". response format, e.g.
	 * ["xml","json"]
	 */
	public List<String> getResponseFormat() {
		return responseFormat;
	}

	/**
	 * Updates the value of property "responseFormat".
	 */
	public void setResponseFormat(List<String> responseFormat) {
		this.responseFormat = responseFormat;
	}

	/**
	 * @return the onDemandPolicyIds
	 */
	public List<Long> getOnDemandPolicyIds() {
		return onDemandPolicyIds;
	}

	/**
	 * @param onDemandPolicyIds
	 *            the onDemandPolicyIds to set
	 */
	public void setOnDemandPolicyIds(List<Long> onDemandPolicyIds) {
		this.onDemandPolicyIds = onDemandPolicyIds;
	}

	/**
	 * @return the onDemandPolicyCriteria
	 */
	public List<SearchField> getOnDemandPolicyCriteria() {
		return onDemandPolicyCriteria;
	}

	/**
	 * @param onDemandPolicyCriteria
	 *            the onDemandPolicyCriteria to set
	 */
	public void setOnDemandPolicyCriteria(List<SearchField> onDemandPolicyCriteria) {
		this.onDemandPolicyCriteria = onDemandPolicyCriteria;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug
	 *            the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return String.format(
				"PolicyValidationDTO [subject=%s, resource=%s, toResource=%s, action=%s, recipient=%s, application=%s, host=%s, environment=%s, otherCategories=%s, responseFormat=%s, onDemandPolicyIds=%s, onDemandPolicyCriteria=%s, debug=%s]",
				subject != null ? subject.subList(0, Math.min(subject.size(), maxLen)) : null,
				resource != null ? resource.subList(0, Math.min(resource.size(), maxLen)) : null,
				toResource != null ? toResource.subList(0, Math.min(toResource.size(), maxLen)) : null,
				action != null ? action.subList(0, Math.min(action.size(), maxLen)) : null,
				recipient != null ? recipient.subList(0, Math.min(recipient.size(), maxLen)) : null,
				application != null ? application.subList(0, Math.min(application.size(), maxLen)) : null,
				host != null ? host.subList(0, Math.min(host.size(), maxLen)) : null,
				environment != null ? environment.subList(0, Math.min(environment.size(), maxLen)) : null,
				otherCategories != null ? otherCategories.subList(0, Math.min(otherCategories.size(), maxLen)) : null,
				responseFormat != null ? responseFormat.subList(0, Math.min(responseFormat.size(), maxLen)) : null,
				onDemandPolicyIds != null ? onDemandPolicyIds.subList(0, Math.min(onDemandPolicyIds.size(), maxLen))
						: null,
				onDemandPolicyCriteria != null
						? onDemandPolicyCriteria.subList(0, Math.min(onDemandPolicyCriteria.size(), maxLen)) : null,
				debug);
	}

}
