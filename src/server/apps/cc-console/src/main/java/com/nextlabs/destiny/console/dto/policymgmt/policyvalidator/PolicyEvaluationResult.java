package com.nextlabs.destiny.console.dto.policymgmt.policyvalidator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
public class PolicyEvaluationResult implements java.io.Serializable {
	/** Default serial version ID. */
	private static final long serialVersionUID = 1L;

	private String decision;

	private List<Obligation> obligations = new ArrayList<>();

	private long timeInMs;

	private Status status;

	private long logId;

	/**
	 * Returns the value of property "decision". the decision of the evaluation,
	 * valid values include "Permit", "Deny"
	 */
	public String getDecision() {
		return decision;
	}

	/**
	 * Updates the value of property "decision".
	 */
	public void setDecision(String decision) {
		this.decision = decision;
	}

	/**
	 * Returns the value of property "obligations".
	 * 
	 */
	public List<Obligation> getObligations() {
		return obligations;
	}

	/**
	 * Updates the value of property "obligations".
	 */
	public void setObligations(List<Obligation> obligations) {
		this.obligations = obligations;
	}

	/**
	 * @return the timeInMs
	 */
	public long getTimeInMs() {
		return timeInMs;
	}

	/**
	 * @param timeInMs
	 *            the timeInMs to set
	 */
	public void setTimeInMs(long timeInMs) {
		this.timeInMs = timeInMs;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the logId
	 */
	public long getLogId() {
		return logId;
	}

	/**
	 * @param logId
	 *            the logId to set
	 */
	public void setLogId(long logId) {
		this.logId = logId;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return String.format("PolicyEvaluationResult [decision=%s, obligations=%s, timeInMs=%s, status=%s, logId=%s]",
				decision, obligations != null ? obligations.subList(0, Math.min(obligations.size(), maxLen)) : null,
				timeInMs, status, logId);
	}

}